package com.jinhanyu.jack.langren.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.SelectRoomAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomActivity extends AppCompatActivity{
    private GridView roomList;
    private SelectRoomAdapter adapter;
    private List<RoomInfo> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_room);
        list=new ArrayList<>();
        roomList= (GridView) findViewById(R.id.gv_roomList);
        adapter=new SelectRoomAdapter(this,list);
        roomList.setAdapter(adapter);
    }
}
