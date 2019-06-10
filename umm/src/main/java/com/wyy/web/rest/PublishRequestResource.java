package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.PublishRequest;
import com.wyy.repository.PublishRequestRepository;
import com.wyy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


/**
 * REST controller for managing PublishRequest.
 */
@RestController
@RequestMapping("/api")
public class PublishRequestResource {

    private final Logger log = LoggerFactory.getLogger(PublishRequestResource.class);
    private final PublishRequestRepository publishRequestRepository;

    public PublishRequestResource(PublishRequestRepository publishRequestRepository) {
        this.publishRequestRepository = publishRequestRepository;
    }

    /**
     * GET  /publish-requests : get pageable publishRequests.
     * @return the ResponseEntity with status 200 (OK) and the list of publishRequests in body
     */
    @GetMapping("/publish-requests")
    @Timed
    public  ResponseEntity<List<PublishRequest>> getPublishRequests(@RequestParam(value = "reviewed", required = false) Boolean reviewed,
                                                                    @RequestParam(value = "requestType", required = false) String requestType,
                                                                    @RequestParam(value = "solutionUuid", required = false) String solutionUuid,
                                                                    Pageable pageable) {
        log.debug("REST request to get all PublishRequests");
        Page<PublishRequest> page;

        if (null != reviewed) {
            if (null != requestType) {
                page = publishRequestRepository.findAllByReviewedAndRequestType(reviewed, requestType, pageable);
            } else {
                page = publishRequestRepository.findAllByReviewed(reviewed, pageable);
            }
        } else if (null != solutionUuid) {
            page = publishRequestRepository.findAllBySolutionUuid(solutionUuid, pageable);
        } else {
            page = publishRequestRepository.findAll(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/publish-requests");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * GET  /publish-requests/:id : get the "id" publishRequest.
     * @param id the id of the publishRequest to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the publishRequest, or with status 404 (Not Found)
     */
    @GetMapping("/publish-requests/{id}")
    @Timed
    public ResponseEntity<PublishRequest> getPublishRequest(@PathVariable Long id) {
        log.debug("REST request to get PublishRequest : {}", id);
        PublishRequest publishRequest = publishRequestRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(publishRequest));
    }

    /**
     * DELETE  /publish-requests/:id : delete the "id" publishRequest.
     * @param id the id of the publishRequest to delete
     * @return the ResponseEntity with status 200 (OK) or 401 Unauthorized
     */
    @DeleteMapping("/publish-requests/{id}")
    @Timed
    @Secured({"ROLE_MANAGER"})  // PublishRequest只能由平台管理员删除
    public ResponseEntity<Void> deletePublishRequest(@PathVariable Long id) {
        log.debug("REST request to delete PublishRequest : {}", id);

        publishRequestRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
