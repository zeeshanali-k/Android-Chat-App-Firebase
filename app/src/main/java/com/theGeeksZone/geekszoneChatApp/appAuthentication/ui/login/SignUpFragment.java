package com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.iid.FirebaseInstanceId;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment implements View.OnClickListener{

    private FragmentSignUpBinding signUpBinding;
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private CollectionReference usersCollec;
    private boolean alreadyExists;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        usersCollec = FirebaseFirestore.getInstance()
                .collection(ValuesKeysConstants.USERS_COLLECTION_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        signUpBinding = FragmentSignUpBinding
                .inflate(inflater,container,false);
        return signUpBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        signUpBinding.appSignUpMainBtn.setOnClickListener(this);
        signUpBinding.signUpEmailEtv.requestFocus();
        signUpBinding.signUpUsernameEtv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usersCollec.whereEqualTo(ValuesKeysConstants.USERNAME,String.valueOf(charSequence))
                        .get(Source.SERVER)
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.getDocuments().size()>0){
                                signUpBinding.appSignUpUsername.setErrorEnabled(true);
                                signUpBinding.appSignUpUsername.setError("Username already exists!");
                                alreadyExists=true;
                            }
                            else{
                                if(String.valueOf(charSequence).equals("")) signUpBinding
                                        .appSignUpUsername.setHelperText("");
                                else signUpBinding
                                        .appSignUpUsername.setHelperText("Username available.");
                                signUpBinding.appSignUpUsername.setErrorEnabled(false);
                                alreadyExists=false;
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.app_sign_up_main_btn){

            //setting UI related preferences
            signUpBinding.setIsSigningUp(true);
            signUpBinding.signupErrorTv.setVisibility(View.GONE);
            setUpFieldsErrorPerfs(false);
            //getting text from all fields
            String email=signUpBinding.signUpEmailEtv.getText().toString();
            String username=signUpBinding.signUpUsernameEtv.getText().toString();
            String fullname=signUpBinding.signUpFullnameEtv.getText().toString();
            String password=signUpBinding.signUpPasswordEtv.getText().toString();
            String confirmPassword=signUpBinding.signUpConfirmPasswordEtv
                    .getText().toString();
            boolean b = checkInputFields(email,username,fullname,password,confirmPassword);
            if (b){
                signUpUser(email,password,username,fullname);
            }
            else signUpBinding.setIsSigningUp(false);
        }
    }

    private void setUpFieldsErrorPerfs(boolean b) {
        signUpBinding.appSignUpEmail.setErrorEnabled(b);
        signUpBinding.appSignUpPassword.setErrorEnabled(b);
        signUpBinding.appSignUpConfirmPassword.setErrorEnabled(b);
        signUpBinding.appSignUpUsername.setErrorEnabled(b);
        signUpBinding.appSignUpFullname.setErrorEnabled(b);

    }

    private void signUpUser(String email, String password,
                            String username, String fullname) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser!=null){
                        firebaseUser.sendEmailVerification();
                        addUserToFirestore(email,username,fullname,firebaseUser.getUid());
                    }
                })
                .addOnFailureListener(e -> {
                    signUpBinding.signupErrorTv.setText(e.getLocalizedMessage());
                    signUpBinding.signupErrorTv.setVisibility(View.VISIBLE);
                    signUpBinding.setIsSigningUp(false);
                });
    }

    private void addUserToFirestore(String email, String username, String fullname, String uid) {

        FirebaseInstanceId
                .getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                    if (!instanceIdResult.getToken().equals("")){
                        usersCollec.document(uid)
                                .set(new User(username,fullname,email
                                        ,instanceIdResult.getToken(),null));
                        navController
                                .navigate(R.id.action_signUpFragment_to_emailVerificationFrag);
                    }
                });

    }

    private boolean checkInputFields(String email, String username
            , String fullname, String password, String confirmPassword) {
        boolean isAllOk=true;
        setUpFieldsErrorPerfs(true);
        if (email.equals("") || username.equals("") || fullname.equals("")
        || password.equals("") || confirmPassword.equals("")){
            isAllOk=false;
            if (email.equals("")) signUpBinding.appSignUpEmail.setError("Field cannot be empty!");

            //verifying username for empty and already exist
            if (username.equals("")) signUpBinding.appSignUpUsername.setError("Field cannot be empty!");

            if (fullname.equals("")) signUpBinding.appSignUpFullname.setError("Field cannot be empty!");
            if (password.equals("")) signUpBinding.appSignUpPassword.setError("Field cannot be empty!");
            if (confirmPassword.equals("")) signUpBinding.appSignUpConfirmPassword.setError("Field cannot be empty!");
        }
        //checking for user name
        if (alreadyExists){
            isAllOk=false;
            setUpFieldsErrorPerfs(true);
            signUpBinding.appSignUpUsername.setErrorEnabled(true);
            signUpBinding.appSignUpUsername.setError("Username already exists!");
        }

        if (password.length()<6){
            signUpBinding.appSignUpPassword.setError("Password cannot be less than 6 characters.");
        }

        else if (!password.equals(confirmPassword)){
            signUpBinding.appSignUpConfirmPassword.setError("Confirm password must be equal to ");
            isAllOk=false;
        }



        return isAllOk;
    }
}