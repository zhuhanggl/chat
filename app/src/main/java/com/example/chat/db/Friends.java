package com.example.chat.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/7/20.
 */

public class Friends extends DataSupport {
    private String userAccount;
    private String name;
    private int AvatarId;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
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
