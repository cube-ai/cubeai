package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.domain.Artifact;
import com.wyy.domain.Deployment;
import com.wyy.domain.Document;
import com.wyy.domain.Solution;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@AuthorizedFeignClient(name = "umm")
public interface UmmClient {

    @RequestMapping(value ="/model/ability", method = RequestMethod.GET)
    List<Deployment> getDeployment(@RequestParam(value = "uuid") String uuid);

    @RequestMapping(value ="/api/deployments", method = RequestMethod.GET)
    List<Deployment> getDeploymentsBySolutionUuid(@RequestParam(value = "solutionUuid") String solutionUuid,
                                                  @RequestParam(value = "isPublic") boolean isPublic,
                                                  @RequestParam(value = "status") String status);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.GET)
    List<Artifact> getArtifacts(@RequestParam(value = "solutionUuid") String solutionUuid, @RequestParam(value = "type") String type);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutionsByUuid(@RequestParam(value = "uuid") String uuid);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutionsByName(@RequestParam(value = "name") String name,
                                      @RequestParam(value = "publishStatus") String publishStatus);


    @RequestMapping(value ="/api/documents", method = RequestMethod.GET)
    List<Document> getApiExamples(@RequestParam(value = "solutionUuid") String solutionUuid,
                                      @RequestParam(value = "name") String name);
}
