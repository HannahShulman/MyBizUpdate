package com.app.mybiz.Objects;

import java.io.Serializable;

/**
 * Created by hannashulmah on 22/01/2017.
 */
public class Specials implements Serializable{

    public String description;
    public String offerTitle;
    public String serviceKey;
    public String serviceUid;
    public long expiryDate;
    public String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/default_images%2Fspecial_offer.png?alt=media&token=eb2824dd-20d2-4796-8eb7-502eee3faacd";
    public boolean toPublic = false;
    public String offerKey;
    public String serviceTitle;
    public String serviceTown;
    public String offerCategory;
    public String offerSubCategory;
    public Service offerCreator;

    public Specials() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getServiceUid() {
        return serviceUid;
    }

    public void setServiceUid(String serviceUid) {
        this.serviceUid = serviceUid;
    }

    public boolean isToPublic() {
        return toPublic;
    }

    public void setToPublic(boolean toPublic) {
        this.toPublic = toPublic;
    }

    public String getOfferKey() {
        return offerKey;
    }

    public void setOfferKey(String offerKey) {
        this.offerKey = offerKey;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceTown() {
        return serviceTown;
    }

    public void setServiceTown(String serviceTown) {
        this.serviceTown = serviceTown;
    }

    public String getOfferCategory() {
        return offerCategory;
    }

    public void setOfferCategory(String offerCategory) {
        this.offerCategory = offerCategory;
    }

    public String getOfferSubCategory() {
        return offerSubCategory;
    }

    public void setOfferSubCategory(String offerSubCategory) {
        this.offerSubCategory = offerSubCategory;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public Service getOfferCreator() {
        return offerCreator;
    }

    public void setOfferCreator(Service offerCreator) {
        this.offerCreator = offerCreator;
    }
}
