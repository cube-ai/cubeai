package com.wyy.service;

import com.wyy.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class LifeCircleManagementService {
    private static final Logger log = LoggerFactory.getLogger(DeployService.class);

    private final UmmClient ummClient;
    private final UaaClient uaaClient;
    private final MessageService messageService;

    @Value("${kubernetes.api.url}")
    private String k8sApiUrl = "";

    @Value("${kubernetes.api.token}")
    private String k8sApiToken = "";

    public LifeCircleManagementService(UmmClient ummClient,
                                       UaaClient uaaClient,
                                       MessageService messageService) {
        this.ummClient = ummClient;
        this.uaaClient = uaaClient;
        this.messageService = messageService;
    }

    public void stop(String taskUuid, String deploymentUuid) {

        Task task = ummClient.getTasks(taskUuid).get(0);
        this.saveTaskProgress(task, "正在执行", 5, "启动部署实例删除...", null);

        this.saveTaskStepProgress(task.getUuid(), "执行", 10, "获取部署实例信息...");
        List<Deployment> deploymentList = ummClient.getDeployment(deploymentUuid);
        if (deploymentList.isEmpty()) {
            this.saveTaskProgress(task, "失败", 100, "获取部署实例信息失败。", Instant.now());
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "获取部署实例信息失败。");
            return;
        }
        Deployment deployment = deploymentList.get(0);

        if (!deployment.getStatus().equals("运行")) {
            this.saveTaskProgress(task, "失败", 100, "部署实例不处于运行状态。", Instant.now());
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "部署实例不处于运行状态。");
            return;
        }

        String nameSpace;
        try {
            nameSpace = "ucumos-" + this.uaaClient.getUser(deployment.getDeployer()).getBody().getPhone();
        } catch (Exception e) {
            this.saveTaskProgress(task, "失败", 100, "获取部署实例用户信息失败。", Instant.now());
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "获取部署实例用户信息失败。");
            return;
        }

        this.saveTaskProgress(task, "正在执行", 30, "访问Kubernetes环境...", null);
        this.saveTaskStepProgress(task.getUuid(), "执行", 30, "访问Kubernetes环境...");
        KubernetesClient kubernetesClient = new KubernetesClient(k8sApiUrl, k8sApiToken, nameSpace);
        if (!kubernetesClient.readNamespace()) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "访问Kubernetes命名空间失败。");
            this.saveTaskProgress(task, "失败", 100, "访问Kubernetes环境失败。", Instant.now());
            return;
        }

        this.saveTaskProgress(task, "正在执行", 60, "删除部署实例...", null);
        this.saveTaskStepProgress(task.getUuid(), "执行", 60, "删除部署实例...");
        if (!kubernetesClient.deleteDeploy(deployment.getUuid())) {
            this.saveTaskStepProgress(task.getUuid(), "失败", 100, "部署实例删除失败。");
            this.saveTaskProgress(task, "失败", 100, "部署实例删除失败。", Instant.now());
            return;
        }

        deployment.setStatus("停止");
        this.ummClient.updateDeployment(deployment);
        this.saveTaskStepProgress(task.getUuid(), "成功", 100, "成功删除部署实例。");
        this.saveTaskProgress(task, "成功", 100, "成功删除部署实例。", Instant.now());

        // TODO 发送messageService.sendMessage
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
        taskStep.setStepName("实例停止");
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
