package com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg;

public class Data {

    private String call_type;
    private String caller_uid;

    public Data(){

    }

    public String getCaller_uid() {
        return caller_uid;
    }

    public void setCaller_uid(String caller_uid) {
        this.caller_uid = caller_uid;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public Data(String caller_uid, String call_type) {
        this.caller_uid = caller_uid;
        this.call_type = call_type;
    }

}
