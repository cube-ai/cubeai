package com.wyy.service;

import com.wyy.client.AuthorizedFeignClient;
import com.wyy.dto.Message;
import com.wyy.dto.MessageDraft;
import com.wyy.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AuthorizedFeignClient(name = "uaa")
public interface UaaClient {

    @RequestMapping(value ="/api/messages/send", method = RequestMethod.POST)
    ResponseEntity<Message> sendMessage(@RequestBody Message message);

    @RequestMapping(value ="/api/messages/multicast", method = RequestMethod.POST)
    ResponseEntity<Void> sendMulticastMessage(@RequestBody MessageDraft messageDraft);

    @RequestMapping(value ="/api/users/{login}", method = RequestMethod.GET)
    ResponseEntity<UserDTO> getUser(@PathVariable(value="login") String login);

}
