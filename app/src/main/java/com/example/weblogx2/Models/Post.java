package com.example.weblogx2.Models;

import android.net.Uri;

import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey;
    private String image;
    private String description;
    private String userID;
    private String userImage;
    private Object timestamp;
    private String userName;

    public Post(String image, String description, String userID, String userImage, String userName) {
        this.image = image;
        this.description = description;
        this.userID = userID;
        this.userImage = userImage;
        this.timestamp = ServerValue.TIMESTAMP;
        this.userName = userName;
    }

    public Post()
    {

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
// Getters

    public String getPostKey() {
        return postKey;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserImage() {
        return userImage;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    // Setters

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
