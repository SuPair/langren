package com.jinhanyu.jack.langren;

import android.app.Application;

import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

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


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("langrensha")
                .clientKey("")
                .server("http://172.168.0.10:3000/parse")
                .enableLocalDataStore()
                .build()
        );

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        initSocket();
    }

    private void initSocket(){
        try {
            if(socket==null) {
                socket = IO.socket(myServer);
            }
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                }

            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static UserInfo userInfo = new UserInfo();
    public static Socket socket;

    private static final String myServer = "http://172.168.0.10:3000/msg";
}
