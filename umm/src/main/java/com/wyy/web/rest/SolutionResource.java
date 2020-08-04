package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.*;
import com.wyy.repository.*;
import com.wyy.service.CreditService;
import com.wyy.service.NexusArtifactClient;
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
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing Solution.
 */
@RestController
@RequestMapping("/api")
public class SolutionResource {

    private final Logger log = LoggerFactory.getLogger(SolutionResource.class);
    private final SolutionRepository solutionRepository;
    private final DescriptionRepository descriptionRepository;
    private final StarRepository starRepository;
    private final CommentRepository commentRepository;
    private final NexusArtifactClient nexusArtifactClient;
    private final ArtifactRepository artifactRepository;
    private final DocumentRepository documentRepository;
    private final CreditService creditService;

    public SolutionResource(SolutionRepository solutionRepository,
                            DescriptionRepository descriptionRepository,
                            StarRepository starRepository,
                            CommentRepository commentRepository,
                            NexusArtifactClient nexusArtifactClient,
                            ArtifactRepository artifactRepository,
                            DocumentRepository documentRepository,
                            CreditService creditService
                            ) {
        this.solutionRepository = solutionRepository;
        this.descriptionRepository = descriptionRepository;
        this.starRepository = starRepository;
        this.commentRepository = commentRepository;
        this.documentRepository = documentRepository;
        this.artifactRepository = artifactRepository;
        this.nexusArtifactClient = nexusArtifactClient;
        this.creditService = creditService;
    }

