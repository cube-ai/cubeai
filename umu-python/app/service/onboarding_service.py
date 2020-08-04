import json
import subprocess
import zipfile
import docker
from app.utils import mytime
from app.service import umm_client, uaa_client, message_service, nexus_client
from app.domain.task import Task
from app.domain.task_step import TaskStep
from app.domain.solution import Solution
from app.domain.artifact import Artifact
from app.utils.file_tools import *
from app.globals.globals import g
from app.resources import dockerfiles


def onboarding(task_uuid):
    tasks = umm_client.get_tasks(task_uuid, jwt=g.oauth_client.get_jwt())
    if tasks is None:
        return

    task = Task()
    task.__dict__= tasks[0]

    save_task_progress(task, "正在执行", 5, "启动模型导入...")

    try:
        user = uaa_client.get_user(task.userLogin, jwt=g.oauth_client.get_jwt())
    except:
        save_task_progress(task, '失败', 100, '获取用户信息失败。', mytime.now())
        save_task_step_progress(task.uuid, '提取模型文件', '失败', 100, '获取用户信息失败。')
        return

    solution = Solution()
    solution.uuid = task.targetUuid
    solution.authorLogin = task.userLogin
    solution.authorName = user.get('fullName')

    base_path = os.path.expanduser('~') + '/tempfile/ucumosmodels/' + task.userLogin + '/' + task.uuid

    if do_onboarding(task, solution, base_path):
        message_service.send_message(
            solution.authorLogin,
            '模型 ' + solution.name + ' 导入完成',
            '你的模型 ' + solution.name + ' 已导入系统！\n\n请点击下方[目标页面]按钮进入模型页面...',
            '/pmodelhub/#/solution/' + solution.uuid,
            False
        )
    else:
        revertback_onboarding(task_uuid, base_path)
        message_service.send_message(
            solution.authorLogin,
            '模型 ' + task.taskName + ' 导入失败',
            '你的模型 ' + task.taskName + ' 导入失败！\n\n请点击下方[目标页面]按钮查看任务执行情况...',
            '/pmodelhub/#/task-onboarding/' + task_uuid + '/' + task.taskName,
            False
        )


def save_task_progress(task, taskStatus, taskProgress, description, endDate=None):
    task.taskStatus = taskStatus
    task.taskProgress = taskProgress
    task.description = description
    if endDate is not None:
        task.endDate = endDate
    umm_client.update_task(task, jwt=g.oauth_client.get_jwt())


def save_task_step_progress(taskUuid, stepName, stepStatus, stepProgress, description):
    task_step = TaskStep()
    task_step.taskUuid = taskUuid
    task_step.stepName = stepName
    task_step.stepStatus = stepStatus
    task_step.stepProgress = stepProgress
    task_step.description = description
    task_step.stepDate = mytime.now()

    umm_client.create_task_step(task_step, jwt=g.oauth_client.get_jwt())


def do_onboarding(task, solution, base_path):
    save_task_progress(task, '正在执行', 10, '提取模型文件...')

    if not extract_modelfile(task, base_path):
        save_task_progress(task, '失败', 100, '提取模型文件失败...', mytime.now())
        del_path(base_path)
        return False
    save_task_progress(task, '正在执行', 20, '完成文件验证并成功提取模型文件。')

    if not create_solution(task, solution, base_path):
        save_task_progress(task, '失败', 100, '创建模型对象失败...', mytime.now())
        del_path(base_path)
        return False
    save_task_progress(task, '正在执行', 40, '完成模型对象创建。')

    if not add_artifacts(task, solution, base_path):
        save_task_progress(task, '失败', 100, '添加artifact文件失败...', mytime.now())
        del_path(base_path)
        return False
    save_task_progress(task, '正在执行', 60, '完成添加artifact文件。')

    if not generate_TOSCA(task, solution, base_path):
        save_task_progress(task, '失败', 100, '生成TOSCA文件失败...', mytime.now())
        del_path(base_path)
        return False
    save_task_progress(task, '正在执行', 70, '完成TOSCA文件生成。')

    if not generate_microservice(task, solution, base_path):
        save_task_progress(task, '失败', 100, '生成微服务失败...', mytime.now())
        del_path(base_path)
        return False
    save_task_progress(task, '正在执行', 90, '完成微服务生成。')

    del_path(base_path)
    save_task_progress(task, '成功', 100, '完成模型导入。', mytime.now())

    return True


