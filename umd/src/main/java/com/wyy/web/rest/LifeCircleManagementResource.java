package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Deployment;
import com.wyy.domain.Task;
import com.wyy.service.KafkaProducer;
import com.wyy.service.UmmClient;
import com.wyy.web.rest.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.UUID;

/**
 * REST controller for ability life-circle-management.
 */
@RestController
@RequestMapping("/api/lcm")
public class LifeCircleManagementResource {

    private final Logger log = LoggerFactory.getLogger(DeployResource.class);
    private final UmmClient ummClient;
    private final KafkaProducer kafkaProducer;

    public LifeCircleManagementResource(UmmClient ummClient,
                                        KafkaProducer kafkaProducer) {
        this.ummClient = ummClient;
        this.kafkaProducer = kafkaProducer;
    }

    @PutMapping("/stop")
    @Timed
    @Secured({"ROLE_OPERATOR"})
    public ResponseEntity<Deployment> stop(HttpServletRequest httpServletRequest,
                                               @Valid @RequestBody Deployment deployment) {
        log.debug("REST request to stop running the deployment (ability)");

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);

        Task task = new Task();
        task.setUuid(UUID.randomUUID().toString().replace("-", "").toLowerCase());
        task.setUserLogin(userLogin);
        task.setTaskName(deployment.getSolutionName());
        task.setTaskType("实例停止");
        task.setTaskStatus("等待调度");
        task.setTaskProgress(0);
        // 约定实例停止任务的targetUuid与deployment的uuid一致
        task.setTargetUuid(deployment.getUuid());
        task.setStartDate(Instant.now());

        try {
            this.ummClient.createTask(task);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }

        JSONObject taskCommand = new JSONObject();
        taskCommand.put("taskType", "ucumos-lcm-stop");
        taskCommand.put("taskUuid", task.getUuid());
        taskCommand.put("deploymentUuid", deployment.getUuid());

        try {
            kafkaProducer.send("async-task-topic", taskCommand.toJSONString());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().build();
    }

}
