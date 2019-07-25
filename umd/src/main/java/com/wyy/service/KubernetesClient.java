package com.wyy.service;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;

import java.util.HashMap;
import java.util.Map;

public class KubernetesClient {

    private CoreV1Api coreApi;
    private AppsV1Api appsApi;
    private String nameSpace;

    public KubernetesClient(String k8sApiUrl, String k8sApiToken, String nameSpace) {
        Configuration.setDefaultApiClient(Config.fromToken(k8sApiUrl, k8sApiToken, false));
        this.coreApi = new CoreV1Api();
        this.appsApi = new AppsV1Api();
        this.nameSpace = nameSpace;
    }

    public boolean readNamespace() {
        try {
            coreApi.readNamespace(this.nameSpace, null, null, null);
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public boolean createNamespace() {
        try {
            if (!readNamespace()) {
                coreApi.createNamespace(new V1Namespace().metadata(new V1ObjectMeta().name(nameSpace)),
                    null, null, null);
            }
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public boolean createDeployment(String name, String imageUrl, String imageName) {
        try {
            Map<String, String> label = new HashMap<>(8);
            label.put("ucumos", name);
            V1Deployment deployYaml = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind("Deployment")
                .withNewMetadata()
                .withNamespace(nameSpace)
                .withName("deployment-" + name)
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
                .withName(imageName)
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
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public V1Deployment readDeploymentStatus(String name) throws Exception {
        return appsApi.readNamespacedDeploymentStatus("deployment-" + name, nameSpace, null);
    }

    public boolean deleteDeployment(String name) {
        try {
            appsApi.deleteNamespacedDeployment("deployment-" + name, nameSpace, new V1DeleteOptions(),
                null, null, null, null, null);
        } catch (ApiException e) {
            if(e.getCode() != 404) {
                return false;
            }
        }
        return true;
    }

    public boolean createService(String name) {
        try {
            Map<String, String> label = new HashMap<>(8);
            label.put("ucumos", name);
            V1Service serviceYaml = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind("Service")
                .withNewMetadata()
                .withNamespace(nameSpace)
                .withName("service-" + name)
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
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public V1Service readServiceStatus(String name) throws Exception {
        return coreApi.readNamespacedServiceStatus("service-" + name, nameSpace, null);
    }

    public boolean deleteService(String name) {
        try {
            coreApi.deleteNamespacedService("service-" + name, nameSpace, new V1DeleteOptions(),
                null, null, null, null, null);
        } catch (ApiException e) {
            if(e.getCode() != 404) {
                return false;
            }
        }
        return true;
    }

    public boolean deleteDeploy(String name) {
        boolean result = deleteDeployment(name);
        if (deleteService(name)) {
            return result;
        } else {
            return false;
        }
    }
}
