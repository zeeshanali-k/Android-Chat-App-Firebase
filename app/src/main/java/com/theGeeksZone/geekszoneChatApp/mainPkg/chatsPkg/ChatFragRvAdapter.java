package com.theGeeksZone.geekszoneChatApp.mainPkg.chatsPkg;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.theGeeksZone.geekszoneChatApp.BR;
import com.theGeeksZone.geekszoneChatApp.dataModels.ChatModel;
import com.theGeeksZone.geekszoneChatApp.databinding.ChatFragmentMainRvLayoutBinding;
import com.theGeeksZone.geekszoneChatApp.databinding.ChatFragmentPhotoRvLayoutBinding;

import java.util.List;

public class ChatFragRvAdapter extends RecyclerView.Adapter<ChatFragRvAdapter.BindingViewHolder> {

    private List<ChatModel> chatModelList;
    private final int TYPE_TEXT=1;
    private final int TYPE_IMAGE=2;
    private final int TYPE_DOC=3;
    private String currentUserUid;


    public ChatFragRvAdapter(List<ChatModel> chatModelList,String currentUserUid){
        this.chatModelList=chatModelList;
        this.currentUserUid = currentUserUid;
    }

    @NonNull
    @Override
    public ChatFragRvAdapter.BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewDataBinding viewDataBinding=null;

        if (viewType==TYPE_TEXT){
            viewDataBinding=ChatFragmentMainRvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()));
        }
        else if (viewType==TYPE_IMAGE){
            viewDataBinding= ChatFragmentPhotoRvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()));
        }
        return new BindingViewHolder(viewDataBinding);
    }

    @Override
    public int getItemViewType(int position) {

        if (chatModelList.get(position).getMessage_type().equals("text")){

            return 1;
        }
        else if (chatModelList.get(position).getMessage_type().equals("image")){
            return 2;
        }
        else if (chatModelList.get(position).getMessage_type().equals("doc")){
            return 3;
        }

        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatFragRvAdapter.BindingViewHolder holder, int position) {

            holder.viewDataBinding.setVariable(BR.chatModelObj,chatModelList.get(position));
            if(chatModelList.get(position).getMember_uid().equals(currentUserUid))
                holder.viewDataBinding.setVariable(BR.isReceiver,true);
            else holder.viewDataBinding.setVariable(BR.isReceiver,false);
            holder.viewDataBinding.executePendingBindings();
    }

    public void updateList(List<ChatModel> chatModelList){
        this.chatModelList.clear();
        this.chatModelList.addAll(chatModelList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    static class BindingViewHolder  extends RecyclerView.ViewHolder{
        private ViewDataBinding viewDataBinding;
        public BindingViewHolder(@NonNull ViewDataBinding itemView) {
            super(itemView.getRoot());
            viewDataBinding=itemView;
        }
    }
}
