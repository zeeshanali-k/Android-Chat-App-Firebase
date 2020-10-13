package com.theGeeksZone.geekszoneChatApp.mainPkg.usersPkg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theGeeksZone.geekszoneChatApp.dataModels.User;
import com.theGeeksZone.geekszoneChatApp.databinding.UsersListRvLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersListFragRvAdapter extends RecyclerView.Adapter<UsersListFragRvAdapter.ULRvAdapter> implements Filterable {

    private List<User> usersList;
    //when search view is empty item from this will be added to above for normal behaviour
    private List<User> usersListFull;

    public UsersListFragRvAdapter(List<User> usersList) {
        this.usersList = usersList;
        usersListFull=new ArrayList<>();
        usersListFull.addAll(usersList);
    }
    @NonNull
    @Override
    public ULRvAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UsersListRvLayoutBinding rvLayoutBinding = UsersListRvLayoutBinding
                .inflate(LayoutInflater.from(parent.getContext()));

        return new ULRvAdapter(rvLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ULRvAdapter holder, int position) {

        holder.rvLayoutBinding.setUserData(usersList.get(position));
        holder.rvLayoutBinding
                .setProfileClickListener((OnProfileRvItemClickListener) holder.context);
        holder.rvLayoutBinding.executePendingBindings();

    }

    public void updateUserList(List<User> usersList){
        this.usersList.clear();
        this.usersList.addAll(usersList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class ULRvAdapter extends RecyclerView.ViewHolder{

        private UsersListRvLayoutBinding rvLayoutBinding;
        private Context context;

        public ULRvAdapter(@NonNull UsersListRvLayoutBinding itemView) {
            super(itemView.getRoot());
            rvLayoutBinding=itemView;
            this.context=rvLayoutBinding.getRoot().getContext();
        }
    }

    //search user setup

    @Override
    public Filter getFilter() {
        return filter;
    }

    //creating filter to return from above getFilter
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<User> tempUsersList=new ArrayList<>();
            tempUsersList.clear();
            if (charSequence==null || charSequence.length()==0)
                tempUsersList.addAll(usersListFull);
            else {
                for (User user:usersListFull){
                    if (user.getFull_name().toLowerCase().contains(charSequence)){
                        tempUsersList.add(user);
                    }
                }
            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=tempUsersList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            updateUserList((List<User>) filterResults.values);
        }
    };

}
