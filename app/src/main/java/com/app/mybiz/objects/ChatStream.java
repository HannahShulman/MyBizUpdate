package com.app.mybiz.objects;

import java.util.ArrayList;

/**
 * Created by hannashulmah on 11/12/2016.
 */
public class ChatStream {

    public ArrayList<Message> messages;
    public String fromUid;
    public String senderName;
    public String toUserId;
    public String connection;
    public String contactUrl;
    public long lastMessage = 0;


    public ChatStream() {
    }

    public ChatStream(String fromUserId, String toUserId, String fromUserName) {
        this.fromUid = fromUserId;
        this.toUserId = toUserId;
        messages = new ArrayList<>();
        connection = fromUserId+"__"+toUserId;
        this.senderName = fromUserName;

    }

    public ChatStream(String fromUserId, ArrayList messages, String toUserId) {
        this.fromUid = fromUserId;
        this.messages = messages;
        this.toUserId = toUserId;
        connection = fromUserId+"__"+toUserId;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUserId) {
        this.fromUid = fromUserId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }


    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public long getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }
}
