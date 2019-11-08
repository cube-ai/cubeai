package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyy.domain.Artifact;
import com.wyy.domain.Blueprint;
import com.wyy.service.ComposerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping(value = "/orchestrate")
public class ComposerResource {
    private final Logger logger = LoggerFactory.getLogger(ComposerResource.class);

    private final ComposerService composerService;

    public ComposerResource(ComposerService composerService) {
        this.composerService = composerService;
    }

    @CrossOrigin
    @PostMapping("/{solutionUuid}/{modelMethod}")
    @Timed
    public ResponseEntity<String> orchestrate(@PathVariable String solutionUuid,
                                              @PathVariable String modelMethod,
                                              @Valid @RequestBody String requestBody,
                                              @RequestHeader MultiValueMap<String,String> requestHeader) throws Exception {
        logger.debug("REST request to access composer");
        composerService.orchestrate(solutionUuid, modelMethod, requestBody, requestHeader);
        return composerService.orchestrate(solutionUuid, modelMethod, requestBody, requestHeader);
    }


}
