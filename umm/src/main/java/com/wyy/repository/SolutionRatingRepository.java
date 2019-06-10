package com.wyy.repository;

import com.wyy.domain.SolutionRating;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the SolutionRating entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolutionRatingRepository extends JpaRepository<SolutionRating, Long> {

    List<SolutionRating> findAllByUserLoginAndSolutionUuid(String userLogin, String solutionUuid);

    Long countAllBySolutionUuidAndRatingScoreGreaterThan(String solutionUuid, Integer zero);

    @Query("SELECT sum(ratingScore) FROM SolutionRating WHERE solutionUuid = :solutionUuid")
    Integer sumRatingScore(@Param("solutionUuid") String solutionUuid);
}
