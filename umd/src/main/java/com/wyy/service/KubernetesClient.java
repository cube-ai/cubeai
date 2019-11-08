package com.wyy.service;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.NetworkingV1Api;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.QuantityFormatter;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class KubernetesClient {

    private CoreV1Api coreApi;
    private AppsV1Api appsApi;
    private NetworkingV1Api networkingApi;
    private String nameSpace;

    public KubernetesClient(String k8sApiUrl, String k8sApiToken, String nameSpace) {
        Configuration.setDefaultApiClient(Config.fromToken(k8sApiUrl, k8sApiToken, false));
        this.coreApi = new CoreV1Api();
        this.appsApi = new AppsV1Api();
        this.networkingApi = new NetworkingV1Api();
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

    public boolean readNetworkPolicy() {
        try {
            networkingApi.readNamespacedNetworkPolicy(
                    "networkpolicy-" + nameSpace, nameSpace, null, null, null);
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public boolean createNetworkPolicy(String ipIpTunnelCidr) {
        try {
            if (!readNetworkPolicy()) {
                V1NetworkPolicy networkPolicyYaml = new V1NetworkPolicyBuilder()
                    .withApiVersion("networking.k8s.io/v1")
                    .withKind("NetworkPolicy")
                    .withNewMetadata()
                        .withName("networkpolicy-" + nameSpace)
                        .withNamespace(nameSpace)
                    .endMetadata()
                    .withNewSpec()
                        .withNewPodSelector()
                        .endPodSelector()
                        .withPolicyTypes(Arrays.asList("Ingress", "Egress"))
                        .addNewIngress()
                            .addNewFrom()
                                .withNewIpBlock()
                                    .withCidr(ipIpTunnelCidr)
                                .endIpBlock()
                            .endFrom()
                            .addNewFrom()
                                .withNewPodSelector()
                                .endPodSelector()
                            .endFrom()
                            .withPorts(new V1NetworkPolicyPort().protocol("TCP").port(new IntOrString(3330)))
                        .endIngress()
                        .addNewEgress()
                            .addNewTo()
                                .withNewIpBlock()
                                    .withCidr(ipIpTunnelCidr)
                                .endIpBlock()
                            .endTo()
                            .addNewTo()
                                .withNewPodSelector()
                                .endPodSelector()
                            .endTo()
                            .withPorts(new V1NetworkPolicyPort().protocol("TCP").port(new IntOrString(3330)))
                        .endEgress()
                    .endSpec()
                    .build();
                networkingApi.createNamespacedNetworkPolicy(
                    nameSpace, networkPolicyYaml, null, null, null);
            }
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    public boolean deleteNetworkPolicy() {
        try {
            networkingApi.deleteNamespacedNetworkPolicy("networkpolicy-" + nameSpace, nameSpace, null,
                    new V1DeleteOptions(), null, null, null, null);
        } catch (ApiException e) {
            if(e.getCode() != 404) {
                return false;
            }
        }
        return true;
    }

    private V1ResourceRequirements buildResource() {
        V1ResourceRequirements resourceRequirements = new V1ResourceRequirements();
        resourceRequirements.putRequestsItem("memory", new QuantityFormatter().parse("2Gi"));
        resourceRequirements.putRequestsItem("cpu", new QuantityFormatter().parse("2"));
        resourceRequirements.putLimitsItem("memory", new QuantityFormatter().parse("15Gi"));
        resourceRequirements.putLimitsItem("cpu", new QuantityFormatter().parse("15"));
        return resourceRequirements;
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
                                .withResources(buildResource())
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
            appsApi.deleteNamespacedDeployment("deployment-" + name, nameSpace, null, new V1DeleteOptions(),
                null, null, null, null);
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
            coreApi.deleteNamespacedService("service-" + name, nameSpace, null, new V1DeleteOptions(),
                null, null, null, null);
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
