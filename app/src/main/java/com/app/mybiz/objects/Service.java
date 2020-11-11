package com.app.mybiz.objects;


import com.app.mybiz.PreferenceKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hannashulmah on 16/12/2016.
 */
public class Service implements Serializable {


    public String title = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            userUid = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            address = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            town = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            serviceHomeNumber,
            phoneNumber = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            openingHours = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            email = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            password = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            category = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            subcategory = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            additionalInfo = PreferenceKeys.DEFAULT_SERVICE_TITLE,
            shortDescription = PreferenceKeys.DEFAULT_SERVICE_TITLE;


    public boolean isApproved = false;
    public int noReviewers = 0;
    public float averageRating = 0.0f;
    public double avarageQualityService = 0.0, avarageReliability = 0.0, avaragePunctuality = 0.0;
    public HashMap<String, String> devices = new HashMap<>();
    public String profileUrl = PreferenceKeys.DEFAULT_SERVICE_PROFILE;
    public String key = "";
    public boolean service = false;
    public HashMap<String, Comment> reviews;
    public HashMap<String, Specials> offers = new HashMap<>();
    public HashMap<String, Tenders> tenders = new HashMap<>();
    public HashMap<String, String> followers = new HashMap<>();
    public ArrayList<Double> l = new ArrayList<>();

    public ArrayList<Double> getL() {
        return l;
    }

    public void setL(ArrayList<Double> l) {
        this.l = l;
    }

    public Service() {

    }

    public Service(Service other) {
        this.additionalInfo = other.getAdditionalInfo();
        this.address = other.getAddress();
        this.avaragePunctuality = other.getAvaragePunctuality();
        this.avarageQualityService = other.getAvarageQualityService();
        this.avarageReliability = other.getAvarageReliability();
        this.averageRating = other.getAverageRating();
        this.category = other.getCategory();
        devices = other.getDevices();
        this.email = other.getEmail();
        this.followers = other.getFollowers();
//        new HashMap<>();
        this.isApproved = other.isApproved();
        this.key = other.getKey();
        this.noReviewers = other.getNoReviewers();
        this.offers = other.getOffers();
        this.openingHours = other.getOpeningHours();
        this.password = other.getPassword();
        this.phoneNumber = other.getPhoneNumber();
        this.profileUrl = other.getProfileUrl();
        this.reviews =  other.getReviews();
        this.service = other.isService();
        this.shortDescription = other.getShortDescription();
        this.subcategory = other.getSubcategory();
        this.tenders = other.getTenders();
        this.title = other.getTitle();
        this.town = other.getTown();
        this.userUid = other.getUserUid();
    }

