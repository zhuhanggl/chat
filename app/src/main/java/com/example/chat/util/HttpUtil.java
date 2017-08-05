package com.example.chat.util;

import com.example.chat.gson.UserAccount;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Administrator on 2017/7/24.
 */

public class HttpUtil {
    public static final String localIP="192.168.1.101";
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpLogin(String address, String account,String password,
                                       okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","0")
                .add("Account",account)
                .add("Password",password)
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpSignUp(String address, UserAccount userAccount,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","1")
                .add("Account",userAccount.getAccount())
                .add("Password",userAccount.getPassword())
                .add("Name",userAccount.getName())
                .add("Avatar",userAccount.getAvatar())
                .add("IP",userAccount.getIp())
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendOkHttpIPUpdate(String address, UserAccount userAccount,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","2")
                .add("Account",userAccount.getAccount())
                .add("IP",userAccount.getIp())
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpFriendsLoading(String address, UserAccount userAccount,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","3")
                .add("FriendsId",userAccount.getFriendsId())
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
