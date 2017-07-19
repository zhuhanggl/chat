package com.example.chat;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<Friend> mFriendList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView AvatarId;
        TextView name;
        public ViewHolder(View view){
            super(view);
            AvatarId=(ImageView)view.findViewById(R.id.friend_Avatar);
            name=(TextView)view.findViewById(R.id.friend_name);
        }
    }

    public FriendAdapter(List<Friend> mFriendList){
        this.mFriendList=mFriendList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item,parent,false);
        ViewHolder viewholder=new ViewHolder(view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,int position){
        Friend friend=mFriendList.get(position);
        viewHolder.AvatarId.setImageResource(friend.getAvatarId());
        viewHolder.name.setText(friend.getName());
    }
    @Override
    public int getItemCount(){
        return mFriendList.size();
    }
}
