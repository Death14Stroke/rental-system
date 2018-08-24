package com.developer.darren.com.rentalsystem.model;

public class Advertisement {
    private String description, userID, adID, category,productName,url;
    private float price;

    public Advertisement() {
    }

    public Advertisement(String description, String userID, String adID, String category, String productName, float price, String url) {
        this.description = description;
        this.userID = userID;
        this.adID = adID;
        this.category = category;
        this.productName = productName;
        this.price = price;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}

