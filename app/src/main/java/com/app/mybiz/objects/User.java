package com.app.mybiz.objects;



import com.app.mybiz.PreferenceKeys;

import java.util.HashMap;

/**
 * Created by hannashulmah on 11/12/2016.
 */
public class User {

    public String mName = " ";
    public String mEmail = " ";
    public String mUid = " ";
    public boolean containsService = false;
    public boolean online = true;
    public String profileUrl = PreferenceKeys.PERSONAL_DEFAULT_PROFILE_URL;
    public String openedAs = "service";
    public Location location = new Location();
    public Location locationGps = new Location();
    public Location lastLocation = new Location();

    public HashMap<String, String> devices = new HashMap<>();
    public HashMap<String, Service> services = new HashMap<>();//
    public HashMap<String, Service> favorites = new HashMap<>();//
    public HashMap<String, ChatSummary> chats = new HashMap<>();//
    public HashMap<String, Tenders> myTenders = new HashMap<>();
    public boolean addressInserted;

    public User() {
    }

    public User(String mName) {
        this.mName = mName;

    }

    public User(String mName, String mEmail) {
        this.mEmail = mEmail;
        this.mName = mName;

    }

    public User( String mName,String mEmail, String mUid, String profileUrl, boolean containsService) {
        this.mEmail = mEmail;
        this.mName = mName;
        this.mUid = mUid;
        this.profileUrl = profileUrl;
        this.containsService = containsService;

    }

    public User( String mName,String mEmail, String mUid, String device) {
        this.devices = new HashMap<>();
        this.mEmail = mEmail;
        this.mName = mName;
        this.mUid = mUid;
        services = new HashMap<>();
        HashMap <String, String> a = this.getDevices();
        a.put(device, device);
        this.setDevices(a);
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public HashMap<String, Service> getServices() {
        return services;
    }

    public void setServices(HashMap<String, Service> services) {
        this.services = services;
    }

    public boolean isContainsService() {
        return containsService;
    }

    public void setContainsService(boolean service) {
        containsService = service;
    }

    public HashMap<String, ChatSummary> getChats() {
        return chats;
    }

    public void setChats(HashMap<String, ChatSummary> chats) {
        this.chats = chats;
    }

    public HashMap<String, String> getDevices() {
        return devices;
    }

    public void setDevices(HashMap<String, String> devices) {
        devices = devices;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public HashMap<String, Service> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, Service> favorites) {
        this.favorites = favorites;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMap<String, Tenders> getMyTenders() {
        return myTenders;
    }

    public void setMyTenders(HashMap<String, Tenders> myTenders) {
        this.myTenders = myTenders;
    }

    public String getOpenedAs() {
        return openedAs;
    }

    public void setOpenedAs(String openedAs) {
        this.openedAs = openedAs;
    }

    @Override
    public String toString() {
        return "User{" +
                "chats=" + chats +
                ", mName='" + mName + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mUid='" + mUid + '\'' +
                ", containsService=" + containsService +
                ", online=" + online +
                ", profileUrl='" + profileUrl + '\'' +
                ", openedAs='" + openedAs + '\'' +
                ", location=" + location +
                ", devices=" + devices +
                ", services=" + services +
                ", favorites=" + favorites +
                ", myTenders=" + myTenders +
                '}';
    }

    public boolean isAddressInserted() {
        return addressInserted;
    }

    public void setAddressInserted(boolean addressInserted) {
        this.addressInserted = addressInserted;
    }
}
