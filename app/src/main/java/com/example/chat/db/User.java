package com.example.chat.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/7/20.
 */

public class User extends DataSupport{
    private String account;
    private String name;
    private int AvatarId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarId() {
        return AvatarId;
    }

    public void setAvatarId(int avatarId) {
        AvatarId = avatarId;
    }
}
