package com.jinhanyu.jack.langren.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.WizardAdapter;

public class WizardActivity extends AppCompatActivity {
 private ListView listView;
    private WizardAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard);
        listView= (ListView) findViewById(R.id.wizard_listView);
        adapter=new WizardAdapter(this, MainApplication.currentRoomUsers);
        listView.setAdapter(adapter);
    }
}
