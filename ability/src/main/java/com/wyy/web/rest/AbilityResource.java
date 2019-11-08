package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import com.wyy.domain.Deployment;
import com.wyy.service.UmmClient;
import com.wyy.service.AbilityService;
import java.util.*;
import javax.validation.Valid;


/**
 * REST controller for provide AI open ability.
 */
@RestController
@RequestMapping("/model")
public class AbilityResource {

    private final Logger log = LoggerFactory.getLogger(AbilityResource.class);

    private final UmmClient ummClient;
    private final AbilityService abilityService;

    @Value("${kubernetes.ability.internalIP}")
    private String internalIP = "";

    public AbilityResource(UmmClient ummClient,
                           AbilityService abilityService) {
        this.ummClient = ummClient;
        this.abilityService = abilityService;
    }

    @CrossOrigin
    @PostMapping("/{deploymentUuid}/{modelMethod}")
    @Timed
    public ResponseEntity<String> openAbility(@PathVariable String deploymentUuid,
                                              @PathVariable String modelMethod,
                                              @Valid @RequestBody String requestBody,
                                              @RequestHeader MultiValueMap<String,String> requestHeader) {
        log.debug("REST request to access AI open ability");

        List<Deployment> deploymentList = this.ummClient.getDeployment(deploymentUuid);
        if(deploymentList.isEmpty()) {
            return ResponseEntity.status(404).body("Cannot find deployment: " + deploymentUuid);
        }
        List<Solution> solutions = ummClient.getSolutionsByUuid(deploymentList.get(0).getSolutionUuid());
        if (!solutions.isEmpty()) {
            if (solutions.get(0).getToolkitType().equals("模型组合")) {
                return abilityService.callComposer(solutions.get(0).getUuid(), modelMethod, requestBody, requestHeader);
            }
        }
        Integer k8sPort = deploymentList.get(0).getk8sPort();
        if(k8sPort == null) {
            return ResponseEntity.status(404).body("Deployment: " + deploymentUuid + " is not running");
        }
        String url = "http://" + internalIP + ":" + k8sPort + "/model/methods/" + modelMethod;
        return abilityService.apiGateway(url, requestBody, requestHeader);
    }

}
