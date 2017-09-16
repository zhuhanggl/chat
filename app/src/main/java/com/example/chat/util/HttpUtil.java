package com.example.chat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.chat.Friend;
import com.example.chat.gson.UserAccount;
import com.example.chat.image.BitmapUtils;
import com.example.chat.image.ImageSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
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

    public static void sendOkHttpFriendIP(String address, Friend friend, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","4")
                .add("Account",friend.getAccount())
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void FriendChatConnect(String address,WebSocketListener webSocketListener){
        OkHttpClient client=new OkHttpClient();
        /*RequestBody requestBody=new FormBody.Builder()
                .add("Req","5")
                .add("Account",friend.getAccount())
                .build();*/
        Request request=new Request.Builder()
                .url(address)
                .build();
        //.post(requestBody)
        client.newWebSocket(request, webSocketListener);
    }

    public static void sendOkHttpAddFriend(String address, UserAccount userAccount,
            String friendAccount, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","5")
                .add("FriendsId",userAccount.getFriendsId())
                .add("FriendAccount",friendAccount)
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpChatInit(String address, UserAccount userAccount,
            Friend friend, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","6")
                .add("FriendsId",userAccount.getFriendsId())
                .add("FriendAccount",friend.getAccount())
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpFriendData(String address, String UserId,
                                            String FriendAccount, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("Req","7")
                .add("UserId",UserId)
                .add("FriendAccount",FriendAccount)
                .build();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpMultipart(String address, String imagePath, okhttp3.Callback callback){
        File mfile=new File(imagePath);
        //ImageSize imageSize=null;
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        List<File>fileList=new ArrayList<>();
        fileList.add(mfile);
        MultipartBody.Builder mbody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        int i=0;
        /*try {
            FileInputStream fis = new FileInputStream(imagePath);
            Bitmap bitmap= BitmapFactory.decodeStream(fis);
            imageSize = BitmapUtils.getImageSize(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        for(File file:fileList){
            Log.d("sendOkHttpMultipart","for!!!!!!!!!");
            if(file.exists()){
                Log.d("sendOkHttpMultipart","file.exists()!!!!!!!!");
                Log.i("imageName:",file.getName());//经过测试，此处的名称不能相同，如果相同，只能保存最后一个图片，不知道那些同名的大神是怎么成功保存图片的。
                mbody.addFormDataPart("t",file.getName(),RequestBody.create(MEDIA_TYPE_PNG,file));
                //mbody.addFormDataPart("imageWidth",String.valueOf(imageSize.getWidth()));
                //mbody.addFormDataPart("imageHeight",String.valueOf(imageSize.getHeight()));
                i++;
            }
        }
        //Log.d("aaaaa",String.valueOf(imageSize.getWidth()));
        //Log.d("bbbbb",String.valueOf(imageSize.getHeight()));
        OkHttpClient client=new OkHttpClient();
        RequestBody requestBody=mbody.build();
        RequestBody requestBodyx=new FormBody.Builder()
                .add("Req","8")
                .build();
        Request request=new Request.Builder()
                .header("Authorization", "Client-ID " + "...")
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
        Log.d("sendOkHttpMultipart","end!!!!!!!!");
    }
}
