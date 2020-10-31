package com.mybiz;

/**
 * Created by itzikalgrisi on 17/12/2016.
 */

public class Device {
    String serialNumber;
    String pushToken;
    String uid;

    public Device() {
    }

    public Device(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getPushToken() {
        return pushToken;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
