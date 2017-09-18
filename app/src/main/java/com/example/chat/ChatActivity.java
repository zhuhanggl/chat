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
import android.graphics.Rect;
import android.icu.text.LocaleDisplayNames;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.gson.UserAccount;
import com.example.chat.image.BitmapUtils;
import com.example.chat.image.ImageSize;
import com.example.chat.service.ChatService;
import com.example.chat.service.IPupdate;
import com.example.chat.util.EchoWebSocketListener;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
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
    public RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    public DrawerLayout drawerLayout;
    private Button backFriendChooseActivity;
    private WebSocket webSocket;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private ChatService.ChatBinder chatBinder;
    private String imagePath;
    private LinearLayout linearLayout;
    private int layoutWidth;
    private int layoutHeight;
    private ViewGroup.LayoutParams imageLP;

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
    private void fix(){//固定用，防止状态栏重新出现时recyclerview的item回不到最低端
        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        linearLayout=(LinearLayout) findViewById(R.id.activity_chat_layout);
        imageLP=linearLayout.getLayoutParams();
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        imageLP.width= DrawerLayout.LayoutParams.MATCH_PARENT;
        imageLP.height=outRect1.height()-statusBarHeight1;
        Log.d("dasdas",""+outRect1.height());
        Log.d("ssssadas",""+statusBarHeight1);
        linearLayout.setLayoutParams(imageLP);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        friend=(Friend)intent.getSerializableExtra("friend");//java是讲究顺序的！！！,不讲究顺序的是声明！
        Log.d("ChatActivity",friend.getName());
        userAccount=(UserAccount)intent.getSerializableExtra("user");
        //逻辑顺序还是有的！！！
        setContentView(R.layout.activity_chat);
        fix();
        sentText=(EditText)findViewById(R.id.sent_text);
        sentText.setText("");
        otherButton=(Button)findViewById(R.id.other_button);
        sentButton=(Button)findViewById(R.id.sent_button);
        otherButton.setVisibility(View.VISIBLE);
        sentButton.setVisibility(View.INVISIBLE);
        otherButton.setOnClickListener(this);
        sentButton.setOnClickListener(this);
        sentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i==0){
                    if(i1==0){
                        if(i2>0){
                            Log.d("1","111111111");
                            sentButton.setVisibility(View.VISIBLE);
                            otherButton.setVisibility(View.INVISIBLE);
                        }else{
                            Log.d("4","444444444");
                        }
                    }else{
                        Log.d("2","2222222222");
                        sentButton.setVisibility(View.INVISIBLE);
                        otherButton.setVisibility(View.VISIBLE);
                    }
                }else{
                    Log.d("3","3333333333333");
                    sentButton.setVisibility(View.VISIBLE);
                    otherButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("5","55555555555555");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("6","666666666666");
            }
        });

        friendName=(TextView)findViewById(R.id.friend_name);
        friendName.setText(friend.getName());
        backFriendChooseActivity=(Button)findViewById(R.id.back_FriendChooseActivity);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        recyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        LinearLayoutManager layoutManager=new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter=new ChatAdapter(mChatList,userAccount,this);
        recyclerView.setAdapter(chatAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.chat.service.message");
        localReceiver=new LocalReceiver();
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        Intent bindIntent=new Intent(ChatActivity.this,ChatService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        chatInit();
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
                , imagePath, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("imagePath:x ",imagePath);
                        JSONObject jsonObject=new JSONObject();
                        try{
                            FileInputStream fis = new FileInputStream(imagePath);
                            Bitmap bitmap= BitmapFactory.decodeStream(fis);
                            ImageSize imageSize = BitmapUtils.getImageSize(bitmap);
                            jsonObject.put("Type","imagePath");
                            jsonObject.put("From",userAccount.getAccount());
                            jsonObject.put("To",friend.getAccount());
                            jsonObject.put("ToId",friend.getFriendId());
                            jsonObject.put("imageWidth",imageSize.getWidth());
                            jsonObject.put("imageHeight",imageSize.getHeight());
                            jsonObject.put("ImagePath",
                                    imagePath.substring(imagePath.lastIndexOf("/"),imagePath.length()));
                            Log.i("imageName",imagePath.substring(imagePath.lastIndexOf("/"),imagePath.length()));
                            webSocket.send(jsonObject.toString());
                            showImage(friend,imagePath,imageSize.getWidth(),
                                    imageSize.getHeight(),Chat.TYPE_SENT);
                        }catch (Exception e){
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
                Chat chat=new Chat(friend,string,null,0,0,Chat.TYPE_SENT);
                mChatList.add(chat);
                chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
                //上面两句若不放在主线程中，在手机端则无法显示出列表中的内容
                Log.d("sendMessage",string+"!!!!!!!!!!!!");
                sentText.setText("");
            }
        });
    }
    public void showImage(final Friend friend,final String imagePath,
                          final int imageWidth,final int imageHeight,final int messageType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chat chat=new Chat(friend,null,imagePath,imageWidth,imageHeight
                        ,messageType);//无需图片大小，直接加载就好
                mChatList.add(chat);
                chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
                //sentText.setText("");
                Log.d("showImage",imagePath+"!!!!!!!!!!!!");
            }
        });
    }

    private void showMessage(final Friend friend,final String message,final int messageType){
        //由于friend和message和messageType要应用于内部类，所以要定义为final
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chat chat=new Chat(friend,message,null,0,0,messageType);
                mChatList.add(chat);
                chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
                Log.d("showMessage",message+"!!!!!!!!!!!!");
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
                showImage(friend,intent.getStringExtra("imagePath"),
                        Integer.parseInt(intent.getStringExtra("imageWidth")),
                        Integer.parseInt(intent.getStringExtra("imageHeight")),
                        Chat.TYPE_RECEIVED);
            }
        }
    }
    @Override
    protected void onResume(){
        super.onResume();

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
                                        String imageWidth=jsonObject.getString("ImageWidth");
                                        String imageHeight=jsonObject.getString("ImageHeight");
                                        //showImage(friend,image,Chat.TYPE_RECEIVED);
                                        Log.d("xxximageWidth",imageWidth);
                                        Log.d("xxximageHeight",imageHeight);
                                        Chat chat=new Chat(friend,null,image,Integer.parseInt(imageWidth),
                                                Integer.parseInt(imageHeight),Chat.TYPE_RECEIVED);
                                        mChatList.add(chat);
                                    }else{
                                        Chat chat=new Chat(friend,message,null,0,0,Chat.TYPE_RECEIVED);
                                        mChatList.add(chat);
                                        //showMessage(friend,message,Chat.TYPE_RECEIVED);
                                    }
                                }
                                if (toAccount.equals(friend.getAccount())){
                                    if (message.equals("")){
                                        String imageWidth=jsonObject.getString("ImageWidth");
                                        String imageHeight=jsonObject.getString("ImageHeight");
                                        //showImage(friend,image,Chat.TYPE_SENT);
                                        Log.d("xxximageWidth",imageWidth);
                                        Log.d("xxximageHeight",imageHeight);
                                        Chat chat=new Chat(friend,null,image,Integer.parseInt(imageWidth),
                                                Integer.parseInt(imageHeight),Chat.TYPE_SENT);
                                        mChatList.add(chat);
                                    }else{
                                        Chat chat=new Chat(friend,message,null,0,0,Chat.TYPE_SENT);
                                        mChatList.add(chat);
                                        //showMessage(friend,message,Chat.TYPE_SENT);
                                    }
                                }
                            }
                            //recyclerView.scrollToPosition(mChatList.size()-1);注意这句放在这里会导致
                            //在手机端运行时列表中显示不出内容！！！！！必须在主线程中
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyItemInserted(mChatList.size()-1);
                                //只要静态设置imageView的大小，就不会出现到不了底的情况！！
                                //到不了底是由于动态加载imageView的大小需要耗时，而还没加载完，就执行了下面的
                                //语句。
                                recyclerView.scrollToPosition(mChatList.size()-1);
                            }
                        });
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }
}
