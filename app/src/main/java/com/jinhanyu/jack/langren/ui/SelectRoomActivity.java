package com.jinhanyu.jack.langren.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.SelectRoomAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SelectRoomActivity extends CommonActivity implements View.OnClickListener {
    private GridView roomList;
    private SelectRoomAdapter adapter;
    private List<RoomInfo> list;
    private ImageView createRoom;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };
    private View view;
    private EditText et_room_name;


    @Override
    protected void prepareViews() {
        setContentView(R.layout.select_room);
        list = new ArrayList<>();
        roomList = (GridView) findViewById(R.id.gv_roomList);
        adapter = new SelectRoomAdapter(this, list);
        roomList.setAdapter(adapter);
        createRoom = (ImageView) findViewById(R.id.iv_createRoom);
        createRoom.setOnClickListener(this);
        view = getLayoutInflater().inflate(R.layout.create_room, null);
        et_room_name = (EditText) view.findViewById(R.id.et_room_name);
    }

    protected void prepareSocket() {
        MainApplication.socket
                .on("login", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("aaa", "aaa");
                        try {
                            JSONArray array = (JSONArray) args[0];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                RoomInfo info = new RoomInfo();
                                info.setRoomId(obj.getString("roomId"));
                                info.setRoomName(obj.getString("name"));
                                info.setPeopleNum(obj.getInt("currentCount"));
                                list.add(info);
                            }

                            adapter.notifyDataSetChanged();
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
                        handler.sendEmptyMessage(0);

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
                        handler.sendEmptyMessage(0);
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
                        handler.sendEmptyMessage(0);
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

                        handler.sendEmptyMessage(0);

                    }
                });


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
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("创建房间")
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
                dialog.show();
        }
    }
}