def revertback_onboarding(solution_uuid, base_path):
    del_path(base_path)

    artifact_list = umm_client.get_all_artifacts(solution_uuid, g.oauth_client.get_jwt())
    if artifact_list:
        for artifact in artifact_list:
            if artifact.get('type') == 'DOCKER镜像':
                nexus_client.delete_docker_image(artifact.get('url'))
            else:
                nexus_client.delete_artifact(artifact.get('url'))

            umm_client.delete_artifact(artifact.get('id'), g.oauth_client.get_jwt())


def extract_modelfile(task, base_path):
    save_task_step_progress(task.uuid, '提取模型文件', '执行', 20, '开始从上传压缩包中提取模型文件...')

    if not os.path.exists(base_path):
        save_task_step_progress(task.uuid, '提取模型文件', '失败', 100, '保存压缩文件的文件夹不存在。')
        return False

    file_list = os.listdir(base_path)
    if len(file_list) < 1:
        save_task_step_progress(task.uuid, '提取模型文件', '失败', 100, '压缩文件包不存在。')
        return False

    filename = file_list[0]
    if not filename.endswith('.zip'):
        save_task_step_progress(task.uuid, '提取模型文件', '失败', 100, '压缩文件名后缀不是.zip。')
        return False

    file_path = os.path.join(base_path, filename)
    file_zip = zipfile.ZipFile(file_path, 'r')
    try:
        for file in file_zip.namelist():
            file_zip.extract(file, base_path)
    except:
        save_task_step_progress(task.uuid, '提取模型文件', '失败', 100, '解压缩zip文件失败。')
        return False
    file_zip.close()
    save_task_step_progress(task.uuid, '提取模型文件', '执行', 40, '解压缩zip文件。')

    os.remove(file_path)
    save_task_step_progress(task.uuid, '提取模型文件', '执行', 60, '删除原始zip文件。')
    save_task_step_progress(task.uuid, '提取模型文件', '执行', 80, '获取文件列表。')

    model_files = {}
    file_list = os.listdir(base_path)
    for filename in file_list:
        file_path = os.path.join(base_path, filename)
        if filename.endswith('.zip'):
            model_files['modelFile'] = file_path
        if filename.endswith('.proto'):
            model_files['schemaFile'] = file_path
        if filename.endswith('.json'):
            model_files['metadataFile'] = file_path

    if model_files.get('modelFile') is None or model_files.get('schemaFile') is None or model_files.get('metadataFile') is None:
        save_task_step_progress(task.uuid, '提取模型文件', '失败', 100, 'zip文件中包含的模型文件不全。')
        return False

    save_task_step_progress(task.uuid, '提取模型文件', '成功', 100, '完成文件验证并成功提取模型文件。')
    return True


def create_solution(task, solution, base_path):
    save_task_step_progress(task.uuid, '创建模型对象', '执行', 20, '开始解析模型元数据...')

    if not parse_metadata(solution, os.path.join(base_path, 'metadata.json')):
        save_task_step_progress(task.uuid, '创建模型对象', '失败', 100, '解析模型元数据文件失败。')
        return False

    save_task_step_progress(task.uuid, '创建模型对象', '成功', 100, '完成模型元数据文件解析。')

    return True


def parse_metadata(solution, metadata_file):
    try:
        with open(metadata_file, 'r') as file:
            metadata_json = json.load(file)

        solution.name = metadata_json.get('name', '未命名')
        solution.version = metadata_json.get('modelVersion', 'v1.0.0')
    except:
        return False

    return True


