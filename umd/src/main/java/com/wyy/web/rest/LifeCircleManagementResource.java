package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Deployment;
import com.wyy.service.UmmClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * REST controller for ability life-circle-management.
 */
@RestController
@RequestMapping("/api/lcm")
public class LifeCircleManagementResource {

    private final Logger log = LoggerFactory.getLogger(DeployResource.class);
    private final UmmClient ummClient;

    public LifeCircleManagementResource(UmmClient ummClient) {
        this.ummClient = ummClient;
    }

    @PutMapping("/stop")
    @Timed
    @Secured({"ROLE_OPERATOR"})
    public ResponseEntity<Deployment> stop(@Valid @RequestBody Deployment deployment) {
        log.debug("REST request to stop running the deployment (ability)");

        Deployment result = this.ummClient.updateDeployment(deployment).getBody();

        // TODO: 实际在k8s中停止运行docker实例

        return ResponseEntity.ok()
            .body(result);
    }

}
