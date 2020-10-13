package com.theGeeksZone.geekszoneChatApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login.AuthenticationActivity;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.ActivityMainBinding;
import com.theGeeksZone.geekszoneChatApp.mainPkg.chatsPkg.ChatFragment;
import com.theGeeksZone.geekszoneChatApp.mainPkg.userProfilepkg.UserProfileFrag;
import com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg.OnProfileRvItemClickListener;
import com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg.UsersListFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnProfileRvItemClickListener {
    private ActivityMainBinding viewDataBinding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DocumentReference userDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDataBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        viewDataBinding.logoutProgressBarMainactivity.hide();
        setUpFirebase();
        setUpUI();


    }

    private void setUpUI() {

        //setting action bar
        setSupportActionBar(viewDataBinding.mainActivityToolbar);

        //setting chat fragment by default
        viewDataBinding.mainActivityBottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_frag_host,new ChatFragment())
                .commit();
        //setting bottom nav bar item click listener
        viewDataBinding.mainActivityBottomNav
                .setOnNavigationItemSelectedListener(item -> {


            if (item.getItemId()==R.id.bbm_chat_btn){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frag_host,new ChatFragment())
                        .commit();
                return true;
            }
            else if (item.getItemId()==R.id.bbm_users_list_btn){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frag_host,new UsersListFragment())
                        .commit();
                return true;
            }
            else if (item.getItemId()==R.id.bbm_user_profile_btn){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frag_host,new UserProfileFrag())
                        .commit();
                return true;
            }

            return false;
        });
    }

    private void setUpFirebase() {
        firebaseAuth= FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        userDoc = FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                .document(currentUser.getUid());
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    userDoc.update(ValuesKeysConstants.FCM_TOKEN,instanceIdResult.getToken());
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
/*

        MenuItem menuItem=menu.findItem(R.id.main_activity_menu_search_user);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                EventBus.getDefault().post(s);
                return false;
            }
        });
*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.main_activity_menu_signout){
            viewDataBinding.logoutProgressBarMainactivity.show();
            setToken();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToken(){
        userDoc.update(ValuesKeysConstants.FCM_TOKEN,null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        firebaseAuth.signOut();
                        viewDataBinding.logoutProgressBarMainactivity.hide();
                        startActivity(new Intent(MainActivity.this,
                                AuthenticationActivity.class));
                        finish();
                    }
                    else {
                        viewDataBinding.logoutProgressBarMainactivity.hide();
                        Snackbar.make(viewDataBinding.getRoot(),
                                "Error Signing Out! Check you internet connection."
                        ,Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
    }

    @Override
    public void onCallClick(String callType, String receiverToken) {
        Map<String,String> callData=new HashMap<>();
        callData.put(ValuesKeysConstants.FCM_TOKEN,receiverToken);
        callData.put(CallingConstants.CALL_TYPE_KEY,callType);
        EventBus.getDefault().post(callData);
    }
}