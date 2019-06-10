package com.wyy.service.docker.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This command removes specified Docker container(s).
 */
public class SaveImageCommand extends DockerCommand {

    private static final Logger logger = LoggerFactory.getLogger(SaveImageCommand.class);

	private final String imageName;

	private final String imageTag;

	private final String destination;

	private final String filename;

	private final boolean ignoreIfNotFound;

	public SaveImageCommand(final String imageName, final String imageTag, final String destination,
			final String filename, final boolean ignoreIfNotFound) {
		this.imageName = imageName;
		this.imageTag = imageTag;
		this.destination = destination;
		this.filename = filename;
		this.ignoreIfNotFound = ignoreIfNotFound;
	}

	public String getImageName() {
		return imageName;
	}

	public String getImageTag() {
		return imageTag;
	}

	public String getDestination() {
		return destination;
	}

	public String getFilename() {
		return filename;
	}

	public boolean getIgnoreIfNotFound() {
		return ignoreIfNotFound;
	}

	@Override
	public void execute() throws DockerException {
		if (imageName == null || imageName.isEmpty()) {
			throw new IllegalArgumentException("Image Name is not configured");
		}
		if (imageTag == null || imageTag.isEmpty()) {
			throw new IllegalArgumentException("Image Tag is not configured");
		}
		if (destination == null || destination.isEmpty()) {
			throw new IllegalArgumentException("Folder Destination is not configured");
		}
		if (filename == null || filename.isEmpty()) {
			throw new IllegalArgumentException("Filename is not configured");
		}
		if (!new File(destination).exists()) {
			throw new IllegalArgumentException("Destination is not a valid path");
		}
		final DockerClient client = getClient();
		try {
			logger.debug(String.format("Started save image '%s' ... ", imageName + " " + imageTag));
			final OutputStream output = new FileOutputStream(new File(destination, filename));
			IOUtils.copy(client.saveImageCmd(imageName + ":" + imageTag).exec(), output);
			IOUtils.closeQuietly(output);
			logger.debug("Finished save image " + imageName + " " + imageTag);
		} catch (NotFoundException e) {
			if (!ignoreIfNotFound) {
				logger.error(String.format("image '%s' not found ", imageName + " " + imageTag));
				throw e;
			} else {
				logger.error(
						String.format("image '%s' not found, but skipping this error is turned on, let's continue ... ",
								imageName + " " + imageTag));
			}
		} catch (IOException e) {
			logger.error(
					String.format("Error to save '%s' ", imageName + " " + imageTag) + " " + e.getLocalizedMessage());
			throw new DockerException(
					String.format("Error to save '%s' ", imageName + " " + imageTag) + " " + e.getLocalizedMessage(),
					org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public String getDisplayName() {
		return "Save image";
	}
}
