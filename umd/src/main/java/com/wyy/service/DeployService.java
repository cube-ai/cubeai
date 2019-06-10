package com.wyy.service;

import com.wyy.domain.*;
import io.kubernetes.client.custom.IntOrString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import io.kubernetes.client.models.*;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.apis.AppsV1Api;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DeployService {
    private static final Logger log = LoggerFactory.getLogger(DeployService.class);

    private final UmmClient ummClient;
    private final UaaClient uaaClient;
    private final MessageService messageService;

    @Value("${kubernetes.api.url}")
    private String k8sApiUrl = "";

    @Value("${kubernetes.api.token}")
    private String k8sApiToken = "";

    public DeployService(UmmClient ummClient,
                         UaaClient uaaClient,
                         MessageService messageService) {
        this.ummClient = ummClient;
        this.uaaClient = uaaClient;
        this.messageService = messageService;
    }

    public void deploy(String taskUuid, String solutionAuthorName, Boolean isPublic) {

        Task task = ummClient.getTasks(taskUuid).get(0);
        this.saveTaskProgress(task, "正在执行", 5, "启动模型部署...", null);

        String nameSpace;
        try {
            nameSpace = "ucumos-" + this.uaaClient.getUser(task.getUserLogin()).getBody().getPhone();
        } catch (Exception e) {
            this.saveTaskProgress(task, "失败", 100, "获取用户信息失败。", Instant.now());
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "获取用户信息失败。");
            return;
        }

        Configuration.setDefaultApiClient(Config.fromToken(this.k8sApiUrl, this.k8sApiToken, false));
        CoreV1Api coreApi = new CoreV1Api();
        AppsV1Api appsApi = new AppsV1Api();

        String msgSubject, msgContent;
        if (this.doDeploy(task, solutionAuthorName, nameSpace, appsApi, coreApi, isPublic)) {
            msgSubject = "完成";
            msgContent = "完成";
        } else if (this.revertDeploy(task, nameSpace, appsApi, coreApi)) {
            msgSubject = "失败";
            msgContent = "失败";
        } else {
            msgSubject = "失败";
            msgContent = "失败！\n\n操作回滚失败，请联系网站管理员";
        }
        this.messageService.sendMessage(task.getUserLogin(),
            "模型 " + task.getTaskName() + " 部署" + msgSubject,
            "你的模型 " + task.getTaskName() + " 部署" + msgContent + "！\n\n请点击下方[目标页面]按钮查看任务详情。",
            "/ucumos/deploy/view/" + taskUuid,
            false);
    }

    private boolean doDeploy(Task task, String solutionAuthorName, String nameSpace, AppsV1Api appsApi, CoreV1Api coreApi, Boolean isPublic) {
        this.saveTaskProgress(task, "正在执行", 10, "创建Kubernetes命名空间...", null);
        try {
            try {
                coreApi.readNamespace(nameSpace, null, null, null);
            } catch (ApiException e) {
                coreApi.createNamespace(new V1Namespace().metadata(new V1ObjectMeta().name(nameSpace)),
                    null, null, null);
            }
        } catch (ApiException e) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "创建Kubernetes命名空间失败。");
            this.saveTaskProgress(task, "失败", 100, "准备Kubernetes环境失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 30, "准备Kubernetes环境成功...", null);

        if (!this.createDeployment(task, nameSpace, appsApi)) {
            this.saveTaskProgress(task, "失败", 100, "创建Kubernetes部署对象失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 60, "创建Kubernetes部署对象成功...", null);

        if (!this.createService(task, nameSpace, coreApi)) {
            this.saveTaskProgress(task, "失败", 100, "创建Kubernetes服务对象失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 90, "创建Kubernetes服务对象成功。", null);

        if (!this.saveData(task, solutionAuthorName, nameSpace, coreApi, isPublic)) {
            this.saveTaskProgress(task, "失败", 100, "获取Kubernetes服务访问端口失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "成功", 100, "成功完成模型部署。", Instant.now());
        return true;
    }

    private boolean createDeployment(Task task, String nameSpace, AppsV1Api appsApi) {
        this.saveTaskStepProgress(task.getUuid(), "执行", 10,
            "提取Docker镜像文件...");
        List<Artifact> artifacts = this.ummClient.getArtifacts(task.getTargetUuid(), "DOCKER镜像");
        if (artifacts.size() < 1) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "Docker镜像不存在。");
            return false;
        }
        String imageUrl = artifacts.get(0).getUrl();

        this.saveTaskStepProgress(task.getUuid(), "执行", 40,
            "创建Kubernetes部署对象...");
        try {
            Map<String, String> label = new HashMap<>();
            label.put("ucumos", task.getUuid());
            V1Deployment deployYaml = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind("Deployment")
                .withNewMetadata()
                    .withNamespace(nameSpace)
                    .withName("deployment-" + task.getUuid())
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector()
                        .withMatchLabels(label)
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .withLabels(label)
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName(task.getTargetUuid())
                                .withImage(imageUrl)
                                .addNewPort()
                                .withContainerPort(3330)
                                .endPort()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();
                appsApi.createNamespacedDeployment(nameSpace, deployYaml, null, null, null);
        } catch (ApiException e) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "Kubernetes部署接口调用失败。");
            return false;
        }
        // 30分钟内，每秒查询1次部署状态，连续30秒无法查询到状态报错。
        for (int i = 0, errCnt = 0; i < 1800; i++) {
            try {
                V1Deployment deploymentStatus = appsApi.readNamespacedDeploymentStatus("deployment-" + task.getUuid(),
                    nameSpace, null);
                errCnt = 0;
                Integer available = deploymentStatus.getStatus().getAvailableReplicas();
                Integer ready = deploymentStatus.getStatus().getReadyReplicas();
                Integer replicas = deploymentStatus.getStatus().getReplicas();
                if(available != null && ready != null && available.equals(replicas) && ready.equals(replicas)) {
                    return true;
                }
            } catch (Exception e) {
                if(++ errCnt == 30) {
                    this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                        "获取Kubernetes部署对象状态失败。");
                    return false;
                }
                continue;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                    "部署线程错误。");
                return false;
            }
        }
        this.saveTaskStepProgress(task.getUuid(), "失败", 100,
            "创建Kubernetes部署对象超时。");
        return false;
    }

    private boolean createService(Task task, String nameSpace, CoreV1Api coreApi) {
        this.saveTaskStepProgress(task.getUuid(), "执行", 70,
            "创建Kubernetes服务对象...");
        try {
            Map<String, String> label = new HashMap<>();
            label.put("ucumos", task.getUuid());
            V1Service serviceYaml = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind("Service")
                .withNewMetadata()
                    .withNamespace(nameSpace)
                    .withName("service-" + task.getUuid())
                .endMetadata()
                .withNewSpec()
                    .withType("NodePort")
                    .withSelector(label)
                    .addNewPort()
                        .withPort(3330)
                        .withTargetPort(new IntOrString(3330))
                    .endPort()
                .endSpec()
                .build();
            coreApi.createNamespacedService(nameSpace, serviceYaml, null, null, null);
        } catch (ApiException e) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "创建Kubernetes服务对象失败。");
            return false;
        }
        return true;
    }

    private boolean saveData(Task task, String solutionAuthorName, String nameSpace, CoreV1Api coreApi, Boolean isPublic) {
        this.saveTaskStepProgress(task.getUuid(), "执行", 90,
            "获取Kubernetes服务访问端口...");
        V1Service serviceStatus;
        try {
            serviceStatus = coreApi.readNamespacedServiceStatus("service-" + task.getUuid(), nameSpace, null);
        } catch (ApiException e) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "获取Kubernetes服务访问端口失败。");
            return false;
        }
        Deployment deployment = new Deployment();
        deployment.setUuid(task.getUuid());
        deployment.setDeployer(task.getUserLogin());
        deployment.setSolutionUuid(task.getTargetUuid());
        deployment.setSolutionName(task.getTaskName());
        deployment.setSolutionAuthor(solutionAuthorName);
        deployment.setk8sPort(serviceStatus.getSpec().getPorts().get(0).getNodePort());
        deployment.setIsPublic(isPublic);
        deployment.setStatus("运行");
        this.ummClient.createDeployment(deployment);

        this.saveTaskStepProgress(task.getUuid(), "成功", 100,
            "模型部署成功。");
        return true;
    }

    private boolean revertDeploy(Task task, String nameSpace, AppsV1Api appsApi, CoreV1Api coreApi) {
		try {
            coreApi.deleteNamespacedService("service-" + task.getUuid(), nameSpace, new V1DeleteOptions(),
                null, null, null, null, null);
        } catch (ApiException e) {
            if(e.getCode() != 404) {
                return false;
            }
        }
        try {
            appsApi.deleteNamespacedDeployment("deployment-" + task.getUuid(), nameSpace, new V1DeleteOptions(),
                null, null, null, null, null);
        } catch (ApiException e) {
            if(e.getCode() != 404) {
                return false;
            }
        }
        return true;
    }

    private void saveTaskProgress(Task task, String taskStatus, Integer taskProgress, String description, Instant endDate) {
        task.setTaskStatus(taskStatus);
        task.setTaskProgress(taskProgress);
        task.setDescription(description);
        task.setEndDate(endDate);
        try {
            this.ummClient.updateTask(task);
        } catch (Exception e) {
            log.info("saveTaskProgress fail");
        }

    }

    private void saveTaskStepProgress(String taskUuid, String stepStatus, Integer stepProgress, String description) {
        TaskStep taskStep = new TaskStep();
        taskStep.setTaskUuid(taskUuid);
        taskStep.setStepName("模型部署");
        taskStep.setStepStatus(stepStatus);
        taskStep.setStepProgress(stepProgress);
        taskStep.setDescription(description);
        taskStep.setStepDate(Instant.now());
        try {
            this.ummClient.createTaskStep(taskStep);
        } catch (Exception e) {
            log.info("saveTaskStepProgress fail");
        }

    }

}
