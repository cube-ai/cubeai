package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Application;

import com.wyy.repository.ApplicationRepository;
import com.wyy.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wyy.web.rest.util.PaginationUtil;
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
import java.util.Optional;

/**
 * REST controller for managing Application.
 */
@RestController
@RequestMapping("/api")
public class ApplicationResource {

    private final Logger log = LoggerFactory.getLogger(ApplicationResource.class);

    private static final String ENTITY_NAME = "application";

    private final ApplicationRepository applicationRepository;

    public ApplicationResource(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * POST  /applications : Create a new application.
     *
     * @param application the application to create
     * @return status 201 Created or status 403 Forbidden
     */
    @PostMapping("/applications")
    @Timed
    @Secured({"ROLE_APPLICATION"})
    public ResponseEntity<Application> createApplication(HttpServletRequest request,
                                                         @Valid @RequestBody Application application) {
        log.debug("REST request to save Application : {}", application);

        String login = request.getRemoteUser();
        application.setDisplayOrder(0L);
        application.setCreatedBy(login);
        application.setModifiedBy(login);
        application.setCreatedDate(Instant.now());
        application.setModifiedDate(Instant.now());

        Application result = applicationRepository.save(application);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * PUT  /applications : Updates an existing application.
     *
     * @param application the application to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated application
     */
    @PutMapping("/applications")
    @Timed
    @Secured({"ROLE_APPLICATION"})
    public ResponseEntity<Application> updateApplication(HttpServletRequest request,
                                                         @Valid @RequestBody Application application) {
        log.debug("REST request to update Application : {}", application);

        application.setModifiedBy(request.getRemoteUser());
        application.setModifiedDate(Instant.now());
        Application result = applicationRepository.save(application);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, application.getId().toString()))
            .body(result);
    }

    /**
     * GET  /applications : get all applications without picture
     * @return the ResponseEntity with status 200 (OK) and the list of applications in body
     */
    @GetMapping("/applications")
    @Timed
    public ResponseEntity<List<Application>> getAllapplications(
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "owner", required = false) String owner,
        @RequestParam(value = "subject1", required = false) String subject1,
        @RequestParam(value = "subject2", required = false) String subject2,
        @RequestParam(value = "subject3", required = false) String subject3,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {
        log.debug("REST request to get all applications");

        Page<Application> page;

        Specification specification = new Specification<Application>() {
            @Override
            public Predicate toPredicate(Root<Application> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();

                if(null != uuid){
                    predicates1.add(criteriaBuilder.equal(root.get("uuid"), uuid));
                }
                if(null != name){
                    predicates1.add(criteriaBuilder.equal(root.get("name"), name));
                }
                if(null != owner){
                    predicates1.add(criteriaBuilder.equal(root.get("owner"), owner));
                }
                if(null != subject1){
                    predicates1.add(criteriaBuilder.equal(root.get("subject1"), subject1));
                }
                if(null != subject2){
                    predicates1.add(criteriaBuilder.equal(root.get("subject2"), subject2));
                }
                if(null != subject3){
                    predicates1.add(criteriaBuilder.equal(root.get("subject3"), subject3));
                }

                if (null != filter) {
                    predicates2.add(criteriaBuilder.like(root.get("name"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("owner"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject1"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject2"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject3"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("summary"), "%"+filter+"%"));
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

        page =  this.applicationRepository.findAll(specification, pageable);
        List<Application> applicationList = page.getContent();
        for (Application application: applicationList) {
            application.setPictureUrl(null);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/applications");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /applications : get applications with picture
     * @return the ResponseEntity with status 200 (OK) and the list of applications in body
     */
    @GetMapping("/applicationsp")
    @Timed
    public ResponseEntity<List<Application>> getAllapplicationsWithPictures(
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "owner", required = false) String owner,
        @RequestParam(value = "subject1", required = false) String subject1,
        @RequestParam(value = "subject2", required = false) String subject2,
        @RequestParam(value = "subject3", required = false) String subject3,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {
        log.debug("REST request to get all applications");

        Page<Application> page;

        Specification specification = new Specification<Application>() {
            @Override
            public Predicate toPredicate(Root<Application> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();

                if(null != uuid){
                    predicates1.add(criteriaBuilder.equal(root.get("uuid"), uuid));
                }
                if(null != name){
                    predicates1.add(criteriaBuilder.equal(root.get("name"), name));
                }
                if(null != name){
                    predicates1.add(criteriaBuilder.equal(root.get("owner"), owner));
                }
                if(null != subject1){
                    predicates1.add(criteriaBuilder.equal(root.get("subject1"), subject1));
                }
                if(null != subject2){
                    predicates1.add(criteriaBuilder.equal(root.get("subject2"), subject2));
                }
                if(null != subject3){
                    predicates1.add(criteriaBuilder.equal(root.get("subject3"), subject3));
                }

                if (null != filter) {
                    predicates2.add(criteriaBuilder.like(root.get("name"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("owner"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject1"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject2"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject3"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("summary"), "%"+filter+"%"));
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

        page =  this.applicationRepository.findAll(specification, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/applications");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /applications/:id : get the "id" application.
     *
     * @param id the id of the application to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the application, or with status 404 (Not Found)
     */
    @GetMapping("/applications/{id}")
    @Timed
    public ResponseEntity<Application> getApplication(@PathVariable Long id) {
        log.debug("REST request to get Application : {}", id);
        Application application = applicationRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(application));
    }

    /**
     * DELETE  /applications/:id : delete the "id" application.
     *
     * @param id the id of the application to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/applications/{id}")
    @Timed
    @Secured({"ROLE_APPLICATION"})
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        log.debug("REST request to delete Application : {}", id);
        applicationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
