package com.wyy.service;

import com.wyy.domain.Credit;
import com.wyy.domain.CreditHistory;
import com.wyy.repository.CreditHistoryRepository;
import com.wyy.repository.CreditRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;


@Service
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    public CreditService(CreditRepository creditRepository, CreditHistoryRepository creditHistoryRepository) {
        this.creditRepository = creditRepository;
        this.creditHistoryRepository = creditHistoryRepository;
    }

    public Credit findCredit(String userLogin) {
        Credit credit = creditRepository.findOneByUserLogin(userLogin);
        if (null == credit) {
            credit = new Credit();
            credit.setUserLogin(userLogin);
            credit.setCredit(50L);
            credit = creditRepository.save(credit);

            CreditHistory creditHistory = new CreditHistory();
            creditHistory.setUserLogin(userLogin);
            creditHistory.setCreditPlus(50L);
            creditHistory.setCurrentCredit(50L);
            creditHistory.setComment("初始化赋值50积分");
            creditHistory.setModifyDate(Instant.now());
            creditHistoryRepository.save(creditHistory);
        }

        return credit;
    }

    public void updateCredit(Credit credit, Long creditPlus, String comment) {
        credit.setCredit(credit.getCredit() + creditPlus);
        creditRepository.save(credit);
        CreditHistory creditHistory = new CreditHistory();
        creditHistory.setUserLogin(credit.getUserLogin());
        creditHistory.setComment(comment);
        creditHistory.setCreditPlus(creditPlus);
        creditHistory.setCurrentCredit(credit.getCredit());
        creditHistory.setModifyDate(Instant.now());
        creditHistoryRepository.save(creditHistory);
    }

}
