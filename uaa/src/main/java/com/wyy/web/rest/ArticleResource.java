package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Article;

import com.wyy.repository.ArticleRepository;
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
 * REST controller for managing Article.
 */
@RestController
@RequestMapping("/api")
public class ArticleResource {

    private final Logger log = LoggerFactory.getLogger(ArticleResource.class);

    private static final String ENTITY_NAME = "article";

    private final ArticleRepository articleRepository;

    public ArticleResource(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * POST  /articles : Create a new article.
     *
     * @param article the article to create
     * @return the ResponseEntity with status 201 (Created) and with body the new article, or with status 401 Unauthorized
     */
    @PostMapping("/articles")
    @Timed
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Article> createArticle(HttpServletRequest httpServletRequest,
                                                 @Valid @RequestBody Article article) {
        log.debug("REST request to save Article : {}", article);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        article.setAuthorLogin(userLogin);
        article.setCreatedDate(Instant.now());
        article.setModifiedDate(Instant.now());
        Article result = articleRepository.save(article);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * PUT  /articles : Updates an existing article.
     * @param article the article to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated article, or with status 401 Unauthorized
     */
    @PutMapping("/articles")
    @Timed
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Article> updateArticle(HttpServletRequest httpServletRequest,
                                                 @Valid @RequestBody Article article) throws URISyntaxException {
        log.debug("REST request to update Article : {}", article);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(article.getAuthorLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        article.setAuthorLogin(userLogin);
        article.setModifiedDate(Instant.now());
        Article result = articleRepository.save(article);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, article.getId().toString()))
            .body(result);
    }

    /**
     * GET  /articles : get articles
     * @return the ResponseEntity with status 200 (OK) and the list of articles in body
     */
    @GetMapping("/articles")
    @Timed
    public ResponseEntity<List<Article>> getAllarticles(
        @RequestParam(value = "uuid", required = false) String uuid,
        @RequestParam(value = "authorLogin", required = false) String authorLogin,
        @RequestParam(value = "subject1", required = false) String subject1,
        @RequestParam(value = "subject2", required = false) String subject2,
        @RequestParam(value = "subject3", required = false) String subject3,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "tag1", required = false) String tag1,
        @RequestParam(value = "tag2", required = false) String tag2,
        @RequestParam(value = "tag3", required = false) String tag3,
        @RequestParam(value = "filter", required = false) String filter,
        Pageable pageable) {
        log.debug("REST request to get all articles");

        Page<Article> page;

        Specification specification = new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates1 = new ArrayList<>();
                List<Predicate> predicates2 = new ArrayList<>();

                if(null != uuid){
                    predicates1.add(criteriaBuilder.equal(root.get("uuid"), uuid));
                }
                if(null != authorLogin){
                    predicates1.add(criteriaBuilder.equal(root.get("authorLogin"), authorLogin));
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
                if(null != title){
                    predicates1.add(criteriaBuilder.equal(root.get("title"), title));
                }
                if(null != tag1){
                    predicates1.add(criteriaBuilder.equal(root.get("tag1"), tag1));
                }
                if(null != tag2){
                    predicates1.add(criteriaBuilder.equal(root.get("tag2"), tag2));
                }
                if(null != tag3){
                    predicates1.add(criteriaBuilder.equal(root.get("tag3"), tag3));
                }

                if (null != filter) {
                    predicates2.add(criteriaBuilder.like(root.get("authorLogin"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("authorName"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject1"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject2"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("subject3"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("title"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("summary"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("tag1"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("tag2"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("tag3"), "%"+filter+"%"));
                    predicates2.add(criteriaBuilder.like(root.get("content"), "%"+filter+"%"));
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

        page =  this.articleRepository.findAll(specification, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/articles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /articles/:id : get the "id" article.
     * @param id the id of the article to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the article, or with status 404 (Not Found)
     */
    @GetMapping("/articles/{id}")
    @Timed
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        log.debug("REST request to get Article : {}", id);
        Article article = articleRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(article));
    }

    /**
     * DELETE  /articles/:id : delete the "id" article.
     * @param id the id of the article to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/articles/{id}")
    @Timed
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> deleteArticle(HttpServletRequest httpServletRequest,
                                              @PathVariable Long id) {
        log.debug("REST request to delete Article : {}", id);

        Article article = articleRepository.findOne(id);
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(article.getAuthorLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        articleRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
