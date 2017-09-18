package com.example.chat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.icu.text.LocaleDisplayNames;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import com.example.chat.gson.UserAccount;
import com.example.chat.image.BitmapUtils;
import com.example.chat.image.ImageSize;
import com.example.chat.service.ChatService;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;
import java.util.logging.LoggingMXBean;

import static android.support.v7.recyclerview.R.attr.layoutManager;
import static java.security.AccessController.getContext;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Chat chat=null;
    private List<Chat> mChatList;
    private Context mContext;
    private Activity activity;
    private ChatActivity chatActivity;
    UserAccount userAccount;
    Bitmap bitmap;

    class Bean {
        private ImageView imageView;
        private ChatAdapter.ViewHolder viewHolder;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public ViewHolder getViewHolder() {
            return viewHolder;
        }

        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {//viewholder相当于列表中对应的子项
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

    public ChatAdapter(List<Chat> mChatList, UserAccount userAccount, Activity activity){//可以用构造函数来实现与活动的交互
        this.mChatList=mChatList;
        this.userAccount=userAccount;
        this.activity=activity;
        chatActivity=(ChatActivity)activity;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item,parent,false);
        final ChatAdapter.ViewHolder viewholder=new ChatAdapter.ViewHolder(view);
        if (mContext==null){
            mContext=parent.getContext();
        }
        viewholder.leftImage.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
                int position=viewholder.getAdapterPosition();//这里获取位置的方法重要！！
                Chat imageChat=mChatList.get(position);
                Intent intent=new Intent(parent.getContext(), ImageActivity.class);
                intent.putExtra("imagePath",imageChat.getImage());
                mContext.startActivity(intent,ActivityOptions
                        .makeSceneTransitionAnimation(activity,view,"share").toBundle());//共享元素动画
            }
        });//该方法另一目的是创建点击事件
        viewholder.rightImage.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
                int position=viewholder.getAdapterPosition();//这里获取位置的方法重要！！
                Chat imageChat=mChatList.get(position);
                Intent intent=new Intent(parent.getContext(), ImageActivity.class);
                intent.putExtra("imagePath",imageChat.getImage());
                mContext.startActivity(intent,ActivityOptions
                        .makeSceneTransitionAnimation(activity,view,"share").toBundle());//共享元素动画
            }
        });//该方法另一目的是创建点击事件
        return viewholder;//主要目的是返回一个viewholder给onBindViewHolder，这个viewholder由之前自定义的
    }
    @Override
    public void onBindViewHolder(final ChatAdapter.ViewHolder viewHolder, final int position){//在这里决定子项的
        //文字和图片的填入
        chat=mChatList.get(position);
        Log.d("position","position="+position);
        if (chat.getType()==Chat.TYPE_RECEIVED){
            if(chat.getImage()==null){
                Log.d("RECEIVE","文字加载中");
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
                viewHolder.friendName.setText(chat.getFriend().getName());
                Glide.with(mContext).load("http://"+ HttpUtil.localIP+":8000/user"
                        +"/"+chat.getFriend().getAvatarId()).into(viewHolder.friendAvatarId);
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftChat.setVisibility(View.GONE);
                viewHolder.leftImage.setVisibility(View.VISIBLE);
                viewHolder.rightImage.setVisibility(View.GONE);
                Log.d(position+" width"," "+chat.getImageWidth());
                Log.d(position+" height"," "+chat.getImageHeight());
                final ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
                imageLP.width = chat.getImageWidth();
                imageLP.height = chat.getImageHeight();
                Log.d("else position"+position+" width:"," "+chat.getImageWidth());
                Log.d("else position"+position+" Height:"," "+chat.getImageHeight());
                viewHolder.leftImage.setLayoutParams(imageLP);
                Glide.with(mContext).load(chat.getImage())//内部有子线程
                        //.placeholder(R.mipmap.ic_launcher)使用占位图会导致一直为占位图
                        .thumbnail( 0.1f )
                        .into(viewHolder.leftImage);
            }
        }else if(chat.getType()==Chat.TYPE_SENT){
            if(chat.getImage()==null){
                Log.d("SENT","文字加载中");
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
                Log.d(position+" width"," "+chat.getImageWidth());
                Log.d(position+" height"," "+chat.getImageHeight());
                Log.d("chat.getImageWidth();","!!!!!!!!");
                final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                imageLP.width = chat.getImageWidth();
                imageLP.height = chat.getImageHeight();
                Log.d("else position"+position+" width:"," "+chat.getImageWidth());
                Log.d("else position"+position+" Height:"," "+chat.getImageHeight());
                viewHolder.rightImage.setLayoutParams(imageLP);
                Glide.with(mContext).load(chat.getImage())//内部有子线程
                        //.placeholder(R.drawable.apple_pic)使用占位图会导致一直为占位图
                        .thumbnail( 0.1f )
                        .into(viewHolder.rightImage);
            }
        }
    }



    @Override
    public int getItemCount(){
        return mChatList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.rightImage.getDrawable()!=null){
            holder.rightImage.setImageResource(R.drawable.x);
        }
        if(holder.leftImage.getDrawable()!=null){
            holder.leftImage.setImageResource(R.drawable.x);
        }
    }
}