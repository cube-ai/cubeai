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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Deployment.
 */
@RestController
@RequestMapping("/api")
public class DeploymentResource {

    private final Logger log = LoggerFactory.getLogger(DeploymentResource.class);

    private static final String ENTITY_NAME = "deployment";

    private final DeploymentRepository deploymentRepository;

    public DeploymentResource(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    /**
     * POST  /deployments : Create a new solution.
     * @param deployment the deployment to create
     * @return status 201 Created or status 403 Forbidden
     */
    @PostMapping("/deployments")
    @Timed
    public ResponseEntity<Void> createDeployment(HttpServletRequest httpServletRequest,
                                                       @Valid @RequestBody Deployment deployment) {
        log.debug("REST request to save Deployment : {}", deployment);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals("system")) {
            // createDeployment只能由umd微服务中的异步任务调用，不能由前端用户调用
            return ResponseEntity.status(403).build();
        }

        deployment.setId(null);
        deployment.setCreatedDate(Instant.now());
        deployment.setModifiedDate(Instant.now());
        deployment.setCallCount(0L);
        deploymentRepository.save(deployment);

        return ResponseEntity.status(201).build(); // 201 Created
    }

    /**
     * PUT  /deployments : Updates an existing deployment.
     *
     * @param deployment the deployment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployment,
     * or with status 400 (Bad Request) if the deployment is not valid,
     * or with status 500 (Internal Server Error) if the deployment couldn't be updated
     */
    @PutMapping("/deployments")
    @Timed
    public ResponseEntity<Deployment> updateDeployment(HttpServletRequest httpServletRequest,
                                                       @Valid @RequestBody Deployment deployment) {
        log.debug("REST request to update Deployment : {}", deployment);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        String userRoles = JwtUtil.getUserRoles(httpServletRequest);
        if (null == userLogin || !(userLogin.equals("system") || ((userRoles != null) && userRoles.contains("ROLE_OPERATOR")))) {
            // updateDeployment只能由umd微服务中的异步任务调用，或者拥有ROLE_OPERATOR权限
            return ResponseEntity.status(403).build();
        }

        Deployment result = deploymentRepository.save(deployment);
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * GET  /deployments : get all the deployments by conditions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deployments in body
     */
    @GetMapping("/deployments")
    @Timed
    public ResponseEntity<List<Deployment>> getAllDeployments(
        HttpServletRequest httpServletRequest,
        @RequestParam(value = "isPublic", required = false) Boolean isPublic,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "solutionUuid", required = false) String solutionUuid,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {

        log.debug("REST request to get all Deployments");

        if (null == uuid && null == isPublic) {
            // 查询条件中必须存在uuid或isPublic字段（可能同时存在），否则为非法访问
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        Page<Deployment> page;

        Specification specification = new Specification<Deployment>() {

            @Override
            public Predicate toPredicate(Root<Deployment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();

                if (null != isPublic) {
                    predicates1.add(criteriaBuilder.equal(root.get("isPublic"), isPublic));
                    if (!isPublic) {
                        predicates1.add(criteriaBuilder.equal(root.get("deployer"), userLogin));
                    }
                }
                if (null != status) {
                    predicates1.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if (null != uuid) {
                    predicates1.add(criteriaBuilder.equal(root.get("uuid"), uuid));
                }
                if (null != solutionUuid) {
                    predicates1.add(criteriaBuilder.equal(root.get("solutionUuid"), solutionUuid));
                }

                if (null != filter) {
                    predicates2.add(criteriaBuilder.like(root.get("solutionName"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("solutionAuthor"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("deployer"), "%"+filter+"%"));
                }

                Predicate predicate1 = criteriaBuilder.and(predicates1.toArray(new Predicate[predicates1.size()]));
                Predicate predicate2 = criteriaBuilder.or(predicates2.toArray(new Predicate[predicates2.size()]));

                if (predicates2.size() > 0) {
                    return criteriaBuilder.and(predicate1, predicate2);
                } else {
                    return predicate1;
                }
            }
        };

        page =  this.deploymentRepository.findAll(specification, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/deployments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /deployments/all : get all the deployments for ROLE_OPERATOR. 专门为ROLE_OPERATOR设置的查询所有部署实例的查询接口
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deployments in body
     */
    @GetMapping("/deployments/all")
    @Timed
    @Secured({"ROLE_OPERATOR"})  // 专门为ROLE_OPERATOR设置的查询所有部署实例的查询接口
    public ResponseEntity<List<Deployment>> getAllDeploymentsByOperator(@RequestParam(value = "filter", required = false) String filter,
                                                                        Pageable pageable) {

        log.debug("REST request to get all Deployments");

        Page<Deployment> page;

        if (null != filter) {
            page =  this.deploymentRepository.findAllBySolutionNameLikeAndSolutionAuthorLikeAndDeployerLike(filter, filter, filter, pageable);
        } else {
            page =  this.deploymentRepository.findAll(pageable);
        }


        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/deployments/all");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /deployments/:id : get the "id" deployment.
     *
     * @param id the id of the deployment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deployment, or with status 404 (Not Found)
     */
    @GetMapping("/deployments/{id}")
    @Timed
    public ResponseEntity<Deployment> getDeployment(@PathVariable Long id) {
        log.debug("REST request to get Deployment : {}", id);
        Deployment deployment = deploymentRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(deployment));
    }

    /**
     * DELETE  /deployments/:id : delete the "id" deployment.
     *
     * @param id the id of the deployment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/deployments/{id}")
    @Timed
    @Secured({"ROLE_OPERATOR"})
    public ResponseEntity<Void> deleteDeployment(@PathVariable Long id) {
        log.debug("REST request to delete Deployment : {}", id);
        deploymentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
