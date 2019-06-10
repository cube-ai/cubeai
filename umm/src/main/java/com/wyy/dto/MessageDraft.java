package com.wyy.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 写信时的消息草稿，含收信人列表
 */
public class MessageDraft implements Serializable {

    private Message message;

    private List<String> receivers;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

}
