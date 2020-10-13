package com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.CallingConstants;
import com.theGeeksZone.geekszoneChatApp.dataModels.ValuesKeysConstants;
import com.theGeeksZone.geekszoneChatApp.databinding.FragmentUsersListBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UsersListFragment extends Fragment {

    private FragmentUsersListBinding usersListBinding;

    private UsersFragViewModel usersFragViewModel;
    private String currentUserFcmToken;


    public UsersListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register((UsersListFragment)this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        usersListBinding = FragmentUsersListBinding
                .inflate(inflater, container, false);
        usersListBinding.setLifecycleOwner(requireActivity());
        return usersListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //instantiating users list view model
        usersFragViewModel = new ViewModelProvider
                .AndroidViewModelFactory(requireActivity().getApplication())
                .create(UsersFragViewModel.class);
        //setting live data in layout
        usersListBinding.setUserDataList(usersFragViewModel.getUsersLiveData());
        getCurrentUserInfo();
    }

    private void getCurrentUserInfo() {
        FirebaseFirestore.getInstance().collection(ValuesKeysConstants.USERS_COLLECTION_NAME)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        currentUserFcmToken=task.getResult().getString(ValuesKeysConstants.FCM_TOKEN);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserCallClick(Map<String,String> callData){
        if (!callData.get(ValuesKeysConstants.FCM_TOKEN).equals(currentUserFcmToken)){
            //launch outgoing call activity
            usersFragViewModel.callUser(callData.get(CallingConstants.CALL_TYPE_KEY),
                    callData.get(ValuesKeysConstants.FCM_TOKEN),
                    requireActivity().getApplicationContext());
        }
        else {
            Toast.makeText(requireActivity().getApplicationContext(),"You can't call yourself!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_search_menu,menu);

        MenuItem menuItem=menu.findItem(R.id.users_frag_menu_search_user);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                UsersListFragRvAdapter rvAdapter= (UsersListFragRvAdapter) usersListBinding
                        .usersListFragRv.getAdapter();

                if (rvAdapter!=null){
                    rvAdapter.getFilter().filter(s);
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}