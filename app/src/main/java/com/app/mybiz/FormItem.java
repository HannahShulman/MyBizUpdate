package com.app.mybiz;

/**
 * Created by hannashulmah on 27/02/2017.
 */
public class FormItem {
    String url;
    String text1;
    String subText;
    boolean isComplete;


    public FormItem(boolean imageExists, String url) {
        isComplete = imageExists;
        this.url = url;
    }

    public FormItem(boolean isComplete, String subText, String text1) {
        this.isComplete = isComplete;
        this.subText = subText;
        this.text1 = text1;
    }


    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