def add_artifacts(task, solution, base_path):
    model_file = os.path.join(base_path, 'model.zip')
    metadata_file =  os.path.join(base_path, 'metadata.json')
    schema_file = os.path.join(base_path, 'model.proto')

    save_task_step_progress(task.uuid, '添加artifact', '执行', 20, '开始添加artifact文件...')

    if not add_artifact(solution, model_file, '模型程序'):
        save_task_step_progress(task.uuid, '添加artifact', '失败', 100, '添加模型程序artifact失败。')
        return False
    save_task_step_progress(task.uuid, '添加artifact', '执行', 40,
                            '成功添加模型镜像artifact文件：' + model_file[model_file.rfind('/') + 1:])

    if not add_artifact(solution, schema_file, 'PROTOBUF文件'):
        save_task_step_progress(task.uuid, '添加artifact', '失败', 100, '添加PROTOBUF artifact文件失败。')
        return False
    save_task_step_progress(task.uuid, '添加artifact', '执行', 70,
                            '成功添加PROTOBUF artifact文件：' + schema_file[schema_file.rfind('/') + 1:])

    if not add_artifact(solution, metadata_file, '元数据'):
        save_task_step_progress(task.uuid, '添加artifact', '失败', 100, '添加元数据artifact文件失败。')
        return False
    save_task_step_progress(task.uuid, '添加artifact', '执行', 90,
                            '成功添加元数据artifact文件：' + metadata_file[metadata_file.rfind('/') + 1:])

    save_task_step_progress(task.uuid, '添加artifact', '成功', 100, '完成artifact文件添加。')

    return True


def add_artifact(solution, file, file_type):
    short_url = '{}/{}/artifact/{}'.format(solution.authorLogin, solution.uuid, file[file.rfind('/') + 1:])
    long_url = nexus_client.upload_artifact(short_url, file)

    if long_url is None:
        return False

    artifact = Artifact()
    artifact.solutionUuid = solution.uuid
    artifact.name = file[file.rfind('/') + 1:]
    artifact.type = file_type
    artifact.url = long_url
    artifact.fileSize = os.path.getsize(file)

    try:
        umm_client.create_artifact(artifact, jwt=g.oauth_client.get_jwt())
    except:
        return False

    return True


def generate_TOSCA(task, solution, base_path):
    save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 20, '开始生成TOSCA文件...')
    save_task_step_progress(task.uuid, '生成TOSCA文件', '成功', 100, '完成TOSCA文件生成。')

    return True


def generate_microservice(task, solution, base_path):
    if 'remote' == g.config.docker_location:
        done = generate_microservice_remote(task, solution, base_path)
    else:
        done = generate_microservice_local(task, solution, base_path)

    if not done:
        return False

    try:
        umm_client.create_solution(solution, jwt=g.oauth_client.get_jwt())
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '向数据库中保存模型对象失败。')
        return False
    save_task_step_progress(task.uuid, '创建微服务', '成功', 100, '完成微服务创建。')

    return True


