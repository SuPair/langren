package com.jinhanyu.jack.langren.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.adapter.SelectRoomAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.util.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SelectRoomActivity extends CommonActivity implements View.OnClickListener {
    private GridView roomList;
    private SelectRoomAdapter adapter;
    private List<RoomInfo> list;
    private ImageView createRoom, settings;
    private TextView game_top;
    private SimpleDraweeView head, userHead;
    private TextView username, nickname, scoreText, title;
    private View view, profile, modify_ip;
    private AlertDialog dialog,modify_ip_dialog;
    private PopupWindow popupWindow;
    private View cannot_connect;
    private boolean isFetching = true;


    private void restartApplication() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Me.update();
    }

    private void populateUserInfoToWidgets() {
        userHead.setImageURI(Me.getHead());
        nickname.setText(Me.getNickname());
        username.setText(Me.getUsername());
        scoreText.setText(Me.getScore() + "");
        title.setText(Me.getTitle());
    }

    @Override
    protected void prepareViews() {
        setContentView(R.layout.select_room);
        watchNetworkState();

        modify_ip = getLayoutInflater().inflate(R.layout.modify_ip, null);
        final EditText et_new_ip_address = (EditText) modify_ip.findViewById(R.id.et_new_ip_address);
        et_new_ip_address.setText(MainApplication.ServerHost);
        modify_ip_dialog = new AlertDialog.Builder(SelectRoomActivity.this).setTitle("修改ip,确定后重启应用").setView(modify_ip)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String new_ip_address = et_new_ip_address.getText().toString().trim();
                        if (!new_ip_address.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
                            Toast.makeText(SelectRoomActivity.this, "格式不对", Toast.LENGTH_SHORT).show();
                        } else {
                            getSharedPreferences("ip", MODE_APPEND).edit().putString("ip", new_ip_address).commit();
                            restartApplication();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        cannot_connect = findViewById(R.id.cannot_connect);
        cannot_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify_ip_dialog.show();
            }
        });

        list = new ArrayList<>();
        roomList = (GridView) findViewById(R.id.gv_roomList);
        game_top = (TextView) findViewById(R.id.game_top);
        head = (SimpleDraweeView) findViewById(R.id.sdv_userHead);
        adapter = new SelectRoomAdapter(this, list);
        roomList.setAdapter(adapter);
        createRoom = (ImageView) findViewById(R.id.iv_createRoom);
        createRoom.setOnClickListener(this);
        game_top.setOnClickListener(this);
        head.setOnClickListener(this);
        head.setImageURI(Me.getHead());

        //个人信息popupWindow
        profile = getLayoutInflater().inflate(R.layout.user_detail, null);
        userHead = (SimpleDraweeView) profile.findViewById(R.id.sdv_userInfo_userHead);
        nickname = (TextView) profile.findViewById(R.id.tv_userInfo_nickname);
        username = (TextView) profile.findViewById(R.id.tv_userInfo_username);
        scoreText = (TextView) profile.findViewById(R.id.tv_userInfo_score);
        title = (TextView) profile.findViewById(R.id.tv_userInfo_title);
        settings = (ImageView) profile.findViewById(R.id.iv_userInfo_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(this);
        popupWindow = new PopupWindow(profile, ScreenUtils.getScreenWidth(this) * 3 / 4, ScreenUtils.getScreenHeight(this) * 2 / 3);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());


        //创建房间对话框
        view = getLayoutInflater().inflate(R.layout.create_room, null);
        final EditText et_room_name;
        et_room_name = (EditText) view.findViewById(R.id.et_room_name);
        et_room_name.setText(Me.getNickname()+"的房间");
        final CheckBox cb_wizard = (CheckBox) view.findViewById(R.id.cb_wizard);
        final CheckBox cb_predictor = (CheckBox) view.findViewById(R.id.cb_predictor);
        final CheckBox cb_guard = (CheckBox) view.findViewById(R.id.cb_guard);
        final CheckBox cb_hunter = (CheckBox) view.findViewById(R.id.cb_hunter);
        final NumberPicker np_wolf_count = (NumberPicker) view.findViewById(R.id.np_wolf_count);
        np_wolf_count.setMinValue(1);
        np_wolf_count.setMaxValue(4);
        final NumberPicker np_citizen_count = (NumberPicker) view.findViewById(R.id.np_citizen_count);
        np_citizen_count.setMinValue(0);
        np_citizen_count.setMaxValue(4);
        dialog = new AlertDialog.Builder(this).setTitle("创建房间")
                .setView(view)
                .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String roomName = et_room_name.getText().toString();
                        if (TextUtils.isEmpty(roomName))
                            Toast.makeText(SelectRoomActivity.this, "房间名称不能为空", Toast.LENGTH_SHORT).show();
                        else if(!cb_wizard.isChecked()&&!cb_predictor.isChecked()&&!cb_guard.isChecked()&&!cb_hunter.isChecked()&& np_citizen_count.getValue()==0){
                            Toast.makeText(SelectRoomActivity.this, "好人阵营没有人！", Toast.LENGTH_SHORT).show();
                        }else{
                            MainApplication.socket.emit("createRoom", roomName, cb_wizard.isChecked(), cb_predictor.isChecked(), cb_guard.isChecked(), cb_hunter.isChecked(), np_wolf_count.getValue(), np_citizen_count.getValue());

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
                                JSONArray types =obj.getJSONArray("types");
                                for (int j = 0; j < types.length(); j++) {
                                    info.getTypes().add(GameRole.Type.values()[(int) types.get(j)]);
                                }
                                list.add(info);
                            }
                            isFetching = false;
                            refreshUI(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .on("createRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            RoomInfo info = new RoomInfo();
                            info.setRoomId((String) args[0]);
                            info.setName((String) args[1]);
                            info.setCurrentCount((Integer) args[2]);
                            info.setMaxCount((Integer) args[3]);
                            JSONArray types = (JSONArray) args[4];
                            for (int i = 0; i < types.length(); i++) {
                                info.getTypes().add(GameRole.Type.values()[(int) types.get(i)]);
                            }
                            list.add(info);
                            refreshUI(adapter);
                            //进入房间
                            MainApplication.roomInfo = info;
                            startActivity(new Intent(SelectRoomActivity.this, RoomActivity.class));
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                })
                .on("destroyRoom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String roomId = (String) args[0];
                        while (isFetching);
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
                        try {
                            RoomInfo info = new RoomInfo();
                            info.setRoomId((String) args[0]);
                            info.setName((String) args[1]);
                            info.setCurrentCount((Integer) args[2]);
                            info.setMaxCount((Integer) args[3]);
                            JSONArray types = (JSONArray) args[4];
                            for (int i = 0; i < types.length(); i++) {
                                info.getTypes().add(GameRole.Type.values()[(int) types.get(i)]);
                            }
                            list.add(info);
                            refreshUI(adapter);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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



    private boolean hasConnected =false;

    private void moreListener() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                 if(!hasConnected)
                     Toast.makeText(SelectRoomActivity.this, "没连接上,ip可能有问题", Toast.LENGTH_SHORT).show();
            }
        },5000);
        
        MainApplication.socket
                .on("serverError", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "游戏错误：" + args[0], Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .on(Socket.EVENT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "服务器可能崩了...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .once(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("connected", "haha");
                        hasConnected = true;
                        MainApplication.socket.emit("login", Me.getUserId());
                    }
                })
                .once(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "socket断开了" + args[0], Toast.LENGTH_SHORT).show();
                                MainApplication.socket.connect();
                            }
                        });

                    }
                })
                .on("alreadyInRoomTag", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress("正在重连...");
                            }
                        });
                    }
                })
                .on("alreadyInRoom", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgress();
                                    }
                                });
                                String roomId = (String) args[0];
                                for (RoomInfo roomInfo : list) {
                                    if (roomInfo.getRoomId().equals(roomId)) {
                                        MainApplication.roomInfo = roomInfo;
                                        break;
                                    }
                                }
                                startActivity(new Intent(SelectRoomActivity.this, RoomActivity.class));
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
                .off("roomChange")
                .off("alreadyInRoom")
                .off("alreadyInRoomTag");
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
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 140);
                break;
            case R.id.iv_userInfo_settings:
                //这里点击设置账号
                SoundEffectManager.play(R.raw.user_detail);
                startActivityForResult(new Intent(SelectRoomActivity.this, PlayerSetActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Me.update();
            userHead.setImageURI(Me.getHead());
            head.setImageURI(Me.getHead());
            nickname.setText(Me.getNickname());
        }
    }
}
