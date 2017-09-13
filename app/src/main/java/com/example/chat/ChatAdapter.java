package com.example.chat;

import android.app.Activity;
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
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;
import java.util.logging.LoggingMXBean;

import static android.support.v7.recyclerview.R.attr.layoutManager;

/**
 * Created by Administrator on 2017/7/19.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    final static int widthMax=400;
    final static int heightMax=400;
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
        viewholder.chatview.setOnClickListener(new View.OnClickListener() {//item的宽度需要为matchparent
            //这样点击空白的地方也可以开启活动
            @Override
            public void onClick(View view) {
            }
        });//该方法另一目的是创建点击事件
        return viewholder;//主要目的是返回一个viewholder给onBindViewHolder，这个viewholder由之前自定义的
    }
    @Override
    public void onBindViewHolder(final ChatAdapter.ViewHolder viewHolder, final int position){//在这里决定子项的
        //文字和图片的填入
        chat=mChatList.get(position);
        Log.d("position","position="+position);
        /*if (chat.getViewHolder()!=null){
            viewHolder.equals(chat.getViewHolder());
        }else{

        }*/
            /*ViewGroup.LayoutParams lp = viewHolder.leftImage.getLayoutParams();
        lp.width = 150;//RecyclerView.LayoutParams.WRAP_CONTENT;
        lp.height = 150;//RecyclerView.LayoutParams.WRAP_CONTENT;
        viewHolder.leftImage.setLayoutParams(lp);
        viewHolder.leftImage.setMaxWidth(widthMax);
        viewHolder.leftImage.setMaxHeight(heightMax);

        ViewGroup.LayoutParams lp1 = viewHolder.rightImage.getLayoutParams();
        lp1.width = 150;//RecyclerView.LayoutParams.WRAP_CONTENT;
        lp1.height = 150;//RecyclerView.LayoutParams.WRAP_CONTENT;
        viewHolder.rightImage.setLayoutParams(lp1);
        viewHolder.rightImage.setMaxWidth(widthMax);
        viewHolder.rightImage.setMaxHeight(heightMax);*/

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
                /*Glide.with(mContext).load(chat.getImage())//内部有子线程
                        .placeholder(R.drawable.zhanwei)
                        .into(viewHolder.leftImage);*/
                /*class GlideTask extends AsyncTask<Void,Integer,Boolean>{
                    @Override
                    protected Boolean doInBackground(Void...params){
                        Log.d("RECEIVE","加载中");
                        return true;
                    }
                    @Override
                    protected void onPostExecute(Boolean result){
                    SimpleTarget target =new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //内部是UI线程
                        ImageSize imageSize = BitmapUtils.getImageSize(resource);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                        byte[] bytes = baos.toByteArray();
                        final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        final ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        viewHolder.leftImage.setLayoutParams(imageLP);
                        viewHolder.leftImage.setImageBitmap(bm);
                        //showImage(viewHolder.leftImage,R.drawable.message_left);
                    }
                };
                Glide.with(mContext).load(chat.getImage())//内部有子线程
                        .asBitmap()
                        .placeholder(R.drawable.zhanwei)
                        .into(target);
                    }
                }*/
                /*GlideTask glideTask=new GlideTask();
                viewHolder.leftImage.setTag(R.id.tag_ImageLeft,glideTask);
                glideTask.execute();*/
                Log.d(position+" width"," "+chat.getImageWidth());
                Log.d(position+" height"," "+chat.getImageHeight());
                if (chat.getImageWidth()==0&&chat.getImageHeight()==0) {
                    SimpleTarget target =new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                            //内部是UI线程
                        /*ImageSize imageSize = BitmapUtils.getImageSize(resource);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                        byte[] bytes = baos.toByteArray();
                        final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        final ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        viewHolder.leftImage.setLayoutParams(imageLP);
                        viewHolder.leftImage.setImageBitmap(bm);*/
                            ImageSize imageSize = BitmapUtils.getImageSize(resource);
                            final ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
                            imageLP.width = imageSize.getWidth();
                            imageLP.height = imageSize.getHeight();
                            //new Thread(new Runnable() {
                                //@Override
                                //public void run() {
                            mChatList.get(position).setImageWidth(imageLP.width);
                            mChatList.get(position).setImageHeight(imageLP.height);
                                    //chat.setImageWidth(imageLP.width);
                                    //chat.setImageHeight(imageLP.height);
                            Log.d("if position"+position+" width:"," "+mChatList.get(position).getImageHeight());
                            Log.d("if position"+position+" Height:"," "+mChatList.get(position).getImageHeight());
                                //}
                            //}).start();
                            viewHolder.leftImage.setLayoutParams(imageLP);
                            viewHolder.leftImage.setImageBitmap(resource);
                            //showImage(viewHolder.leftImage,R.drawable.message_left,viewHolder);
                        }
                    };
                    Glide.with(mContext).load(chat.getImage())//内部有子线程
                            .asBitmap()
                            .into(target);
                }else {
                    Log.d("chat.getImageWidth();","!!!!!!!!");
                    final ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
                    imageLP.width = chat.getImageWidth();
                    imageLP.height = chat.getImageHeight();
                    Log.d("else position"+position+" width:"," "+chat.getImageWidth());
                    Log.d("else position"+position+" Height:"," "+chat.getImageHeight());
                    viewHolder.leftImage.setLayoutParams(imageLP);
                    Glide.with(mContext).load(chat.getImage())//内部有子线程
                            .into(viewHolder.leftImage);
                    //showImage(viewHolder.leftImage,R.drawable.message_left,viewHolder);
                }



                /*ImageSize imageSize =BitmapUtils.getImageSizeNew(chat.getImage());
                final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                imageLP.width = imageSize.getWidth();
                imageLP.height = imageSize.getHeight();
                viewHolder.leftImage.setLayoutParams(imageLP);*/
                //new Thread(new Runnable() {
                //@Override
                //public void run() {
                //activity.runOnUiThread(new Runnable() {
                //@Override
                //public void run() {

                /*Glide.with(mContext).load(chat.getImage())//内部有子线程
                        .into(viewHolder.leftImage);
                //loadIntoUseFitWidth(mContext, chat.getImage(), R.drawable.apple_pic, viewHolder.leftImage);
                showImage(viewHolder.leftImage,R.drawable.message_left);*/
                //}
                //});
                //}
                // }).start();

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final Bitmap bitmap=Glide.with(mContext).load(chat.getImage())//内部有子线程
                                    .asBitmap()
                                    .centerCrop()
                                    .into(500,500)
                                    .get();
                            ImageSize imageSize = BitmapUtils.getImageSize(bitmap);
                            final ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
                            imageLP.width = imageSize.getWidth();
                            imageLP.height = imageSize.getHeight();
                            viewHolder.leftImage.setLayoutParams(imageLP);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.leftImage.setImageBitmap(bitmap);
                                }
                            });
                            showImage(viewHolder.leftImage,R.drawable.message_left);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();*/

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
                /*Glide.with(mContext).load(chat.getImage())//内部有子线程
                        .placeholder(R.drawable.zhanwei)
                        .into(viewHolder.rightImage);*/
                /*class GlideTask extends AsyncTask<Void,Void,Boolean>{
                        @Override
                        protected Boolean doInBackground(Void...params){
                            Log.d("SENT","加载中");
                            return true;
                        }
                        @Override
                        protected void onPostExecute(Boolean result){
                            SimpleTarget target =new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    //回调到主线程中
                                    ImageSize imageSize = BitmapUtils.getImageSize(resource);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    resource.compress(Bitmap.CompressFormat.JPEG, 10, baos);//数字最大为100,100为不压缩
                                    byte[] bytes = baos.toByteArray();
                                    final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                                    imageLP.width = imageSize.getWidth();
                                    imageLP.height = imageSize.getHeight();
                                    viewHolder.rightImage.setLayoutParams(imageLP);
                                    viewHolder.rightImage.setImageBitmap(bm);
                                    //showImage(viewHolder.rightImage,R.drawable.message_right);
                                }
                            };
                            Glide.with(mContext).load(chat.getImage())//内部有子线程
                                    .asBitmap()
                                    .placeholder(R.drawable.zhanwei)//设置图片占位符
                                    .into(target);
                        }
                    }
                GlideTask glideTask=new GlideTask();
                viewHolder.rightImage.setTag(R.id.tag_ImageRight,glideTask);
                glideTask.execute();
                //glide使用override时，可能会导致列表无法到最后一个元素的位置
                //glide还需要多研究，可以考虑。get
                */
                Log.d(position+" width"," "+chat.getImageWidth());
                Log.d(position+" height"," "+chat.getImageHeight());
                if (chat.getImageWidth()==0&&chat.getImageHeight()==0){
                    SimpleTarget target =new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                            //回调到主线程中
                        /*ImageSize imageSize = BitmapUtils.getImageSize(resource);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 10, baos);//数字最大为100,100为不压缩
                        byte[] bytes = baos.toByteArray();
                        final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                        imageLP.width = imageSize.getWidth();
                        imageLP.height = imageSize.getHeight();
                        viewHolder.rightImage.setLayoutParams(imageLP);
                        viewHolder.rightImage.setImageBitmap(bm);*/
                            ImageSize imageSize = BitmapUtils.getImageSize(resource);
                            final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                            imageLP.width = imageSize.getWidth();
                            imageLP.height = imageSize.getHeight();
                            //new Thread(new Runnable() {
                                //@Override
                                //public void run() {
                            mChatList.get(position).setImageWidth(imageLP.width);//要用position来明确是哪个chat
                            mChatList.get(position).setImageHeight(imageLP.height);
                            //chat.setImageWidth(imageLP.width);不能用这个的原因是，在回调函数中，进程跟不上主线程
                            //所以chat可能指的是null
                            //chat.setImageHeight(imageLP.height);
                            Log.d("if position"+position+" width:"," "+mChatList.get(position).getImageHeight());
                            Log.d("if position"+position+" Height:"," "+mChatList.get(position).getImageHeight());
                                //}
                            //}).start();
                            viewHolder.rightImage.setLayoutParams(imageLP);
                            viewHolder.rightImage.setImageBitmap(resource);
                            //showImage(viewHolder.rightImage,R.drawable.message_right,viewHolder);
                        }
                    };
                    Glide.with(mContext).load(chat.getImage())//内部有子线程
                            .asBitmap()
                            .into(target);
                }else {
                    Log.d("chat.getImageWidth();","!!!!!!!!");
                    final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                    imageLP.width = chat.getImageWidth();
                    imageLP.height = chat.getImageHeight();
                    Log.d("else position"+position+" width:"," "+chat.getImageWidth());
                    Log.d("else position"+position+" Height:"," "+chat.getImageHeight());
                    viewHolder.rightImage.setLayoutParams(imageLP);
                    Glide.with(mContext).load(chat.getImage())//内部有子线程
                            .into(viewHolder.rightImage);
                    //showImage(viewHolder.rightImage,R.drawable.message_right,viewHolder);
                }


                    /*ImageSize imageSize =BitmapUtils.getImageSizeNew(chat.getImage());
                final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                imageLP.width = imageSize.getWidth();
                imageLP.height = imageSize.getHeight();
                viewHolder.rightImage.setLayoutParams(imageLP);*/
                //new Thread(new Runnable() {
                //@Override
                //public void run() {
                //activity.runOnUiThread(new Runnable() {
                //@Override
                //public void run() {

                /*Glide.with(mContext).load(chat.getImage())//内部有子线程
                        .into(viewHolder.rightImage);//直接使用Glide和使用target的加载速度不同
                //loadIntoUseFitWidth(mContext, chat.getImage(), R.drawable.apple_pic, viewHolder.rightImage);
                                //直接加载在快速翻动列表时加载速度几乎不留痕迹
                                //Glide不能运行于子线程中
                showImage(viewHolder.rightImage,R.drawable.message_right);*/
                // }
                //});
                // }
                // }).start();

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final Bitmap bitmap=Glide.with(mContext).load(chat.getImage())//内部有子线程
                                    .asBitmap()
                                    .centerCrop()
                                    .into(500,500)
                                    .get();
                            //这里into是分辨率
                            ImageSize imageSize = BitmapUtils.getImageSize(bitmap);
                            final ViewGroup.LayoutParams imageLP = viewHolder.rightImage.getLayoutParams();
                            imageLP.width = imageSize.getWidth();
                            imageLP.height = imageSize.getHeight();
                            viewHolder.rightImage.setLayoutParams(imageLP);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.rightImage.setImageBitmap(bitmap);
                                }
                            });
                            showImage(viewHolder.rightImage,R.drawable.message_right);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();*/


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
        /*AsyncTask asyncTaskLeft = (AsyncTask) holder.leftImage.getTag(R.id.tag_ImageLeft);
        AsyncTask asyncTaskRight = (AsyncTask) holder.rightImage.getTag(R.id.tag_ImageRight);
        if (asyncTaskLeft!=null){
            Log.d("asyncTaskLeft!=null","!!!!!!!!!");
            asyncTaskLeft.cancel(true);
        }
        if(asyncTaskRight!=null){
            Log.d("asyncTaskRight!=null","!!!!!!!!!");
            asyncTaskRight.cancel(true);
        }*/
    }

    private Handler setPicHandler = new Handler() {//import android.os.Handler
        @Override
        public void handleMessage(android.os.Message msg) {
            //Log.d("handleMessage","!!!!!!!!");
            Bean bean=(Bean)msg.obj;
            showImage(bean.getImageView(), msg.arg1,bean.getViewHolder());
        }
    };

    private void showImage(final ImageView imageView, final int pointNineImage,final ChatAdapter.ViewHolder viewHolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {//runOnUiThread如果当前线程是UI线程,那么行动是立即执行。
                    // 如果当前线程不是UI线程,操作是发布到事件队列的UI 线程，UI线程就是主线程
                    @Override
                    public void run() {
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();
                        Bitmap bitmap_bg = BitmapFactory.decodeResource(activity.getResources(), pointNineImage);
                        Bitmap bitmap_in = imageView.getDrawingCache();
                        if (bitmap_bg != null && bitmap_in != null) {
                            //Log.d("showImage if","!!!!!!!!!");
                            final Bitmap bp = getRoundCornerImage(imageView.getWidth(), imageView.getHeight(), bitmap_bg, bitmap_in);
                            final Bitmap bp2 = getShardImage(imageView.getWidth(), imageView.getHeight(), bitmap_bg, bp);
                            imageView.setImageBitmap(bp2);

                        } else {
                            //Log.d("showImage else","!!!!!!!!!");
                            //如果照片没有设置好，再发通知重新设置，直到照片设置完成
                            final android.os.Message message = new android.os.Message();
                            Bean bean=new Bean();//很重要！！！没有对象组就自己创造！！！
                            bean.setViewHolder(viewHolder);
                            bean.setImageView(imageView);
                            message.obj = bean;
                            message.arg1 = pointNineImage;
                            //showImage(imageView,pointNineImage);
                            //new Thread(new Runnable() {
                                //@Override
                                //public void run() {
                            //Log.d("showImage else Thread","!!!!!!!!!");
                            setPicHandler.sendMessageAtTime(message, 100);//由于有时间，所以必须放在子线程中
                            //可能游戏与操作是发布到事件队列的UI 线程，所以这里这个耗时不会出现卡的情况
                            //}
                            //}).start();//!!!!!!!!!!!!!!!!start别忘了！！！
                        }
                        imageView.setDrawingCacheEnabled(false);
                    }
                });
            }
        }).start();
    }




    public Bitmap getRoundCornerImage(int width, int height, Bitmap bitmap_bg, Bitmap bitmap_in) {
        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        Rect rectF = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rectF, rect, paint);
        return roundConcerImage;
    }

    public Bitmap getShardImage(int width, int height, Bitmap bitmap_bg, Bitmap bitmap_in) {
        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        Rect rect2 = new Rect(1, 1, width - 1, height - 1);
        canvas.drawBitmap(bitmap_in, rect, rect2, paint);
        return roundConcerImage;
    }

    public static void loadIntoUseFitWidth(Context context, final String imageUrl, int errorImageId, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
                        float scale = (float) vw / (float) resource.getIntrinsicWidth();
                        int vh = Math.round(resource.getIntrinsicHeight() * scale);
                        params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(params);
                        return false;
                    }
                })
                .placeholder(errorImageId)
                .error(errorImageId)
                .into(imageView);
    }
}


/*Glide.with(mContext).load(chat.getImage())//内部有子线程
        .asBitmap()
        .into(new SimpleTarget<Bitmap>() {
@Override
public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        activity.runOnUiThread(new Runnable() { //  使用这种回调的方式会使在列表中滚动的时候加载变慢，可能是
        //这种方法没有生成缓存的缘故,旅游回来后接着用这个方法进行尝试
@Override
public void run() {
        ImageSize imageSize = BitmapUtils.getImageSize(resource);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //ViewGroup.LayoutParams imageLP = viewHolder.leftImage.getLayoutParams();
        //imageLP.width = imageSize.getWidth();
        //imageLP.height = imageSize.getHeight();
        //viewHolder.leftImage.setLayoutParams(imageLP);
        viewHolder.leftImage.setImageBitmap(bm);
        showImage(viewHolder.leftImage,R.drawable.message_left);
        //chatActivity.recyclerView.scrollToPosition(mChatList.size()-1);//之前无法到底的原因是
        //线程快慢的问题！！
        //不能在这里，否则每次滚到这里就会立即回到最底部
        }
        });
        }
        });*/