def generate_microservice_local(task, solution, base_path):
    config = g.get_central_config()
    docker_server = config['nexus']['docker']['imagetagPrefix']
    username = config['nexus']['docker']['registryUsername']
    password = config['nexus']['docker']['registryPassword']
    image_id_local = '{}:{}'.format(solution.uuid, solution.version)
    image_id_remote = '{}/{}'.format(docker_server, image_id_local)

    model_file = os.path.join(base_path, 'model.zip')
    metadata_file =  os.path.join(base_path, 'metadata.json')
    schema_file = os.path.join(base_path, 'model.proto')
    output_path = os.path.join(base_path, 'app')
    os.makedirs(output_path)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 10, '开始创建微服务...')

    save_task_step_progress(task.uuid, '创建微服务', '执行', 15, '从模型文件中提取元数据...')
    try:
        with open(metadata_file, 'r') as file:
            metadata = json.load(file)
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '从模型文件中提取元数据失败。')
        return False

    try:
        if metadata['runtime']['name'] != 'python':
            save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '暂时不支持非Python运行环境。')
            return False
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中无有效运行环境描述。')
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 20, '生成微服务Dockerfile文件...')
    try:
        python_version = metadata['runtime']['version']
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中无Python版本号。')
        return False

    if python_version.startswith('3.5'):
        dockerfile_text = dockerfiles.python35
    elif python_version.startswith('3.6'):
        dockerfile_text = dockerfiles.python36
    elif python_version.startswith('3.7'):
        dockerfile_text = dockerfiles.python37
    else:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中Python版本号无效。')
        return False

    dockerfile_text = dockerfile_text.replace(r'{DOCKER-SERVER}', docker_server)
    dockerfile_path = os.path.join(output_path, 'Dockerfile')
    with open(dockerfile_path, 'w') as file:
        file.write(dockerfile_text)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 30, '生成微服务requirements文件...')
    try:
        requirements = metadata['runtime']['dependencies']['pip']['requirements']
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中无有效requirements。')
        return False

    with open(os.path.join(output_path, 'requirements.txt'), 'w') as file:
        for req in requirements:
            line = req.get('name')
            version = req.get('version')
            if version:
                line += '==' + version
            file.write(line)
            file.write('\n')

    save_task_step_progress(task.uuid, '创建微服务', '执行', 40, '准备微服务运行环境...')
    model_path = os.path.join(output_path, 'model')
    os.makedirs(model_path)
    shutil.move(model_file, model_path)
    shutil.move(metadata_file, model_path)
    shutil.move(schema_file, model_path)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 50, '生成微服务docker镜像...')
    res = os.system('docker build -t {} {}'.format(image_id_local, output_path))
    if res != 0:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '生成微服务docker镜像失败。')
        return False

    output = subprocess.getoutput('docker image inspect {}'.format(image_id_local))
    image_size = json.loads(output)[0].get('Size')

    save_task_step_progress(task.uuid, '创建微服务', '执行', 70, '为docker镜像打tag...')
    res = os.system('docker tag {} {}'.format(image_id_local, image_id_remote))
    if res != 0:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '为docker镜像打tag失败。')
        os.system('docker image rm {}'.format(image_id_local))
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 75, '登录镜像仓库...')
    res = os.system('docker login -u {} -p {} {}'.format(username, password, docker_server))
    if res != 0:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '登录镜像仓库失败。')
        os.system('docker image rm {}'.format(image_id_remote))
        os.system('docker image rm {}'.format(image_id_local))
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 80, '推送docker至镜像仓库...')
    res = os.system('docker push {}'.format(image_id_remote))
    if res != 0:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '推送docker至镜像仓库失败。')
        os.system('docker image rm {}'.format(image_id_remote))
        os.system('docker image rm {}'.format(image_id_local))
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 85, '删除本地docker镜像...')
    os.system('docker image rm {}'.format(image_id_remote))
    os.system('docker image rm {}'.format(image_id_local))

    save_task_step_progress(task.uuid, '创建微服务', '执行', 90, '创建docker镜像的Artifact对象并写入数据库...')
    artifact = Artifact()
    artifact.solutionUuid = solution.uuid
    artifact.name = image_id_local
    artifact.type = 'DOCKER镜像'
    artifact.url = image_id_remote
    artifact.fileSize = image_size
    try:
        umm_client.create_artifact(artifact, jwt=g.oauth_client.get_jwt())
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '创建docker镜像的Artifact对象并写入数据库失败。')
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 95, '成功创建微服务docker镜像。')

    return True


