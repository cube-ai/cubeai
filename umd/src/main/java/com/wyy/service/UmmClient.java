package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.domain.Solution;
import com.wyy.domain.Task;
import com.wyy.domain.TaskStep;
import com.wyy.domain.Artifact;
import com.wyy.domain.Deployment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AuthorizedFeignClient(name = "umm")
public interface UmmClient {

    @RequestMapping(value ="/api/tasks", method = RequestMethod.GET)
    List<Task> getTasks(@RequestParam(value = "uuid") String uuid);

    @RequestMapping(value ="/api/tasks", method = RequestMethod.POST)
    ResponseEntity<Task> createTask(@RequestBody Task task);

    @RequestMapping(value ="/api/tasks", method = RequestMethod.PUT)
    ResponseEntity<Task> updateTask(@RequestBody Task task);

    @RequestMapping(value ="/api/task-steps", method = RequestMethod.POST)
    ResponseEntity<TaskStep> createTaskStep(@RequestBody TaskStep taskStep);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutions(@RequestParam(value = "uuid") String uuid);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.GET)
    List<Artifact> getArtifacts(@RequestParam(value = "solutionUuid") String solutionUuid, @RequestParam(value = "type") String type);

    @RequestMapping(value ="/api/deployments", method = RequestMethod.POST)
    ResponseEntity<Void> createDeployment(@RequestBody Deployment deployment);

    @RequestMapping(value ="/api/deployments", method = RequestMethod.PUT)
    ResponseEntity<Deployment> updateDeployment(@RequestBody Deployment deployment);

    @RequestMapping(value ="/api/deployments", method = RequestMethod.GET)
    List<Deployment> getDeployment(@RequestParam(value = "uuid") String uuid);
}
