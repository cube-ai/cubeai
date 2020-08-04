package com.wyy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;


@Service
public class AbilityService {
    private static final Logger log = LoggerFactory.getLogger(AbilityService.class);
    public AbilityService() {
    }

    public ResponseEntity<String> apiGateway(String url, String requestBody, MultiValueMap<String,String> requestHeader) {
        log.debug("Start API forwarding");

        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, requestHeader);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8"))); // 直接使用RestTemplate的POST方法时，字符串默认使用“ISO-8859-1”编码，需要转换
            ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
            return ResponseEntity.status(response.getStatusCodeValue()).body(response.getBody());
        } catch(HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

}
