package com.wyy.repository;

import com.wyy.domain.Description;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Description entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DescriptionRepository extends JpaRepository<Description, Long> {

    List<Description> findAllBySolutionUuid(String solutionUuid);
}
