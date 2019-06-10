package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Document;
import com.wyy.domain.Solution;
import com.wyy.repository.DocumentRepository;
import com.wyy.repository.SolutionRepository;
import com.wyy.web.rest.util.JwtUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Document.
 */
@RestController
@RequestMapping("/api")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);
    private final DocumentRepository documentRepository;
    private final SolutionRepository solutionRepository;

    public DocumentResource(DocumentRepository documentRepository, SolutionRepository solutionRepository) {
        this.documentRepository = documentRepository;
        this.solutionRepository = solutionRepository;
    }

    /**
     * POST  /documents : Create a new document.
     * @param document the document to create
     * @return the ResponseEntity with status 201 (Created) and with body the new document, or with status 400 (Bad Request) or 401 Unauthorized
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/documents")
    @Timed
    public ResponseEntity<Void> createDocument(HttpServletRequest httpServletRequest,
                                               @Valid @RequestBody Document document) throws URISyntaxException {
        log.debug("REST request to save Document : {}", document);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);

        List<Solution> solutions = solutionRepository.findAllByUuid(document.getSolutionUuid());
        if (solutions.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Solution solution = solutions.get(0);
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        document.setAuthorLogin(userLogin);
        document.setCreatedDate(Instant.now());
        document.setModifiedDate(Instant.now());

        Document result = documentRepository.save(document);
        return ResponseEntity.status(201).build(); // 201 Created
    }

    /**
     * GET  /documents : get all the documents bu solutionUuid.
     * @return the ResponseEntity with status 200 (OK) and the list of documents in body
     */
    @GetMapping("/documents")
    @Timed
    public List<Document> getAllDocuments(@RequestParam(value = "solutionUuid") String solutionUuid,
                                          @RequestParam(value = "name", required = false) String name) {
        log.debug("REST request to get all Documents");

        if (null == name) {
            return documentRepository.findAllBySolutionUuid(solutionUuid);
        } else {
            return documentRepository.findAllBySolutionUuidAndName(solutionUuid, name);
        }
    }

    /**
     * GET  /documents/:id : get the "id" document.
     * @param id the id of the document to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the document, or with status 404 (Not Found)
     */
    @GetMapping("/documents/{id}")
    @Timed
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        log.debug("REST request to get Document : {}", id);
        Document document = documentRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(document));
    }

    /**
     * DELETE  /documents/:id : delete the "id" document.
     * @param id the id of the document to delete
     * @return the ResponseEntity with status 200 (OK) or 401 Unauthorized
     */
    @DeleteMapping("/documents/{id}")
    @Timed
    public ResponseEntity<Void> deleteDocument(HttpServletRequest httpServletRequest,
                                               @PathVariable Long id) {
        log.debug("REST request to delete Document : {}", id);

        Document document = documentRepository.findOne(id);
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(document.getAuthorLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        documentRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
