package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.HunterAdapter;

public class HunterActivity extends AppCompatActivity {
    private ListView listView;
    private HunterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hunter);
        listView = (ListView) findViewById(R.id.hunter_listView);
        adapter = new HunterAdapter(this, MainApplication.currentRoomUsers);
        listView.setAdapter(adapter);

    }
}
