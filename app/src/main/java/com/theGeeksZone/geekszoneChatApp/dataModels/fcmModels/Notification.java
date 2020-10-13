package com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels;

public class Notification {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private String body;
    private String icon;

    public Notification(){

    }

    public Notification(String title, String body, String icon) {
        this.title = title;
        this.body = body;
        this.icon = icon;
    }
}
