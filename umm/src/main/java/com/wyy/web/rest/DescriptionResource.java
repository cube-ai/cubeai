package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Description;
import com.wyy.repository.DescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing Description.
 */
@RestController
@RequestMapping("/api")
public class DescriptionResource {

    private final Logger log = LoggerFactory.getLogger(DescriptionResource.class);
    private final DescriptionRepository descriptionRepository;

    public DescriptionResource(DescriptionRepository descriptionRepository) {
        this.descriptionRepository = descriptionRepository;
    }

    /**
     * PUT  /descriptions/content : Updates an existing description's content.
     * @param jsonObject the JSONObject with content to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solution, or status 403 Forbidden
     */
    @PutMapping("/descriptions/content")
    @Timed
    public ResponseEntity<Description> updateDescription(HttpServletRequest request,
                                                         @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update Description : {}", jsonObject);

        Description description = descriptionRepository.findOne(jsonObject.getLong("id"));
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(description.getAuthorLogin()) || hasRole)) {
            // description中的content字段只能由作者自己或管理员修改
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        description.setContent(jsonObject.getString("content"));
        Description result = descriptionRepository.save(description);

        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /descriptions : get all the descriptions by solutionUuid.
     * @return the ResponseEntity with status 200 (OK) and the list of descriptions in body
     */
    @GetMapping("/descriptions")
    @Timed
    public List<Description> getAllDescriptions(@RequestParam(value = "solutionUuid") String solutionUuid) {
        log.debug("REST request to get all Descriptions");

        return descriptionRepository.findAllBySolutionUuid(solutionUuid);
    }

    /**
     * DELETE  /descriptions/:id : delete the "id" description.
     * @param id the id of the description to delete
     * @return the ResponseEntity with status 200 (OK) or 403 Forbidden
     */
    @DeleteMapping("/descriptions/{id}")
    @Timed
    public ResponseEntity<Void> deleteDescription(HttpServletRequest request,
                                                  @PathVariable Long id) {
        log.debug("REST request to delete Description : {}", id);

        Description description = descriptionRepository.findOne(id);
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");
        if (null == userLogin || !(userLogin.equals(description.getAuthorLogin()) || hasRole)) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        descriptionRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
