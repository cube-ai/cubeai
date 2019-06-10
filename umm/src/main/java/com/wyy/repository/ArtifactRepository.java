package com.wyy.repository;

import com.wyy.domain.Artifact;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Artifact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

    List<Artifact> findAllBySolutionUuid(String solutionUuid);
    List<Artifact> findAllBySolutionUuidAndType(String solutionUuid, String type);

}
