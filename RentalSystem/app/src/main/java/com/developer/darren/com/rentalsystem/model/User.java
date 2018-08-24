package com.developer.darren.com.rentalsystem.model;

public class User {
    private String name;
    private String userID;
    boolean prime;

    public User() {
    }

    public User(String name, String userID, boolean prime) {
        this.name = name;
        this.userID = userID;
        this.prime = prime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isPrime() {
        return prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }
}
