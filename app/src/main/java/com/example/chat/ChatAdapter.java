package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.chat.gson.UserAccount;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private List<Chat> mChatList;
    private Context mContext;
    UserAccount userAccount;
    static class ViewHolder extends RecyclerView.ViewHolder{//viewholder相当于列表中对应的子项
        TextView friendName;
        ImageView friendAvatarId;
        TextView leftChat;
        TextView rightChat;
        TextView meName;
        ImageView meAvatarId;
        ImageView rightImage;
        ImageView leftImage;
        View chatview;
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        public ViewHolder(View view){
            super(view);
            chatview=view;
            friendName=(TextView)view.findViewById(R.id.friend_name); //这里id和friend_item中的重了
            //貌似不会有影响，但还是有疑问
            friendAvatarId=(ImageView)view.findViewById(R.id.friend_Avatar);
            leftChat=(TextView)view.findViewById(R.id.left_chat);
            rightChat=(TextView)view.findViewById(R.id.right_chat);
            rightImage=(ImageView)view.findViewById(R.id.right_image);
            leftImage=(ImageView)view.findViewById(R.id.left_image);
            meName=(TextView)view.findViewById(R.id.me_name);
            meAvatarId=(ImageView)view.findViewById(R.id.me_Avatar);
            leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout=(LinearLayout) view.findViewById(R.id.right_layout);
        }
    }

    public ChatAdapter(List<Chat> mChatList, UserAccount userAccount){//可以用构造函数来实现与活动的交互
        this.mChatList=mChatList;
        this.userAccount=userAccount;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item,parent,false);
        final ChatAdapter.ViewHolder viewholder=new ChatAdapter.ViewHolder(view);
        if (mContext==null){
            mContext=parent.getContext();
        }
        viewholder.chatview.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
            }
        });//该方法另一目的是创建点击事件
        return viewholder;//主要目的是返回一个viewholder给onBindViewHolder，这个viewholder由之前自定义的
    }
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder viewHolder, int position){//在这里决定子项的
        //文字和图片的填入
        Chat chat=mChatList.get(position);
        if (chat.getType()==Chat.TYPE_RECEIVED){
            if(chat.getImage()==null){
                viewHolder.friendName.setText(chat.getFriend().getName());
                Glide.with(mContext).load("http://"+ HttpUtil.localIP+":8000/user"
                        +"/"+chat.getFriend().getAvatarId()).into(viewHolder.friendAvatarId);
                //viewHolder.friendAvatarId.setImageResource(chat.getFriend().getAvatarId());
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftChat.setVisibility(View.VISIBLE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.GONE);
                viewHolder.leftChat.setText(chat.getChatText());
            }else{
                Glide.with(mContext).load("http://"+ HttpUtil.localIP+":8000/user"
                        +"/"+userAccount.getAvatar())
                        .into(viewHolder.meAvatarId);
                viewHolder.meName.setText(userAccount.getName());
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftChat.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.VISIBLE);
                viewHolder.rightImage.setVisibility(View.GONE);
                Glide.with(mContext).load(chat.getImage())
                        .into(viewHolder.leftImage);

            }

        }else if(chat.getType()==Chat.TYPE_SENT){
            if(chat.getImage()==null){
                Glide.with(mContext).load("http://"+ HttpUtil.localIP+":8000/user"
                        +"/"+userAccount.getAvatar())
                        .into(viewHolder.meAvatarId);
                viewHolder.meName.setText(userAccount.getName());
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightChat.setVisibility(View.VISIBLE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.GONE);
                viewHolder.rightChat.setText(chat.getChatText());
            }else {
                Glide.with(mContext).load("http://"+ HttpUtil.localIP+":8000/user"
                        +"/"+userAccount.getAvatar())
                        .into(viewHolder.meAvatarId);
                viewHolder.meName.setText(userAccount.getName());
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightChat.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.GONE);
                viewHolder.rightImage.setVisibility(View.VISIBLE);
                Log.d("chat.getImage()",chat.getImage());
                Glide.with(mContext).load(chat.getImage())
                       .into(viewHolder.rightImage);
            }
        }
    }
    @Override
    public int getItemCount(){
        return mChatList.size();
    }
}
