package com.wyy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ModelCacheDemon {
    @Autowired
    SolutionService solutionService;

    private static final Logger logger = LoggerFactory.getLogger(ModelCacheDemon.class);

    /**
     * This method is Execute the Cron job i.e triggers for every 1 hours
     *
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    public void ExecuteForHour() throws Exception {
        logger.debug("Scheduled on ExecuteForHour() Begin ");
        try {
            solutionService.updateModelCache();
        } catch (Exception e) {
            logger.error("Interrupted Exception Occured in ExecuteForHour() {}", e);
            throw new Exception("Failed for Creating the Cache");
        }
        logger.debug("Scheduled on ExecuteForHour() End ");
    }
}
