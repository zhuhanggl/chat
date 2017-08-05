package com.example.chat.gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/24.
 */

public class UserAccount implements Serializable{//gson解析与键值的顺序无关,以验证过了，可以放心使用
    //因为json的设计本身就是无序的
    private String FriendsId;
    private String account;
    private String password;
    private String name;
    private String Avatar;
    private String ip;

    public String getFriendsId() {
        return FriendsId;
    }

    public void setFriendsId(String friendsId) {
        FriendsId = friendsId;
    }

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    /*
    private String FriendsId;
    private String password;
    private String ip;
    private String name;
    private String account;
    private String Avatar;


    public String getFriendsId() {
        return FriendsId;
    }

    public void setFriendsId(String friendsId) {
        FriendsId = friendsId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }*/




}
