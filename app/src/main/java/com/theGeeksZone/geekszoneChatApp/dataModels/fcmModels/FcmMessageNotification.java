package com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FcmMessageNotification {

    public FcmMessageNotification() {
    }

    @SerializedName("registration_ids")
    private List<String> registration_ids;

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public FcmMessageNotification(List<String> registration_ids, Notification notification) {
        this.registration_ids = registration_ids;
        this.notification = notification;
    }

    private Notification notification;

}
