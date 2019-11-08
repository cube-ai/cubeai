package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.domain.Deployment;
import com.wyy.domain.Solution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@AuthorizedFeignClient(name = "umm")
public interface UmmClient {

    @RequestMapping(value ="/model/ability", method = RequestMethod.GET)
    List<Deployment> getDeployment(@RequestParam(value = "uuid") String uuid);

    @RequestMapping(value ="/api/solutions", method = RequestMethod.GET)
    List<Solution> getSolutionsByUuid(@RequestParam(value = "uuid") String uuid);
}
