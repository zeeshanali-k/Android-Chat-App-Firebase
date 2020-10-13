package com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg;

import com.google.gson.annotations.SerializedName;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.Notification;

public class FcmCallData {

    public FcmCallData(){

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public FcmCallData(String to, Data data, Notification notification) {
        this.to = to;
        this.data = data;
        this.notification = notification;
    }

    @SerializedName("to")
    private String to;
    @SerializedName("data")
    private Data data;
    @SerializedName("notification")
    private Notification notification;
}
