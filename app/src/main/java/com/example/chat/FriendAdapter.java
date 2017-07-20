package com.example.chat;

import android.content.Intent;
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
        View friendview;
        public ViewHolder(View view){
            super(view);
            friendview=view;
            AvatarId=(ImageView)view.findViewById(R.id.friend_Avatar);
            name=(TextView)view.findViewById(R.id.friend_name);
        }
    }

    public FriendAdapter(List<Friend> mFriendList){
        this.mFriendList=mFriendList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item,parent,false);
        final ViewHolder viewholder=new ViewHolder(view);
        viewholder.friendview.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
                int position=viewholder.getAdapterPosition();
                Friend friend=mFriendList.get(position);
                Intent intent=new Intent(parent.getContext(),ChatActivity.class);//这里关于怎么获取
                // 上下文的方法要注意
                intent.putExtra("friend",friend);
                parent.getContext().startActivity(intent);
            }
        });
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
