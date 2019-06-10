package com.wyy.repository;

import com.wyy.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByUuid(String uuid, Pageable pageable);
    Page<Task> findAllByUserLogin(String userLogin, Pageable pageable);
    Page<Task> findAllByUserLoginAndTaskStatus(String userLogin, String taskStatus, Pageable pageable);

}
