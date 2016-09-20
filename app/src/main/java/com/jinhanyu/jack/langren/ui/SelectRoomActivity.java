package com.jinhanyu.jack.langren.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.SelectRoomAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.util.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    protected void prepareViews() {
        setContentView(R.layout.select_room);
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
        head.setImageURI(MainApplication.userInfo.getHead());

        //个人信息popupWindow
        profile=getLayoutInflater().inflate(R.layout.user_detail,null);
        userHead= (SimpleDraweeView) profile.findViewById(R.id.sdv_userInfo_userHead);
        userHead.setImageURI(MainApplication.userInfo.getHead());
        nickname= (TextView) profile.findViewById(R.id.tv_userInfo_nickname);
        nickname.setText(MainApplication.userInfo.getNickname());
        username= (TextView) profile.findViewById(R.id.tv_userInfo_username);
        username.setText(MainApplication.userInfo.getName());
        scoreText= (TextView) profile.findViewById(R.id.tv_userInfo_score);
        scoreText.setText(MainApplication.userInfo.getScore()+"");
        title= (TextView) profile.findViewById(R.id.tv_userInfo_title);
        title.setText(MainApplication.userInfo.getTitle());
        settings= (ImageView) profile.findViewById(R.id.iv_userInfo_settings);
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
                            MainApplication.socket.emit("createRoom", roomName, MainApplication.userInfo.getUserId());
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
                .on("login", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            JSONArray array = (JSONArray) args[0];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                RoomInfo info = new RoomInfo();
                                info.setRoomId(obj.getString("roomId"));
                                info.setRoomName(obj.getString("name"));
                                info.setPeopleNum(obj.getInt("currentCount"));
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
                        info.setRoomName((String) args[1]);
                        info.setPeopleNum((Integer) args[2]);

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
                        info.setRoomName((String) args[1]);
                        info.setPeopleNum((Integer) args[2]);

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

        MainApplication.socket.connect();
        MainApplication.socket.emit("login", MainApplication.userInfo.getUserId());
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
                dialog.show();
                break;
            case R.id.game_top:
                Intent intent = new Intent(this, GameTopActivity.class);
                startActivity(intent);
                break;
            case R.id.sdv_userHead:
                popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
                break;
            case R.id.iv_userInfo_settings:
            //这里点击切换账号
                break;
        }
    }
}
