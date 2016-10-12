package com.jinhanyu.jack.langren;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.Parse;
import com.parse.ParseObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class MainApplication extends Application {

    public static String ServerHost;

    public static RoomInfo roomInfo = new RoomInfo();
    public static Socket socket;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init() {

        MainApplication.ServerHost = getSharedPreferences("ip",MODE_PRIVATE).getString("ip",null);

        if(MainApplication.ServerHost==null)
            MainApplication.ServerHost = Constants.ServerHost;
        Fresco.initialize(getApplicationContext());

        ParseObject.registerSubclass(UserInfo.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("langrensha")
                .clientKey("")
                .server(Constants.makeNewIpAddress(MainApplication.ServerHost) + "/parse")
                .enableLocalDataStore()
                .build()
        );


        initSocket();

        SoundEffectManager.init(this);
    }


    private void initSocket() {
        try {
            if (MainApplication.socket == null) {
                IO.Options options = new IO.Options();
                options.reconnection = false;
                MainApplication.socket = IO.socket(Constants.makeNewIpAddress(MainApplication.ServerHost) + "/msg",options);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


}
