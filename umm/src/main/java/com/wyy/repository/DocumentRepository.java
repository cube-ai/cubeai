package com.wyy.repository;

import com.wyy.domain.Document;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Document entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllBySolutionUuid(String solutionUuid);
    List<Document> findAllBySolutionUuidAndName(String solutionUuid, String name);

}
