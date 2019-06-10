package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.SolutionShared;
import com.wyy.repository.SolutionSharedRepository;
import com.wyy.web.rest.util.JwtUtil;
import com.wyy.web.rest.util.PaginationUtil;
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


/**
 * REST controller for managing SolutionShared.
 */
@RestController
@RequestMapping("/api")
public class SolutionSharedResource {

    private final Logger log = LoggerFactory.getLogger(SolutionSharedResource.class);
    private final SolutionSharedRepository solutionSharedRepository;

    public SolutionSharedResource(SolutionSharedRepository solutionSharedRepository) {
        this.solutionSharedRepository = solutionSharedRepository;
    }

    /**
     * POST  /solution-shareds : Create a new solutionShared.
     * @param solutionShared the solutionShared to create
     * @return the ResponseEntity with status 201 (Created) or 401 Unauthorized
     */
    @PostMapping("/solution-shareds")
    @Timed
    public ResponseEntity<SolutionShared> createSolutionShared(HttpServletRequest httpServletRequest,
                                                               @Valid @RequestBody SolutionShared solutionShared) {
        log.debug("REST request to save SolutionShared : {}", solutionShared);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        solutionShared.setFromUserLogin(userLogin);
        solutionShared.shareDate(Instant.now());
        SolutionShared result = solutionSharedRepository.save(solutionShared);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * GET  /solution-shared : get pageable solution-shareds.
     * @return the ResponseEntity with status 200 (OK) and the list of solution-shareds in body
     */
    @GetMapping("/solution-shareds")
    @Timed
    public  ResponseEntity<List<SolutionShared>> getAllSolutionShared(@RequestParam(value = "toUserLogin") String toUserLogin,
                                                                            Pageable pageable) {
        log.debug("REST request to get all solution-shareds");
        Page<SolutionShared> page = solutionSharedRepository.findAllByToUserLogin(toUserLogin, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/solution-shared");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /solution-shareds/:id : delete the "id" solutionShared.
     * @param id the id of the solutionShared to delete
     * @return the ResponseEntity with status 200 (OK) or 401 Unauthorized
     */
    @DeleteMapping("/solution-shareds/{id}")
    @Timed
    public ResponseEntity<Void> deleteSolutionShared(HttpServletRequest httpServletRequest,
                                                     @PathVariable Long id) {
        log.debug("REST request to delete SolutionShared : {}", id);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        SolutionShared solutionFavorite = solutionSharedRepository.findOne(id);
        if (null == userLogin || !userLogin.equals(solutionFavorite.getToUserLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        solutionSharedRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
