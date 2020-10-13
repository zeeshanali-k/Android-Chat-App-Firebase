package com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theGeeksZone.geekszoneChatApp.MainActivity;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.FragmentEmailVerificationBinding;


public class EmailVerificationFrag extends Fragment implements View.OnClickListener,
EmailVerificationBSD.onVerificationPasswordEnterd{

    private FragmentEmailVerificationBinding emailVerificationBinding;
    private NavController navController;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;


    public EmailVerificationFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        emailVerificationBinding = FragmentEmailVerificationBinding
                .inflate(inflater, container, false);

        return emailVerificationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        emailVerificationBinding.emailVerfSignOutBtn.setOnClickListener(this);
        emailVerificationBinding.appVerfResendBtn.setOnClickListener(this);
        emailVerificationBinding.verifyEmailVerificationBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.email_verf_sign_out_btn){
                emailVerificationBinding.setIsLoading(true);
                signTheUserOut();
        }
        else if (view.getId()==R.id.app_verf_resend_btn){
            emailVerificationBinding.setIsLoading(true);
            if (currentUser!=null) {
                currentUser.sendEmailVerification()
                        .addOnSuccessListener(aVoid -> {
                           Snackbar.make(emailVerificationBinding.getRoot(),
                                   "Verification email sent!",Snackbar.LENGTH_SHORT)
                                   .show();
                           emailVerificationBinding.setIsLoading(false);
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(emailVerificationBinding.getRoot(),
                                    e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                                    .show();
                            emailVerificationBinding.setIsLoading(false);
                        });
            }
        }
        else if (view.getId()==R.id.verify_email_verification_btn){
            EmailVerificationBSD emailVerificationBSD=
                    new EmailVerificationBSD(this);
            emailVerificationBSD.show(requireActivity().getSupportFragmentManager(),
                    "bsd");
        }

    }

    private void signTheUserOut() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!=null) {
            FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                    .document(user.getUid()).update(ValuesKeysConstants.FCM_TOKEN,null)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            firebaseAuth.signOut();
                            navController.navigate(R.id.action_emailVerificationFrag_to_appLoginFragment);
                        }
                    });
        }
        else {
            firebaseAuth.signOut();
            navController.navigate(R.id.action_emailVerificationFrag_to_appLoginFragment);
        }
    }

    @Override
    public void onPasswordEntered(String password) {
        emailVerificationBinding.setIsLoading(true);
        FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                .document(currentUser.getUid()).update(ValuesKeysConstants.FCM_TOKEN,null);
        firebaseAuth.signOut();
        firebaseAuth.signInWithEmailAndPassword(currentUser.getEmail(),password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser().isEmailVerified()){
                        requireActivity()
                                .startActivity(new Intent(requireActivity().getApplicationContext()
                                        , MainActivity.class));
                        requireActivity().finish();
                    }
                    else{
                        Snackbar.make(emailVerificationBinding.getRoot()
                        ,"Email isn't verified!",Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    emailVerificationBinding.setIsLoading(false);
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(emailVerificationBinding.getRoot(),
                            e.getLocalizedMessage(),Snackbar.LENGTH_LONG)
                            .show();
                    emailVerificationBinding.setIsLoading(false);
                });
    }
}