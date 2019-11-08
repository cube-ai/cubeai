package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Description;
import com.wyy.domain.PublishRequest;
import com.wyy.domain.Solution;
import com.wyy.repository.*;
import com.wyy.service.MessageService;
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
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * REST controller for managing Solution.
 */
@RestController
@RequestMapping("/api")
public class SolutionResource {

    private final Logger log = LoggerFactory.getLogger(SolutionResource.class);
    private final SolutionRepository solutionRepository;
    private final DescriptionRepository descriptionRepository;
    private final SolutionRatingRepository solutionRatingRepository;
    private final CommentRepository commentRepository;
    private final PublishRequestRepository publishRequestRepository;
    private final MessageService messageService;

    public SolutionResource(SolutionRepository solutionRepository,
                            DescriptionRepository descriptionRepository,
                            SolutionRatingRepository solutionRatingRepository,
                            CommentRepository commentRepository,
                            PublishRequestRepository publishRequestRepository,
                            MessageService messageService) {
        this.solutionRepository = solutionRepository;
        this.descriptionRepository = descriptionRepository;
        this.solutionRatingRepository = solutionRatingRepository;
        this.commentRepository = commentRepository;
        this.publishRequestRepository = publishRequestRepository;
        this.messageService = messageService;
    }

    /**
     * POST  /solutions : Create a new solution.
     * @param solution the solution to create
     * @return status 201 Created or status 403 Forbidden
     */
    @PostMapping("/solutions")
    @Timed
    public ResponseEntity<Void> createSolution(HttpServletRequest httpServletRequest,
                                               @Valid @RequestBody Solution solution) {
        log.debug("REST request to save Solution : {}", solution);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || userLogin.equals("system"))) {
            // createSolution只能由umu微服务中的异步任务OnBoardingServie调用，或者作者自己调用
            return ResponseEntity.status(403).build();
        }

        solution.setId(null);
        solution.setActive(true);
        solution.setPublishStatus("下架");
        solution.setPublishRequest("无申请");
        solution.setViewCount(0L);
        solution.setDownloadCount(0L);
        solution.setCommentCount(0L);
        solution.setRatingCount(0L);
        solution.setRatingAverage(0.0);
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

        return ResponseEntity.status(201).build(); // 201 Created
    }

    /**
     * PUT  /solutions/baseinfo : 更新Solution对象中的基础可变字段
     * @param jsonObject JSONObject with base info fields to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 401 Unauthorized
     */
    @PutMapping("/solutions/baseinfo")
    @Timed
    public ResponseEntity<Solution> updateBaseinfo(HttpServletRequest httpServletRequest,
                                                   @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution base info: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            // solution中的下属基础信息只能由作者自己修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solution.setCompany(jsonObject.getString("company"));
        solution.setCoAuthors(jsonObject.getString("coAuthors"));
        solution.setVersion(jsonObject.getString("version"));
        solution.setSummary(jsonObject.getString("summary"));
        solution.setTag1(jsonObject.getString("tag1"));
        solution.setTag2(jsonObject.getString("tag2"));
        solution.setTag3(jsonObject.getString("tag3"));
        solution.setModelType(jsonObject.getString("modelType"));
        solution.setToolkitType(jsonObject.getString("toolkitType"));

        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/picture-url : 更新Solution的 pictureUrl
     * @param jsonObject the JSONObject with pictureUrl to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 401
     */
    @PutMapping("/solutions/picture-url")
    @Timed
    public ResponseEntity<Solution> updatePictureUrl(HttpServletRequest httpServletRequest,
                                                           @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution pictureUrl: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            // solution中的pictureUrl字段只能由作者自己修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solution.setPictureUrl(jsonObject.getString("pictureUrl"));
        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/active : 更新Solution的 active
     * @param jsonObject the JSONObject with active to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 403 Forbidden
     */
    @PutMapping("/solutions/active")
    @Timed
    public ResponseEntity<Solution> updateActive(HttpServletRequest httpServletRequest,
                                                             @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution active: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            // solution中的active字段只能由作者自己修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solution.setActive(jsonObject.getBoolean("active"));
        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/publish-request : 申请模型上架/下架
     * @param jsonObject the JSONObject with publishRequest to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 401
     */
    @PutMapping("/solutions/publish-request")
    @Timed
    public ResponseEntity<Solution> requestPublish(HttpServletRequest httpServletRequest,
                                                         @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution publishRequest: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            // 模型上架/下架申请只能由作者自己提出
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setSolutionUuid(solution.getUuid());
        publishRequest.setRequestUserLogin(userLogin);
        publishRequest.setSolutionName(solution.getName());
        publishRequest.setRequestType(jsonObject.getString("requestType"));
        publishRequest.setRequestReason(jsonObject.getString("requestReason"));
        publishRequest.setReviewed(false);
        publishRequest.setRequestDate(Instant.now());
        publishRequestRepository.save(publishRequest);

        solution.setPublishRequest(jsonObject.getString("requestType"));
        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/publish-status : 批准模型上架/下架申请
     * @param jsonObject the JSONObject with publishStatus and publishRequest to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/publish-approve")
    @Timed
    @Secured({"ROLE_MANAGER"})  // 模型上架/下架申请只能由平台管理员进行批准
    public ResponseEntity<Solution> approvePublish(HttpServletRequest httpServletRequest,
                                                   @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution publishStatus: {}", jsonObject);
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("solutionId"));
        if (jsonObject.getBoolean("toPublish")) {
            solution.setPublishStatus(jsonObject.getBoolean("approved") ? "上架" : "下架");
        } else {
            solution.setPublishStatus(jsonObject.getBoolean("approved") ? "下架" : "上架");
        }
        solution.setPublishRequest("无申请");
        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        PublishRequest publishRequest = publishRequestRepository.findOne(jsonObject.getLong("publishRequestId"));
        publishRequest.setReviewUserLogin(userLogin);
        publishRequest.setReviewed(true);
        publishRequest.setReviewResult(jsonObject.getBoolean("approved") ? "批准" : "拒绝");
        publishRequest.setReviewComment(jsonObject.getString("reviewComment"));
        publishRequest.setReviewDate(Instant.now());
        publishRequestRepository.save(publishRequest);

        String title = "模型" + publishRequest.getSolutionName() + publishRequest.getRequestType() + "审批被" + publishRequest.getReviewResult();
        String content = "你的模型" + publishRequest.getSolutionName() + publishRequest.getRequestType() + "审批被" + publishRequest.getReviewResult()
            + "。\n\n请点击下方[目标页面]按钮进入模型页面进行后续处理...";
        String url = "/ucumos/solution/" + publishRequest.getSolutionUuid() + "/edit";
        messageService.sendMessage(publishRequest.getRequestUserLogin(), title, content, url, false);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/subjects : 更新Solution对象中的subjectdeng诸字段
     * @param jsonObject the JSONObject with subjects to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/subjects")
    @Timed
    @Secured({"ROLE_MANAGER"})  // subject字段只能由平台管理员更新
    public ResponseEntity<Solution> updateSolutionSubjects(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution subjects: {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setSubject1(jsonObject.getString("subject1"));
        solution.setSubject2(jsonObject.getString("subject2"));
        solution.setSubject3(jsonObject.getString("subject3"));
        solution.setDisplayOrder(jsonObject.getLong("displayOrder"));

        solution.setModifiedDate(Instant.now());
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT /solutions/rating-stats : Updates an existing solution's ratingCount and ratingAverage.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/rating-stats")
    @Timed
    public ResponseEntity<Solution> updateSolutionRatingStats(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution rating stats : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));

        Long ratingCount = solutionRatingRepository.countAllBySolutionUuidAndRatingScoreGreaterThan(solution.getUuid(), 0);
        Integer sumRatingScore = solutionRatingRepository.sumRatingScore(solution.getUuid());
        solution.setRatingCount(ratingCount);
        solution.setRatingAverage(sumRatingScore * 1.0 / ratingCount);

        solution.setModifiedDate(Instant.now()); // 每次更新时修改modifiedDate
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT /solutions/comment-count : Updates an existing solution's commentCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/comment-count")
    @Timed
    public ResponseEntity<Solution> updateSolutionCommentCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution comment count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));

        Long commentCount = commentRepository.countAllBySolutionUuid(solution.getUuid());
        solution.setCommentCount(commentCount);

        solution.setModifiedDate(Instant.now()); // 每次更新时修改modifiedDate
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/view-count : Updates an existing solution's viewCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/view-count")
    @Timed
    public ResponseEntity<Solution> updateSolutionViewCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution view count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setViewCount(solution.getViewCount() + 1);
        solution.setModifiedDate(Instant.now()); // 每次更新时修改modifiedDate
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * PUT  /solutions/download-count : Updates an existing solution's downloadCount.
     * @param jsonObject the JSONObject with soluton id to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 400 (Bad Request)
     */
    @PutMapping("/solutions/download-count")
    @Timed
    public ResponseEntity<Solution> updateSolutionDownloadCount(@Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Solution download count : {}", jsonObject);

        Solution solution = solutionRepository.findOne(jsonObject.getLong("id"));
        solution.setDownloadCount(solution.getDownloadCount() + 1);
        solution.setLastDownload(Instant.now());
        solution.setModifiedDate(Instant.now()); // 每次更新时修改modifiedDate
        Solution result = solutionRepository.save(solution);

        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /solutions: get all the solutions by different parameters
     * @return the ResponseEntity with status 200 (OK) and the list of solutions in body
     */
    @GetMapping("/solutions")
    @Timed
    public ResponseEntity<List<Solution>> getSolutions(
        HttpServletRequest httpServletRequest,
        @RequestParam(value = "active", required = false) Boolean active,
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "authorLogin", required = false) String authorLogin,
        @RequestParam(value = "company", required = false) String company,
        @RequestParam(value = "modelType", required = false) String modelType,
        @RequestParam(value = "toolkitType", required = false) String toolkitType,
        @RequestParam(value = "publishStatus", required = false) String publishStatus,
        @RequestParam(value = "publishRequest", required = false) String publishRequest,
        @RequestParam(value = "subject1", required = false) String subject1,
        @RequestParam(value = "subject2", required = false) String subject2,
        @RequestParam(value = "subject3", required = false) String subject3,
        @RequestParam(value = "tag", required = false) String tag,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {

        log.debug("REST request to get all Solutions");

        if (null == uuid && null == publishStatus) {
            // 查询条件中必须存在uuid或publishStatus字段（不可能同时存在），否则为非法访问
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null != publishStatus && publishStatus.equals("下架") && null != authorLogin && !authorLogin.equals(userLogin)) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        Page<Solution> page;

        Specification specification = new Specification<Solution>() {

            @Override
            public Predicate toPredicate(Root<Solution> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();
                List<Predicate> predicates3 = new ArrayList<>();

                if(null != active){
                    predicates1.add(criteriaBuilder.equal(root.get("active"), active));
                }
                if(null != uuid){
                    predicates1.add(criteriaBuilder.equal(root.get("uuid"), uuid));
                }
                if (null != name) {
                    predicates1.add(criteriaBuilder.equal(root.get("name"), name));
                }
                if (null != authorLogin) {
                    predicates1.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("authorLogin"), authorLogin),
                        criteriaBuilder.equal(root.get("authorName"), authorLogin)));
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
                if (null != publishStatus) {
                    predicates1.add(criteriaBuilder.equal(root.get("publishStatus"), publishStatus));
                }
                if (null != publishRequest) {
                    predicates1.add(criteriaBuilder.equal(root.get("publishRequest"), publishRequest));
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
                    // predicates3.add(criteriaBuilder.like(root.get("coAuthors"), "%"+filter+"%"));
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
     * GET  /solutions/:id : get the "id" solution.
     * @param id the id of the solution to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the solution, or with status 404 (Not Found)
     */
    @GetMapping("/solutions/{id}")
    @Timed
    public ResponseEntity<Solution> getSolution(@PathVariable Long id) {
        log.debug("REST request to get Solution : {}", id);
        Solution solution = solutionRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(solution));
    }

    /**
     * GET  /solutions/:id/picture-url : get the picture-url of "id" solution.
     * @param id the id of the solution to retrieve
     * @return the ResponseEntity with status 200 (OK) and with the solution pictureUrl, or with status 404 (Not Found)
     */
    @GetMapping("/solutions/{id}/picture-url")
    @Timed
    public ResponseEntity<JSONObject> getPictureUrl(@PathVariable Long id) {
        log.debug("REST request to get Solution : {}", id);

        Solution solution = solutionRepository.findOne(id);
        if (null == solution) {
            return ResponseEntity.notFound().build();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pictureUrl", solution.getPictureUrl());
        return ResponseEntity.ok().body(jsonObject);
    }

    /**
     * DELETE  /solutions/:id : delete the "id" solution.
     * @param id the id of the solution to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/solutions/{id}")
    @Timed
    public ResponseEntity<Void> deleteSolution(HttpServletRequest httpServletRequest,
                                               @PathVariable Long id) {
        log.debug("REST request to delete Solution : {}", id);

        Solution solution = solutionRepository.findOne(id);
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || userLogin.equals("system"))) {
            // solution只能由作者自己删除，或者由umu微服务中的onboardService异步服务删除
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solutionRepository.delete(id);
        return ResponseEntity.ok().build();
    }

}
