package com.wyy.repository;

import com.wyy.domain.VerifyCode;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the VerifyCode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Long> {

}
