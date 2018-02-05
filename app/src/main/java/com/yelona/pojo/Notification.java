package com.yelona.pojo;

/**
 * Created by Satish Gadde on 19-07-2016.
 */
public class Notification {

    String id;
    String title;
    String message;
    String date;


    public Notification(String id, String title, String message, String date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }






}
