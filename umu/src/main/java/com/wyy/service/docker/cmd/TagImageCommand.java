package com.wyy.service.docker.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This command removes specified Docker image.
 *
 * @see <A HREF="https://docs.docker.com/reference/api/docker_remote_api_v1.19/#tag-an-image-into-a-repository">Docker tag</A>
 */
public class TagImageCommand extends DockerCommand {

    private static final Logger logger = LoggerFactory.getLogger(CreateImageCommand.class);

	private final String image;

	private final String repository;

	private final String tag;

	private final boolean ignoreIfNotFound;

	private final boolean withForce;

	public TagImageCommand(final String image, final String repository, final String tag, final boolean ignoreIfNotFound, final boolean withForce) {
		this.image = image;
		this.repository = repository;
		this.tag = tag;
		this.ignoreIfNotFound = ignoreIfNotFound;
		this.withForce = withForce;
	}

	public String getImage() {
		return image;
	}

	public String getRepository() {
		return repository;
	}

	public String getTag() {
		return tag;
	}

	public boolean getIgnoreIfNotFound() {
		return ignoreIfNotFound;
	}

	public boolean getWithForce() {
		return withForce;
	}

	@Override
	public void execute() throws DockerException {
		if (image == null || image.isEmpty()) {
			throw new IllegalArgumentException("Please provide an image name");
		} else if (repository == null || repository.isEmpty()) {
			throw new IllegalArgumentException("Please provide a repository");
		} else if (tag == null || tag.isEmpty()) {
			throw new IllegalArgumentException("Please provide a tag for the image");
		}
		DockerClient client = getClient();
		try {
			logger.debug("start tagging image " + image + " in " + repository + " as " + tag);
			client.tagImageCmd(image, repository, tag).withForce(withForce).exec();
			logger.debug("Tagged image " + image + " in " + repository + " as " + tag);
		} catch (NotFoundException e) {
			if (!ignoreIfNotFound) {
				logger.error(String.format("image '%s' not found ", image));
				throw e;
			} else {
				logger.error(String.format(
						"image '%s' not found, but skipping this error is turned on, let's continue ... ", image));
			}
		}
	}

	@Override
	public String getDisplayName() {
		return "Tag image";
	}
}
