package com.wyy.service.docker.cmd;



import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;

/**
 * Parent class of all Docker commands.
 */
public abstract class DockerCommand
{

	protected DockerClient client;

	public abstract void execute() throws DockerException;

	public abstract String getDisplayName();

	public DockerClient getClient()
	{
		return client;
	}

	public void setClient(DockerClient dockerClient)
	{
		this.client = dockerClient;
	}
}
