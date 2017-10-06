package com.yelona.pojo;

import android.graphics.drawable.Drawable;
import android.provider.Telephony;

/**
 * Created by PC2 on 18-Mar-17.
 */

public class MainCategory {


    String categoryName;
    String imageUrl;
    String parentid;
    Drawable img;
    String categoryId;


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }


    public MainCategory(String categoryId, String categoryName, String imageUrl, String parentid) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.parentid = parentid;
    }

    public MainCategory(String categoryId, String categoryName, String imageUrl, String parentid, Drawable img) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.parentid = parentid;
        this.img = img;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }


}
