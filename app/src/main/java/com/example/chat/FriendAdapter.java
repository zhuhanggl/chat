package com.example.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chat.gson.UserAccount;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<Friend> mFriendList;
    private Context mContext;
    private UserAccount userAccount;
    private Activity activity;
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

    public FriendAdapter(List<Friend> mFriendList, UserAccount userAccount, Activity activity){
        //可以用构造函数来实现与活动（碎片）的交互
        this.mFriendList=mFriendList;
        this.userAccount=userAccount;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item,parent,false);
        final ViewHolder viewholder=new ViewHolder(view);
        if (mContext==null){
            mContext=parent.getContext();
        }
        viewholder.friendview.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
                int position=viewholder.getAdapterPosition();//这里获取位置的方法重要！！
                Friend friend=mFriendList.get(position);
                if(activity instanceof FriendChooseActivity){
                    Intent intent=new Intent(parent.getContext(),ChatActivity.class);//这里关于怎么获取
                    // 上下文的方法要注意
                    intent.putExtra("friend",friend);
                    intent.putExtra("user",userAccount);
                    mContext.startActivity(intent);
                }else if (activity instanceof ChatActivity){
                    ChatActivity chatActivity=(ChatActivity)activity;
                    chatActivity.drawerLayout.closeDrawers();
                    Intent intent=new Intent(parent.getContext(),ChatActivity.class);//这里关于怎么获取
                    // 上下文的方法要注意
                    intent.putExtra("friend",friend);
                    intent.putExtra("user",userAccount);
                    mContext.startActivity(intent);
                    chatActivity.finish();
                }
            }
        });
        return viewholder;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,int position){
        Friend friend=mFriendList.get(position);
        Glide.with(mContext).load("http://"+ HttpUtil.localIP+":8000/user" +
                "/"+friend.getAvatarId()).into(viewHolder.AvatarId);
        viewHolder.name.setText(friend.getName());
    }
    @Override
    public int getItemCount(){
        return mFriendList.size();
    }
}
