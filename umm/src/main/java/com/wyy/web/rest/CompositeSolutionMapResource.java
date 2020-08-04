package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.CompositeSolutionMap;

import com.wyy.repository.CompositeSolutionMapRepository;
import com.wyy.web.rest.errors.BadRequestAlertException;
import com.wyy.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing CompositeSolutionMap.
 */
@RestController
@RequestMapping("/api")
public class CompositeSolutionMapResource {

    private final Logger log = LoggerFactory.getLogger(CompositeSolutionMapResource.class);

    private static final String ENTITY_NAME = "compositeSolutionMap";

    private final CompositeSolutionMapRepository compositeSolutionMapRepository;

    public CompositeSolutionMapResource(CompositeSolutionMapRepository compositeSolutionMapRepository) {
        this.compositeSolutionMapRepository = compositeSolutionMapRepository;
    }

    /**
     * POST  /composite-solution-maps : Create a new compositeSolutionMap.
     *
     * @param compositeSolutionMap the compositeSolutionMap to create
     * @return the ResponseEntity with status 201 (Created) and with body the new compositeSolutionMap, or with status 400 (Bad Request) if the compositeSolutionMap has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/composite-solution-maps")
    @Timed
    public ResponseEntity<CompositeSolutionMap> createCompositeSolutionMap(@Valid @RequestBody CompositeSolutionMap compositeSolutionMap) throws URISyntaxException {
        log.debug("REST request to save CompositeSolutionMap : {}", compositeSolutionMap);
        if (compositeSolutionMap.getId() != null) {
            throw new BadRequestAlertException("A new compositeSolutionMap cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CompositeSolutionMap result = compositeSolutionMapRepository.save(compositeSolutionMap);
        return ResponseEntity.created(new URI("/api/composite-solution-maps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /composite-solution-maps : Updates an existing compositeSolutionMap.
     *
     * @param compositeSolutionMap the compositeSolutionMap to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated compositeSolutionMap,
     * or with status 400 (Bad Request) if the compositeSolutionMap is not valid,
     * or with status 500 (Internal Server Error) if the compositeSolutionMap couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/composite-solution-maps")
    @Timed
    public ResponseEntity<CompositeSolutionMap> updateCompositeSolutionMap(@Valid @RequestBody CompositeSolutionMap compositeSolutionMap) throws URISyntaxException {
        log.debug("REST request to update CompositeSolutionMap : {}", compositeSolutionMap);
        if (compositeSolutionMap.getId() == null) {
            return createCompositeSolutionMap(compositeSolutionMap);
        }
        CompositeSolutionMap result = compositeSolutionMapRepository.save(compositeSolutionMap);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, compositeSolutionMap.getId().toString()))
            .body(result);
    }

    /**
     * GET  /composite-solution-maps : get all the compositeSolutionMaps.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of compositeSolutionMaps in body
     */
    @GetMapping("/composite-solution-maps")
    @Timed
    public ResponseEntity<List<CompositeSolutionMap>> getAllCompositeSolutionMaps(
        @RequestParam(value = "parentUuid", required = false) String parentUuid) {

        log.debug("REST request to get all CompositeSolutionMaps");
        Specification specification = new Specification<CompositeSolutionMap>() {
            @Override
            public Predicate toPredicate(Root<CompositeSolutionMap> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates1 = new ArrayList<>();
                if (null != parentUuid) {
                    predicates1.add(criteriaBuilder.equal(root.get("parentUuid"), parentUuid));
                }


                Predicate predicate1 = criteriaBuilder.and(predicates1.toArray(new Predicate[predicates1.size()]));
                return predicate1;
            }
        };
        List list = compositeSolutionMapRepository.findAll(specification);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
        }


    /**
     * GET  /composite-solution-maps/:id : get the "id" compositeSolutionMap.
     *
     * @param id the id of the compositeSolutionMap to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the compositeSolutionMap, or with status 404 (Not Found)
     */
    @GetMapping("/composite-solution-maps/{id}")
    @Timed
    public ResponseEntity<CompositeSolutionMap> getCompositeSolutionMap(@PathVariable Long id) {
        log.debug("REST request to get CompositeSolutionMap : {}", id);
        CompositeSolutionMap compositeSolutionMap = compositeSolutionMapRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(compositeSolutionMap));
    }

    /**
     * DELETE  /composite-solution-maps/:id : delete the "id" compositeSolutionMap.
     *
     * @param id the id of the compositeSolutionMap to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/composite-solution-maps/{id}")
    @Timed
    public ResponseEntity<Void> deleteCompositeSolutionMap(@PathVariable Long id) {
        log.debug("REST request to delete CompositeSolutionMap : {}", id);
        compositeSolutionMapRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
