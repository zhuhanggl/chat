package com.example.chat;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private List<Chat> mChatList;
    static class ViewHolder extends RecyclerView.ViewHolder{//viewholder相当于列表中对应的子项
        TextView friendName;
        ImageView friendAvatarId;
        TextView leftChat;
        TextView rightChat;
        TextView meName;
        ImageView meAvatarId;
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
            meName=(TextView)view.findViewById(R.id.me_name);
            meAvatarId=(ImageView)view.findViewById(R.id.me_Avatar);
            leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout=(LinearLayout) view.findViewById(R.id.right_layout);
        }
    }

    public ChatAdapter(List<Chat> mChatList){
        this.mChatList=mChatList;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item,parent,false);
        final ChatAdapter.ViewHolder viewholder=new ChatAdapter.ViewHolder(view);
        viewholder.chatview.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
            }
        });
        return viewholder;
    }
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder viewHolder, int position){//在这里决定子项的
        //文字和图片的填入
        Chat chat=mChatList.get(position);
        if (chat.getType()==Chat.TYPE_RECEIVED){
            viewHolder.friendName.setText(chat.getFriend().getName());
            viewHolder.friendAvatarId.setImageResource(chat.getFriend().getAvatarId());
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftChat.setText(chat.getChatText());
        }else if(chat.getType()==Chat.TYPE_SENT){
            viewHolder.meAvatarId.setImageResource(R.drawable.mango_pic);
            viewHolder.meName.setText("zhuhanggl");
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightChat.setText(chat.getChatText());
        }

    }
    @Override
    public int getItemCount(){
        return mChatList.size();
    }
}
