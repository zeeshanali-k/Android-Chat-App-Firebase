package com.theGeeksZone.geekszoneChatApp.mainPkg.customBindingAdapters;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.theGeeksZone.geekszoneChatApp.R;
import com.theGeeksZone.geekszoneChatApp.dataModels.ChatModel;
import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.mainPkg.chatsPkg.ChatFragRvAdapter;
import com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg.UsersListFragRvAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DBindingCustomAdapters {

    @BindingAdapter("chatFragRvSetter")
    public static void setUpChatFragRv(RecyclerView chatFragRv, List<ChatModel> chatModelList){

        if (chatModelList==null || chatModelList.size()==0)
            return;

            ChatFragRvAdapter rvAdapter= (ChatFragRvAdapter) chatFragRv.getAdapter();
            if (rvAdapter==null){
                //getting current signed user uid for setting the message layout in adapter
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //setting layout manager on recycler view
                chatFragRv.setLayoutManager(new LinearLayoutManager(chatFragRv.getContext()));
                //setting and instantiating adapter
                rvAdapter = new ChatFragRvAdapter(chatModelList,currentUserUid);
                chatFragRv.setAdapter(rvAdapter);
                chatFragRv.smoothScrollToPosition(chatFragRv.getAdapter().getItemCount());
            }
            else{
                rvAdapter.updateList(chatModelList);
                chatFragRv.smoothScrollToPosition(chatFragRv.getAdapter().getItemCount());
            }

    }

    @BindingAdapter("usersListFragRvSetter")
    public static void setUpUsersListRv(RecyclerView usersListRv, List<User> usersList){
        if (usersList==null || usersList.size()==0) return;

        UsersListFragRvAdapter usersListFragRvAdapter=
                (UsersListFragRvAdapter) usersListRv.getAdapter();
        if (usersListFragRvAdapter==null){
            //setting layout manager
            usersListRv
                    .setLayoutManager(new LinearLayoutManager(usersListRv.getContext()
                            ,RecyclerView.VERTICAL,false));
            //setting adapter
            usersListFragRvAdapter=new UsersListFragRvAdapter(usersList);
            usersListRv.setAdapter(usersListFragRvAdapter);
        }
        else usersListFragRvAdapter.updateUserList(usersList);
    }

    @BindingAdapter("imageViewSetter")
    public static void setRvImageView(CircleImageView rvImageView, String imageUrl){
        if (imageUrl==null || imageUrl.equals("")) return;
        Picasso.get()
                .load(Uri.parse(imageUrl))
                .placeholder(R.drawable.profile_place_holder)
                .into(rvImageView);

    }

    @BindingAdapter("simpleImageViewSetter")
    public static void setRvSimpleImageView(ImageView rvImageView, String imageUrl){
        if (imageUrl==null || imageUrl.equals("")) return;
        Picasso.get()
                .load(Uri.parse(imageUrl))
                .placeholder(R.drawable.profile_place_holder)
                .into(rvImageView);

    }

}
