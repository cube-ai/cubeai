package com.wyy.repository;

import com.wyy.domain.SolutionFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the SolutionFavorite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SolutionFavoriteRepository extends JpaRepository<SolutionFavorite, Long> {

    List<SolutionFavorite> findAllByUserLoginAndSolutionUuid(String userLogin, String solutionUuid);

    Page<SolutionFavorite> findAllByUserLoginAndSolutionUuid(String userLogin, String solutionUuid, Pageable pageable);

    Page<SolutionFavorite> findAllByUserLogin(String userLogin, Pageable pageable);

    @Query("SELECT solutionUuid FROM SolutionFavorite WHERE userLogin = :userLogin")
    List<String> findFavoriteSolutionUuidList(@Param("userLogin") String userLogin);

}
