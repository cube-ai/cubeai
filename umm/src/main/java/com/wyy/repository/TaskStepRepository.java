package com.wyy.repository;

import com.wyy.domain.TaskStep;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the TaskStep entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskStepRepository extends JpaRepository<TaskStep, Long> {

    List<TaskStep> findAllByIdGreaterThanAndTaskUuidAndStepName(Long id, String taskUuid, String stepName);

}
