package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Credit;
import com.wyy.repository.CreditRepository;
import com.wyy.service.CreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * REST controller for managing Credit.
 */
@RestController
@RequestMapping("/api")
public class CreditResource {

    private final Logger log = LoggerFactory.getLogger(CreditResource.class);

    private static final String ENTITY_NAME = "credit";

    private final CreditRepository creditRepository;
    private final CreditService creditService;

    public CreditResource(CreditRepository creditRepository, CreditService creditService) {
        this.creditRepository =creditRepository;
        this.creditService = creditService;
    }

    /**
     * GET  /credits/myself : get the credit of myself.
     *
     * @return the ResponseEntity with status 200 (OK) and the credit of myself in body
     */
    @GetMapping("/credits/myself")
    @Timed
    public ResponseEntity<Credit> getCredit(HttpServletRequest request) {
        log.debug("REST request to get credit of myself");

        String userLogin = request.getRemoteUser();

        if (null == userLogin) {
            return ResponseEntity.status(403).build();
        }

        Credit credit = creditService.findCredit(userLogin);
        return ResponseEntity.ok().body(credit);
    }


    /**
     * GET  /credits : get all credits.
     *
     * @return the ResponseEntity with status 200 (OK) and all credits in body
     */
    @GetMapping("/credits")
    @Timed
    @Secured({"ROLE_ADMIN"})
    public List<Credit> getAllCredits(HttpServletRequest request,
                                      @RequestParam(value = "userLogin", required = false) String userLogin) {
        log.debug("REST request to get all credits");

        if (null != userLogin) {
            return creditRepository.findAllByUserLogin(userLogin);
        } else {
            return creditRepository.findAll();
        }
    }

    /**
     * PUT  /credits/:id/:plus : Updates an existing credit.
     * @return the ResponseEntity with status 200 (OK)
     */
    @PutMapping("/credits/{id}/{plus}")
    @Timed
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> updateCredit(@PathVariable Long id, @PathVariable Long plus) {
        log.debug("REST request to update Credit");

        Credit credit = creditRepository.findOne(id);
        creditService.updateCredit(credit, plus, "管理员后台配置");

        return ResponseEntity.ok().build();
    }

}
