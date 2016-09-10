package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.SelectRoomAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SelectRoomActivity extends AppCompatActivity{
    private GridView roomList;
    private SelectRoomAdapter adapter;
    private List<RoomInfo> list;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_room);



        list=new ArrayList<>();
        roomList= (GridView) findViewById(R.id.gv_roomList);
        adapter=new SelectRoomAdapter(this,list);
        roomList.setAdapter(adapter);

        prepareSocket();

    }

    private void prepareSocket(){
        MainApplication.socket.on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("aaa","aaa");
                try {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        RoomInfo info = new RoomInfo();
                        info.setRoomName(obj.getString("name"));
                        info.setPeopleNum(obj.getInt("currentCount"));
                        list.add(info);
                    }

                    adapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).on("createRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                RoomInfo info =new RoomInfo();
                info.setRoomId((String) args[0]);
                info.setRoomName((String)args[1]);
                info.setPeopleNum((Integer) args[2]);

                list.add(info);
                handler.sendEmptyMessage(0);


            }
        }).on("destroyRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                  String roomId = (String) args[0];

                  int i;
                  for(i=0;i<list.size();i++)
                  {
                      if(list.get(i).getRoomId().equals(roomId))
                          break;
                  }

                  list.remove(i);
                  handler.sendEmptyMessage(0);
            }
        });


        MainApplication.socket.emit("login",MainApplication.userInfo.getUserId());
    }
}
