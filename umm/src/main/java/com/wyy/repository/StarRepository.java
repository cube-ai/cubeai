package com.wyy.repository;

import com.wyy.domain.Star;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Star entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StarRepository extends JpaRepository<Star, Long> {

    Page<Star> findAllByUserLoginAndTargetUuid(String userLogin,String targetUuid, Pageable pageable);

    Page<Star> findAllByUserLogin(String userLogin, Pageable pageable);

    Page<Star> findAllByTargetUuid(String targetUuid, Pageable pageable);

    @Query("SELECT targetUuid FROM Star WHERE userLogin = :userLogin")
    List<String> findStaredUuidList(@Param("userLogin") String userLogin);

    Long countAllByTargetUuid(String targetUuid);
}
