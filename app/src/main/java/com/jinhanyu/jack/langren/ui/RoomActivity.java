package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.WaitRoomAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import static com.jinhanyu.jack.langren.R.id.gv_waitingList;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private List<UserInfo> list;
    private GridView waitList;
    private WaitRoomAdapter adapter;
    private ImageButton cancel;
    private ToggleButton ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room);
        list=new ArrayList<>();
        waitList= (GridView) findViewById(R.id.gv_waitingList);
        cancel= (ImageButton) findViewById(R.id.ib_waitRoom_cancel);
        ready= (ToggleButton) findViewById(R.id.ib_waitRoom_ready);
        adapter=new WaitRoomAdapter(this,list);
        waitList.setAdapter(adapter);
        cancel.setOnClickListener(this);
        ready.setOnCheckedChangeListener(this);
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
            MainApplication.socket.emit("prepare",null);
        }else {
            MainApplication.socket.emit("unprepare",null);
        }
    }
}
