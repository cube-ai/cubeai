package com.wyy.repository;

import com.wyy.domain.Credit;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Credit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    Credit findOneByUserLogin(String userLogin);
    List<Credit> findAllByUserLogin(String userLogin);
}
