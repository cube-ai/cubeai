package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Deployment;

import com.wyy.repository.DeploymentRepository;
import com.wyy.web.rest.errors.BadRequestAlertException;
import com.wyy.web.rest.util.HeaderUtil;
import com.wyy.web.rest.util.JwtUtil;
import com.wyy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Deployment.
 */
@RestController
@RequestMapping("/model")
public class AbilityResource {

    private final Logger log = LoggerFactory.getLogger(AbilityResource.class);

    private final DeploymentRepository deploymentRepository;

    public AbilityResource(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    /**
     * GET  /ability : get deployments by uuid.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deployments in body
     */
    @GetMapping("/ability")
    @Timed
    public List<Deployment> getAllDeployments(@RequestParam(value = "uuid") String uuid) {
        log.debug("REST request to get all Deployments by uuid");

        List<Deployment> deploymentList = this.deploymentRepository.findAllByUuid(uuid);

        if (!deploymentList.isEmpty()) {
            // 该接口只有在用户调用AI能力时才会被调用，所以选择在这里递加能力的接口调用次数
            Deployment deployment = deploymentList.get(0);
            deployment.setCallCount(deployment.getCallCount() + 1);
            this.deploymentRepository.save(deployment);
        }

        return deploymentList;
    }

}
