package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {
    private Button back_hall;//返回大厅
    private Button again;//再来一局
    private ListView listView;
    private List<UserInfo> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        list=new ArrayList<>();
        listView= (ListView) findViewById(R.id.game_over_listView);

        back_hall = (Button) findViewById(R.id.back_hall);
        again = (Button) findViewById(R.id.again);

        back_hall.setOnClickListener(this);
        again.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_hall:
                Intent intent = new Intent(this, SelectRoomActivity.class);
                startActivity(intent);
                break;
            case R.id.again:
                Intent intent1 = new Intent(this, RoomActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
