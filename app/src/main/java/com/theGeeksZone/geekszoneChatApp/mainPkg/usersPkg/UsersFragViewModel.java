package com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theGeeksZone.geekszoneChatApp.backend.FcmRetrofit;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.Notification;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.Data;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.callMsg.FcmCallData;
import com.theGeeksZone.geekszoneChatApp.mainPkg.callPkg.OutGoingCallActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragViewModel extends AndroidViewModel {

    private String currentUserUid;
    private MutableLiveData<List<User>> usersLiveData;
    private FcmRetrofit fcmRetrofit;
    private User currentUserObj;
    private final String TAG="tagg";



    public UsersFragViewModel(@NonNull Application application) {
        super(application);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //getting realtime users list updates from firestore
        CollectionReference userCollecRef = FirebaseFirestore
                .getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME);

        userCollecRef
                .addSnapshotListener((value, error) -> {
                    if (error==null && value!=null){
                        List<DocumentSnapshot> valueDocuments = value.getDocuments();
                        if(valueDocuments.size()>0){
                            //retrieving all users
                            List<User> usersList=new ArrayList<>();
                            for (DocumentSnapshot snapshot:valueDocuments)
                                usersList.add(snapshot.toObject(User.class));
                            usersLiveData.setValue(usersList);
                        }
                    }
                });
        userCollecRef.document(currentUserUid)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        currentUserObj = task.getResult().toObject(User.class);
                    }
                });



        //creating retrofit instance
        fcmRetrofit = FcmRetrofit.retrofit.create(FcmRetrofit.class);

        //end of main function
    }

    public LiveData<List<User>> getUsersLiveData(){
        if (usersLiveData==null) usersLiveData=new MutableLiveData<>();
        return usersLiveData;
    }

    public void callUser(String callType, String receiverToken, Context context) {
        if (receiverToken==null){
            Toast.makeText(context,"User not available.",Toast.LENGTH_SHORT)
            .show();
            return;
        }
        FcmCallData fcmCallData=new FcmCallData();
        //setting data for notification
        fcmCallData.setData(new Data(currentUserUid,callType));

        //setting notification values
        fcmCallData.setNotification(new Notification(currentUserObj.getFull_name(),"is calling you.",
                ValuesKeysConstants.FCM_NOTI_ICON));
        //setting receiver's token
        fcmCallData.setTo(receiverToken);

        Call<JSONObject> jsonObjectCall = fcmRetrofit.sendCallInvitation(ValuesKeysConstants.getFcmHeaders(context),
                fcmCallData);

        jsonObjectCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                if (response.isSuccessful()){
                    //intent to start outgoing call
                    Intent intent=new Intent(context, OutGoingCallActivity.class);
                    intent.putExtra(CallingConstants.CALL_TYPE_KEY,callType);
                    intent.putExtra(CallingConstants.RECEIVER_TOKEN_KEY,receiverToken);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {
                Toast.makeText(context,"-"+t.getLocalizedMessage(),Toast.LENGTH_LONG)
                        .show();
            }
        });

    }
}
