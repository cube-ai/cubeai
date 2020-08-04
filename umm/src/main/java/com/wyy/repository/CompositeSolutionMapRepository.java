package com.wyy.repository;

import com.wyy.domain.CompositeSolutionMap;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the CompositeSolutionMap entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompositeSolutionMapRepository extends JpaRepository<CompositeSolutionMap, Long> ,JpaSpecificationExecutor{

}
