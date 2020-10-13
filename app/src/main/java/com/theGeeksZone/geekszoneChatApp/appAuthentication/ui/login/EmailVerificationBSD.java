package com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.databinding.BsdEmailVerficationLayoutBinding;

public class EmailVerificationBSD extends BottomSheetDialogFragment implements View.OnClickListener{

    private BsdEmailVerficationLayoutBinding bsdBindind;
    private onVerificationPasswordEnterd onVerificationPasswordEnterd;

    public EmailVerificationBSD(onVerificationPasswordEnterd onVerificationPasswordEnterd){
        this.onVerificationPasswordEnterd=onVerificationPasswordEnterd;
    }



    public interface onVerificationPasswordEnterd{
        void onPasswordEntered(String password);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bsdBindind = BsdEmailVerficationLayoutBinding.inflate(inflater, container, false);
        return bsdBindind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bsdBindind.bsdPasswordSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.bsd_password_submit_btn){
            String password=bsdBindind.emailVerfPasswordBsdEtv.getText().toString();
            if (password.equals("")){
                bsdBindind.emailVerificationBsdTv.setError("Field cannot be empty!");
            }
            else if (password.length()<6){
                bsdBindind.emailVerificationBsdTv.setError("Password is less than 6 characters.");
            }
            else {
                this.onVerificationPasswordEnterd.onPasswordEntered(password);
                dismiss();
            }
        }
    }
}
