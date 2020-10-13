package com.theGeeksZone.geekszoneChatApp.backend;

import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.FcmMessageNotification;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.FcmCallData;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface FcmRetrofit {

    String BASE_URL = "https://fcm.googleapis.com/fcm/";
    Retrofit retrofit=new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build();


    @POST("send")
    Call<JSONObject> sendMessageNotification(@HeaderMap Map<String,String> headers,
                                             @Body FcmMessageNotification notification);

    @POST("send")
    Call<JSONObject> sendCallInvitation(@HeaderMap Map<String,String> headers,
                            @Body FcmCallData fcmCallData);


}
