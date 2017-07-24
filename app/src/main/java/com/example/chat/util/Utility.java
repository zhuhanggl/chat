package com.example.chat.util;

import android.text.TextUtils;

import com.example.chat.gson.UserAccount;
import com.example.chat.gson.UserFriend;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class Utility {
    public static List<UserAccount> handleAccountResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONObject ChatAccountsObject=new JSONObject(response);
                JSONArray accountArray=ChatAccountsObject.getJSONArray("ChatAccounts");
                String accountContent=accountArray.toString();
                Gson gson= new Gson();
                return gson.fromJson(accountContent, new TypeToken<List<UserAccount>>(){}.getType());
            }catch (Exception e){//注意这里是Exception不是IOE！
                e.printStackTrace();
            }
        }
        return null;
    }

    public static UserFriend handleUserFriendResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                Gson gson= new Gson();
                return gson.fromJson(response, UserFriend.class);
            }catch (Exception e){//注意这里是Exception不是IOE！
                e.printStackTrace();
            }
        }
        return null;
    }
}
