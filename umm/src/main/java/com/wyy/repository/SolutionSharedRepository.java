package com.wyy.repository;

import com.wyy.domain.SolutionShared;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the SolutionShared entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolutionSharedRepository extends JpaRepository<SolutionShared, Long> {

    Page<SolutionShared> findAllByToUserLogin(String toUserLogin, Pageable pageable);

}
