package com.app.mybiz.objects;

import com.firebase.geofire.GeoLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hannashulmah on 22/01/2017.
 */
public class Tenders  implements Serializable{

    public String uid = "";
    public String request = "";
    public String category = "";
    public String subCategory = "";
    public String town = "town";
    public String profileUrl;
    public String requester;
    public GeoLocation location;
    public String key;
    public long expiryTime;
    public HashMap<String, String> followers = new HashMap<>();
    public ArrayList<Double> l = new ArrayList<>();



    public Tenders() {
    }


    public ArrayList<Double> getL() {
        return l;
    }

    public void setL(ArrayList<Double> l) {
        this.l = l;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getExpiryTime() {
        return expiryTime;
    }
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public HashMap<String, String> getFollowers() {
        return followers;
    }

    public void setFollowers(HashMap<String, String> followers) {
        this.followers = followers;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    @Override
    public String toString() {
        return "Tenders{" +
                "category='" + category + '\'' +
                ", uid='" + uid + '\'' +
                ", request='" + request + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", town='" + town + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", requester='" + requester + '\'' +
                ", location=" + location +
                ", key='" + key + '\'' +
                ", expiryTime=" + expiryTime +
                ", followers=" + followers +
                '}';
    }
}
