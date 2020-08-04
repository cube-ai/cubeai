package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Comment;
import com.wyy.repository.CommentRepository;
import com.wyy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Comment.
 */
@RestController
@RequestMapping("/api")
public class CommentResource {

    private final Logger log = LoggerFactory.getLogger(CommentResource.class);
    private final CommentRepository commentRepository;

    public CommentResource(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * POST  /comments : Create a new comment.
     * @param comment the comment to create
     * @return the ResponseEntity with status 201 (Created) or with status 403
     */
    @PostMapping("/comments")
    @Timed
    public ResponseEntity<Void> createComment(HttpServletRequest request,
                                              @Valid @RequestBody Comment comment) {
        log.debug("REST request to save Comment : {}", comment);

        String userLogin = request.getRemoteUser();
        if (null == userLogin) {
            return ResponseEntity.status(403).build();
        }

        comment.setUserLogin(userLogin);
        comment.setCreatedDate(Instant.now());
        comment.setModifiedDate(Instant.now());
        commentRepository.save(comment);

        return ResponseEntity.status(201).build();
    }

    /**
     * GET  /comments: get all comments.
     * @return the ResponseEntity with status 200 (OK) and the list of comments in body
     */
    @GetMapping("/comments")
    @Timed
    public  ResponseEntity<List<Comment>> getAllComments(@RequestParam(value = "solutionUuid") String solutionUuid,
                                                         @RequestParam(value = "parentUuid") String parentUuid,
                                                         Pageable pageable) {
        log.debug("REST request to get all comments");
        Page<Comment> page = commentRepository.findAllBySolutionUuidAndParentUuid(solutionUuid, parentUuid, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/comments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /comments/:id : get the "id" comment.
     * @param id the id of the comment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the comment, or with status 404 (Not Found)
     */
    @GetMapping("/comments/{id}")
    @Timed
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        log.debug("REST request to get Comment : {}", id);
        Comment comment = commentRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(comment));
    }

    /**
     * DELETE  /comments/:id : delete the "id" comment.
     * @param id the id of the comment to delete
     * @return the ResponseEntity with status 200 (OK) or with status 403 Forbidden
     */
    @DeleteMapping("/comments/{id}")
    @Timed
    public ResponseEntity<Void> deleteComment(HttpServletRequest request,
                                              @PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);

        Comment comment = commentRepository.findOne(id);
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(comment.getUserLogin()) || hasRole)) {
            // 只能由申请者自己或者ROLE_MANAGER删除
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        commentRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
