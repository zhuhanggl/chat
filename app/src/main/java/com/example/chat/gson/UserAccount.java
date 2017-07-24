package com.example.chat.gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/24.
 */

public class UserAccount implements Serializable{
    private String account;
    private String password;
    private String name;
    private String Avatar;
    private String FriendsId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendsId() {
        return FriendsId;
    }

    public void setFriendsId(String friendsId) {
        FriendsId = friendsId;
    }
}
