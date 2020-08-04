package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Artifact;
import com.wyy.domain.Solution;
import com.wyy.repository.ArtifactRepository;
import com.wyy.repository.SolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

/**
 * REST controller for managing Artifact.
 */
@RestController
@RequestMapping("/api")
public class ArtifactResource {

    private final Logger log = LoggerFactory.getLogger(ArtifactResource.class);
    private final ArtifactRepository artifactRepository;
    private final SolutionRepository solutionRepository;

    public ArtifactResource(ArtifactRepository artifactRepository, SolutionRepository solutionRepository) {
        this.artifactRepository = artifactRepository;
        this.solutionRepository = solutionRepository;
    }

    /**
     * POST  /artifacts : Create a new artifact.
     * @param artifact the artifact to create
     * @return status 201 Created or status 403 Forbidden
     */
    @PostMapping("/artifacts")
    @Timed
    public ResponseEntity<Void> createArtifact(HttpServletRequest request,
                                               @Valid @RequestBody Artifact artifact) throws URISyntaxException {
        log.debug("REST request to save Artifact : {}", artifact);

        String userLogin = request.getRemoteUser();
        Solution solution = solutionRepository.findAllByUuid(artifact.getSolutionUuid()).get(0);
        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || userLogin.equals("internal"))) {
            // createArtifact只能由umu微服务中的异步任务OnBoardingServie调用，或者solution的作者调用
            return ResponseEntity.status(403).build();
        }

        artifact.setCreatedDate(Instant.now());
        artifact.setModifiedDate(Instant.now());
        artifactRepository.save(artifact);
        return ResponseEntity.status(201).build(); // 201 Created
    }

    /**
     * GET  /artifacts : get all the artifacts.
     * @return the ResponseEntity with status 200 (OK) and the list of artifacts in body
     */
    @GetMapping("/artifacts")
    @Timed
    public List<Artifact> getAllArtifacts(@RequestParam(value = "solutionUuid") String solutionUuid,
                                          @RequestParam(value = "type", required = false) String type) {
        log.debug("REST request to get all Artifacts");

        List<Artifact> artifacts;
        if (null != type) {
            artifacts = artifactRepository.findAllBySolutionUuidAndType(solutionUuid, type);
        } else {
            artifacts = artifactRepository.findAllBySolutionUuid(solutionUuid);
        }

        return artifacts;
    }

    /**
     * DELETE  /artifacts/:id : delete the "id" artifact.
     * @param id the id of the artifact to delete
     * @return the ResponseEntity with status 200 (OK) or 403 Forbidden
     */
    @DeleteMapping("/artifacts/{id}")
    @Timed
    public ResponseEntity<Void> deleteArtifact(HttpServletRequest request,
                                               @PathVariable Long id) {
        log.debug("REST request to delete Artifact : {}", id);

        Artifact artifact = artifactRepository.findOne(id);
        Solution solution = solutionRepository.findAllByUuid(artifact.getSolutionUuid()).get(0);
        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_MANAGER");

        if (null == userLogin || !(userLogin.equals(solution.getAuthorLogin()) || userLogin.equals("internal") || hasRole)) {
            // 只能由作者自己或管理员删除，或者由umu微服务中的onboardService异步服务删除
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        artifactRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
