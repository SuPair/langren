package com.jinhanyu.jack.langren;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.ui.GameMainActivity;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .server(Constants.ServerHost + "/parse")
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
                options.reconnection = false;
                socket = IO.socket(Constants.ServerHost + "/msg",options);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public static RoomInfo roomInfo = new RoomInfo();
    public static Socket socket;


}
