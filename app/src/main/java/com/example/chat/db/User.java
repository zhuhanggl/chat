package com.example.chat.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/20.
 */

public class User extends DataSupport{
    private int id;//id这个字段必须要有
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getFriendsId() {
        return FriendsId;
    }

    public void setFriendsId(String friendsId) {
        FriendsId = friendsId;
    }
}
