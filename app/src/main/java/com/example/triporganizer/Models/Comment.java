package com.example.triporganizer.Models;

import java.util.ArrayList;

public class Comment {

    private String user;
    private String tripID;
    private String comment;
    private ArrayList<String> pictures;
    private long timestamp;

    public Comment() {

    }

    public Comment(String user, String tripID, String comment, ArrayList<String> pictures, long timestamp) {
        this.user = user;
        this.tripID = tripID;
        this.comment = comment;
        this.pictures = pictures;
        this.timestamp = timestamp;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
