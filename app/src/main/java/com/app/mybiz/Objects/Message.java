package com.app.mybiz.Objects;

/**
 * Created by hannashulmah on 11/12/2016.
 */
public class Message {
    public String senderName = "";
    public String key;
    public String content = "";
    public long dateSent=0; //this represents the time receivers phone has gotten message
    public long dateCreated = System.currentTimeMillis();
    public long dateUploaded = 0;
    public long dateVisible = 0;
    public String fromUid = "";
    public String toUid = "";
    public String pictureUrl;
    public boolean amIService;
    public Service senderService = new Service();

    public Message() {
    }

    public Message(String content, String fromUid, String toUid,String senderName) {
        this.content = content;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.senderName = senderName;

    }

    public Message(String content, long dateCreated) {
        this.content = content;
        this.dateCreated = dateCreated;
    }

    public Message(String content, long dateCreated, long dateSent, long dateUploaded) {
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateSent = dateSent;
        this.dateUploaded = dateUploaded;
    }

    public Message(String content, long dateCreated, long dateSent, long dateUploaded, long dateVisible) {
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateSent = dateSent;
        this.dateUploaded = dateUploaded;
        this.dateVisible = dateVisible;
    }


    public long getDateVisible() {
        return dateVisible;
    }

    public void setDateVisible(long dateVisible) {
        this.dateVisible = dateVisible;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public long getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(long dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public boolean isAmIService() {
        return amIService;
    }

    public void setAmIService(boolean amIService) {
        this.amIService = amIService;
    }

    public Service getSenderService() {
        return senderService;
    }

    public void setSenderService(Service senderService) {
        this.senderService = senderService;
    }

    @Override
    public String toString() {
        return "Message{" +
                "amIService=" + amIService +
                ", senderName='" + senderName + '\'' +
                ", key='" + key + '\'' +
                ", content='" + content + '\'' +
                ", dateSent=" + dateSent +
                ", dateCreated=" + dateCreated +
                ", dateUploaded=" + dateUploaded +
                ", dateVisible=" + dateVisible +
                ", fromUid='" + fromUid + '\'' +
                ", toUid='" + toUid + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", senderService=" + senderService +
                '}';
    }

//    @Override
//    public String toString() {
//        return "Message{" +
//                "content='" + content + '\'' +
//                ", senderName='" + senderName + '\'' +
//                ", key='" + key + '\'' +
//                ", dateSent=" + dateSent +
//                ", dateCreated=" + dateCreated +
//                ", dateUploaded=" + dateUploaded +
//                ", dateVisible=" + dateVisible +
//                ", fromUid='" + fromUid + '\'' +
//                ", toUid='" + toUid + '\'' +
//                '}';
//    }
}
