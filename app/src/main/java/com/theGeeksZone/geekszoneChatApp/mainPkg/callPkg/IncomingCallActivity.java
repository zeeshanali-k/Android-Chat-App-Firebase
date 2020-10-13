package com.theGeeksZone.geekszoneChatApp.mainPkg.callPkg;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.backend.FcmRetrofit;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.Data;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.FcmCallData;
import com.theGeeksZone.geekszoneChatApp.databinding.ActivityIncommingCallBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityIncommingCallBinding callBinding;
    private String TAG = "tagg";
    private FcmRetrofit fcmRetrofit;
    private User callerInfo;
    private String currentUserFcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callBinding = DataBindingUtil
                .setContentView(this,R.layout.activity_incomming_call);

        String callerUID = getIntent().getExtras().getString(CallingConstants.CALLER_UID_KEY);
        String callType = getIntent().getExtras().getString(CallingConstants.CALL_TYPE_KEY);

        setUpRetrofit();
        getAndSetCallDetails(callerUID, callType);
        getCurrentUserFcmToken();

    }

    private void getCurrentUserFcmToken(){
        //fcm token of current user is required for jitsi meet room

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!uid.equals("")){
            FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                    .document(uid).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult()!=null){
                            currentUserFcmToken=task.getResult()
                                    .getString(ValuesKeysConstants.FCM_TOKEN);
                        }
                    });
        }

    }


    private void setUpRetrofit() {
        fcmRetrofit = FcmRetrofit.retrofit.create(FcmRetrofit.class);
    }

    private FcmCallData getFcmCallObj(String callType) {
        FcmCallData fcmCallData=new FcmCallData();
        fcmCallData.setData(new Data(null,callType));
        fcmCallData.setTo(callerInfo.getFcm_token());
        fcmCallData.setNotification(null);
        return fcmCallData;
    }

    private void getAndSetCallDetails(String callerUID, String callType) {
        if (callerUID!=null && callType!=null){
            FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                    .document(callerUID)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult()!=null){
                            callerInfo = task.getResult().toObject(User.class);
                            callBinding.setCaller(callerInfo);
                        }
                        else {
                            Log.d(TAG, "onCreate: "+task.getException().getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.incoming_call_accept_btn){
            Call<JSONObject> jsonObjectCall = fcmRetrofit.sendCallInvitation(ValuesKeysConstants.getFcmHeaders(getApplicationContext()),
                    getFcmCallObj(CallingConstants.CALL_ACCEPTED));
            jsonObjectCall.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                    if (response.isSuccessful()){
                        startJitsiMeetCall();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(),t.getLocalizedMessage(),Toast.LENGTH_LONG)
                            .show();
                }
            });

        }
        else if (view.getId()==R.id.incoming_call_cancel_btn){
            Call<JSONObject> jsonObjectCall = fcmRetrofit.sendCallInvitation(ValuesKeysConstants.getFcmHeaders(getApplicationContext()),
                    getFcmCallObj(CallingConstants.CALL_CANCELLED));
            jsonObjectCall.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                    if (response.isSuccessful()){
                        finish();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(),t.getLocalizedMessage(),Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

    }

    private void startJitsiMeetCall() {
        //creating url
        URL url=null;
        try {
            url = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url!=null) {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions
                    .Builder().setServerURL(url)
                    .setWelcomePageEnabled(false)
                    .setRoom(currentUserFcmToken)
                    .build();
            JitsiMeetActivity.launch(getApplicationContext(),options);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseReceived(Data data){
        if (data.getCall_type().equals(CallingConstants.CALL_CANCELLED)){
            Toast.makeText(getApplicationContext(),"Call Declined",Toast.LENGTH_SHORT)
                    .show();;
            finish();
        }
    }
}