package com.theGeeksZone.geekszoneChatApp.appAuthentication.ui.login;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.databinding.ActivityAuthenticationBinding;

public class AuthenticationActivity extends AppCompatActivity {

    private ActivityAuthenticationBinding authenticationBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticationBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_authentication);

        setSupportActionBar(authenticationBinding.appAuthenticationToolbar);
        NavController navController = Navigation.findNavController(this, R.id.authentication_activity_frag_host);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId()==R.id.signUpFragment){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
            }
            else getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            authenticationBinding.appAuthenticationToolbar.setTitle(destination.getLabel());
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            onBackPressed();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}