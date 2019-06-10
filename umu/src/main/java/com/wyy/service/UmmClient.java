package com.wyy.service;

import com.alibaba.fastjson.JSONObject;
import com.wyy.client.AuthorizedFeignClient;
import com.wyy.domain.*;
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

    @RequestMapping(value ="/api/solutions", method = RequestMethod.POST)
    ResponseEntity<Void> createSolution(@RequestBody Solution solution);

    @RequestMapping(value ="/api/solutions/picture-url", method = RequestMethod.PUT)
    ResponseEntity<Solution> updateSolutionPictureUrl(@RequestBody JSONObject jsonObject);

    @RequestMapping(value ="/api/solutions/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteSolution(@PathVariable(value = "id") Long id);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.POST)
    ResponseEntity<Artifact> createArtifact(@RequestBody Artifact artifact);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.GET)
    List<Artifact> getArtifacts(@RequestParam(value = "solutionUuid") String solutionUuid, @RequestParam(value = "type") String type);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.GET)
    List<Artifact> getAllArtifacts(@RequestParam(value = "solutionUuid") String solutionUuid);

    @RequestMapping(value ="/api/artifacts/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteArtifact(@PathVariable(value = "id") Long id);

    @RequestMapping(value ="/api/documents", method = RequestMethod.POST)
    ResponseEntity<Document> createDocument(@RequestBody Document document);

    @RequestMapping(value ="/api/documents/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteDocument(@PathVariable(value = "id") Long id);

    @RequestMapping(value ="/api/documents/{id}", method = RequestMethod.GET)
    ResponseEntity<Document> getDocument(@PathVariable(value = "id") Long id);


}
