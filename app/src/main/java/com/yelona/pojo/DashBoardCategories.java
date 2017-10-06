package com.yelona.pojo;

/**
 * Created by PC2 on 17-Mar-17.
 */

public class DashBoardCategories {
    String categoryName, categoryId;

    public DashBoardCategories(String categoryName, String categoryId) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


}
