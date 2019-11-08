package com.wyy.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.wyy.domain.*;
import com.wyy.service.docker.*;
import com.wyy.service.docker.cmd.CreateImageCommand;
import com.wyy.service.docker.cmd.DeleteImageCommand;
import com.wyy.service.docker.cmd.PushImageCommand;
import com.wyy.service.docker.cmd.TagImageCommand;
import com.wyy.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;
import java.io.*;
import java.time.Instant;


@Service
public class MicroserviceGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceGenerator.class);

    private final ResourceLoader resourceLoader;
    private final DockerConfiguration dockerConfiguration;
    private final UmmClient ummClient;

    public MicroserviceGenerator(ResourceLoader resourceLoader, DockerConfiguration dockerConfiguration, UmmClient ummClient) {
        this.resourceLoader = resourceLoader;
        this.dockerConfiguration = dockerConfiguration;
        this.ummClient = ummClient;
    }

    public boolean generateMicroservice(Solution solution, JSONObject modelFiles) {
        File modelFile = modelFiles.getObject("modelFile", File.class);
        File schemaFile = modelFiles.getObject("schemaFile", File.class);
        File metadataFile = modelFiles.getObject("metadataFile", File.class);
        File outputFolder = new File(modelFiles.getString("basePath"), "app");
        outputFolder.mkdir();

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 5,
            "从模型文件中提取元数据...");
        String metadataText;
        try {
            metadataText = IOUtils.toString(new FileInputStream(metadataFile), "UTF-8");
        } catch (Exception e) {
            this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 10,
                "从模型文件中提取元数据失败。");
            return false;
        }
        JSONObject metadata = JSONObject.parseObject(metadataText);

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 10,
            "提取Dockerfile模板文件...");
        if (metadata.getJSONObject("runtime").getString("name").equals("python")) {
            Resource[] resources;
            try {
                resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("classpath*:docker-templates/python/*");
            } catch (Exception e) {
                this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 15,
                    "提取Dockerfile模板文件失败。");
                return false;
            }

            for (Resource resource : resources) {
                FileUtil.copyFile(resource, new File(outputFolder, resource.getFilename()));
            }
            JSONArray requirementsJSONArray = metadata.getJSONObject("runtime").getJSONObject("dependencies").getJSONObject("pip").getJSONArray("requirements");
            String pythonVersion = metadata.getJSONObject("runtime").getString("version");

            if (!PythonUtil.generateRequirementTxt(outputFolder, requirementsJSONArray)) {
                this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 15,
                    "生成requirement文件失败。");
                return false;
            }
            PythonUtil.generateDockerfile(outputFolder, requirementsJSONArray, pythonVersion);

            File modelFolder = new File(outputFolder, "model");
            modelFolder.mkdir();
            modelFile.renameTo(new File(modelFolder, modelFile.getName()));
            schemaFile.renameTo(new File(modelFolder, schemaFile.getName()));
            metadataFile.renameTo(new File(modelFolder, metadataFile.getName()));
        } else {
            this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 15,
                "暂时不支持非Python运行环境。");
            return false;
        }

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 20,
            "生成DockerClient对象...");
        DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
        if (null == dockerClient) {
            this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 25,
                "生成DockerClient对象失败。");
            return false;
        }

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 30,
            "创建docker镜像...");
        // String imageName = solution.getName() + "-" + solution.getUuid();
        String imageName = solution.getUuid(); // 因为模型名称可能为中文，而docker image的名字不能为中文，因此imageName直接使用solution的uuid而不再使用其名字
        String imageTag = solution.getVersion();
        CreateImageCommand createCMD = new CreateImageCommand(outputFolder, imageName, imageTag, null, false, true);
        createCMD.setClient(dockerClient);
        try {
            createCMD.execute();
        } catch (Exception e) {
            this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 50,
                "创建docker镜像失败。");
            return false;
        }

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 60,
            "为docker镜像打tag标签...");
        String imageTagName = dockerConfiguration.getImagetagPrefix() + File.separator + imageName;
        TagImageCommand tagImageCommand = new TagImageCommand(imageName + ":" + imageTag, imageTagName, imageTag, true, false);
        tagImageCommand.setClient(dockerClient);
        try {
            tagImageCommand.execute();
        } catch (Exception e) {
            this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 65,
                "为docker镜像打tag标签失败。");
            return false;
        }

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 65,
            "推送docker镜像至镜像仓库...");
        PushImageCommand pushImageCmd = new PushImageCommand(imageTagName, imageTag, dockerConfiguration.getRegistryUrl());
        pushImageCmd.setClient(dockerClient);
        try {
            pushImageCmd.execute();
        } catch (Exception e) {
            this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "失败", 70,
                "推送docker镜像至镜像仓库失败。");
            return false;
        }

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 90,
            "创建DOCKER镜像Artifact对象并写入数据库...");
        Artifact artifact = new Artifact();
        artifact.setSolutionUuid(solution.getUuid());
        artifact.setName(imageName);
        artifact.setType("DOCKER镜像");
        artifact.setUrl(imageTagName + ":" + imageTag);
        String imageId = createCMD.getImageId();
        InspectImageResponse dockerInfo = dockerClient.inspectImageCmd(imageId).withImageId(imageId).exec();
        artifact.setFileSize(dockerInfo.getSize());
        this.ummClient.createArtifact(artifact);

        this.saveTaskStepProgress(solution.getUuid(), "创建微服务", "执行", 95,
            "关闭DockerClient对象...");
        try {
            dockerClient.close();
        } catch (Exception e) {
        }
        return true;
    }

    public void revertbackOnboarding(Solution solution, String imageUri) {

        try {
            logger.debug("In RevertbackOnboarding method");

            DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);

            // Remove the image from docker registry
            // Check the value of imageUri, if it is null then do not delete the image
            logger.debug("Image Name from dockerize file method: " + imageUri);

            if (StringUtils.isNotBlank(imageUri)) {
                String imageTagName = dockerConfiguration.getImagetagPrefix() + "/" + solution.getName() + "-" + solution.getUuid();

                logger.debug("Image Name: " + imageTagName);
                DeleteImageCommand deleteImageCommand = new DeleteImageCommand(imageTagName, solution.getVersion(), "");
                deleteImageCommand.setClient(dockerClient);
                deleteImageCommand.execute();
                logger.debug("Successfully Deleted the image from Docker Registry");
            }
        } catch (Exception e) {

        }
    }

    private void saveTaskStepProgress(String taskUuid, String stepName, String stepStatus, Integer stepProgress, String description) {
        TaskStep taskStep = new TaskStep();
        taskStep.setTaskUuid(taskUuid);
        taskStep.setStepName(stepName);
        taskStep.setStepStatus(stepStatus);
        taskStep.setStepProgress(stepProgress);
        taskStep.setDescription(description);
        taskStep.setStepDate(Instant.now());
        this.ummClient.createTaskStep(taskStep);
    }

}

