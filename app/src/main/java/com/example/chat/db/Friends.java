package com.example.chat.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/7/20.
 */

public class Friends extends DataSupport {
    private int id;//id这个字段必须要有
    private String FriendId;
    private String account;
    private String name;
    private String Avatar;

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
