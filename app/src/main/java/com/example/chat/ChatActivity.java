package com.example.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.gson.UserAccount;
import com.example.chat.service.ChatService;
import com.example.chat.service.IPupdate;
import com.example.chat.util.EchoWebSocketListener;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatActivity extends BaseActivity implements View.OnClickListener{
    public static final int CHOOSE_PHOTO=2;
    private EditText sentText;
    private Button sentButton;
    private TextView friendName;
    private Button otherButton;
    private List<Chat> mChatList=new ArrayList<>();
    private Friend friend;
    private UserAccount userAccount;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    public DrawerLayout drawerLayout;
    private Button backFriendChooseActivity;
    private WebSocket webSocket;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private ChatService.ChatBinder chatBinder;
    private String imagePath;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatBinder=(ChatService.ChatBinder)iBinder;
            chatBinder.setFriend(friend);
            webSocket=chatBinder.getWebSocket();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        friend=(Friend)intent.getSerializableExtra("friend");//java是讲究顺序的！！！,不讲究顺序的是声明！
        Log.d("ChatActivity",friend.getName());
        userAccount=(UserAccount)intent.getSerializableExtra("user");
        //逻辑顺序还是有的！！！
        setContentView(R.layout.activity_chat);
        sentText=(EditText)findViewById(R.id.sent_text);
        sentText.setText("");
        sentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i==0){
                    if(i1==0){
                        sentButton.setVisibility(View.VISIBLE);
                        otherButton.setVisibility(View.INVISIBLE);
                    }else{
                        sentButton.setVisibility(View.INVISIBLE);
                        otherButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    sentButton.setVisibility(View.VISIBLE);
                    otherButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otherButton=(Button)findViewById(R.id.other_button);
        sentButton=(Button)findViewById(R.id.sent_button);
        friendName=(TextView)findViewById(R.id.friend_name);
        friendName.setText(friend.getName());
        backFriendChooseActivity=(Button)findViewById(R.id.back_FriendChooseActivity);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        recyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter=new ChatAdapter(mChatList,userAccount);
        recyclerView.setAdapter(chatAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.chat.service.message");
        localReceiver=new LocalReceiver();
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        Intent bindIntent=new Intent(ChatActivity.this,ChatService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        chatInit();
        otherButton.setOnClickListener(this);
        sentButton.setOnClickListener(this);
        backFriendChooseActivity.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sent_button:
                JSONObject jsonObject=new JSONObject();
                try{
                    if(TextUtils.isEmpty(sentText.getText())){

                    }else{
                        jsonObject.put("Type","message");
                        jsonObject.put("From",userAccount.getAccount());
                        jsonObject.put("To",friend.getAccount());
                        jsonObject.put("ToId",friend.getFriendId());
                        jsonObject.put("Message",sentText.getText().toString());
                        webSocket.send(jsonObject.toString());
                        sendMessage(sentText.getText().toString());
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            case R.id.back_FriendChooseActivity:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.other_button:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
                break;
        }
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        sendOkHttpImage();
        //displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        sendOkHttpImage();
        //displayImage(imagePath);
    }
    private void sendOkHttpImage(){
        HttpUtil.sendOkHttpMultipart("http://" + HttpUtil.localIP + ":8080/okhttp3_test/FileServlet"
                , new File(imagePath), new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("imagePath:x ",imagePath);
                        JSONObject jsonObject=new JSONObject();
                        try{
                            jsonObject.put("Type","imagePath");
                            jsonObject.put("From",userAccount.getAccount());
                            jsonObject.put("To",friend.getAccount());
                            jsonObject.put("ToId",friend.getFriendId());
                            jsonObject.put("ImagePath",
                                    imagePath.substring(imagePath.lastIndexOf("/"),imagePath.length()));
                            Log.i("imageName",imagePath.substring(imagePath.lastIndexOf("/"),imagePath.length()));
                            webSocket.send(jsonObject.toString());
                            showImage(friend,imagePath,Chat.TYPE_SENT);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //rightImage.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }


    public UserAccount getUserAccount() {
        return userAccount;
    }


    public void sendMessage(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chat chat=new Chat(friend,string,null,Chat.TYPE_SENT);
                mChatList.add(chat);
                //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
                sentText.setText("");
            }
        });
    }
    public void showImage(final Friend friend,final String imagePath,final int messageType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chat chat=new Chat(friend,null,imagePath,messageType);
                mChatList.add(chat);
                //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
                sentText.setText("");
            }
        });
    }

    private void showMessage(final Friend friend,final String message,final int messageType){
        //由于friend和message和messageType要应用于内部类，所以要定义为final
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chat chat=new Chat(friend,message,null,messageType);
                mChatList.add(chat);
                //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
        unbindService(connection);
        //webSocket.close(1000,null);//一定要在活动和服务中销毁时将连接中断！！！，否则服务器端会出现错误
        //但是如果使用后台杀死该程序的话，则不会调用该函数
    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){
            if(intent.getStringExtra("Type").equals("message")){
                showMessage(friend,intent.getStringExtra("message"),Chat.TYPE_RECEIVED);
            }else if(intent.getStringExtra("Type").equals("imagePath")){

            }

        }
    }

    private void chatInit(){
        HttpUtil.sendOkHttpChatInit("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet"
                , userAccount, friend, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                        try{
                            JSONArray jsonArray=new JSONArray(responseData);
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String fromAccount=jsonObject.getString("FromAccount");
                                String toAccount=jsonObject.getString("ToAccount");
                                String message=jsonObject.getString("Message");
                                String image=jsonObject.getString("ImagePath");
                                if (fromAccount.equals(friend.getAccount())){
                                    if (message.equals("")){
                                        showImage(friend,image,Chat.TYPE_RECEIVED);
                                    }else{
                                        showMessage(friend,message,Chat.TYPE_RECEIVED);
                                    }
                                }
                                if (toAccount.equals(friend.getAccount())){
                                    if (message.equals("")){
                                        showImage(friend,image,Chat.TYPE_SENT);
                                    }else{
                                        showMessage(friend,message,Chat.TYPE_SENT);
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }

}
