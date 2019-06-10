package com.wyy.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.LocalDirectorySSLConfig;
import com.github.dockerjava.core.SSLConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;


/**
 * Methods for communicating with Docker
 */
public final class DockerClientFactory {

	public static DockerClient getDockerClient(DockerConfiguration config) {
		SSLConfig sslConfig;
		if (config.isTlsVerify()) {
			if (config.getCertPath() == null) {
                return null;
            }
			sslConfig = new LocalDirectorySSLConfig(config.getCertPath());
		} else {
			// docker-java requires an implementation of SslConfig interface
			// to be available for DockerCmdExecFactoryImpl
			sslConfig = new NoImplSslConfig();
		}

        DefaultDockerClientConfig.Builder configBuilder;
		try {
            configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(config.toUrl()).withApiVersion(config.getApiVersion())
                .withDockerTlsVerify(config.isTlsVerify()).withRegistryUrl(config.getRegistryUrl())
                .withRegistryUsername(config.getRegistryUsername()).withRegistryPassword(config.getRegistryPassword())
                .withRegistryEmail(config.getRegistryEmail()).withCustomSslConfig(sslConfig);
        } catch (Exception e) {
		    return null;
        }

		configBuilder.withDockerConfig(config.getConfig());
		if (config.getCertPath() != null) {
			configBuilder.withDockerCertPath(config.getCertPath());
		}
		String cmdExecFactory = config.getCmdExecFactory();
		DockerCmdExecFactory factory;
		if (cmdExecFactory.equals(JerseyDockerCmdExecFactory.class.getName())) {
			factory = new JerseyDockerCmdExecFactory();
			((JerseyDockerCmdExecFactory) factory).withReadTimeout(config.getRequestTimeout())
					.withConnectTimeout(config.getRequestTimeout())
					.withMaxTotalConnections(config.getMaxTotalConnections())
					.withMaxPerRouteConnections(config.getMaxPerRouteConnections());
		} else if (cmdExecFactory.equals(NettyDockerCmdExecFactory.class.getName())) {
			factory = new NettyDockerCmdExecFactory();
			((NettyDockerCmdExecFactory) factory).withConnectTimeout(config.getRequestTimeout());
		} else {
			try {
				@SuppressWarnings("unchecked")
				Class<DockerCmdExecFactory> clazz = (Class<DockerCmdExecFactory>) Class.forName(cmdExecFactory);
				factory = clazz.newInstance();
			} catch (Exception e) {
				return null;
			}
		}

		return DockerClientBuilder.getInstance(configBuilder).withDockerCmdExecFactory(factory).build();
	}
}
