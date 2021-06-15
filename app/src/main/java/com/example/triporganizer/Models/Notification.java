package com.example.triporganizer.Models;

public class Notification {

    private String notificationID;
    private String forUserID;
    private String title;
    private String text;
    private long timestamp;

    public Notification(){

    }

    public Notification(String notificationID, String forUserID, String title, String text, long timestamp) {
        this.notificationID = notificationID;
        this.forUserID = forUserID;
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getForUserID() {
        return forUserID;
    }

    public void setForUserID(String forUserID) {
        this.forUserID = forUserID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
