package com.wyy.service;

import com.wyy.domain.VerifyCode;
import com.wyy.repository.VerifyCodeRepository;
import org.springframework.scheduling.annotation.Scheduled;
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

        try {
            verifyCodeRepository.delete(id);
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * Not used verify code should be automatically deleted everyday, at 03:00 (am).
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void removeNotUsedVerifyCode() {
        verifyCodeRepository.deleteAll();
    }

}
