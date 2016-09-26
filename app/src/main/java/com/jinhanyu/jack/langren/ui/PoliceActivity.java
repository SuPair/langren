package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.PoliceAdapter;

/**
 * Created by anzhuo on 2016/9/26.
 */
public class PoliceActivity extends AppCompatActivity {

    private PoliceAdapter adapter;
    private ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.police);
        listView = (ListView) findViewById(R.id.police_listView);
        adapter = new PoliceAdapter(this, MainApplication.roomInfo.getAliveUsers());
        listView.setAdapter(adapter);

    }
}
