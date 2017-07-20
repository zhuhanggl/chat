package com.example.chat;



/**
 * Created by Administrator on 2017/7/19.
 */

public class Chat {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private Friend friend;
    private String chatText;
    private int type;

    public Chat(Friend friend,String chatText,int type){
        this.friend=friend;
        this.chatText=chatText;
        this.type=type;
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
}
