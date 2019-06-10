package com.wyy.repository;

import com.wyy.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllBySolutionUuidAndParentUuidOrderByIdDesc(String solutionUuid, String parentUuid);
    Page<Comment> findAllBySolutionUuidAndParentUuid(String solutionUuid, String parentUuid, Pageable pageable);

    Long countAllBySolutionUuid(String solutionUuid);

}
