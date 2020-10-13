package com.theGeeksZone.geekszoneChatApp.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String full_name;
    private String email;
    private String fcm_token;
    private String photo_url;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public User(String username, String full_name, String email, String fcm_token,
                String photo_url) {
        this.username = username;
        this.full_name = full_name;
        this.email = email;
        this.fcm_token=fcm_token;
        this.photo_url=photo_url;

    }

    public User(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.full_name);
        dest.writeString(this.email);
        dest.writeString(this.fcm_token);
        dest.writeString(this.photo_url);
    }

    protected User(Parcel in) {
        this.username = in.readString();
        this.full_name = in.readString();
        this.email = in.readString();
        this.fcm_token = in.readString();
        this.photo_url = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
