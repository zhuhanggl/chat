package com.example.chat;



/**
 * Created by Administrator on 2017/7/19.
 */

public class Chat {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private Friend friend;
    private String chatText;
    private String image;
    private int type;
    private int imageWidth;
    private int imageHeight;
    private ChatAdapter.ViewHolder viewHolder;

    public Chat(Friend friend,String chatText,String image,int imageWidth,int imageHeight,int type){
        this.friend=friend;
        this.chatText=chatText;
        this.image=image;
        this.imageWidth=imageWidth;
        this.imageHeight=imageHeight;
        this.type=type;
        viewHolder=null;
    }

    public int getType() {
        return type;
    }

    public Friend getFriend() {
        return friend;
    }

    public String getChatText() {
        return chatText;
    }

    public String getImage() {
        return image;
    }

    public ChatAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(ChatAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
}
