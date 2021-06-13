package com.example.triporganizer.Models;

public class Task {

    private String taskID;
    private String task_str;
    private boolean isDone = false;

    public Task() {
    }

    public Task(String taskID, String task_str, boolean isDone) {
        this.taskID = taskID;
        this.task_str = task_str;
        this.isDone = isDone;
    }

    public Task(String taskID, String task_str) {
        this.taskID = taskID;
        this.task_str = task_str;
    }


    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTask_str() {
        return task_str;
    }

    public void setTask_str(String task_str) {
        this.task_str = task_str;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
