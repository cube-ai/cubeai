package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.dto.Message;
import com.wyy.dto.MessageDraft;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AuthorizedFeignClient(name = "uaa")
public interface UaaClient {

    @RequestMapping(value ="/api/messages/send", method = RequestMethod.POST)
    ResponseEntity<Message> sendMessage(@RequestBody Message message);

    @RequestMapping(value ="/api/messages/multicast", method = RequestMethod.POST)
    ResponseEntity<Void> sendMulticastMessage(@RequestBody MessageDraft messageDraft);

}
