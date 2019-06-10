package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.domain.Deployment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@AuthorizedFeignClient(name = "umm")
public interface UmmClient {

    @RequestMapping(value ="/model/ability", method = RequestMethod.GET)
    List<Deployment> getDeployment(@RequestParam(value = "uuid") String uuid);
}
