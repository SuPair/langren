package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.VoteResultAdapter;

public class VoteResultActivity extends AppCompatActivity {


    private VoteResultAdapter adapter;
    private ListView listView;
    private TextView tv_voteResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_result);
        listView = (ListView) findViewById(R.id.vote_result_listview);
        tv_voteResult = (TextView) findViewById(R.id.tv_vote_result);

        String finalUserName = getIntent().getStringExtra("finalUserName");
        Log.i("今晚刀的人：",finalUserName);
        tv_voteResult.setText(getIntent().getStringExtra("finalUserName"));

        adapter = new VoteResultAdapter(this,MainApplication.voteResults);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.voteResults.clear();
    }
}
