package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Solution;
import com.wyy.domain.Task;
import com.wyy.service.KafkaProducer;
import com.wyy.service.UmmClient;
import com.wyy.web.rest.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;


/**
 * REST controller for deploy solution.
 */
@RestController
@RequestMapping("/api")
public class DeployResource {

    private final Logger log = LoggerFactory.getLogger(DeployResource.class);

    private final UmmClient ummClient;
    private final KafkaProducer kafkaProducer;

    public DeployResource(UmmClient ummClient,
                          KafkaProducer kafkaProducer) {
        this.ummClient = ummClient;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/deploy")
    @Timed
    public ResponseEntity<Void> deploy(HttpServletRequest httpServletRequest,
                                           @Valid @RequestBody JSONObject jsonObject) {
        log.debug("REST request to deploy");

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);

        Solution solution = this.ummClient.getSolutions(jsonObject.getString("solutionUuid")).get(0);

        Task task = new Task();
        task.setUuid(jsonObject.getString("taskUuid"));
        task.setUserLogin(userLogin);
        task.setTaskName(solution.getName());
        task.setTaskType("模型部署");
        task.setTaskStatus("等待调度");
        task.setTaskProgress(0);
        task.setTargetUuid(solution.getUuid());  // 约定部署实例的targetUuid与solution的uuid一致
        task.setStartDate(Instant.now());

        try {
            this.ummClient.createTask(task);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }

        JSONObject taskCommand = new JSONObject();
        taskCommand.put("taskType", "ucumos-deploy");
        taskCommand.put("taskUuid", task.getUuid());
        taskCommand.put("isPublic", jsonObject.getBoolean("public"));

        try {
            kafkaProducer.send("async-task-topic", taskCommand.toJSONString());
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }

        return ResponseEntity.ok().build();
    }

}
