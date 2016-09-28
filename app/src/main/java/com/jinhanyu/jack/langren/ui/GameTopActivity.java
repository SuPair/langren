package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.ListView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.PullRefresh;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GameTopAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class GameTopActivity extends AppCompatActivity{

    private ListView listView;
    private List<UserInfo> list;
    private GameTopAdapter adapter;
    in.srain.cube.views.ptr.PtrFrameLayout iv_ptrFeame;
    PullRefresh pullRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_top);
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.top_listView);
        adapter = new GameTopAdapter(this, list);
        listView.setAdapter(adapter);

        loadData();

        //主页面下拉刷新控件 实例化
        iv_ptrFeame = (PtrFrameLayout) findViewById(R.id.iv_ptrFeame);

        pullRefresh = new PullRefresh(this);

        iv_ptrFeame.setHeaderView(pullRefresh);
        iv_ptrFeame.addPtrUIHandler(pullRefresh);
        iv_ptrFeame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_ptrFeame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameTopActivity.this,"已是最新数据！",Toast.LENGTH_SHORT).show();
                        iv_ptrFeame.refreshComplete();
                    }
                }, 2000);
            }
        });


    }

    private void loadData() {
        ParseQuery<UserInfo> query = ParseQuery.getQuery(UserInfo.class);
        query.orderByDescending("score").setLimit(10);
        query.findInBackground(new FindCallback<UserInfo>() {
            @Override
            public void done(List<UserInfo> objects, ParseException e) {
                list.addAll(objects);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }


}
