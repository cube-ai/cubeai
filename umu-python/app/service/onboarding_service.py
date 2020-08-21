import json
import subprocess
import zipfile
import docker
import yaml

from app.service.tosca.protobuf_generator import ProtobufGenerator
from app.service.tosca import tgif_generator
from app.utils import mytime
from app.service import umm_client, uaa_client, message_service, nexus_client
from app.domain.task import Task
from app.domain.task_step import TaskStep
from app.domain.solution import Solution
from app.domain.artifact import Artifact
from app.utils.file_tools import *
from app.globals.globals import g
from app.resources import dockerfiles
import logging
import platform


def onboarding(task_uuid):
    tasks = umm_client.get_tasks(task_uuid, jwt=g.oauth_client.get_jwt())
    if tasks is None or len(tasks) == 0:
        return

    task = Task()
    task.__dict__ = tasks[0]

    save_task_progress(task, '正在执行', 5, '启动模型导入...')

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

    save_task_step_progress(task.uuid, '提取模型文件', '成功', 100, '完成文件验证并成功提取模型文件。')
    return True


def get_model_files(base_path):
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
    return model_files


def create_solution(task, solution, base_path):
    save_task_step_progress(task.uuid, '创建模型对象', '执行', 20, '开始解析模型配置文件...')

    try:
        with open(os.path.join(base_path, 'application.yml'), 'r', encoding='utf-8') as file:
            yml = yaml.load(file, Loader=yaml.SafeLoader)
    except:
        save_task_step_progress(task.uuid, '创建模型对象', '失败', 100, '解析模型配置文件失败。')
        return False

    try:
        solution.name = yml['model']['name']
    except:
        solution.name = '未命名'
    try:
        solution.version = yml['model']['version']
    except:
        solution.version = 'v0.0.1'

    save_task_step_progress(task.uuid, '创建模型对象', '成功', 100, '完成模型配置文件解析。')

    return True


def add_artifacts(task, solution, base_path):
    save_task_step_progress(task.uuid, '添加artifact', '执行', 20, '开始添加artifact文件...')

    yml_file = os.path.join(base_path, 'application.yml')
    if not add_artifact(solution, yml_file, '模型配置'):
        save_task_step_progress(task.uuid, '添加artifact', '失败', 100, '添加模型配置artifact文件失败。')
        return False
    save_task_step_progress(task.uuid, '添加artifact', '执行', 90,
                            '成功添加模型配置文件：' + yml_file[yml_file.rfind('/') + 1:])

    save_task_step_progress(task.uuid, '添加artifact', '成功', 100, '完成artifact文件添加。')

    return True


def add_artifact_data(solution, file_name, data, file_type):
    short_url = '{}/{}/artifact/{}'.format(solution.authorLogin, solution.uuid, file_name)
    long_url = nexus_client.upload_artifact_data(short_url, data)

    if long_url is None:
        return False
    return create_artifact(solution, file_name, file_type, long_url, len(data))


def add_artifact(solution, file, file_type):
    file_name = file[file.rfind('/') + 1:]
    short_url = '{}/{}/artifact/{}'.format(solution.authorLogin, solution.uuid, file_name)
    long_url = nexus_client.upload_artifact(short_url, file)

    if long_url is None:
        return False
    return create_artifact(solution, file_name, file_type, long_url, os.path.getsize(file))


def create_artifact(solution, file_name, file_type, long_url, file_size):
    artifact = Artifact()
    artifact.solutionUuid = solution.uuid
    artifact.name = file_name
    artifact.type = file_type
    artifact.url = long_url
    artifact.fileSize = file_size

    try:
        umm_client.create_artifact(artifact, jwt=g.oauth_client.get_jwt())
    except:
        return False

    return True


