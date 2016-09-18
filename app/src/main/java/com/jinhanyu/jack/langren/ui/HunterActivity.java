package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.HunterAdapter;

public class HunterActivity extends AppCompatActivity {
         HunterAdapter hunterAdapter;
         ListView hunter_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hunter);

        hunter_listview = (ListView) findViewById(R.id.hunter_listView);

        hunterAdapter = new HunterAdapter(this,MainApplication.currentRoomUsers);
        hunter_listview.setAdapter(hunterAdapter);
    }
}
