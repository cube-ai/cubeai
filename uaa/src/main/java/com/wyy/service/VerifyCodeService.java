package com.wyy.service;

import com.wyy.domain.VerifyCode;
import com.wyy.repository.VerifyCodeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class VerifyCodeService {

    private final VerifyCodeRepository verifyCodeRepository;

    public VerifyCodeService(VerifyCodeRepository verifyCodeRepository) {
        this.verifyCodeRepository = verifyCodeRepository;
    }

    public int validateVerifyCode(Long id, String code) {

        int result;
        VerifyCode verifyCode;
        verifyCode = verifyCodeRepository.findOne(id);
        if (null != verifyCode && code.toLowerCase().equals(verifyCode.getCode().toLowerCase()) && Instant.now().isBefore(verifyCode.getExpire())) {
            result = 1;
        } else {
            result = 0;
        }

        verifyCodeRepository.delete(id);
        return result;
    }

}
