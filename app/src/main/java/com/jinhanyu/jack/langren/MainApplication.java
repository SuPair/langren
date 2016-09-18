package com.jinhanyu.jack.langren;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.entity.VoteResult;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
                     Log.i("connected to socket io","nice");
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.i("disconnected to socket", "sorry");
                }

            }).on("serverError", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Toast.makeText(MainApplication.this,"服务器错误："+ args[0], Toast.LENGTH_SHORT).show();
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static UserInfo userInfo = new UserInfo();
    public static RoomInfo roomInfo;
    public static Socket socket;
    public static List<UserInfo> currentRoomUsers= new ArrayList<UserInfo>();
    public static List<VoteResult> voteResults = new ArrayList<>();
    public static String login_preference_name = "login";


    public static UserInfo findUserInRoom(String userId){
        for (UserInfo info : MainApplication.currentRoomUsers) {
            if (info.getUserId().equals(userId)) {
               return info;
            }
        }
        throw new RuntimeException("客户端：  用户未找到");
    }

    private static final String myServer = "http://172.168.0.10:3000/msg";
}
