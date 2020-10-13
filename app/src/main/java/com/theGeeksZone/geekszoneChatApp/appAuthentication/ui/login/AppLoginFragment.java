package com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.theGeeksZone.geekszoneChatApp.MainActivity;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.FragmentAppLoginBinding;


public class AppLoginFragment extends Fragment
        implements View.OnClickListener{

    private NavController navController;
    private FragmentAppLoginBinding appLoginBinding;

    private FirebaseAuth firebaseAuth;
    private boolean emailNotVerified;
    private CollectionReference usersCollection;


    public AppLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser!=null && !currentUser.isEmailVerified()){
            emailNotVerified=true;
        }
        usersCollection =FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appLoginBinding = FragmentAppLoginBinding.inflate(inflater,container,false);
        return appLoginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        if (emailNotVerified) {
            navController.navigate(R.id.action_appLoginFragment_to_emailVerificationFrag);
        }
        appLoginBinding.appLoginRegisterBtn.setOnClickListener(this);
        appLoginBinding.mainLoginBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.app_login_register_btn){
            navController.navigate(R.id.action_appLoginFragment_to_signUpFragment);
        }
        else if (view.getId()==R.id.main_login_btn){
            if(!appLoginBinding.getIsSigningIn()){
                appLoginBinding.setIsSigningIn(true);
                appLoginBinding.authenticationFailErrorTv.setVisibility(View.INVISIBLE);
                setUpPerfsOnClick();
                appLoginBinding.authenticationFailErrorTv.setVisibility(View.GONE);
                String email=appLoginBinding.loginEmailEtv.getText().toString();
                String password=appLoginBinding.loginPasswordEtv.getText().toString();
                if (email.equals("") || password.equals("")){

                    appLoginBinding.appLoginEmailInputField.setErrorEnabled(true);
                    appLoginBinding.appLoginPasswordInputField.setErrorEnabled(true);
                    if (email.equals("")){
                        appLoginBinding.setIsSigningIn(false);
                        appLoginBinding.appLoginEmailInputField.setError("Field cannot be empty!");
                    }
                    if (password.equals("")){
                        appLoginBinding.setIsSigningIn(false);
                        appLoginBinding.appLoginPasswordInputField.setError("Field cannot be empty!");
                    }
                    appLoginBinding.loginEmailEtv.setClickable(true);
                    appLoginBinding.loginPasswordEtv.setClickable(true);
                }
                else
                    verifyUser(email,password);
            }
        }
    }

    private void setUpPerfsOnClick() {
        appLoginBinding.loginEmailEtv.clearFocus();
        appLoginBinding.loginPasswordEtv.clearFocus();
        appLoginBinding.appLoginEmailInputField.setErrorEnabled(false);
        appLoginBinding.appLoginPasswordInputField.setErrorEnabled(false);
    }

    private void verifyUser(String email,String password) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser !=null){
                        logTheUserIn(firebaseUser);
                    }
                })
                .addOnFailureListener(e -> {
                    appLoginBinding.authenticationFailErrorTv.setVisibility(View.VISIBLE);
                    appLoginBinding.setIsSigningIn(false);
                    appLoginBinding.appLoginEmailInputField.setErrorEnabled(true);
                    appLoginBinding.appLoginPasswordInputField.setErrorEnabled(true);
                    appLoginBinding.authenticationFailErrorTv.setText(e.getLocalizedMessage());
                });
    }

    private void logTheUserIn(FirebaseUser firebaseUser) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        usersCollection.document(firebaseUser.getUid())
                                .update(ValuesKeysConstants.FCM_TOKEN,
                                        task.getResult().getToken())
                                .addOnSuccessListener(aVoid -> {
                                    if (!firebaseUser.isEmailVerified()) {
                                        navController
                                                .navigate(R.id.action_appLoginFragment_to_emailVerificationFrag);
                                    } else {
                                        startActivity(new Intent(requireActivity().getApplicationContext()
                                                , MainActivity.class));
                                        requireActivity().finish();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Snackbar.make(appLoginBinding.getRoot(),
                                            "Error Signing In! " + e.getLocalizedMessage(),
                                            Snackbar.LENGTH_LONG).show();
                                });
                    }
                });
    }
}