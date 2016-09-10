package com.jinhanyu.jack.langren;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
             socket = IO.socket("http://172.168.0.10:3000");
             socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("connected","nice!");

                    socket.emit("event", "hi");

                }

            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    try {
                        Log.i("received data", obj.getString("some"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.close();
    }

    public void sendMsg(View view) {
        socket.emit("cmd","开始游戏");
    }
}
