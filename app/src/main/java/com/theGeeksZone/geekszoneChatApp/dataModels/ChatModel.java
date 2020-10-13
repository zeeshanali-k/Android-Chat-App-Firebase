package com.theGeeksZone.geekszoneChatApp.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatModel implements Parcelable {
    public ChatModel(String full_name, String member_uid, String the_message_string, String member_dp_url, String message_type, String message_time_stamp,String attachment_url) {
        this.full_name = full_name;
        this.member_uid = member_uid;
        this.the_message_string = the_message_string;
        this.member_dp_url = member_dp_url;
        this.message_type = message_type;
        this.message_time_stamp = message_time_stamp;
        this.attachment_url=attachment_url;
    }

    public ChatModel() {
    }

    private String full_name;
    private String member_uid;
    private String the_message_string;
    private String member_dp_url;
    private String message_type;
    private String attachment_url;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getMember_uid() {
        return member_uid;
    }

    public void setMember_uid(String member_uid) {
        this.member_uid = member_uid;
    }

    public String getThe_message_string() {
        return the_message_string;
    }

    public void setThe_message_string(String the_message_string) {
        this.the_message_string = the_message_string;
    }

    public String getMember_dp_url() {
        return member_dp_url;
    }

    public void setMember_dp_url(String member_dp_url) {
        this.member_dp_url = member_dp_url;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMessage_time_stamp() {
        return message_time_stamp;
    }

    public void setMessage_time_stamp(String message_time_stamp) {
        this.message_time_stamp = message_time_stamp;
    }

    public static Creator<ChatModel> getCREATOR() {
        return CREATOR;
    }

    private String message_time_stamp;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.full_name);
        dest.writeString(this.member_uid);
        dest.writeString(this.the_message_string);
        dest.writeString(this.member_dp_url);
        dest.writeString(this.message_type);
        dest.writeString(this.message_time_stamp);
        dest.writeString(this.attachment_url);
    }

    protected ChatModel(Parcel in) {
        this.full_name = in.readString();
        this.member_uid = in.readString();
        this.the_message_string = in.readString();
        this.member_dp_url = in.readString();
        this.message_type = in.readString();
        this.message_time_stamp=in.readString();
        this.attachment_url=in.readString();
    }

    public static final Parcelable.Creator<ChatModel> CREATOR = new Parcelable.Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel source) {
            return new ChatModel(source);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }
}