def generate_microservice_remote(task, solution, base_path):
    config = g.get_central_config()
    docker_host = config['nexus']['docker']['host']
    docker_port = config['nexus']['docker']['port']
    docker_server = config['nexus']['docker']['imagetagPrefix']
    username = config['nexus']['docker']['registryUsername']
    password = config['nexus']['docker']['registryPassword']
    image_id_local = '{}:{}'.format(solution.uuid, solution.version)
    image_id_remote = '{}/{}'.format(docker_server, image_id_local)

    model_file = os.path.join(base_path, 'model.zip')
    metadata_file =  os.path.join(base_path, 'metadata.json')
    schema_file = os.path.join(base_path, 'model.proto')
    output_path = os.path.join(base_path, 'app')
    os.makedirs(output_path)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 10, '开始创建微服务...')

    save_task_step_progress(task.uuid, '创建微服务', '执行', 15, '从模型文件中提取元数据...')
    try:
        with open(metadata_file, 'r') as file:
            metadata = json.load(file)
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '从模型文件中提取元数据失败。')
        return False

    try:
        if metadata['runtime']['name'] != 'python':
            save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '暂时不支持非Python运行环境。')
            return False
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中无有效运行环境描述。')
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 20, '生成微服务Dockerfile文件...')
    try:
        python_version = metadata['runtime']['version']
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中无Python版本号。')
        return False

    if python_version.startswith('3.5'):
        dockerfile_text = dockerfiles.python35
    elif python_version.startswith('3.6'):
        dockerfile_text = dockerfiles.python36
    elif python_version.startswith('3.7'):
        dockerfile_text = dockerfiles.python37
    else:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中Python版本号无效。')
        return False

    dockerfile_text = dockerfile_text.replace(r'{DOCKER-SERVER}', docker_server)
    dockerfile_path = os.path.join(output_path, 'Dockerfile')
    with open(dockerfile_path, 'w') as file:
        file.write(dockerfile_text)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 30, '生成微服务requirements文件...')
    try:
        requirements = metadata['runtime']['dependencies']['pip']['requirements']
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '元数据文件中无有效requirements。')
        return False

    with open(os.path.join(output_path, 'requirements.txt'), 'w') as file:
        for req in requirements:
            line = req.get('name')
            version = req.get('version')
            if version:
                line += '==' + version
            file.write(line)
            file.write('\n')

    save_task_step_progress(task.uuid, '创建微服务', '执行', 40, '准备微服务运行环境...')
    model_path = os.path.join(output_path, 'model')
    os.makedirs(model_path)
    shutil.move(model_file, model_path)
    shutil.move(metadata_file, model_path)
    shutil.move(schema_file, model_path)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 50, '生成微服务docker镜像...')
    docker_cilent = docker.DockerClient('{}:{}'.format(docker_host, docker_port))
    docker_images = docker_cilent.images
    try:
        docker_images.build(tag=image_id_local, path=output_path, rm=True)
    except Exception as e:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '生成微服务docker镜像失败。')
        return False

    image_obj = docker_images.get(image_id_local)
    image_size = image_obj.attrs.get('Size')

    save_task_step_progress(task.uuid, '创建微服务', '执行', 70, '为docker镜像打tag...')
    try:
        image_obj.tag(image_id_remote)
    except Exception as e:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '生成微服务docker镜像失败。')
        try:
            docker_images.remove(image_id_local)
        except:
            pass
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 80, '推送docker至镜像仓库...')
    try:
        docker_images.push(image_id_remote, auth_config={'username': username, 'password': password})
    except Exception as e:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '生成微服务docker镜像失败。')
        try:
            docker_images.remove(image_id_remote)
            docker_images.remove(image_id_local)
        except:
            pass
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 85, '删除本地docker镜像...')
    try:
        docker_images.remove(image_id_remote)
        docker_images.remove(image_id_local)
    except:
        pass

    save_task_step_progress(task.uuid, '创建微服务', '执行', 90, '创建docker镜像的Artifact对象并写入数据库...')
    artifact = Artifact()
    artifact.solutionUuid = solution.uuid
    artifact.name = image_id_local
    artifact.type = 'DOCKER镜像'
    artifact.url = image_id_remote
    artifact.fileSize = image_size
    try:
        umm_client.create_artifact(artifact, jwt=g.oauth_client.get_jwt())
    except:
        save_task_step_progress(task.uuid, '创建微服务', '失败', 100, '创建docker镜像的Artifact对象并写入数据库失败。')
        return False

    save_task_step_progress(task.uuid, '创建微服务', '执行', 95, '成功创建微服务docker镜像。')

    return True
