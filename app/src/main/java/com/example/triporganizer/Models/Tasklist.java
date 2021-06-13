package com.example.triporganizer.Models;

import java.util.ArrayList;

public class Tasklist {

    private String tasklistID;
    private String tripID;
    private String userID;
    private String tripID_userID;
    private String username;
    private ArrayList<Task> tasks;


    public Tasklist(String tasklistID, String tripID, String userID, String tripID_userID, String username, ArrayList<Task> tasks) {
        this.tasklistID = tasklistID;
        this.tripID = tripID;
        this.userID = userID;
        this.tripID_userID = tripID_userID;
        this.username = username;
        this.tasks = tasks;
    }

    public Tasklist() {
    }



    public String getTasklistID() {
        return tasklistID;
    }

    public void setTasklistID(String tasklistID) {
        this.tasklistID = tasklistID;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTripID_userID() {
        return tripID_userID;
    }

    public void setTripID_userID(String tripID_userID) {
        this.tripID_userID = tripID_userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
