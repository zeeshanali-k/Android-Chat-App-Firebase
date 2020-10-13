package com.theGeeksZone.geekszoneChatApp.mainPkg.userProfilepkg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.FragmentUserProfileBinding;
import com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg.OnProfileRvItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class UserProfileFrag extends Fragment implements View.OnClickListener{

    private static final int DP_REQ_CODE = 8254;
    private FragmentUserProfileBinding userProfileBinding;
    private StorageReference storageReference;
    private DocumentReference usersCollecRef;
    private String currentUserUID;




    public UserProfileFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //getting firebase storage users endpoint ref
        storageReference = FirebaseStorage
                .getInstance()
                .getReference(ValuesKeysConstants.USERS_FB_STORAGE_REF)
                .child(currentUserUID);
        //firestore users collection reference
        usersCollecRef = FirebaseFirestore.getInstance()
                .collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                .document(currentUserUID);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userProfileBinding = FragmentUserProfileBinding.inflate(inflater);
        return userProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userProfileBinding.userProfileProgressBar.hide();
        userProfileBinding.userProfileFragDpIv.setOnClickListener(this);
        usersCollecRef.addSnapshotListener((value, error) -> {
            if (error==null && value!=null){
                userProfileBinding.setUserData(value.toObject(User.class));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.user_profile_frag_dp_iv){
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,DP_REQ_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==DP_REQ_CODE && data!=null){

            Uri uri = data.getData();
            if (uri!=null && uri.getLastPathSegment()!=null) {
                userProfileBinding.userProfileProgressBar.show();
                //setting the file name as the uri's last path segment, in firebase storage
                StorageReference child = storageReference
                        .child(uri.getLastPathSegment());
                child.putFile(uri).addOnCompleteListener(task -> {
                    //checking if upload was successful
                    if (task.isSuccessful() && task.getException()==null){
                        //getting uploaded img url and check with complete listener
                        child.getDownloadUrl().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getException()==null){
                                Uri imgLink = task1.getResult();
                                //if link is valid insert it into firestore
                                if (imgLink!=null) {
                                    usersCollecRef.update(ValuesKeysConstants.PHOTO_URL,
                                            imgLink.toString());
                                    setProfileInChat(imgLink);
                                }

                            }
                        });
                    }
                });

            }
            //if uri is null
            else{
                Snackbar.make(userProfileBinding.getRoot(),"Failed to load image!",
                        Snackbar.LENGTH_SHORT).show();
            }

        }

    }

    private void setProfileInChat(Uri imgLink) {
        //getting chat collection ref
        CollectionReference chatRef = FirebaseFirestore.getInstance()
                .collection(ValuesKeysConstants.CHAT_COLLECTION_NAME);
        //querying chat collection ref
        chatRef.whereEqualTo(ValuesKeysConstants.MEMBER_UID,currentUserUID)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        for (DocumentSnapshot snapshot:documentSnapshots){
                            chatRef.document(snapshot.getString(ValuesKeysConstants.MEMBER_UID))
                                    .update(ValuesKeysConstants.MEMBER_DP_URL,imgLink.toString());
                        }
                    }
                    //hiding the progress bar
                    userProfileBinding
                            .userProfileProgressBar.hide();
                });
    }

}