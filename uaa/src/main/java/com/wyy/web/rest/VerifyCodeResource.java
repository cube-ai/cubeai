package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.VerifyCode;

import com.wyy.repository.VerifyCodeRepository;
import com.wyy.service.VerifyCodeService;
import com.wyy.web.rest.util.HeaderUtil;
import com.wyy.web.rest.util.VerifyCodeUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Map;


/**
 * REST controller for managing VerifyCode.
 */
@RestController
@RequestMapping("/api")
public class VerifyCodeResource {

    private final Logger log = LoggerFactory.getLogger(VerifyCodeResource.class);

    private static final String ENTITY_NAME = "verifyCode";

    private final VerifyCodeRepository verifyCodeRepository;

    private final VerifyCodeService verifyCodeService;

    public VerifyCodeResource(VerifyCodeRepository verifyCodeRepository,
                              VerifyCodeService verifyCodeService) {
        this.verifyCodeRepository = verifyCodeRepository;
        this.verifyCodeService = verifyCodeService;
    }

    /**
     * GET  /verify-codes : return a new created verifyCode.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the verifyCode, or with status 404 (Not Found)
     */
    @GetMapping("/verify-codes")
    @Timed
    public String getVerifyCode() {
        log.debug("REST request to get a new VerifyCode");

        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode(VerifyCodeUtil.genRandomCode());
        verifyCode.setExpire(Instant.now().plusSeconds(60));
        verifyCode = verifyCodeRepository.save(verifyCode);

        JSONObject result = new JSONObject();
        result.put("verifyId", verifyCode.getId());
        result.put("verifyCode", VerifyCodeUtil.drawCodePicture(verifyCode.getCode()));

        return JSONObject.toJSONString(result);
    }

    /**
     * POST  /verify-codes : Validate a verifyCode.
     *
     * @return the 1 or 0 for success or fail
     */
    @PostMapping("/verify-codes")
    @Timed
    public int validateVerifyCode(@Valid @RequestBody Map<String, String> params) {
        log.debug("REST request to validate user submitted verifyCode");

        Long id = Long.valueOf(params.get("verifyId"));
        String code = params.get("verifyCode");

        return this.verifyCodeService.validateVerifyCode(id, code);
    }

}
