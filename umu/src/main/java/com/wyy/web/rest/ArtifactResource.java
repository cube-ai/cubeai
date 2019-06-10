package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Artifact;
import com.wyy.service.NexusArtifactClient;
import com.wyy.service.UmmClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * REST controller for get arifact file text.
 */
@RestController
@RequestMapping("/api")
public class ArtifactResource {

    private final Logger log = LoggerFactory.getLogger(ArtifactResource.class);

    private UmmClient ummClient;
    private NexusArtifactClient nexusArtifactClient;

    public ArtifactResource(UmmClient ummClient, NexusArtifactClient nexusArtifactClient) {
        this.ummClient = ummClient;
        this.nexusArtifactClient = nexusArtifactClient;
    }

    @RequestMapping(value = "/artifact/metadata/{solutionUuid}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<JSONObject> getMetadataText(@PathVariable("solutionUuid") String solutionUuid) {
        log.debug("REST request to view metadata text");

        List<Artifact> artifacts = this.ummClient.getArtifacts(solutionUuid, "元数据");

        if (artifacts.size() < 1) {
            return ResponseEntity.notFound().build();
        }

        String metadataText = nexusArtifactClient.getArtifact(artifacts.get(0).getUrl()).toString();
        JSONObject result = new JSONObject();
        result.put("metadata", metadataText);

        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/artifact/protobuf/{solutionUuid}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<JSONObject> getProtobufText(@PathVariable("solutionUuid") String solutionUuid) {
        log.debug("REST request to view metadata text");

        List<Artifact> artifacts = this.ummClient.getArtifacts(solutionUuid, "PROTOBUF文件");

        if (artifacts.size() < 1) {
            return ResponseEntity.notFound().build();
        }

        String protobufText = nexusArtifactClient.getArtifact(artifacts.get(0).getUrl()).toString();
        JSONObject result = new JSONObject();
        result.put("protobuf", protobufText);

        return ResponseEntity.ok().body(result);
    }

}
