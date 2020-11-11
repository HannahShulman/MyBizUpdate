package com.app.mybiz;

import com.app.mybiz.Objects.Service;

import java.util.HashMap;

/**
 * Created by hannashulmah on 07/01/2017.
 */
public class ChatSummary {
    Service otherContactService;
    Long lastMessageTime= Long.valueOf(0);
    Long invertLastMessageTime = Long.valueOf(0);
    Long lastMessageRecieved;
    Long lastMessageSeen = Long.valueOf(0);
    String myName, otherName, myId, otherId, type, otherPhoneNumber, otherServiceKey;
    String lastMessageContent;
    String lastMessageSenderId;
    String lastSender;
    boolean isContactService;
    int unseenMessageNumber;
    String isTyping = "false";
    String contactProfileUrl = "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/defaultProfile%2F%D7%AA%D7%9E%D7%95%D7%A0%D7%AA%20%D7%A4%D7%A8%D7%95%D7%A4%D7%99%D7%9C%20%D7%A2%D7%99%D7%A1%D7%A7%D7%99%20%D7%96%D7%9E%D7%A0%D7%99%D7%AA.png?alt=media&token=ab524e31-5662-40b2-a7ad-29031185ff7f";
    HashMap<String, String> unseenMessages;

    //rrrr


    public ChatSummary() {
    }

//    public ChatSummary(Long lastMessageTime, String myName, String otherName) {
//        this.lastMessageTime = lastMessageTime;
//        this.invertLastMessageTime = -1 * lastMessageTime;
//        this.myName = myName;
//        this.otherName = otherName;
//    }

    public Long getInvertLastMessageTime() {
        return invertLastMessageTime;
    }

    public void setInvertLastMessageTime(Long invertLastMessageTime) {
        this.invertLastMessageTime = invertLastMessageTime;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public int getUnseenMessageNumber() {
        return unseenMessageNumber;
    }

    public void setUnseenMessageNumber(int unseenMessageNumber) {
        this.unseenMessageNumber = unseenMessageNumber;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getContactProfileUrl() {
        return contactProfileUrl;
    }

    public void setContactProfileUrl(String contactProfileUrl) {
        this.contactProfileUrl = contactProfileUrl;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getIsTyping() {
        return isTyping;
    }

    public void setIsTyping(String isTyping) {
        this.isTyping = isTyping;
    }

    public String getType() {
        return type;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(HashMap<String, String> unseenMessages) {
        this.unseenMessages = unseenMessages;
    }

    public Long getLastMessageSeen() {
        return lastMessageSeen;
    }

    public void setLastMessageSeen(Long lastMessageSeen) {
        this.lastMessageSeen = lastMessageSeen;
    }

    public Long getLastMessageRecieved() {
        return lastMessageRecieved;
    }

    public void setLastMessageRecieved(Long lastMessageRecieved) {
        this.lastMessageRecieved = lastMessageRecieved;
    }

    public Service getOtherContactService() {
        return otherContactService;
    }

    public void setOtherContactService(Service otherContactService) {
        this.otherContactService = otherContactService;
    }

    public boolean isContactService() {
        return isContactService;
    }

    public void setContactService(boolean contactService) {
        isContactService = contactService;
    }

    public String getOtherPhoneNumber() {
        return otherPhoneNumber;
    }

    public void setOtherPhoneNumber(String otherPhoneNumber) {
        this.otherPhoneNumber = otherPhoneNumber;
    }

    public String getOtherServiceKey() {
        return otherServiceKey;
    }

    public void setOtherServiceKey(String otherServiceKey) {
        this.otherServiceKey = otherServiceKey;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }
}
