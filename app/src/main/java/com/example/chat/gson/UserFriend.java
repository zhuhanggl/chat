package com.example.chat.gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class UserFriend implements Serializable {
    private String FriendId;
    private List<OneFriend> oneFriendList;

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public List<OneFriend> getOneFriendList() {
        return oneFriendList;
    }

    public void setOneFriendList(List<OneFriend> oneFriendList) {
        this.oneFriendList = oneFriendList;
    }
}
