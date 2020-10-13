package com.theGeeksZone.geekszoneChatApp.dataModels;

import android.content.Context;

import com.theGeeksZone.geekszoneChatApp.R;

import java.util.HashMap;
import java.util.Map;

public class ValuesKeysConstants {
    
    //NOTE: there maybe some keys that are never used in the app.

    //users collection name and data keys
    public static final String USERS_COLLECTION_NAME="users";
    public static final String USERNAME="username";
    public static final String EMAIL="email";
    public static final String FULL_NAME="full_name";
    public static final String FCM_TOKEN="fcm_token";
    public static final String PHOTO_URL ="photo_url";

    //chat group collection name and keys
    public static final String CHAT_COLLECTION_NAME="chats";
    //each document's name in this collection is timestamp
    //full name is also required so above full name field is used
    public static final String MEMBER_DP_URL="member_dp_url";
    public static final String MEMBER_UID="member_uid";
    public static final String THE_MESSAGE_STRING="the_message_string";
    public static final String MESSAGE_TYPE="message_type";
    public static final String MESSAGES_COUNT="messages_count";

    //firebase refs endpoint names
    public static final String USERS_FB_STORAGE_REF = "users";
    public static final String CHATS_FB_STORAGE_REF = "chats";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_DOC = "doc";
    public static final String FCM_NOTI_ICON = null;

    public static Map<String, String> getFcmHeaders(Context context){
        Map<String,String> headersMap=new HashMap<>();
        //update original key from firebase
        headersMap.put("Authorization",context.getString(R.string.fcm_app_key));
        headersMap.put("Content-Type","application/json");
        return headersMap;
    }

}
