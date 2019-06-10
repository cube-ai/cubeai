package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.SolutionFavorite;
import com.wyy.repository.SolutionFavoriteRepository;
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
 * REST controller for managing SolutionFavorite.
 */
@RestController
@RequestMapping("/api")
public class SolutionFavoriteResource {

    private final Logger log = LoggerFactory.getLogger(SolutionFavoriteResource.class);
    private final SolutionFavoriteRepository solutionFavoriteRepository;

    public SolutionFavoriteResource(SolutionFavoriteRepository solutionFavoriteRepository) {
        this.solutionFavoriteRepository = solutionFavoriteRepository;
    }

    /**
     * POST  /solution-favorites : Create a new solutionFavorite.
     * @param solutionFavorite the solutionFavorite to create
     * @return the ResponseEntity with status 201 (Created) or 401 Unauthorized
     */
    @PostMapping("/solution-favorites")
    @Timed
    public ResponseEntity<SolutionFavorite> createSolutionFavorite(HttpServletRequest httpServletRequest,
                                                                   @Valid @RequestBody SolutionFavorite solutionFavorite) {
        log.debug("REST request to save SolutionFavorite : {}", solutionFavorite);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        solutionFavorite.setUserLogin(userLogin);
        solutionFavorite.setFavoriteDate(Instant.now());
        solutionFavoriteRepository.save(solutionFavorite);

        return ResponseEntity.status(201).build();
    }

    /**
     * GET  /solution-favorites : get pageable solution-favorites.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of solution-favorites in body
     */
    @GetMapping("/solution-favorites")
    @Timed
    public  ResponseEntity<List<SolutionFavorite>> getAllSolutionFavorite(@RequestParam(value = "userLogin") String userLogin,
                                                                                @RequestParam(value = "solutionUuid", required = false) String solutionUuid,
                                                                                Pageable pageable) {
        log.debug("REST request to get all solution-favorites");
        Page<SolutionFavorite> page;

        if (null != solutionUuid) {
            page = solutionFavoriteRepository.findAllByUserLoginAndSolutionUuid(userLogin, solutionUuid, pageable);
        } else {
            page = solutionFavoriteRepository.findAllByUserLogin(userLogin, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/solution-favorites");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /solution-favorites/:id : delete the "id" solutionFavorite.
     *
     * @param id the id of the solutionFavorite to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/solution-favorites/{id}")
    @Timed
    public ResponseEntity<Void> deleteSolutionFavorite(HttpServletRequest httpServletRequest,
                                                       @PathVariable Long id) {
        log.debug("REST request to delete SolutionFavorite : {}", id);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        SolutionFavorite solutionFavorite = solutionFavoriteRepository.findOne(id);
        if (null == userLogin || !userLogin.equals(solutionFavorite.getUserLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        solutionFavoriteRepository.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE  /solution-favorites/solutionUuid : delete solutionFavorite by solutionUuid.
     * @return the ResponseEntity with status 200 (OK) or 401 Unauthorized
     */
    @DeleteMapping("/solution-favorites/uuid/{solutionUuid}")
    @Timed
    public ResponseEntity<Void> deleteSolutionFavoriteByUserLoginAndSolutionUuid(HttpServletRequest httpServletRequest,
                                                                                 @PathVariable String solutionUuid) {
        log.debug("REST request to delete SolutionFavorite by userLogin and solutionUuid");

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        SolutionFavorite solutionFavorite = solutionFavoriteRepository.findAllByUserLoginAndSolutionUuid(userLogin, solutionUuid).get(0);
        if (null == userLogin || !userLogin.equals(solutionFavorite.getUserLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        solutionFavoriteRepository.delete(solutionFavorite.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /solutions/favorites : get all the Favorite solution uuids of a user.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of solution uuids in body
     */
    @GetMapping("solution-favorites/uuids")
    @Timed
    public List<String> getFavoriteSolutionUuidList(@RequestParam(value = "userLogin") String userLogin) {
        log.debug("REST request to get all the Favorite solutions of a user");
        return solutionFavoriteRepository.findFavoriteSolutionUuidList(userLogin);
    }

}
