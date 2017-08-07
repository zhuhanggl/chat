package com.example.chat.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.chat.MainActivity;
import com.example.chat.gson.UserAccount;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.SignUpActivity;
import com.example.chat.util.Utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class IPupdate extends Service {
    UserAccount userAccount;
    public IPupdate() {
    }
    @Override
    public void onCreate(){
        super.onCreate();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        userAccount=(UserAccount)intent.getSerializableExtra("User");
        getAddress();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int time=10*60*1000;//10分钟
        long triggerAtTime= SystemClock.elapsedRealtime()+time;
        Intent i=new Intent(this,IPupdate.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void getAddress(){
        String ip;
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
            userAccount.setIp(ip);
        }else {
            ip = getLocalIpAddress();
            userAccount.setIp(ip);
        }
        HttpUtil.sendOkHttpIPUpdate("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet",
                userAccount, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }
    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF);
    }
    public String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
