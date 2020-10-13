package com.theGeeksZone.geekszoneChatApp.appUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theGeeksZone.geekszoneChatApp.MainActivity;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login.AuthenticationActivity;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.mainPkg.callPkg.IncomingCallActivity;

public class SplashGeeksZone extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_geeks_zone);


        if ( getIntent().getExtras() !=null && getIntent().getExtras().getString(CallingConstants.CALL_TYPE_KEY)!=null &&
                !getIntent().getExtras().getString(CallingConstants.CALL_TYPE_KEY).equals(CallingConstants.CALL_CANCELLED)){
            Bundle fcmNotificationExtras = getIntent().getExtras();
            String callType = fcmNotificationExtras.getString(CallingConstants.CALL_TYPE_KEY);
            String receiverToken=fcmNotificationExtras.getString(CallingConstants.RECEIVER_TOKEN_KEY);
            Intent intent=new Intent(this, IncomingCallActivity.class);
            intent.putExtra(CallingConstants.CALL_TYPE_KEY,callType);
            intent.putExtra(CallingConstants.RECEIVER_TOKEN_KEY,receiverToken);
            startActivity(intent);
            finish();
        }
        else {
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            takeActionOnAuthCompleted(currentUser);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void takeActionOnAuthCompleted(FirebaseUser currentUser) {
        new Handler(getMainLooper()).postDelayed(() -> {
            if (currentUser==null || !currentUser.isEmailVerified()){
                startActivity(new Intent(SplashGeeksZone.this, AuthenticationActivity.class));
                finish();
            }
            else {
                startActivity(new Intent(SplashGeeksZone.this, MainActivity.class));
                finish();
            }
        },500);

    }
}