    /**
     * POST  /solutions : Create a new solution.
     * @param solution the solution to create
     * @return status 201 Created or status 403 Forbidden
     */
    @PostMapping("/solutions")
    @Timed
    public ResponseEntity<Void> createSolution(HttpServletRequest request,
                                               @Valid @RequestBody Solution solution) {
        log.debug("REST request to save Solution : {}", solution);

        String userLogin = request.getRemoteUser();
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || userLogin.equals("internal"))) {
            // createSolution只能由umu微服务中的异步任务OnBoardingServie调用，或者作者自己调用
            return ResponseEntity.status(403).build();
        }

        solution.setId(null);
        solution.setActive(true); // 重定义为表示：公开或私有
        solution.setStarCount(0L);
        solution.setViewCount(0L);
        solution.setDownloadCount(0L);
        solution.setCommentCount(0L);
        solution.setDisplayOrder(0L);
        solution.setCreatedDate(Instant.now());
        solution.setModifiedDate(Instant.now());
        this.solutionRepository.save(solution);

        // 为solution创建描述，其uuid设为与solution的uuid一致。 ----huolongshe
        // description表只能在这里创建
        Description description = new Description();
        description.setSolutionUuid(solution.getUuid());
        description.setAuthorLogin(solution.getAuthorLogin());
        description.setContent("<p>无内容</p>");
        this.descriptionRepository.save(description);

        Credit credit = creditService.findCredit(solution.getAuthorLogin());
        creditService.updateCredit(credit, 3L, "创建新模型<" + solution.getName() + ">");

        return ResponseEntity.status(201).build(); // 201 Created
    }

    /**
     * PUT  /solutions/baseinfo : 更新Solution对象中的基础信息相关字段
     * @param jsonObject JSONObject with base info fields to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 401 Unauthorized
     */
    @PutMapping("/solutions/baseinfo")
    @Timed
    public ResponseEntity<Void> updateBaseinfo(HttpServletRequest request,
                                                   @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution base info: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || hasRole)) {
            // solution中的基础信息字段只能由作者自己或管理员修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solution.setName(jsonObject.getString("name"));
        solution.setCompany(jsonObject.getString("company"));
        solution.setVersion(jsonObject.getString("version"));
        solution.setSummary(jsonObject.getString("summary"));
        solution.setTag1(jsonObject.getString("tag1"));
        solution.setTag2(jsonObject.getString("tag2"));
        solution.setTag3(jsonObject.getString("tag3"));
        solution.setModelType(jsonObject.getString("modelType"));
        solution.setToolkitType(jsonObject.getString("toolkitType"));

        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /solutions/name : 更新Solution的 name
     * @param jsonObject the JSONObject with name to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 403
     */
    @PutMapping("/solutions/name")
    @Timed
    public ResponseEntity<Void> updateName(HttpServletRequest request,
                                               @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution Name: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = request.getRemoteUser();
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            // solution中的name字段只能由作者自己修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solution.setName(jsonObject.getString("name"));
        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /solutions/picture-url : 更新Solution的 pictureUrl
     * @param jsonObject the JSONObject with pictureUrl to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 403
     */
    @PutMapping("/solutions/picture-url")
    @Timed
    public ResponseEntity<Void> updatePictureUrl(HttpServletRequest request,
                                                     @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution pictureUrl: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || hasRole)) {
            // solution中的pictureUrl字段只能由作者自己或管理员修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solution.setPictureUrl(jsonObject.getString("pictureUrl"));
        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /solutions/active : 更新Solution的 active
     * @param jsonObject the JSONObject with active to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 403 Forbidden
     */
    @PutMapping("/solutions/active")
    @Timed
    public ResponseEntity<Void> updateActive(HttpServletRequest request,
                                                 @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution active: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || hasRole)) {
            // solution中的active字段只能由作者自己或管理员修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        if (solution.isActive() && !jsonObject.getBoolean("active")) {
            Credit credit = creditService.findCredit(userLogin);
            if (credit.getCredit() < 20) {
                return ResponseEntity.status(400).build(); // bad request
            } else {
                solution.setActive(jsonObject.getBoolean("active"));
                solution.setModifiedDate(Instant.now());
                Solution result = solutionRepository.save(solution);
                creditService.updateCredit(credit, -20L, "将AI模型<" + solution.getName() + ">设为私有");

                return ResponseEntity.ok().build();
            }
        } else {
            solution.setActive(jsonObject.getBoolean("active"));
            solution.setModifiedDate(Instant.now());
            Solution result = solutionRepository.save(solution);

            return ResponseEntity.ok().build();
        }
    }

    /**
     * PUT  /solutions/admininfo : 更新Solution对象中的管理相关字段
     * @param jsonObject the JSONObject with subjects to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/admininfo")
    @Timed
    @Secured({"ROLE_MANAGER"})  // subject字段只能由平台管理员更新
    public ResponseEntity<Void> updateSolutionSubjects(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution subjects: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setSubject1(jsonObject.getString("subject1"));
        solution.setSubject2(jsonObject.getString("subject2"));
        solution.setSubject3(jsonObject.getString("subject3"));
        solution.setDisplayOrder(jsonObject.getLong("displayOrder"));

        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT /solutions/star-count : Updates an existing solution's starCount.
     * @param jsonObject the JSONObject with solution id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution
     */
    @PutMapping("/solutions/star-count")
    @Timed
    public ResponseEntity<Void> updateSolutionStarCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution star count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setStarCount(starRepository.countAllByTargetUuid(solution.getUuid()));
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT /solutions/comment-count : Updates an existing solution's commentCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution
     */
    @PutMapping("/solutions/comment-count")
    @Timed
    public ResponseEntity<Void> updateSolutionCommentCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution comment count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));

        Long commentCount = commentRepository.countAllBySolutionUuid(solution.getUuid());
        solution.setCommentCount(commentCount);

        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /solutions/view-count : Updates an existing solution's viewCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution
     */
    @PutMapping("/solutions/view-count")
    @Timed
    public ResponseEntity<Void> updateSolutionViewCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution view count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setViewCount(solution.getViewCount() + 1);
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * PUT  /solutions/download-count : Updates an existing solution's downloadCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution
     */
    @PutMapping("/solutions/download-count")
    @Timed
    public ResponseEntity<Void> updateSolutionDownloadCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution download count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setDownloadCount(solution.getDownloadCount() + 1);
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().build();
    }

    /**
     * GET  /solutions: get all the solutions by different parameters
     * @return the ResponseEntity with status 200 (OK) and the list of solutions in body
     */
    @GetMapping("/solutions")
    @Timed
    public ResponseEntity<List<Solution>> getSolutions(
        HttpServletRequest request,
        @RequestParam(value = "active", required = false) Boolean active,
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "authorLogin", required = false) String authorLogin,
        @RequestParam(value = "company", required = false) String company,
        @RequestParam(value = "modelType", required = false) String modelType,
        @RequestParam(value = "toolkitType", required = false) String toolkitType,
        @RequestParam(value = "subject1", required = false) String subject1,
        @RequestParam(value = "subject2", required = false) String subject2,
        @RequestParam(value = "subject3", required = false) String subject3,
        @RequestParam(value = "tag", required = false) String tag,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {

        log.debug("REST request to get all Solutions");

        String userLogin = request.getRemoteUser();

        // 将active的含义重定义为：公开或私有
        if ((null != active) && !active && null == userLogin) {
            // 非登录用户不能查询私有模型
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        Page<Solution> page;
        Specification specification = new Specification<Solution>() {
            @Override
            public Predicate toPredicate(Root<Solution> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();
                List<Predicate> predicates3 = new ArrayList<>();

                if (null != active) {
                    predicates1.add(criteriaBuilder.equal(root.get("active"), active));
                    if (!active) {
                        // 用户只能查询自己的私有模型
                        predicates1.add(criteriaBuilder.equal(root.get("authorLogin"), userLogin));
                    }
                }
                if (null != authorLogin && (null == active || active)) {
                    predicates1.add(criteriaBuilder.equal(root.get("authorLogin"), authorLogin));
                }
                if (null != uuid) {
                    predicates1.add(criteriaBuilder.equal(root.get("uuid"), uuid));
                }
                if (null != name) {
                    predicates1.add(criteriaBuilder.equal(root.get("name"), name));
                }
                if (null != company) {
                    predicates1.add(criteriaBuilder.equal(root.get("company"), company));
                }
                if (null != modelType) {
                    predicates1.add(criteriaBuilder.equal(root.get("modelType"), modelType));
                }
                if (null != toolkitType) {
                    predicates1.add(criteriaBuilder.equal(root.get("toolkitType"), toolkitType));
                }
                if (null != subject1) {
                    predicates1.add(criteriaBuilder.equal(root.get("subject1"), subject1));
                }
                if (null != subject2) {
                    predicates1.add(criteriaBuilder.equal(root.get("subject2"), subject2));
                }
                if (null != subject3) {
                    predicates1.add(criteriaBuilder.equal(root.get("subject3"), subject3));
                }

                if (null != tag) {
                    predicates2.add(criteriaBuilder.like(root.get("tag1"), "%"+tag+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("tag2"), "%"+tag+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("tag3"), "%"+tag+"%"));
                }

                if (null != filter) {
                    predicates3.add(criteriaBuilder.like(root.get("name"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("authorLogin"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("authorName"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("modelType"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("toolkitType"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("summary"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("tag1"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("tag2"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("tag3"), "%"+filter+"%"));
                    predicates3.add(criteriaBuilder.like(root.get("company"), "%"+filter+"%"));
                }

                Predicate predicate1 = criteriaBuilder.and(predicates1.toArray(new Predicate[predicates1.size()]));
                Predicate predicate2 = criteriaBuilder.or(predicates2.toArray(new Predicate[predicates2.size()]));
                Predicate predicate3 = criteriaBuilder.or(predicates3.toArray(new Predicate[predicates3.size()]));

                if (predicates2.size() > 0 && predicates3.size() > 0) {
                    return criteriaBuilder.and(predicate1, predicate2, predicate3);
                } else if (predicates2.size() > 0) {
                    return criteriaBuilder.and(predicate1, predicate2);
                } else if (predicates3.size() > 0) {
                    return criteriaBuilder.and(predicate1, predicate3);
                } else {
                    return predicate1;
                }
            }
        };

        page =  this.solutionRepository.findAll(specification, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/solutions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /solutions/:id : delete the "id" solution.
     * @param id the id of the solution to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/solutions/{id}")
    @Timed
    public ResponseEntity<Void> deleteSolution(HttpServletRequest request,
                                               @PathVariable Long id) {
        log.debug("REST request to delete Solution : {}", id);

        Solution solution = solutionRepository.findOne(id);
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || userLogin.equals("internal") || hasRole)) {
            // solution只能由作者自己或者管理员删除，或者由umu微服务中的onboardService异步服务删除
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        List<Document> documentList = documentRepository.findAllBySolutionUuid(solution.getUuid());
        for (Document document: documentList) {
            this.nexusArtifactClient.deleteArtifact(document.getUrl());
            this.documentRepository.delete(document.getId());
        }

        List<Artifact> artifactList = artifactRepository.findAllBySolutionUuid(solution.getUuid());
        for (Artifact artifact: artifactList) {
            if (artifact.getType().equals("DOCKER镜像")) {
                this.nexusArtifactClient.deleteDockerImage(artifact.getUrl());
            } else {
                this.nexusArtifactClient.deleteArtifact(artifact.getUrl());
            }
            this.artifactRepository.delete(artifact.getId());
        }

        List<Star> starList = starRepository.findAllByTargetUuid(solution.getUuid(), null).getContent();
        for (Star star: starList) {
            this.starRepository.delete(star.getId());
        }

        solutionRepository.delete(id);
        return ResponseEntity.ok().build();
    }

}
