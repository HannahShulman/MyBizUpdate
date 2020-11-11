package com.app.mybiz.Objects;

import java.util.HashMap;

/**
 * Created by hannashulmah on 05/01/2017.
 */

public class Category {

    public  String title ="";
    public String profileImage = "";
    public HashMap<String, Category> subCategoroies = new HashMap<>();
    public HashMap<String, Tenders> categoryTenders = new HashMap<>();

    public Category() {
    }

    public Category(String title, String imagePath) {
        this.title = title;
        profileImage = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public HashMap<String, Category> getSubCategoroies() {
        return subCategoroies;
    }

    public void setSubCategoroies(HashMap<String, Category> subCategoroies) {
        this.subCategoroies = subCategoroies;
    }

    public HashMap<String, Tenders> getCategoryTenders() {
        return categoryTenders;
    }

    public void setCategoryTenders(HashMap<String, Tenders> categoryTenders) {
        this.categoryTenders = categoryTenders;
    }

}
