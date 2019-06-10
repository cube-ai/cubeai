package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Task;
import com.wyy.repository.TaskRepository;
import com.wyy.web.rest.util.JwtUtil;
import com.wyy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Task.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    private final TaskRepository taskRepository;

    public TaskResource(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * POST  /tasks : Create a new task.
     * @param task the task to create
     * @return the ResponseEntity with status 201 (Created) or 401 Unauthorized
     */
    @PostMapping("/tasks")
    @Timed
    public ResponseEntity<Void> createTask(HttpServletRequest httpServletRequest,
                                           @Valid @RequestBody Task task) {
        log.debug("REST request to save Task : {}", task);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(task.getUserLogin())) {
            return ResponseEntity.status(401).build();
        }

        taskRepository.save(task);
        return ResponseEntity.status(201).build(); // 201 Created
    }

    /**
     * PUT  /tasks : Updates an existing task.
     * @param task the task to update
     * @return the ResponseEntity with status 200 (OK) or 401 Unauthorized
     */
    @PutMapping("/tasks")
    @Timed
    public ResponseEntity<Task> updateTask(HttpServletRequest httpServletRequest,
                                           @Valid @RequestBody Task task) {
        log.debug("REST request to update Task : {}", task);

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        // updateTask只能由umu微服务中的异步任务OnBoardingServie调用，不能由前端用户调用
        if (null == userLogin || !userLogin.equals("system")) {
            return ResponseEntity.status(401).build();
        }

        Task result = taskRepository.save(task);
        return ResponseEntity.ok().body(result);
    }


    /**
     * GET  /tasks : get pageable tasks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tasks in body
     */
    @GetMapping("/tasks")
    @Timed
    public  ResponseEntity<List<Task>> getTasks(@RequestParam(value = "uuid", required = false) String uuid,
                                                         @RequestParam(value = "userLogin", required = false) String userLogin,
                                                         @RequestParam(value = "taskStatus", required = false) String taskStatus,
                                                         Pageable pageable) {
        log.debug("REST request to get all tasks");
        Page<Task> page;

        if (null != uuid) {
            page = taskRepository.findAllByUuid(uuid, pageable);
        } else if (null != userLogin) {
            if (null != taskStatus) {
                page = taskRepository.findAllByUserLoginAndTaskStatus(userLogin, taskStatus, pageable);
            } else {
                page = taskRepository.findAllByUserLogin(userLogin, pageable);
            }
        } else {
            page = taskRepository.findAll(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tasks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tasks/:id : get the "id" task.
     * @param id the id of the task to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the task, or with status 404 (Not Found)
     */
    @GetMapping("/tasks/{id}")
    @Timed
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        log.debug("REST request to get Task : {}", id);
        Task task = taskRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(task));
    }

    /**
     * DELETE  /tasks/:id : delete the "id" task.
     *
     * @param id the id of the task to delete
     * @return the ResponseEntity with status 200 (OK) or 401 Unauthorized
     */
    @DeleteMapping("/tasks/{id}")
    @Timed
    public ResponseEntity<Void> deleteTask(HttpServletRequest httpServletRequest,
                                           @PathVariable Long id) {
        log.debug("REST request to delete Task : {}", id);

        Task task = taskRepository.findOne(id);
        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        if (null == userLogin || !userLogin.equals(task.getUserLogin())) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        taskRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
