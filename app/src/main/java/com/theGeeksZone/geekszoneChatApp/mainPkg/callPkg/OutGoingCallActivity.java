package com.theGeeksZone.geekszoneChatApp.mainPkg.callPkg;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.backend.FcmRetrofit;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.Notification;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.Data;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.FcmCallData;
import com.theGeeksZone.geekszoneChatApp.databinding.ActivityOutGoingCallBinding;

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

public class OutGoingCallActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityOutGoingCallBinding outGoingBinding;
    private String receiverFcmToken;
    private FcmRetrofit fcmRetrofit;
    private String currentUserFullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outGoingBinding = DataBindingUtil
                .setContentView(this,R.layout.activity_out_going_call);
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            setUpRetrofit();
            setUpReceivedData(extras);
        }
    }

    private void setUpRetrofit() {
        fcmRetrofit = FcmRetrofit.retrofit.create(FcmRetrofit.class);
    }

    private void setUpReceivedData(Bundle extras) {

            receiverFcmToken = extras.getString(CallingConstants.RECEIVER_TOKEN_KEY);
            //setting text depending on call type
            String callType = extras.getString(CallingConstants.CALL_TYPE_KEY);
        if (callType.equals(CallingConstants.CALL_TYPE_AUDIO))
            outGoingBinding.setIsAudio(true);
        else outGoingBinding.setIsAudio(false);

        //getting receiver info
        CollectionReference userCollecRef = FirebaseFirestore.getInstance()
                .collection(ValuesKeysConstants.USERS_COLLECTION_NAME);
        userCollecRef
                .whereEqualTo(ValuesKeysConstants.FCM_TOKEN,receiverFcmToken)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null)
                    outGoingBinding.setCaller(task.getResult().toObjects(User.class).get(0));
                });
        //getting current user info
        userCollecRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        currentUserFullname=task.getResult().getString(ValuesKeysConstants.FULL_NAME);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.out_going_call_cancel_btn){
            FcmCallData fcmCallData=new FcmCallData();
            fcmCallData.setData(new Data(null,CallingConstants.CALL_CANCELLED));
            //setting audio or video call string
            String callT=outGoingBinding.getIsAudio()?"audio":"video";

            fcmCallData.setNotification(new Notification("You missed a "+callT+" call from",currentUserFullname,
                    ValuesKeysConstants.FCM_NOTI_ICON));

            fcmCallData.setTo(receiverFcmToken);
            Call<JSONObject> jsonObjectCall = fcmRetrofit.sendCallInvitation(ValuesKeysConstants.getFcmHeaders(getApplicationContext()),
                    fcmCallData);
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
                    .setRoom(receiverFcmToken)
                    .build();
            JitsiMeetActivity.launch(getApplicationContext(),options);
            finish();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFcmResponseReceived(Data data){
        if (data.getCall_type().equals(CallingConstants.CALL_CANCELLED)){
            Toast.makeText(getApplicationContext(),"Call Rejected",Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
        else if (data.getCall_type().equals(CallingConstants.CALL_ACCEPTED)){
            startJitsiMeetCall();
        }
    }
}