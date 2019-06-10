package com.wyy.service.docker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DockerConfiguration {

	private String config;

	private String apiVersion = "1.23";

    @Value("${nexus.docker.host}")
	private String host;

    @Value("${nexus.docker.port}")
	private Integer port;

    @Value("${nexus.docker.registryUsername}")
	private String registryUsername;

    @Value("${nexus.docker.registryPassword}")
	private String registryPassword;

    @Value("${nexus.docker.imagetagPrefix}")
	private String imagetagPrefix;

	private String registryUrl = "";

	private String registryEmail = "";

	private Integer requestTimeout;

	private boolean tlsVerify;

	private String certPath;

	private boolean socket = false;

	private String cmdExecFactory = "com.github.dockerjava.netty.NettyDockerCmdExecFactory";

	private Integer maxTotalConnections = 100;

	private Integer maxPerRouteConnections = 100;

    public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String toUrl() {
		if (this.host == null)
			return null;
		if (this.port == null)
			return null;
		return ((this.socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getRegistryUsername() {
		return registryUsername;
	}

	public void setRegistryUsername(String registryUsername) {
		this.registryUsername = registryUsername;
	}

	public String getRegistryPassword() {
		return registryPassword;
	}

	public void setRegistryPassword(String registryPassword) {
		this.registryPassword = registryPassword;
	}

	public String getImagetagPrefix() {
		return imagetagPrefix;
	}

	public void setImagetagPrefix(String imagetagPrefix) {
		this.imagetagPrefix = imagetagPrefix;
	}

	public String getRegistryUrl() {
		return registryUrl;
	}

	public void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
	}

	public String getRegistryEmail() {
		return registryEmail;
	}

	public void setRegistryEmail(String registryEmail) {
		this.registryEmail = registryEmail;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public boolean isTlsVerify() {
		return tlsVerify;
	}

	public void setTlsVerify(boolean tlsVerify) {
		this.tlsVerify = tlsVerify;
	}

	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	public boolean isSocket() {
		return socket;
	}

	public void setSocket(boolean socket) {
		this.socket = socket;
	}

	public String getCmdExecFactory() {
		return cmdExecFactory;
	}

	public void setCmdExecFactory(String cmdExecFactory) {
		this.cmdExecFactory = cmdExecFactory;
	}

	public Integer getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(Integer maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public Integer getMaxPerRouteConnections() {
		return maxPerRouteConnections;
	}

	public void setMaxPerRouteConnections(Integer maxPerRouteConnections) {
		this.maxPerRouteConnections = maxPerRouteConnections;
	}

}