    public Service(String title, String userUid, String additionalInfo, String address, String category, String email, String openingHours, String password,
                   String phoneNumber, String profilePicture, boolean service, String shortDescription, String town) {
        this.title = title;
        this.additionalInfo = additionalInfo;
        this.address = address;
        this.category = category;
        this.email = email;
        this.openingHours = openingHours;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.service = service;
        this.shortDescription = shortDescription;
        this.town = town;
        this.userUid = userUid;
    }


    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public boolean isService() {
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public HashMap<String, String> getDevices() {
        return devices;
    }

    public void setDevices(HashMap<String, String> devices) {
        this.devices = devices;
    }

    public HashMap<String, Tenders> getTenders() {
        return tenders;
    }

    public void setTenders(HashMap<String, Tenders> tenders) {
        this.tenders = tenders;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }


    public double getAvarageReliability() {
        return avarageReliability;
    }

    public void setAvarageReliability(double avarageReliability) {
        this.avarageReliability = avarageReliability;
    }

    public double getAvarageQualityService() {
        return avarageQualityService;
    }

    public void setAvarageQualityService(double avarageQualityService) {
        this.avarageQualityService = avarageQualityService;
    }

    public double getAvaragePunctuality() {
        return avaragePunctuality;
    }

    public void setAvaragePunctuality(double avaragePunctuality) {
        this.avaragePunctuality = avaragePunctuality;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashMap<String, Comment> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, Comment> reviews) {
        this.reviews = reviews;
    }

    public int getNoReviewers() {
        return noReviewers;
    }

    public void setNoReviewers(int noReviewers) {
        this.noReviewers = noReviewers;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public HashMap<String, String> getFollowers() {
        return followers;
    }

    public void setFollowers(HashMap<String, String> followers) {
        this.followers = followers;
    }

    public HashMap<String, Specials> getOffers() {
        return offers;
    }

    public void setOffers(HashMap<String, Specials> offers) {
        this.offers = offers;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getServiceHomeNumber() {
        return serviceHomeNumber;
    }

    public void setServiceHomeNumber(String serviceHomeNumber) {
        this.serviceHomeNumber = serviceHomeNumber;
    }

    @Override
    public String toString() {
        return "{"+"\"additionalInfo\":" +"\""+ additionalInfo+"\""+
                ", \"title\":" + "\""+title +"\""+
                ", \"userUid\":" + "\""+userUid +"\""+
                ", \"address\": " + "\""+address +"\""+
                ", \"town\":" +"\""+ town +"\""+
                ", \"serviceHomeNumber\":" +"\""+ serviceHomeNumber +"\""+
                ", \"phoneNumber\":" +"\""+ phoneNumber +"\""+
                ", \"openingHours\":" + "\""+openingHours +"\""+
                ", \"email\": " + "\""+email +"\""+
                ", \"password\":" + "\""+password +"\""+
                ", \"category\":" + "\""+category +"\""+
                ", \"subcategory\":" + "\""+subcategory +"\""+
                ", \"shortDescription\":" + "\""+shortDescription +"\""+
                ", \"isApproved\":" +"\""+ isApproved+ "\""+
                ", \"noReviewers\":" + "\""+noReviewers +"\""+
                ", \"averageRating\":" +"\""+ averageRating +"\""+
                ", \"avarageQualityService\":" + "\""+avarageQualityService +"\""+
                ", \"avarageReliability\":" + "\""+avarageReliability +"\""+
                ", \"avaragePunctuality\":" + "\""+avaragePunctuality +"\""+
                ", \"devices\":" + devices+
                ", \"profileUrl\":" + "\""+profileUrl + "\"" +
                ", \"key\":" + "\""+key +"\""+
                ", \"service\":" +"\""+ service +"\""+
//                ", \"reviews\":" +reviews+
//                ", \"offers\":" +"\""+offers.toString()+"\""+
//                ", \"tenders\":" + tenders +
//                ", \"followers\":" +followers+
                ",\"l\":"+l
                +"}";
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();
        System.out.println("l = "+l);
        try {
            jsonObject.put("additionalInfo",additionalInfo);
            jsonObject.put("title",title);
            jsonObject.put("userUid",userUid);
            jsonObject.put("address",address);
            jsonObject.put("town",town);
            jsonObject.put("serviceHomeNumber", serviceHomeNumber);
            jsonObject.put("phoneNumber",phoneNumber);
            jsonObject.put("openingHours",openingHours);
            jsonObject.put("email",email);
            jsonObject.put("password",password);
            jsonObject.put("category",category);
            jsonObject.put("subcategory",subcategory);
            jsonObject.put("shortDescription",shortDescription);
            jsonObject.put("isApproved",isApproved);
            jsonObject.put("noReviewers",noReviewers);
            jsonObject.put("averageRating", averageRating);
            jsonObject.put("avarageQualityService", avarageQualityService);
            jsonObject.put("avarageReliability", avarageReliability);
            jsonObject.put("avaragePunctuality", avaragePunctuality);
            jsonObject.put("profileUrl", profileUrl);
            jsonObject.put("key", key);
            jsonObject.put("service", service);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public String toJson() {

        return toJsonObject().toString();
    }
}
