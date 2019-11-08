package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AuthorizedFeignClient(name = "ucomposer")
public interface UComposerClient {

    @RequestMapping(value ="/orchestrate/{solutionUuid}/{modelMethod}", method = RequestMethod.POST)
    ResponseEntity<String> callModelMethod(@PathVariable(value = "solutionUuid") String solutionUuid,
                                         @PathVariable(value = "modelMethod") String modelMethod,
                                         @Valid @RequestBody String requestBody,
                                         @RequestHeader MultiValueMap<String,String> requestHeader);

}
