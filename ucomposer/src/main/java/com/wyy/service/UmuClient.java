package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@AuthorizedFeignClient(name = "umu")
public interface UmuClient {

    @RequestMapping(value ="/api/get-file-text", method = RequestMethod.GET)
    JSONObject getFileText(@RequestParam(value = "url") String url);

}
