package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.VoteResultAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;

public class VoteResultActivity extends AppCompatActivity {


    private VoteResultAdapter adapter;
    private ListView listView;
    private TextView tv_voteResult;
    private TextView tv_voteType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_result);
        listView = (ListView) findViewById(R.id.vote_result_listview);
        tv_voteResult = (TextView) findViewById(R.id.tv_vote_result);
        tv_voteType = (TextView) findViewById(R.id.tv_vote_result_type);

        int type = getIntent().getIntExtra("type",-1);
        String typeText="";

        switch (type){
            case RoomInfo.VOTE_KILL:
                typeText = "被杀";
                break;
            case RoomInfo.VOTE_POLICE:
                typeText = "当选为警长";
                break;
            case RoomInfo.VOTE_WOLF:
                typeText = "被淘汰出局";
                break;
            default:
                throw new RuntimeException("投票的类型没有指定");
        }

        tv_voteType.setText(typeText);

        String finalUserName = getIntent().getStringExtra("finalUserName");
        if(finalUserName==null)
            tv_voteResult.setText("没有人");
        else
            tv_voteResult.setText(finalUserName);

        adapter = new VoteResultAdapter(this,MainApplication.roomInfo.getVoteResults());
        listView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.roomInfo.getVoteResults().clear();
    }
}
