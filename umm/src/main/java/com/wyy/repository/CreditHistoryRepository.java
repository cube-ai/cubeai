package com.wyy.repository;

import com.wyy.domain.CreditHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the CreditHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
    Page<CreditHistory> findAllByUserLogin(String userLogin, Pageable pageable);
}
