package com.developer.darren.com.rentalsystem.model;

public class Product {
    private String productID, category, features;

    public Product() {
    }

    public Product(String productID, String category, String features) {
        this.productID = productID;
        this.category = category;
        this.features = features;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}
