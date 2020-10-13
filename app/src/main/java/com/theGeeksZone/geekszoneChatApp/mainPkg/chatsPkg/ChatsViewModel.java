package com.theGeeksZone.geekszoneChatApp.mainPkg.chatsPkg;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theGeeksZone.geekszoneChatApp.backend.FcmRetrofit;
import com.theGeeksZone.geekszoneChatApp.dataModels.ChatModel;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.FcmMessageNotification;
import com.theGeeksZone.geekszoneChatApp.dataModels.fcmModels.Notification;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsViewModel extends AndroidViewModel {

    private final String TAG="tagg";
    private User currentUserObj;
    private CollectionReference chatCollecRef;
    private MutableLiveData<List<ChatModel>> chatLiveData;
    private FcmRetrofit fcmRetrofit;
    private String currentUserUid;
    private List<String> allUsers;


    public ChatsViewModel(@NonNull Application application) {
        super(application);
        Context applicationContext = application.getApplicationContext();
        //getting chats collection ref to send messages
        chatCollecRef = FirebaseFirestore.getInstance()
                .collection(ValuesKeysConstants.CHAT_COLLECTION_NAME);
        chatCollecRef.addSnapshotListener((value, error) -> {
            if (error==null && value!=null) {
                if (value.getDocuments().size() > 0) {
                    List<ChatModel> chatModelList = new ArrayList<>();
                    for (DocumentSnapshot docSnap : value.getDocuments()) {
                        chatModelList.add(docSnap.toObject(ChatModel.class));
                    }
                    if (chatModelList.size() > 0) {
                        chatLiveData.setValue(chatModelList);
                    }
                }
            }
            else if (error!=null){
                Toast.makeText(applicationContext, error.getLocalizedMessage()
                ,Toast.LENGTH_SHORT).show();
            }
        });

        //getting users collec ref to get user data like dp url
        CollectionReference userCollecRef = FirebaseFirestore.getInstance()
                .collection(ValuesKeysConstants.USERS_COLLECTION_NAME);
        //setting up firebase authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserUid=currentUser.getUid();
        //getting current user data
        userCollecRef.document(currentUserUid)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot!=null) {
                                Log.d(TAG, "ChatsViewModel: not null");
                                currentUserObj=documentSnapshot.toObject(User.class);
                        }
                    }
                });
        //getting all user token for fcm message
        userCollecRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null){
                List<DocumentSnapshot> allUsersDocs = task.getResult().getDocuments();
                if (allUsersDocs.size()>0){
                    //initializing string array containing all users token
                    allUsers=new ArrayList<>();
                    for (DocumentSnapshot snapshot: allUsersDocs) {
                        if (!snapshot.getId().equals(currentUserUid))
                            allUsers.add(snapshot.getString(ValuesKeysConstants.FCM_TOKEN));
                    }

                }
            }
        });

        //setting up retrofit
        fcmRetrofit = FcmRetrofit.retrofit.create(FcmRetrofit.class);

    }

    public void sendMessage(String messageBody, String attachmentUrl, String messageType,Context context){

        //creating {@link ChatModel} object
        ChatModel chatModel=new ChatModel(currentUserObj.getFull_name(),currentUserUid,
                messageBody,currentUserObj.getPhoto_url(),messageType
                ,new SimpleDateFormat("hh:mm aaa",Locale.getDefault())
        .format(new Date()),attachmentUrl);

        //sending fcm message to all users
        Call<JSONObject> jsonObjectCall = fcmRetrofit.sendMessageNotification(ValuesKeysConstants.getFcmHeaders(context),
                new FcmMessageNotification(allUsers,
                        getNotification(messageBody, messageType)));
        jsonObjectCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                if (response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {
                Toast.makeText(context,t.getLocalizedMessage(),Toast.LENGTH_LONG)
                        .show();
            }
        });

        chatCollecRef.document(String.valueOf(new Date().getTime()))
                .set(chatModel);
    }

    private Notification getNotification(String messageBody, String messageType) {
        //if message is not text then set message body to following message:
        if (!messageType.equals(ValuesKeysConstants.TYPE_TEXT)) {
            messageBody= (messageType.equals(ValuesKeysConstants.TYPE_IMAGE)?
                    "sent an image.":"sent a document.");
        }

        return new Notification(currentUserObj.getFull_name(),messageBody, null);
    }

    public LiveData<List<ChatModel>> getChatLiveData(){
        if (chatLiveData==null){
            chatLiveData=new MutableLiveData<>();
        }
        return chatLiveData;
    }
}
