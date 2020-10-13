package com.theGeeksZone.geekszoneChatApp.backend;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.Data;
import com.theGeeksZone.geekszoneChatApp.mainPkg.callPkg.IncomingCallActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class FcmManager extends FirebaseMessagingService {
    
    private final String TAG="tagg";
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().get(CallingConstants.CALL_TYPE_KEY)!=null){
            String dataCallType = remoteMessage.getData().get(CallingConstants.CALL_TYPE_KEY);
            String dataReceiverToken=remoteMessage.getData().get(CallingConstants.CALLER_UID_KEY);
            if (dataCallType.equals(CallingConstants.CALL_TYPE_AUDIO) ||
                    dataCallType.equals(CallingConstants.CALL_TYPE_VIDEO)){
                Intent intent = new Intent(getApplicationContext(),
                        IncomingCallActivity.class);
                intent.putExtra(CallingConstants.CALLER_UID_KEY,dataReceiverToken);
                intent.putExtra(CallingConstants.CALL_TYPE_KEY,dataCallType);
                getApplicationContext()
                        .startActivity(intent);
            }
            else {
                Map<String, String> data = remoteMessage.getData();
                EventBus.getDefault().post(new Data(data.get(CallingConstants.CALLER_UID_KEY),
                        data.get(CallingConstants.CALL_TYPE_KEY)));
            }
        }

    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
    }

}
