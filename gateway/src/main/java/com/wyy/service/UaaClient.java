package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@AuthorizedFeignClient(name = "uaa")
public interface UaaClient {

    @RequestMapping(value ="/api/verify-codes", method = RequestMethod.POST)
    int validateVerifyCode(@RequestBody Map<String, String> params);

}
