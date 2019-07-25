package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.SolutionRating;
import com.wyy.repository.SolutionRatingRepository;
import com.wyy.web.rest.util.JwtUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SolutionRating.
 */
@RestController
@RequestMapping("/api")
public class SolutionRatingResource {

    private final Logger log = LoggerFactory.getLogger(SolutionRatingResource.class);
    private final SolutionRatingRepository solutionRatingRepository;

    public SolutionRatingResource(SolutionRatingRepository solutionRatingRepository) {
        this.solutionRatingRepository = solutionRatingRepository;
    }

    /**
     * POST  /solution-ratings : Create a new solutionRating.
     * @param solutionRating the solutionRating to create
     * @return the ResponseEntity with status 201 (Created) and with body the new solutionRating, or with status 401 Unauthorized
     */
    @PostMapping("/solution-ratings")
    @Timed
    public ResponseEntity<SolutionRating> createSolutionRating(HttpServletRequest httpServletRequest,
                                                               @Valid @RequestBody SolutionRating solutionRating) {
        log.debug("REST request to save SolutionRating : {}", solutionRating);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        solutionRating.setUserLogin(userLogin);
        solutionRating.setRatingScore(0);
        solutionRating.setRatingText("");
        solutionRating.setCreatedDate(Instant.now());
        solutionRating.setModifiedDate(Instant.now());

        SolutionRating result = solutionRatingRepository.save(solutionRating);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * PUT  /solution-ratings/score : Updates an existing solutionRating's score.
     * @param jsonObject JSONObject with fields to be updated
     * @return the ResponseEntity with status 200 (OK) and with body the updated solutionRating, or with status 403 Forbidden
     */
    @PutMapping("/solution-ratings/score")
    @Timed
    public ResponseEntity<SolutionRating> updateSolutionRatingScore(HttpServletRequest httpServletRequest,
                                                                    @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to update SolutionRating : {}", jsonObject);

        SolutionRating solutionRating = solutionRatingRepository.findOne(jsonObject.getLong("id"));
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(solutionRating.getUserLogin())) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        solutionRating.setRatingScore(jsonObject.getInteger("score"));
        SolutionRating result = solutionRatingRepository.save(solutionRating);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /solution-ratings : get all the solutionRatings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of solutionRatings in body
     */
    @GetMapping("/solution-ratings")
    @Timed
    public List<SolutionRating> getAllSolutionRatings(@RequestParam(value = "userLogin") String userLogin,
                                                      @RequestParam(value = "solutionUuid") String solutionUuid) {
        log.debug("REST request to get all SolutionRatings");
        return solutionRatingRepository.findAllByUserLoginAndSolutionUuid(userLogin, solutionUuid);
    }

    /**
     * GET  /solution-ratings/:id : get the "id" solutionRating.
     *
     * @param id the id of the solutionRating to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the solutionRating, or with status 404 (Not Found)
     */
    @GetMapping("/solution-ratings/{id}")
    @Timed
    public ResponseEntity<SolutionRating> getSolutionRating(@PathVariable Long id) {
        log.debug("REST request to get SolutionRating : {}", id);
        SolutionRating solutionRating = solutionRatingRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(solutionRating));
    }

}
