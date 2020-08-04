package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Deployment;
import com.wyy.domain.Solution;
import com.wyy.repository.DeploymentRepository;
import com.wyy.repository.SolutionRepository;
import com.wyy.repository.StarRepository;
import com.wyy.web.rest.util.HeaderUtil;
import com.wyy.web.rest.util.PaginationUtil;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing Deployment.
 */
@RestController
@RequestMapping("/api")
public class DeploymentResource {

    private final Logger log = LoggerFactory.getLogger(DeploymentResource.class);

    private static final String ENTITY_NAME = "deployment";

    private final DeploymentRepository deploymentRepository;
    private final SolutionRepository solutionRepository;
    private final StarRepository starRepository;

    public DeploymentResource(DeploymentRepository deploymentRepository,
                              SolutionRepository solutionRepository,
                              StarRepository starRepository) {
        this.deploymentRepository = deploymentRepository;
        this.solutionRepository = solutionRepository;
        this.starRepository = starRepository;
    }

    /**
     * POST  /deployments : Create a new solution.
     * @param deployment the deployment to create
     * @return status 201 Created or status 403 Forbidden
     */
    @PostMapping("/deployments")
    @Timed
    public ResponseEntity<Void> createDeployment(HttpServletRequest request,
                                                 @Valid @RequestBody Deployment deployment) {
        log.debug("REST request to save Deployment : {}", deployment);

        String userLogin = request.getRemoteUser();
        if (null == userLogin || !(userLogin.equals("internal") || userLogin.equals(deployment.getDeployer()))) {
            // createDeployment只能由umd微服务中的异步任务调用，或者拥有者自己调用
            return ResponseEntity.status(403).build();
        }

        deployment.setId(null);
        deployment.setStarCount(0L);
        deployment.setCallCount(0L);
        deployment.setDisplayOrder(0L);
        deployment.setCreatedDate(Instant.now());
        deployment.setModifiedDate(Instant.now());
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
    public ResponseEntity<Void> updateDeployment(HttpServletRequest request,
                                                       @Valid @RequestBody Deployment deployment) {
        log.debug("REST request to update Deployment : {}", deployment);

        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_OPERATOR");
        Deployment old = deploymentRepository.findOne(deployment.getId());
        if (null == userLogin || !userLogin.equals("internal")) {
            // updateDeployment只能由umd微服务中的异步任务在进行生命周期管理过程中调用
            return ResponseEntity.status(403).build();
        }

        deploymentRepository.save(deployment);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /deployments/solutioninfo : 更新deployment对象中的solution相关字段
     * @param jsonObject the JSONObject with ability id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployment
     */
    @PutMapping("/deployments/solutioninfo")
    @Timed
    public ResponseEntity<Void> updateDeploymentSolutionInfo(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Deployment subjects: {}", jsonObject);

        Deployment deployment = deploymentRepository.findOne(jsonObject.getLong("id"));
        List<Solution> solutionList = solutionRepository.findAllByUuid(deployment.getSolutionUuid());
        if (!solutionList.isEmpty()) {
            deployment.setSolutionName(solutionList.get(0).getName());
            deployment.setPictureUrl(solutionList.get(0).getPictureUrl());
            deploymentRepository.save(deployment);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /deployments/admininfo : 更新deployment对象中的管理相关字段
     * @param jsonObject the JSONObject with subjects to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployment
     */
    @PutMapping("/deployments/admininfo")
    @Timed
    @Secured({"ROLE_OPERATOR"})  // admin信息相关字段只能由能力开放平台管理员更新
    public ResponseEntity<Void> updateDeploymentAdminInfo(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Deployment subjects: {}", jsonObject);

        Deployment deployment = deploymentRepository.findOne(jsonObject.getLong("id"));
        deployment.setSubject1(jsonObject.getString("subject1"));
        deployment.setSubject2(jsonObject.getString("subject2"));
        deployment.setSubject3(jsonObject.getString("subject3"));
        deployment.setDisplayOrder(jsonObject.getLong("displayOrder"));
        deployment.setModifiedDate(Instant.now());
        deploymentRepository.save(deployment);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /deployments/demourl : 更新deployment对象中的demoUrl字段
     * @param jsonObject the JSONObject with subjects to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployment
     */
    @PutMapping("/deployments/demourl")
    @Timed
    @Secured({"ROLE_OPERATOR"})  // demoUrl字段只能由能力开放平台管理员更新
    public ResponseEntity<Void> updateDeploymentDemoUrl(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Deployment subjects: {}", jsonObject);

        Deployment deployment = deploymentRepository.findOne(jsonObject.getLong("id"));
        deployment.setDemoUrl(jsonObject.getString("demoUrl"));
        deployment.setModifiedDate(Instant.now());
        Deployment result = deploymentRepository.save(deployment);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT /deployments/star-count : Updates an existing deployment's starCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated deployment
     */
    @PutMapping("/deployments/star-count")
    @Timed
    public ResponseEntity<Void> updateDeploymentStarCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Deployment star count : {}", jsonObject);

        Deployment deployment = deploymentRepository.findOne(jsonObject.getLong("id"));
        deployment.setStarCount(starRepository.countAllByTargetUuid(deployment.getUuid()));
        Deployment result = deploymentRepository.save(deployment);

        return ResponseEntity.ok().build();
    }

    /**
     * GET  /deployments : get all the deployments by conditions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deployments in body
     */
    @GetMapping("/deployments")
    @Timed
    public ResponseEntity<List<Deployment>> getAllDeployments(
        HttpServletRequest request,
        @RequestParam(value = "isPublic", required = false) Boolean isPublic,
        @RequestParam(value = "deployer", required = false) String deployer,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "solutionUuid", required = false) String solutionUuid,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {

        log.debug("REST request to get all Deployments");

        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_OPERATOR");

        Page<Deployment> page;

        Specification specification = new Specification<Deployment>() {
            @Override
            public Predicate toPredicate(Root<Deployment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();

                if (null != isPublic) {
                    predicates1.add(criteriaBuilder.equal(root.get("isPublic"), isPublic));
                    if (null != deployer) {
                        predicates1.add(criteriaBuilder.equal(root.get("deployer"), deployer));
                    } else if (!isPublic && !hasRole) {
                        predicates1.add(criteriaBuilder.equal(root.get("deployer"), userLogin));
                    }
                } else if (null != deployer) {
                    predicates1.add(criteriaBuilder.equal(root.get("deployer"), deployer));
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
                    predicates2.add(criteriaBuilder.like(root.get("status"), "%"+filter+"%"));
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
     * DELETE  /deployments/:id : delete the "id" deployment. deployment正常不应该被删除，所以暂不使用
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
