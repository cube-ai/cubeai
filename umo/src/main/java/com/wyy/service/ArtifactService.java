package com.wyy.service;

import com.wyy.domain.Artifact;
import com.wyy.util.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ArtifactService {
    private static final Logger logger = LoggerFactory.getLogger(ArtifactService.class);

    private final UmmClient ummClient;

    private final NexusArtifactClient nexusArtifactClient;

    private final ConfigurationProperties configurationProperties;

    public ArtifactService(UmmClient ummClient, NexusArtifactClient nexusArtifactClient, ConfigurationProperties configurationProperties) {
        this.ummClient = ummClient;
        this.nexusArtifactClient = nexusArtifactClient;
        this.configurationProperties = configurationProperties;
    }

    public String readJsonTOSCA(String solutionId) throws Exception {
        logger.debug("readJsonTOSCA() : Begin");
        String result = readArtifact(solutionId, configurationProperties.getToscaInputArtifactType());
        logger.debug("readJsonTOSCA() : End");
        return result;
    }
    private ByteArrayOutputStream getPayload(String uri) {

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = nexusArtifactClient.getArtifact(uri);
        } catch (Exception ex) {
            logger.error(" Exception in getListOfArtifacts() ", ex);
        }
        return outputStream;
    }
    public String readArtifact(String solutionId, String artifactType) throws Exception{
        logger.debug("readArtifact() : Begin");

        String result = "";

        ByteArrayOutputStream byteArrayOutputStream = null;
        List<Artifact> artifacts = ummClient.getArtifacts(solutionId, artifactType);

        if (null != artifacts && !artifacts.isEmpty()) {
            try {
                String nexusURI = artifacts.get(0).getUrl();
                logger.debug(" Nexus URI :  {} ", nexusURI);
                if (null != nexusURI) {
                    byteArrayOutputStream = getPayload(nexusURI);
                    logger.debug(" Response in String Format :  {} ", byteArrayOutputStream.toString());
                    result = byteArrayOutputStream.toString();
                }
            } catch (NoSuchElementException | NullPointerException e) {
                logger.error("Error : Exception in readArtifact() : Failed to fetch the artifact URI for artifactType", e);
                throw new NoSuchElementException("Could not search the artifact URI for artifactType " + artifactType);
            } catch (Exception e) {
                logger.error("Error :Exception in readArtifact() : Failed to fetch the artifact URI for artifactType", e);
                throw new Exception("Exception Occured decryptAndWriteTofile() , 501, Could not search the artifact URI for artifactType "
                    + artifactType, e.getCause());
            } finally {
                try {
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                } catch (IOException e) {
                    logger.error("Error : Exception in readArtifact() : Failed to close the byteArrayOutputStream", e);
                }
            }
        }
        logger.debug("readArtifact() : End");
        return result;
    }
}
