package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.WaitRoomAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private GridView waitList;
    private WaitRoomAdapter adapter;
    private ImageView cancel;
    private ToggleButton ready;
    private RoomInfo roomInfo;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);

        waitList= (GridView) findViewById(R.id.gv_waitingList);
        cancel= (ImageView) findViewById(R.id.ib_waitRoom_cancel);
        ready= (ToggleButton) findViewById(R.id.tb_waitRoom_ready);
        adapter=new WaitRoomAdapter(this,MainApplication.currentRoomUsers);
        waitList.setAdapter(adapter);
        cancel.setOnClickListener(this);
        ready.setOnCheckedChangeListener(this);

        prepareSocket();
    }

    private void prepareSocket() {
        roomInfo = (RoomInfo) getIntent().getSerializableExtra("roomInfo");
        MainApplication.socket.on("enterRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    List<String> userIds = new ArrayList<String>();

                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        String userId = (String) array.get(i);
                        userIds.add(userId);
                    }
                    query.whereContainedIn("objectId",userIds).findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            for(ParseUser parseUser : objects){
                                  UserInfo info = new UserInfo();
                                  info.setUserId(parseUser.getObjectId());
                                  info.setHead((String) parseUser.get("head"));
                                  info.setName((String) parseUser.get("name"));
                                  info.setScore((Integer) parseUser.get("score"));

                                  MainApplication.currentRoomUsers.add(info);

                            }

                            handler.sendEmptyMessage(0);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).on("joinRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String userId = (String) args[0];
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId",userId).getFirstInBackground(new GetCallback<ParseUser>() {

                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        UserInfo info = new UserInfo();
                        info.setUserId(parseUser.getObjectId());
                        info.setHead((String) parseUser.get("head"));
                        info.setName((String) parseUser.get("name"));
                        info.setScore((Integer) parseUser.get("score"));
                        MainApplication.currentRoomUsers.add(info);
                        handler.sendEmptyMessage(0);
                    }


                });
            }
        }).on("leaveRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String userId = (String) args[0];
                int i;
                for ( i=0;i< MainApplication.currentRoomUsers.size();i++){
                     if(MainApplication.currentRoomUsers.get(i).getUserId().equals(userId))
                         break;
                }
                MainApplication.currentRoomUsers.remove(i);
                handler.sendEmptyMessage(0);
            }
        }).on("prepare", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String userId = (String) args[0];
                for(UserInfo info : MainApplication.currentRoomUsers){
                    if(info.getUserId().equals(userId))
                    {
                        info.setReady(true);
                        break;
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).on("unprepare", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String userId = (String) args[0];
                for(UserInfo info : MainApplication.currentRoomUsers){
                    if(info.getUserId().equals(userId))
                    {
                        info.setReady(false);
                        break;
                    }
                }
                handler.sendEmptyMessage(0);
            }
        });

        MainApplication.socket.emit("enterRoom",roomInfo.getRoomId(),MainApplication.userInfo.getUserId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_waitRoom_cancel:
                Intent intent=new Intent(this,SelectRoomActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            MainApplication.socket.emit("prepared",roomInfo.getRoomId(),MainApplication.userInfo.getUserId());

        }else {
            MainApplication.socket.emit("unprepare",roomInfo.getRoomId(),MainApplication.userInfo.getUserId());

        }
    }
}
