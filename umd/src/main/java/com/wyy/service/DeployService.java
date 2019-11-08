package com.wyy.service;

import com.wyy.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import io.kubernetes.client.models.*;
import java.util.List;


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

    @Value("${kubernetes.api.ipIpTunnelCidr}")
    private String ipIpTunnelCidr = "";

    public DeployService(UmmClient ummClient,
                         UaaClient uaaClient,
                         MessageService messageService) {
        this.ummClient = ummClient;
        this.uaaClient = uaaClient;
        this.messageService = messageService;
    }

    public void deploy(String taskUuid, Boolean isPublic) {

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

        KubernetesClient kubernetesClient = new KubernetesClient(k8sApiUrl, k8sApiToken, nameSpace);

        String msgSubject, msgContent;
        if (this.doDeploy(task, kubernetesClient, isPublic)) {
            msgSubject = "完成";
            msgContent = "完成";
        } else if (kubernetesClient.deleteDeploy(task.getUuid())) {
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

    private boolean doDeploy(Task task, KubernetesClient kubernetesClient, Boolean isPublic) {
        this.saveTaskProgress(task, "正在执行", 10, "创建Kubernetes命名空间...", null);
        if (!kubernetesClient.createNamespace()) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "创建Kubernetes命名空间失败。");
            this.saveTaskProgress(task, "失败", 100, "准备Kubernetes环境失败。", Instant.now());
            return false;
        }
        // 暂不执行NetworkPolicy
        //if (!kubernetesClient.createNetworkPolicy(this.ipIpTunnelCidr)) {
        //    this.saveTaskStepProgress(task.getUuid(), "失败", 100, "创建Kubernetes网络策略失败。");
        //    this.saveTaskProgress(task, "失败", 100, "准备Kubernetes环境失败。", Instant.now());
        //    return false;
        //}
        this.saveTaskProgress(task, "正在执行", 30, "准备Kubernetes环境成功...", null);

        if (!this.createDeployment(task, kubernetesClient)) {
            this.saveTaskProgress(task, "失败", 100, "创建Kubernetes部署对象失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 60, "创建Kubernetes部署对象成功...", null);

        if (!this.createService(task, kubernetesClient)) {
            this.saveTaskProgress(task, "失败", 100, "创建Kubernetes服务对象失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 90, "创建Kubernetes服务对象成功。", null);

        if (!this.saveData(task, kubernetesClient, isPublic)) {
            this.saveTaskProgress(task, "失败", 100, "获取Kubernetes服务访问端口失败。", Instant.now());
            return false;
        }
        this.saveTaskProgress(task, "成功", 100, "成功完成模型部署。", Instant.now());
        return true;
    }

    private boolean createDeployment(Task task, KubernetesClient kubernetesClient) {
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
        if (!kubernetesClient.createDeployment(task.getUuid(), imageUrl, task.getTargetUuid())) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "Kubernetes部署接口调用失败。");
            return false;
        }
        // 30分钟内，每秒查询1次部署状态，连续30秒无法查询到状态报错。
        for (int i = 0, errCnt = 0, okCnt = 0; i < 1800 || okCnt > 0; i++) {
            try {
                V1Deployment deploymentStatus = kubernetesClient.readDeploymentStatus(task.getUuid());
                errCnt = 0;
                Integer available = deploymentStatus.getStatus().getAvailableReplicas();
                Integer ready = deploymentStatus.getStatus().getReadyReplicas();
                Integer replicas = deploymentStatus.getStatus().getReplicas();
                if(available != null && ready != null && available.equals(replicas) && ready.equals(replicas)) {
                    // 需维持成功状态连续15秒。
                    if (++ okCnt == 15) {
                        return true;
                    }
                } else if (okCnt > 0) {
                    this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                        "模型程序运行出错。");
                    return false;
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

    private boolean createService(Task task, KubernetesClient kubernetesClient) {
        this.saveTaskStepProgress(task.getUuid(), "执行", 70,
            "创建Kubernetes服务对象...");
        if (!kubernetesClient.createService(task.getUuid())) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "创建Kubernetes服务对象失败。");
            return false;
        }
        return true;
    }

    private boolean saveData(Task task, KubernetesClient kubernetesClient, Boolean isPublic) {
        this.saveTaskStepProgress(task.getUuid(), "执行", 90,
            "获取Kubernetes服务访问端口...");
        V1Service serviceStatus;
        try {
            serviceStatus = kubernetesClient.readServiceStatus(task.getUuid());
        } catch (Exception e) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100,
                "获取Kubernetes服务访问端口失败。");
            return false;
        }

        Solution solution = this.ummClient.getSolutions(task.getTargetUuid()).get(0);
        Deployment deployment = new Deployment();
        deployment.setUuid(task.getUuid());
        deployment.setDeployer(task.getUserLogin());
        deployment.setSolutionUuid(task.getTargetUuid());
        deployment.setSolutionName(task.getTaskName());
        deployment.setSolutionAuthor(solution.getAuthorName());
        deployment.setSolutionCompany(solution.getCompany());
        deployment.setModelType(solution.getModelType());
        deployment.setToolkitType(solution.getToolkitType());
        deployment.setPictureUrl(solution.getPictureUrl());
        deployment.setk8sPort(serviceStatus.getSpec().getPorts().get(0).getNodePort());
        deployment.setIsPublic(isPublic);
        deployment.setStatus("运行");
        this.ummClient.createDeployment(deployment);

        this.saveTaskStepProgress(task.getUuid(), "成功", 100,
            "模型部署成功。");
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
