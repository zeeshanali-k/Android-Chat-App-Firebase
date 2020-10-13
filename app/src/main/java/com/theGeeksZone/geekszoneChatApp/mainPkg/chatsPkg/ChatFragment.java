package com.theGeeksZone.geekszoneChatApp.mainPkg.chatsPkg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.FragmentChatBinding;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private static final int ATTACH_GALLERY_IMG_CODE = 1324;
    private static final int ATTACH_CAPTURE_IMG_CODE = 4321;
    private static final String CHAT_IMAGES_STORAGE_ENDPOINT = "images";
    private FragmentChatBinding chatBinding;
    private ChatsViewModel chatsViewModel;
    private boolean areAttachmentsVisible=false;

    //firebase
    private StorageReference storageReference;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageReference = FirebaseStorage.getInstance()
                .getReference(ValuesKeysConstants.CHATS_FB_STORAGE_REF);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatBinding = FragmentChatBinding
                .inflate(inflater, container, false);
        chatBinding.setLifecycleOwner(requireActivity());

        return chatBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatsViewModel = new ViewModelProvider
                .AndroidViewModelFactory(requireActivity().getApplication())
                .create(ChatsViewModel.class);
        chatBinding.setChatLivaData(chatsViewModel.getChatLiveData());
        chatBinding.chatFragSendMessageBtn.setOnClickListener(this);
        setUpUIActions();
    }

    private void setUpUIActions() {
        chatBinding.chatFragAttachImgCaptureBtn.hide();
        chatBinding.chatFragAttachImgGalleryBtn.hide();
        setUpAttachBtnClickListeners();
        setUpChatMainEtvWatcher();
    }

    private void setUpAttachBtnClickListeners() {
        chatBinding.chatFragAttachBtn.setOnClickListener(view -> {
            if (!areAttachmentsVisible) {
                chatBinding.chatFragAttachImgGalleryBtn.show();
                chatBinding.chatFragAttachImgCaptureBtn.show();
                areAttachmentsVisible=true;
            }else {
                chatBinding.chatFragAttachImgGalleryBtn.hide();
                chatBinding.chatFragAttachImgCaptureBtn.hide();
                areAttachmentsVisible=false;
            }
        });
        chatBinding.chatFragAttachImgGalleryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,ATTACH_GALLERY_IMG_CODE);
            chatBinding.chatFragAttachImgGalleryBtn.hide();
            chatBinding.chatFragAttachImgCaptureBtn.hide();
            areAttachmentsVisible=false;
        });
        chatBinding.chatFragAttachImgCaptureBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(intent,"Choose App To Capture Image:")
                    ,ATTACH_CAPTURE_IMG_CODE);
            chatBinding.chatFragAttachImgGalleryBtn.hide();
            chatBinding.chatFragAttachImgCaptureBtn.hide();
            areAttachmentsVisible=false;
        });

    }

    private void setUpChatMainEtvWatcher() {
        chatBinding.chatFragMainMessageEtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (String.valueOf(charSequence).length()>0) {
                    chatBinding.chatFragAttachBtn.hide();
                    chatBinding.chatFragAttachImgCaptureBtn.hide();
                    chatBinding.chatFragAttachImgGalleryBtn.hide();
                    areAttachmentsVisible=false;
                }
                else
                    chatBinding.chatFragAttachBtn.show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.chat_frag_send_message_btn) {
            if (chatBinding.chatFragMainMessageEtv.getText() != null
                    && !chatBinding.chatFragMainMessageEtv.getText().toString().equals("")) {
                //sending message through view model
                chatsViewModel
                        .sendMessage(chatBinding.chatFragMainMessageEtv
                                .getText().toString(),null,
                                ValuesKeysConstants.TYPE_TEXT,requireActivity().getApplicationContext());
                chatBinding.chatFragMainMessageEtv.setText("");
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && requestCode==ATTACH_GALLERY_IMG_CODE){
            //move all this to view model
            Uri selectedImageUri = data.getData();
            if (selectedImageUri!=null) {
                uploadToFirebaseURI(selectedImageUri);
            }

        }
        else if (data!=null && requestCode==ATTACH_CAPTURE_IMG_CODE){

            Bitmap capturedImage= (Bitmap) data.getExtras().get("data");
            if (capturedImage!=null) {

                //instantiating byte array stream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //merging bitmap into stream
                capturedImage.compress(Bitmap.CompressFormat.JPEG, 100,
                        outputStream);
                //getting bytes array from stream
                byte[] byteArray = outputStream.toByteArray();
                //uploading to storage
                uploadToFirebaseByteArray(byteArray);
            }
        }
    }


    private void uploadToFirebaseURI(Uri selectedImageUri) {
        StorageReference child = storageReference.child(CHAT_IMAGES_STORAGE_ENDPOINT+"/" +selectedImageUri
                .getLastPathSegment());
        child.putFile(selectedImageUri)
                .addOnProgressListener(snapshot -> {
                    if (!chatBinding.getAttachmentProgress())
                        chatBinding.setAttachmentProgress(true);
                    chatBinding.chatFragAttachmentUploadProgressBar
                            .setProgress((int)
                                    ((snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100));
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getException()==null){
                        child.getDownloadUrl()
                                .addOnCompleteListener(task1 -> {
                                   if (task1.isSuccessful()){
                                       Uri downloadUri = task1.getResult();
                                       if (downloadUri!=null)
                                       chatsViewModel.sendMessage(null,downloadUri.toString(),
                                               ValuesKeysConstants.TYPE_IMAGE,requireActivity().getApplicationContext());
                                       chatBinding.setAttachmentProgress(false);
                                   }
                                });
                    }
                });
    }
    private void uploadToFirebaseByteArray(byte []selectedImageBA) {
        StorageReference child = storageReference.child(new Date().toString());
        child.putBytes(selectedImageBA)
                .addOnProgressListener(snapshot -> {
                    if (!chatBinding.getAttachmentProgress())
                        chatBinding.setAttachmentProgress(true);
                    chatBinding.chatFragAttachmentUploadProgressBar
                            .setProgress((int)
                                    ((snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100));
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getException()==null){
                        child.getDownloadUrl()
                                .addOnCompleteListener(task1 -> {
                                   if (task1.isSuccessful()){
                                       Uri downloadUri = task1.getResult();
                                       if (downloadUri!=null)
                                       chatsViewModel.sendMessage(null,downloadUri.toString(),
                                               ValuesKeysConstants.TYPE_IMAGE,requireActivity().getApplicationContext());
                                       chatBinding.setAttachmentProgress(false);
                                   }
                                });
                    }
                });
    }
}