def generate_TOSCA(task, solution, base_path):
    save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 10, '开始生成TOSCA文件...')

    if False:
        model_files = get_model_files(base_path)
        protobuf_generator = ProtobufGenerator()
        try:
            proto_json_str = protobuf_generator.create_proto_json(model_files.get('schemaFile'))
        except Exception as e:
            logging.error('error in createProtoJson, {}'.format(repr(e)))
            return False
        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 20, '将POTOBUF文件内容转换成JSON格式的TOSCA-SCHEMA字符串。')

        model_files['protobufFile'] = proto_json_str
        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 30, '将JSON格式的TOSCA-SCHEMA字符串写入临时文件。')

        # 上传文件作为artifact到nexus服务器，并添加信息到数据库。
        if not add_artifact_data(solution, 'protobuf.json', proto_json_str, 'TOSCA-SCHEMA'):
            save_task_step_progress(task.uuid, '生成TOSCA文件', '失败',  100, '上传TOSCA-SCHEMA文件到NEXUS服务器，并向数据库中插入artifact记录失败。')
            return False

        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行',  40, '上传TOSCA-SCHEMA文件到NEXUS服务器，并向数据库中插入artifact记录。')
        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行',  50, '开始生成TOSCA生成器输入文件（tgif）...')
        file_name = model_files.get('metadataFile')
        with open(file_name, 'r', encoding='utf-8') as file:
            text_lines = file.readlines()
            meatDataStr = ''
            for line in text_lines:
                meatDataStr += line
                meatDataStr += '\n'

        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 60, '从metadata文件读取JSON字符串。')

        metaDataJson = json.loads(meatDataStr.replace('\t', ''))
        protobufJson = json.loads(proto_json_str.replace('\t', ''))
        try:
            tgif = tgif_generator.populate_tgif(solution.version, metaDataJson, protobufJson)
        except Exception as e:
            logging.error('error in populateTgif, {}'.format(repr(e)))
            return False
        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 70, '基于metadata和proto文件内容生成tgif。')
        tgif_json_string = json.dumps(tgif, ensure_ascii=False)
        tgif_json_string = tgif_json_string.replace("[null]", "[]")
        tgif_json_string = tgif_json_string.replace("null", "{}")

        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 75, "将tgif转换成JSON格式。")

        if not add_artifact_data(solution, 'tgif.json', tgif_json_string, 'TOSCA生成器输入文件'):
            save_task_step_progress(task.uuid, '生成TOSCA文件', '失败', 100, '上传tgif文件到NEXUS服务器，并向数据库中插入artifact记录失败。')
            return False
        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 80, '上传tgif文件到NEXUS服务器，并向数据库中插入artifact记录。')
        save_task_step_progress(task.uuid, '生成TOSCA文件', '执行', 90, '成功生成TOSCA生成器输入文件（tgif）。')

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

    output_path = os.path.join(base_path, 'app')
    os.makedirs(output_path)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 10, '开始创建微服务...')
    save_task_step_progress(task.uuid, '创建微服务', '执行', 15, '从模型文件中提取元数据...')

    try:
        with open(os.path.join(base_path, 'application.yml'), 'r', encoding='utf-8') as file:
            yml = yaml.load(file, Loader=yaml.SafeLoader)
    except:
        return False

    try:
        python_version = str(yml['python']['version'])
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
    with open(dockerfile_path, 'w', encoding='utf-8') as file:
        file.write(dockerfile_text)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 30, '准备微服务运行环境...')
    if platform.system() == "Windows":
        os.system('move {}\* {}'.format(base_path, output_path))
        os.system('move {}\core {}'.format(base_path, output_path))
    else:
        os.system('mv {}/* {}'.format(base_path, output_path))

    save_task_step_progress(task.uuid, '创建微服务', '执行', 40, '生成微服务docker镜像...')
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

    output_path = os.path.join(base_path, 'app')
    os.makedirs(output_path)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 10, '开始创建微服务...')
    save_task_step_progress(task.uuid, '创建微服务', '执行', 15, '从模型文件中提取元数据...')

    try:
        with open(os.path.join(base_path, 'application.yml'), 'r', encoding='utf-8') as file:
            yml = yaml.load(file, Loader=yaml.SafeLoader)
    except:
        return False

    try:
        python_version = yml['python']['version']
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
    with open(dockerfile_path, 'w', encoding='utf-8') as file:
        file.write(dockerfile_text)

    save_task_step_progress(task.uuid, '创建微服务', '执行', 30, '准备微服务运行环境...')
    if platform.system() == "Windows":
        os.system('move {}\* {}'.format(base_path, output_path))
        os.system('move {}\core {}'.format(base_path, output_path))
    else:
        os.system('mv {}/* {}'.format(base_path, output_path))

    save_task_step_progress(task.uuid, '创建微服务', '执行', 40, '生成微服务docker镜像...')
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
