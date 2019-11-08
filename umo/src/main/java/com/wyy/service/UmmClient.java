package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.domain.Artifact;
import com.wyy.domain.CompositeSolutionMap;
import com.wyy.domain.Solution;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AuthorizedFeignClient(name = "umm")
public interface UmmClient {

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutions(@RequestParam(value = "active") String active,
                                @RequestParam(value = "uuid") String uuid,
                                @RequestParam(value = "name") String name,
                                @RequestParam(value = "authorLogin") String authorLogin,
                                @RequestParam(value = "modelType") String modelType,
                                @RequestParam(value = "toolkitType") String toolkitType,
                                @RequestParam(value = "publishStatus") String publishStatus,
                                @RequestParam(value = "publishRequest") String publishRequest,
                                @RequestParam(value = "subject1") String subject1,
                                @RequestParam(value = "subject2") String subject2,
                                @RequestParam(value = "subject3") String subject3,
                                @RequestParam(value = "tag") String tag,
                                @RequestParam(value = "filter") String filter,
                                @RequestParam(value = "page") int page,
                                @RequestParam(value = "size") int size);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutions(@RequestParam(value = "authorLogin") String authorLogin,
                                @RequestParam(value = "filter") String filter,
                                Pageable pageable);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutionsByUuid(@RequestParam(value = "uuid") String uuid);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutionsByUser(@RequestParam(value = "authorLogin") String authorLogin,
                                      @RequestParam(value = "publishStatus") String publishStatus);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getPublishedSolutions(@RequestParam(value = "publishStatus") String publishStatus);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.POST)
    ResponseEntity<Void> createSolution(@RequestBody Solution solution);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.POST)
    ResponseEntity<Void> createArtifact(@RequestBody Artifact artifact);

    @RequestMapping(value ="/api/artifacts/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteArtifact(@PathVariable(value = "id")  Long id);

    @RequestMapping(value ="/api/artifacts", method = RequestMethod.GET)
    List<Artifact> getArtifacts(@RequestParam(value = "solutionUuid") String solutionUuid, @RequestParam(value = "type") String type);

    @RequestMapping(value ="/api/composite-solution-maps", method = RequestMethod.GET)
    ResponseEntity<List<CompositeSolutionMap>> getAllCompositeSolutionMaps();

    @RequestMapping(value ="/api/composite-solution-maps", method = RequestMethod.POST)
    ResponseEntity<CompositeSolutionMap> createCompositeSolutionMap(@RequestBody CompositeSolutionMap compositeSolutionMap);

    @RequestMapping(value ="/api/composite-solution-maps/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteCompositeSolutionMap(@PathVariable(value = "id") Long id);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.PUT)
    ResponseEntity<Void> updateSolution(@RequestBody Solution solution);

    @RequestMapping(value ="/api/solutions/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteSolution(@PathVariable(value = "id") Long id);


}
