package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.adapter.WaitRoomAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.util.ScreenUtils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
    private Button cancel;
    private ToggleButton ready;
    private TextView username,nickname,scoreText,title;
    private View profile;
    private PopupWindow popupWindow;
    private SimpleDraweeView userHead;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MainApplication.roomInfo.update();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void prepareViews() {
        setContentView(R.layout.room);

        waitList = (GridView) findViewById(R.id.gv_waitingList);
        cancel = (Button) findViewById(R.id.ib_waitRoom_cancel);
        ready = (ToggleButton) findViewById(R.id.tb_waitRoom_ready);
        adapter = new WaitRoomAdapter(this, MainApplication.roomInfo.getUsers());
        waitList.setAdapter(adapter);
        //个人信息popupWindow
        profile=getLayoutInflater().inflate(R.layout.user_detail,null);
        userHead= (SimpleDraweeView) profile.findViewById(R.id.sdv_userInfo_userHead);
        nickname= (TextView) profile.findViewById(R.id.tv_userInfo_nickname);
        username= (TextView) profile.findViewById(R.id.tv_userInfo_username);
        scoreText= (TextView) profile.findViewById(R.id.tv_userInfo_score);
        title= (TextView) profile.findViewById(R.id.tv_userInfo_title);
        popupWindow = new PopupWindow(profile, ScreenUtils.getScreenWidth(this)*3/4,ScreenUtils.getScreenHeight(this)*2/3);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        waitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserInfo userInfo = MainApplication.roomInfo.getUsers().get(i);
                username.setText(userInfo.getUsername());
                nickname.setText(userInfo.getNickname());
                userHead.setImageURI(userInfo.getHead());
                scoreText.setText(userInfo.getScore()+"");
                title.setText(userInfo.getTitle());
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
            }
        });
        cancel.setOnClickListener(this);
        ready.setOnCheckedChangeListener(this);
    }

    protected void prepareSocket() {

        MainApplication.socket
                .on("enterRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            SoundEffectManager.play(R.raw.enter_room);//音效
                            ParseQuery<UserInfo> query = ParseQuery.getQuery(UserInfo.class);
                            final List<String> userIds = new ArrayList<String>();
                            final Map<String,Boolean> readys = new HashMap<>();

                            JSONArray array = (JSONArray) args[0];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject user = array.getJSONObject(i);
                                String userId = (String) user.get("userId");
                                userIds.add(userId);
                                readys.put(userId,user.getBoolean("ready"));

                            }
                            query.whereContainedIn("objectId", userIds).findInBackground(new FindCallback<UserInfo>() {
                                @Override
                                public void done(List<UserInfo> objects, ParseException e) {
                                    for (UserInfo userInfo : objects) {
                                        userInfo.getGameRole().setReady(readys.get(userInfo.getObjectId()));
                                        MainApplication.roomInfo.getUsers().add(userInfo);
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
                        ParseQuery<UserInfo> query = ParseQuery.getQuery(UserInfo.class);
                        query.whereEqualTo("objectId", userId).getFirstInBackground(new GetCallback<UserInfo>() {
                            @Override
                            public void done(UserInfo object, ParseException e) {
                                MainApplication.roomInfo.getUsers().add(object);
                                refreshUI(adapter);
                            }
                        });
                    }
                })
                .on("leaveRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        UserInfo userInfo = MainApplication.roomInfo.findUserInRoom(userId);
                        MainApplication.roomInfo.getUsers().remove(userInfo);
                        refreshUI(adapter);
                    }
                })
                .on("prepare", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        MainApplication.roomInfo.findUserInRoom(userId).getGameRole().setReady(true);
                        refreshUI(adapter);
                    }
                })
                .on("unprepare", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        MainApplication.roomInfo.findUserInRoom(userId).getGameRole().setReady(false);
                        refreshUI(adapter);
                    }
                })
                .on("willstart", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(RoomActivity.this, GameMainActivity.class));
                                ready.setChecked(false);
                            }
                        });

                    }
                });
            MainApplication.socket.emit("enterRoom", MainApplication.roomInfo.getRoomId(), Me.getUserId());
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
            MainApplication.socket.emit("prepare", MainApplication.roomInfo.getRoomId(), Me.getUserId());

        } else {
            MainApplication.socket.emit("unprepare", MainApplication.roomInfo.getRoomId(), Me.getUserId());

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.roomInfo.getUsers().clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveRoom();
        finish();
    }

    private void leaveRoom() {
        MainApplication.socket.emit("leaveRoom", MainApplication.roomInfo.getRoomId(), Me.getUserId());
    }
}
