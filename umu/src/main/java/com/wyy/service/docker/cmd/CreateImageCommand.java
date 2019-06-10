package com.wyy.service.docker.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import java.io.File;
import java.util.*;

/**
 * This command creates a new image from specified Dockerfile.
 *
 * @see <A HREF=
 *      "http://docs.docker.com/reference/api/docker_remote_api_v1.13/#build-an-image-from-dockerfile-via-stdin">Docker
 *      build</A>
 */
public class CreateImageCommand extends DockerCommand {
    private static final Logger logger = LoggerFactory.getLogger(CreateImageCommand.class);

	private final File dockerFolder;

	private final String imageName;

	private final String imageTag;

	private final String dockerFile;

	private final boolean noCache;

	private final boolean rm;

	private String buildArgs;

	private String imageId;

	public CreateImageCommand(File dockerFolder, String imageName, String imageTag, String dockerFile, boolean noCache,
			boolean rm) {
		this.dockerFolder = dockerFolder;
		this.imageName = imageName;
		this.imageTag = imageTag;
		this.dockerFile = dockerFile;
		this.noCache = noCache;
		this.rm = rm;
        System.out.println("***************************imageTag:"+imageTag);
	}

	public String getBuildArgs() {
		return buildArgs;
	}

	public void setBuildArgs(String buildArgs) {
		this.buildArgs = buildArgs;
	}

	public String getImageId() {
		return imageId;
	}

	@Override
	public void execute() throws DockerException {
		if (dockerFolder == null) {
			logger.error( "dockerFolder is not configured");
			throw new IllegalArgumentException("dockerFolder is not configured");
		}
		if (imageName == null) {
			logger.error( "imageName is not configured");
			throw new IllegalArgumentException("imageName is not configured");
		}
		if (imageTag == null) {
			logger.error( "imageName is not configured");
			throw new IllegalArgumentException("imageTag is not configured");
		}
		if (!dockerFolder.exists()) {
			logger.error( "configured dockerFolder '" + dockerFolder + "' does not exist.");
			throw new IllegalArgumentException("configured dockerFolder '" + dockerFolder + "' does not exist.");
		}
		final Map<String, String> buildArgsMap = new HashMap<String, String>();
		if ((buildArgs != null) && (!buildArgs.trim().isEmpty())) {
			logger.debug("Parsing buildArgs: " + buildArgs);
			String[] split = buildArgs.split(",|;");
			for (String arg : split) {
				String[] pair = arg.split("=");
				if (pair.length == 2) {
					buildArgsMap.put(pair[0].trim(), pair[1].trim());
				} else {
					logger.error("Invalid format for " + arg + ". Buildargs should be formatted as key=value");
				}
			}
		}
		String dockerFile = this.dockerFile == null ? "Dockerfile" : this.dockerFile;
		File docker = new File(dockerFolder, dockerFile);
		if (!docker.exists()) {
			logger.error( "Configured Docker file '%s' does not exist. {}", dockerFile);
			throw new IllegalArgumentException(String.format("Configured Docker file '%s' does not exist.", dockerFile));
		}
		DockerClient client = getClient();
		try {
			BuildImageResultCallback callback = new BuildImageResultCallback() {
				@Override
				public void onNext(BuildResponseItem item) {
					super.onNext(item);
				}
			};

			BuildImageCmd buildImageCmd = client.buildImageCmd(docker)
					.withTags(new HashSet<>(Arrays.asList(imageName + ":" + imageTag))).withNoCache(noCache)
					.withRemove(rm); // .withTag(imageName + ":" + imageTag)
			if (!buildArgsMap.isEmpty()) {
				for (final Map.Entry<String, String> entry : buildArgsMap.entrySet()) {
					buildImageCmd = buildImageCmd.withBuildArg(entry.getKey(), entry.getValue());
				}
			}
			BuildImageResultCallback result = buildImageCmd.exec(callback);
			this.imageId = result.awaitImageId();

		} catch (Exception e) {
			logger.error( "Error {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getDisplayName() {
		return "Create/build image";
	}
}
