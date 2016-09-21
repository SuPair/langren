package com.jinhanyu.jack.langren.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.GuardAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GuardActivity extends AppCompatActivity implements ActionPerformer{
    private GuardAdapter adapter;
    private ListView listView;
    private TickTimer tickTimer;
    private TextView time_label;
    private String guardUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guard);
        listView = (ListView) findViewById(R.id.guard_listView);
        time_label = (TextView) findViewById(R.id.time_label);
        adapter = new GuardAdapter(this, MainApplication.roomInfo.getUsers());
        listView.setAdapter(adapter);

        tickTimer = new TickTimer(time_label,15,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                MainApplication.socket.emit("guard",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),guardUserId);
            }
        };
        tickTimer.startTick();
    }

    @Override
    public void doAction(Object... params) {
        guardUserId = (String) params[0];
        time_label.setText("守卫完成");
    }
}
