package com.example.chat;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Friend {
    private int AvatarId;
    private String name;
    public Friend(int AvatarId,String name){
        this.AvatarId=AvatarId;
        this.name=name;
    }

    public int getAvatarId() {
        return AvatarId;
    }

    public void setAvatarId(int avatarId) {
        AvatarId = avatarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}