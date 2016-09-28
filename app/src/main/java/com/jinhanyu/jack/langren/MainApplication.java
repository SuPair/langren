package com.jinhanyu.jack.langren;

import android.app.Application;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.Parse;
import com.parse.ParseObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();


        Fresco.initialize(getApplicationContext());

        ParseObject.registerSubclass(UserInfo.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("langrensha")
                .clientKey("")
                .server(ServerHost + "/parse")
                .enableLocalDataStore()
                .build()
        );

        initSocket();

        SoundEffectManager.init(this);
    }


    private void initSocket() {
        try {
            if (socket == null) {
                IO.Options options = new IO.Options();
                options.reconnection = true;
                options.reconnectionAttempts=5;
                socket = IO.socket(ServerHost + "/msg",options);
            }
            socket.on("serverError", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Looper.prepare();
                    Toast.makeText(MainApplication.this, "服务器错误：" + args[0], Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Looper.prepare();
                    Toast.makeText(MainApplication.this, "socket error", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public static RoomInfo roomInfo;
    public static Socket socket;
    private static final String ServerHost = "http://172.168.0.10:3000";
//    private static final String ServerHost = "http://langren.applinzi.com";

}
