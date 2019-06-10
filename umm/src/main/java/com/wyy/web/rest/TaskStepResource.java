package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.TaskStep;
import com.wyy.repository.TaskStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


/**
 * REST controller for managing TaskStep.
 */
@RestController
@RequestMapping("/api")
public class TaskStepResource {

    private final Logger log = LoggerFactory.getLogger(TaskStepResource.class);

    private final TaskStepRepository taskStepRepository;

    public TaskStepResource(TaskStepRepository taskStepRepository) {
        this.taskStepRepository = taskStepRepository;
    }

    /**
     * POST  /task-steps : Create a new taskStep.
     * @param taskStep the taskStep to create
     * @return the ResponseEntity with status 201 (Created)
     */
    @PostMapping("/task-steps")
    @Timed
    public ResponseEntity<Void> createTaskStep(@Valid @RequestBody TaskStep taskStep) {
        log.debug("REST request to save TaskStep : {}", taskStep);
        taskStepRepository.save(taskStep);
        return ResponseEntity.status(201).build();
    }

    /**
     * GET  /task-steps : get all the taskSteps.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of taskSteps in body
     */
    @GetMapping("/task-steps")
    @Timed
    public List<TaskStep> getAllTaskSteps(@RequestParam(value = "id") Long id,
                                          @RequestParam(value = "taskUuid") String taskUuid,
                                          @RequestParam(value = "stepName") String stepName) {
        log.debug("REST request to get all TaskSteps");
        return taskStepRepository.findAllByIdGreaterThanAndTaskUuidAndStepName(id, taskUuid, stepName);
    }

    /**
     * DELETE  /task-steps/:id : delete the "id" taskStep.
     * @param id the id of the taskStep to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/task-steps/{id}")
    @Timed
    public ResponseEntity<Void> deleteTaskStep(@PathVariable Long id) {
        log.debug("REST request to delete TaskStep : {}", id);
        taskStepRepository.delete(id);
        return ResponseEntity.ok().build();
    }
}
