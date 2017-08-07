package com.example.chat.util;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Administrator on 2017/8/6.
 */

public class EchoWebSocketListener extends WebSocketListener {
    @Override
    public void onOpen(WebSocket webSocket, Response response) {

        webSocket.send("hello world");


    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);

    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {

    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {

    }
}
