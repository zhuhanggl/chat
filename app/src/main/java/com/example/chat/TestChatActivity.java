package com.example.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class TestChatActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView chatText;
    private EditText chatContent;
    private Button chatSendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chat);
        chatText=(TextView)findViewById(R.id.chat_text);
        chatText.setText("");
        chatContent=(EditText)findViewById(R.id.chat_content);
        chatSendButton=(Button)findViewById(R.id.chat_send_button);
        initConnect();
        chatSendButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.chat_send_button:

                break;
        }
    }
    private void initConnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {//下面这段网络申请必须放在子线程中！！！否则不会执行
                try {
                    String str;
                    InetAddress addr= InetAddress.getByName("192.168.1.106");
                    Socket socket=new Socket(addr,8000);
                    chatText.setText("zzzz");
                    chatText.append("socket="+socket);
                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(socket.getInputStream()));
                    PrintStream out=new PrintStream(socket.getOutputStream());
                    //BufferedReader userin=new BufferedReader(new InputStreamReader(System.in));
                    //while(true) {
                    chatText.append("send string:");
                    str="ok?";//chatContent.getText().toString();
                    out.println(str);
                    chatContent.setText("");
                    //if(str.equals("bye"))break;
                    chatText.append("waiting for server");
                    str=in.readLine();
                    chatText.append("server:"+str);
                    //if(str.equals("bye"))break;
                    //}
                    out.close();
                    in.close();
                    socket.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
