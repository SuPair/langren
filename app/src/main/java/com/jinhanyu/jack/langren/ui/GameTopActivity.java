package com.jinhanyu.jack.langren.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GameTopAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GameTopActivity extends AppCompatActivity implements View.OnClickListener {
    private Button back;
    private ListView listView;
    private List<UserInfo> list;
    private GameTopAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_top);
        list = new ArrayList<>();
        back = (Button) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.top_listView);
        adapter = new GameTopAdapter(this, list);
        listView.setAdapter(adapter);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
