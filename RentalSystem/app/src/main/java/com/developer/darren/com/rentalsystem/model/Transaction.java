package com.developer.darren.com.rentalsystem.model;

public class Transaction {
    private String trID, buyerID, sellerID, adID;
    private float amount;

    public Transaction(String trID, String buyerID, String sellerID, String adID, float amount) {
        this.trID = trID;
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.adID = adID;
        this.amount = amount;
    }

    public Transaction() {
    }

    public String getTrID() {
        return trID;
    }

    public void setTrID(String trID) {
        this.trID = trID;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
