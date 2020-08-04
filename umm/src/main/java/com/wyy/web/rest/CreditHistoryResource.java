package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.CreditHistory;
import com.wyy.repository.CreditHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * REST controller for managing CreditHistory.
 */
@RestController
@RequestMapping("/api")
public class CreditHistoryResource {

    private final Logger log = LoggerFactory.getLogger(CreditHistoryResource.class);

    private static final String ENTITY_NAME = "creditHistory";

    private final CreditHistoryRepository creditHistoryRepository;

    public CreditHistoryResource(CreditHistoryRepository creditHistoryRepository) {
        this.creditHistoryRepository = creditHistoryRepository;
    }

    /**
     * GET  /credit-histories : get all the creditHistories.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of creditHistories in body
     */
    @GetMapping("/credit-histories")
    @Timed
    public ResponseEntity<List<CreditHistory>> getAllCreditHistories(HttpServletRequest request,
                                                                     @RequestParam(value = "userName", required = false) String userName,
                                                                     Pageable pageable) {
        log.debug("REST request to get all CreditHistories");

        String userLogin = request.getRemoteUser();
        Boolean hasRole = request.isUserInRole("ROLE_ADMIN");

        if (null == userLogin || (null != userName && !hasRole)) {
            return ResponseEntity.status(403).build();
        }

        if (null != userName) {
            userLogin = userName;
        }

        List<CreditHistory> creditHistoryList = creditHistoryRepository.findAllByUserLogin(userLogin, pageable).getContent();
        return ResponseEntity.ok().body(creditHistoryList);
    }

}
