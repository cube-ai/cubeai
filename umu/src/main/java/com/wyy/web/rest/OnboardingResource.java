package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Task;
import com.wyy.service.KafkaProducer;
import com.wyy.service.UmmClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.Instant;


/**
 * REST controller for onboarding solution.
 */
@RestController
@RequestMapping("/api")
public class OnboardingResource {

    private final Logger log = LoggerFactory.getLogger(OnboardingResource.class);

    private final UmmClient ummClient;
    private final KafkaProducer kafkaProducer;

    public OnboardingResource(UmmClient ummClient,
                              KafkaProducer kafkaProducer) {
        this.ummClient = ummClient;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * POST  /onboarding/{taskUuid} : onboarding a new solution.
     * @param taskUuid the taskUuid of the onboarding task, same as the new created solutionUuid
     * @return status 200 OK, or 500 Internal server error
     */
    @PostMapping("/onboarding/{taskUuid}")
    @Timed
    public ResponseEntity<Void> onboarding(HttpServletRequest request,
                                           @PathVariable String taskUuid) {
        log.debug("REST request to onboarding");

        String userLogin = request.getRemoteUser();

        if (null == userLogin) {
            return ResponseEntity.status(403).build();
        }

        String basePath = System.getProperty("user.home") + "/tempfile/ucumosmodels/" + userLogin + "/" + taskUuid;
        File filefolder = new File(basePath);
        String[] tmpList = filefolder.list();
        String fileName = null;
        if (null != tmpList) {
            fileName = tmpList[0];
        }

        Task task = new Task();
        task.setUuid(taskUuid);
        task.setUserLogin(userLogin);
        task.setTaskName(fileName);
        task.setTaskType("模型导入");
        task.setTaskStatus("等待调度");
        task.setTaskProgress(0);
        task.setTargetUuid(taskUuid);
        task.setStartDate(Instant.now());

        try {
            this.ummClient.createTask(task);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }

        JSONObject taskCommand = new JSONObject();
        taskCommand.put("taskType", "ucumos-onboarding");
        taskCommand.put("taskUuid", task.getUuid());

        try {
            kafkaProducer.send("async-task-topic", taskCommand.toJSONString());
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Internal server error
        }

        return ResponseEntity.ok().build();
    }

}
