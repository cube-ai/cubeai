package com.wyy.service;

import com.alibaba.fastjson.JSONObject;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;


@Service()
public class KafkaConsumer {

    @Value("${spring.cloud.stream.kafka.binder.zk-nodes}")
    private String zkNodes;

    private final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final DeployService deployService;
    private final LifeCircleManagementService lifeCircleManagementService;

    KafkaConsumer(DeployService deployService, LifeCircleManagementService lifeCircleManagementService) {
        this.deployService = deployService;
        this.lifeCircleManagementService = lifeCircleManagementService;
    }

    @PostConstruct
    private void init() {
        createKafkaTopics();
    }

    private void createKafkaTopics() {
        log.info("--------------------------------------------------------------------------");
        log.info("-----------------------Begin to create Kafka topics-----------------------");

        ZkUtils zkUtils = ZkUtils.apply(zkNodes + ":2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());

        if (!AdminUtils.topicExists(zkUtils, "async-task-topic")) {
            AdminUtils.createTopic(zkUtils, "async-task-topic", 1, 1,  new Properties(), new RackAwareMode.Enforced$());
        }

        zkUtils.close();

        log.info("-----------------------Kafka topics created-------------------------------");
        log.info("--------------------------------------------------------------------------");
    }

    //对应命令行： /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka_IP:9092 --topic topic-name --from-beginning
    @KafkaListener(topics = {"async-task-topic"}, group = "umu")
    @Async
    public void receive(String message) {
        log.info("Kafka received message='{}'", message);

        JSONObject taskCommand = JSONObject.parseObject(message);
        String taskType = taskCommand.getString("taskType");
        String taskUuid = taskCommand.getString("taskUuid");

        if (null != taskUuid) {
            if (taskType.equals("ucumos-deploy")) {
                this.deployService.deploy(taskUuid, taskCommand.getBoolean("isPublic"));
            }
            if (taskType.equals("ucumos-lcm-stop")) {
                this.lifeCircleManagementService.stop(taskUuid, taskCommand.getString("deploymentUuid"));
            }
        }
    }

}
