package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.WaitRoomAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.emitter.Emitter;

public class RoomActivity extends CommonActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private GridView waitList;
    private WaitRoomAdapter adapter;
    private ImageView cancel;
    private ToggleButton ready;
    private boolean isForwarding;

    @Override
    protected void prepareViews() {
        setContentView(R.layout.room);

        waitList = (GridView) findViewById(R.id.gv_waitingList);
        cancel = (ImageView) findViewById(R.id.ib_waitRoom_cancel);
        ready = (ToggleButton) findViewById(R.id.tb_waitRoom_ready);
        adapter = new WaitRoomAdapter(this, MainApplication.currentRoomUsers);
        waitList.setAdapter(adapter);
        cancel.setOnClickListener(this);
        ready.setOnCheckedChangeListener(this);
    }

    protected void prepareSocket() {

        MainApplication.socket
                .on("enterRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {

                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            List<String> userIds = new ArrayList<String>();
                            final Map<String,Boolean> readys = new HashMap<>();

                            JSONArray array = (JSONArray) args[0];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject user = array.getJSONObject(i);
                                String userId = (String) user.get("userId");
                                userIds.add(userId);
                                readys.put(userId,user.getBoolean("ready"));

                            }

                            query.whereContainedIn("objectId", userIds).findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    for (ParseUser parseUser : objects) {
                                        UserInfo info = new UserInfo();
                                        info.populateFromParseServer(parseUser);
                                        info.setReady(readys.get(parseUser.getObjectId()));
                                        MainApplication.currentRoomUsers.add(info);
                                    }

                                    refreshUI(adapter);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .on("joinRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        Log.i("join", userId);
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("objectId", userId).getFirstInBackground(new GetCallback<ParseUser>() {

                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                UserInfo info = new UserInfo();
                                info.populateFromParseServer(parseUser);
                                MainApplication.currentRoomUsers.add(info);
                                refreshUI(adapter);
                            }


                        });
                    }
                })
                .on("leaveRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        int i;
                        for (i = 0; i < MainApplication.currentRoomUsers.size(); i++) {
                            if (MainApplication.currentRoomUsers.get(i).getUserId().equals(userId))
                                break;
                        }
                        MainApplication.currentRoomUsers.remove(i);

                        refreshUI(adapter);
                    }
                })
                .on("prepare", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        MainApplication.findUserInRoom(userId).setReady(true);

                        refreshUI(adapter);
                    }
                })
                .on("unprepare", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        MainApplication.findUserInRoom(userId).setReady(false);

                        refreshUI(adapter);
                    }
                })
                .on("willstart", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        startActivity(new Intent(RoomActivity.this, GameMainActivity.class));
                        isForwarding =true;
                        finish();
                    }
                });

        MainApplication.socket.emit("enterRoom", MainApplication.roomInfo.getRoomId(), MainApplication.userInfo.getUserId());
    }

    @Override
    protected void unbindSocket() {
        MainApplication.socket
                .off("enterRoom")
                .off("joinRoom")
                .off("leaveRoom")
                .off("prepare")
                .off("unprepare")
                .off("willstart");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_waitRoom_cancel:
                leaveRoom();
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            MainApplication.socket.emit("prepare", MainApplication.roomInfo.getRoomId(), MainApplication.userInfo.getUserId());

        } else {
            MainApplication.socket.emit("unprepare", MainApplication.roomInfo.getRoomId(), MainApplication.userInfo.getUserId());

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isForwarding)
           MainApplication.currentRoomUsers.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveRoom();
        finish();
    }

    private void leaveRoom() {
        MainApplication.socket.emit("leaveRoom", MainApplication.roomInfo.getRoomId(), MainApplication.userInfo.getUserId());
    }
}
