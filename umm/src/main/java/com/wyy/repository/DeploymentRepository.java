package com.wyy.repository;

import com.wyy.domain.Deployment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Deployment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long>, JpaSpecificationExecutor {

    List<Deployment> findAllByUuid(String uuid);

}
