package com.example.chat.gson;

/**
 * Created by Administrator on 2017/7/24.
 */

public class OneFriend {
    private String account;
    private String name;
    private String Avatar;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
