package com.wyy.service;

import com.wyy.dto.Message;
import com.wyy.dto.MessageDraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final UaaClient uaaClient;

    public MessageService(UaaClient uaaClient) {
        this.uaaClient = uaaClient;
    }

    public void sendMessage(String receiver, String subject, String content, String url, Boolean urgent) {

        Message message = new Message();
        message.setReceiver(receiver);
        message.setSubject(subject);
        message.setContent(content);
        message.setUrl(url);
        message.setUrgent(urgent);

        this.uaaClient.sendMessage(message);

        log.info("Sent a message with subject = {}", subject);
    }

    public void sendMulticastMessage(List<String> receivers, String subject, String content, String url, Boolean urgent) {

        Message message = new Message();
        MessageDraft messageDraft = new MessageDraft();
        message.setSubject(subject);
        message.setContent(content);
        message.setUrl(url);
        message.setUrgent(urgent);

        messageDraft.setMessage(message);
        messageDraft.setReceivers(receivers);

        this.uaaClient.sendMulticastMessage(messageDraft);

        log.info("Sent a message with subject = {}", subject);
    }

}

