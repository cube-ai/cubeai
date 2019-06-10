package com.wyy.service.docker.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.RemoveImageCmd;
import com.github.dockerjava.api.exception.DockerException;
import org.apache.commons.lang.StringUtils;

public class DeleteImageCommand extends DockerCommand {

	private final String imageName;

	private final String tag;

	private final String registry;

	public DeleteImageCommand(String image, String tag, String registry) {

		this.imageName = image;
		this.tag = tag;
		this.registry = registry;
	}

	public String getImageName() {
		return imageName;
	}

	public String getTag() {
		return tag;
	}

	public String getRegistry() {
		return registry;
	}

	@Override
	public void execute() throws DockerException {
		if (!StringUtils.isNotBlank(imageName)) {
			throw new IllegalArgumentException("Image name must be provided");
		}
		// Don't include tag in the image name. Docker daemon can't handle it.
		// put tag in query string parameter.
		String imageFullName = CommandUtils.imageFullNameFrom(registry, imageName, tag);
		final DockerClient client = getClient();
		RemoveImageCmd removeImageCmd = client.removeImageCmd(imageFullName);
		removeImageCmd.exec();
	}

	@Override
	public String getDisplayName() {
		return "Push image";
	}

}
