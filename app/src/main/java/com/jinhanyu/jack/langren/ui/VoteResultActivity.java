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
        if(finalUserName==null)
            tv_voteResult.setText("今晚谁都不杀");
        else
            tv_voteResult.setText(finalUserName+" 被杀");

        adapter = new VoteResultAdapter(this,MainApplication.roomInfo.getVoteResults());
        listView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.roomInfo.getVoteResults().clear();
    }
}
