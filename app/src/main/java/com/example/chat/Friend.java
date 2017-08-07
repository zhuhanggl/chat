package com.example.chat;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Friend implements Serializable{
    private String FriendId;
    private String Account;
    private String AvatarId;
    private String name;
    public Friend(String FriendId,String Account,String AvatarId,String name){
        this.FriendId=FriendId;
        this.Account=Account;
        this.AvatarId=AvatarId;
        this.name=name;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        this.Account = account;
    }

    public String getAvatarId() {
        return AvatarId;
    }

    public void setAvatarId(String avatarId) {
        AvatarId = avatarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
