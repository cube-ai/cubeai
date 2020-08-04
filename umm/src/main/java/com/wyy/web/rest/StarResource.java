package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Star;

import com.wyy.repository.StarRepository;
import com.wyy.web.rest.errors.BadRequestAlertException;
import com.wyy.web.rest.util.HeaderUtil;
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
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Star.
 */
@RestController
@RequestMapping("/api")
public class StarResource {

    private final Logger log = LoggerFactory.getLogger(StarResource.class);

    private static final String ENTITY_NAME = "star";

    private final StarRepository starRepository;

    public StarResource(StarRepository starRepository) {
        this.starRepository = starRepository;
    }

    /**
     * POST  /stars : Create a new star.
     *
     * @param star the star to create
     * @return the ResponseEntity with status 201 (Created) or 404 forbidden
     */
    @PostMapping("/stars")
    @Timed
    public ResponseEntity<Star> createStar(HttpServletRequest request, @RequestBody Star star) {
        log.debug("REST request to save Star : {}", star);

        String userLogin = request.getRemoteUser();

        if (null == userLogin) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        star.setUserLogin(userLogin);
        star.setStarDate(Instant.now());
        starRepository.save(star);

        return ResponseEntity.status(201).build();
    }

    /**
     * GET  /stars : get pageable stars.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of stars in body
     */
    @GetMapping("/stars")
    @Timed
    public  ResponseEntity<List<Star>> getAllStars(@RequestParam(value = "userLogin", required = false) String userLogin,
                                                   @RequestParam(value = "targetUuid", required = false) String targetUuid,
                                                   @RequestParam(value = "targetType", required = false) String targetType,
                                                   Pageable pageable) {
        log.debug("REST request to get all stars");
        Page<Star> page;

        if (null != userLogin && null != targetUuid) {
            page = starRepository.findAllByUserLoginAndTargetUuid(userLogin, targetUuid, pageable);
        } else if (null != userLogin) {
            page = starRepository.findAllByUserLogin(userLogin, pageable);
        } else if (null != targetUuid) {
            page = starRepository.findAllByTargetUuid(targetUuid, pageable);
        } else {
            page = starRepository.findAll(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stars");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /stars/:id : delete the "id" star.
     *
     * @param id the id of the star to delete
     * @return the ResponseEntity with status 200 (OK) or 403 Forbidden
     */
    @DeleteMapping("/stars/{id}")
    @Timed
    public ResponseEntity<Void> deleteStar(HttpServletRequest request, @PathVariable Long id) {
        log.debug("REST request to delete Star : {}", id);

        String userLogin = request.getRemoteUser();
        Star star = starRepository.findOne(id);
        if (null == userLogin || !userLogin.equals(star.getUserLogin())) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        starRepository.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE  /stars/targetUuid : delete star by targetUuid.
     * @return the ResponseEntity with status 200 (OK) or 403 Forbidden
     */
    @DeleteMapping("/stars/uuid/{targetUuid}")
    @Timed
    public ResponseEntity<Void> deleteStarByTargetUuid(HttpServletRequest request,
                                                       @PathVariable String targetUuid) {
        log.debug("REST request to delete Star by targetUuid");

        String userLogin = request.getRemoteUser();
        if (null == userLogin) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        Star star;
        try {
            star = starRepository.findAllByUserLoginAndTargetUuid(userLogin, targetUuid, null).getContent().get(0);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }

        starRepository.delete(star.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /stars/uuids : get all the Favorite solution uuids of a user.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of target uuids in body
     */
    @GetMapping("/stars/uuids")
    @Timed
    public List<String> getStaredUuidList(@RequestParam(value = "userLogin") String userLogin) {
        log.debug("REST request to get all the stared uuids of a user");
        return starRepository.findStaredUuidList(userLogin);
    }

    /**
     * GET  /stars/count/{targetUuid} : get all the star count of a targetUuid.
     *
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping("/stars/count/{targetUuid}")
    @Timed
    public ResponseEntity<JSONObject> getStaredCount(@PathVariable String targetUuid) {
        log.debug("REST request to get the stared count by targetUuid");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starCount", starRepository.countAllByTargetUuid(targetUuid));

        return ResponseEntity.ok().body(jsonObject);
    }

}
