package com.jinhanyu.jack.langren.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.adapter.SelectRoomAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.util.ScreenUtils;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SelectRoomActivity extends CommonActivity implements View.OnClickListener {
    private GridView roomList;
    private SelectRoomAdapter adapter;
    private List<RoomInfo> list;
    private ImageView createRoom,settings;
    private TextView game_top;
    private SimpleDraweeView head,userHead;
    private TextView username,nickname,scoreText,title;
    private View view,profile;
    private EditText et_room_name;
    private AlertDialog dialog;
    private PopupWindow popupWindow;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Me.update();
    }

    private void populateUserInfoToWidgets(){
        userHead.setImageURI(Me.getHead());
        nickname.setText(Me.getNickname());
        username.setText(Me.getUsername());
        scoreText.setText(Me.getScore()+"");
        title.setText(Me.getTitle());
    }

    @Override
    protected void prepareViews() {
        setContentView(R.layout.select_room);
        watchNetworkState();

        list = new ArrayList<>();
        roomList = (GridView) findViewById(R.id.gv_roomList);
        game_top = (TextView) findViewById(R.id.game_top);
        head= (SimpleDraweeView) findViewById(R.id.sdv_userHead);
        adapter = new SelectRoomAdapter(this, list);
        roomList.setAdapter(adapter);
        createRoom = (ImageView) findViewById(R.id.iv_createRoom);
        createRoom.setOnClickListener(this);
        game_top.setOnClickListener(this);
        head.setOnClickListener(this);
        head.setImageURI(Me.getHead());

        //个人信息popupWindow
        profile=getLayoutInflater().inflate(R.layout.user_detail,null);
        userHead= (SimpleDraweeView) profile.findViewById(R.id.sdv_userInfo_userHead);
        nickname= (TextView) profile.findViewById(R.id.tv_userInfo_nickname);
        username= (TextView) profile.findViewById(R.id.tv_userInfo_username);
        scoreText= (TextView) profile.findViewById(R.id.tv_userInfo_score);
        title= (TextView) profile.findViewById(R.id.tv_userInfo_title);
        settings= (ImageView) profile.findViewById(R.id.iv_userInfo_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(this);
        popupWindow = new PopupWindow(profile, ScreenUtils.getScreenWidth(this)*3/4,ScreenUtils.getScreenHeight(this)*2/3);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());


        //创建房间对话框
        view = getLayoutInflater().inflate(R.layout.create_room, null);
        et_room_name = (EditText) view.findViewById(R.id.et_room_name);
        dialog = new AlertDialog.Builder(this).setTitle("创建房间")
                .setView(view)
                .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String roomName = et_room_name.getText().toString();
                        if (TextUtils.isEmpty(roomName))
                            Toast.makeText(SelectRoomActivity.this, "房间名称不能为空", Toast.LENGTH_SHORT).show();
                        else {
                            MainApplication.socket.emit("createRoom", roomName, Me.getUserId());
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    protected void prepareSocket() {

        MainApplication.socket
                .once("login", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            JSONArray array = (JSONArray) args[0];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                RoomInfo info = new RoomInfo();
                                info.setRoomId(obj.getString("roomId"));
                                info.setName(obj.getString("name"));
                                info.setCurrentCount(obj.getInt("currentCount"));
                                info.setMaxCount(obj.getInt("maxCount"));
                                list.add(info);
                            }
                             refreshUI(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .on("createRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        RoomInfo info = new RoomInfo();
                        info.setRoomId((String) args[0]);
                        info.setName((String) args[1]);
                        info.setCurrentCount((Integer) args[2]);
                        info.setMaxCount((Integer) args[3]);

                        list.add(info);
                        refreshUI(adapter);
                        //进入房间
                        MainApplication.roomInfo = info;
                        startActivity(new Intent(SelectRoomActivity.this, RoomActivity.class));


                    }
                })
                .on("destroyRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String roomId = (String) args[0];

                        int i;
                        for (i = 0; i < list.size(); i++) {
                            if (list.get(i).getRoomId().equals(roomId))
                                break;
                        }
                        list.remove(i);
                        refreshUI(adapter);
                    }
                })
                .on("newRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        RoomInfo info = new RoomInfo();
                        info.setRoomId((String) args[0]);
                        info.setName((String) args[1]);
                        info.setCurrentCount((Integer) args[2]);
                        info.setMaxCount((Integer) args[3]);

                        list.add(info);

                        refreshUI(adapter);
                    }
                })
                .on("roomChange", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String roomId = (String) args[0];
                        int diff = (int) args[1];
                        int i;
                        for (i = 0; i < list.size(); i++) {
                            if (list.get(i).getRoomId().equals(roomId))
                                break;
                        }
                        list.get(i).changePeopleNum(diff);

                        refreshUI(adapter);

                    }
                });
        moreListener();
        MainApplication.socket.connect();

    }


    private void moreListener(){
        MainApplication.socket.on("serverError", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "服务器错误：" + args[0], Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "socket error", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("connected", "haha");
                MainApplication.socket.emit("login", Me.getUserId());
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "socket断开" + args[0], Toast.LENGTH_SHORT).show();
                MainApplication.socket.connect();
                Looper.loop();
            }
        }).on("reJoinGame", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            JSONObject room = (JSONObject) args[0];
                            MainApplication.roomInfo.setRoomId(room.getString("roomId"));
                            MainApplication.roomInfo.setMaxCount(room.getInt("maxCount"));
                            MainApplication.roomInfo.setCurrentCount(room.getInt("currentCount"));
                            MainApplication.roomInfo.setName(room.getString("name"));
                            MainApplication.roomInfo.setHasPoisoned(room.getBoolean("hasPoisoned"));
                            MainApplication.roomInfo.setHasSaved(room.getBoolean("hasSaved"));
                            try {
                                MainApplication.roomInfo.setPoliceId(room.getString("policeId"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ParseQuery<UserInfo> query = ParseQuery.getQuery(UserInfo.class);
                            final List<String> userIds = new ArrayList<String>();
                            final Map<String, Boolean> readys = new HashMap<>();

                            JSONArray array = room.getJSONArray("users");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject user = array.getJSONObject(i);
                                String userId = (String) user.get("userId");
                                userIds.add(userId);
                                readys.put(userId, user.getBoolean("dead"));

                            }
                            synchronized (MainApplication.roomInfo) {
                                MainApplication.roomInfo.getUsers().clear();
                                List<UserInfo> objects = query.whereContainedIn("objectId", userIds).find();
                                for (UserInfo userInfo : objects) {
                                    userInfo.getGameRole().setDead(readys.get(userInfo.getObjectId()));
                                    MainApplication.roomInfo.getUsers().add(userInfo);
                                }
                            }
                            int type = (int) args[1];
                            MainApplication.roomInfo.findMeInRoom().getGameRole().setType(type);
                            boolean isFromDark = (boolean) args[2];
                            JSONArray companys = (JSONArray) args[3];
                            for (int i = 0; i < companys.length(); i++) {
                                String userId = (String) companys.get(i);
                                MainApplication.roomInfo.findUserInRoom(userId).getGameRole().setType(1);
                            }
                            Log.i("reJoinGame", room.toString());
                            Log.i("willgotoGameMain", "true");
                            startActivity(new Intent(getApplicationContext(), GameMainActivity.class).putExtra("reJoinGame", true).putExtra("isFromDark", isFromDark));
                            MainApplication.socket.off("reJoinGame");
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "重新加入了游戏", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

        );
    }

    @Override
    protected void unbindSocket() {

        MainApplication.socket
                .off("login")
                .off("createRoom")
                .off("newRoom")
                .off("destroyRoom")
                .off("roomChange");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_createRoom:
                SoundEffectManager.play(R.raw.user_detail);
                dialog.show();
                break;
            case R.id.game_top:
                Intent intent = new Intent(this, GameTopActivity.class);
                startActivity(intent);
                break;
            case R.id.sdv_userHead:
                SoundEffectManager.play(R.raw.user_detail);
                populateUserInfoToWidgets();
                popupWindow.showAtLocation(view,Gravity.CENTER,0,140);
                break;
            case R.id.iv_userInfo_settings:
            //这里点击设置账号
                SoundEffectManager.play(R.raw.user_detail);
              startActivityForResult(new Intent(SelectRoomActivity.this,PlayerSetActivity.class),0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Me.update();
            userHead.setImageURI(Me.getHead());
            head.setImageURI(Me.getHead());
            nickname.setText(Me.getNickname());
        }
    }
}
