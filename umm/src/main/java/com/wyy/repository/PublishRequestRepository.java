package com.wyy.repository;

import com.wyy.domain.PublishRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PublishRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublishRequestRepository extends JpaRepository<PublishRequest, Long> {

    Page<PublishRequest> findAllBySolutionUuid(String solutionUuid, Pageable pageable);
    Page<PublishRequest> findAllByReviewed(Boolean reviewed, Pageable pageable);
    Page<PublishRequest> findAllByReviewedAndRequestType(Boolean reviewed, String requestType, Pageable pageable);

}
