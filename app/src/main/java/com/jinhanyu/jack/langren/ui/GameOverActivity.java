package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GameOverAdapter;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {
    private Button back_hall;//返回大厅
    private Button again;//再来一局
    private ListView listView;
    private GameOverAdapter adapter;
    private MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        listView= (ListView) findViewById(R.id.game_over_listView);
        adapter = new GameOverAdapter(this,MainApplication.roomInfo.getUsers());
        listView.setAdapter(adapter);

        back_hall = (Button) findViewById(R.id.back_hall);
        again = (Button) findViewById(R.id.again);

        back_hall.setOnClickListener(this);
        again.setOnClickListener(this);
        player = MediaPlayer.create(this, R.raw.gameover);
        player.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_hall:
                MainApplication.socket.emit("leaveRoom",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId());
                player.stop();
                finish();
                break;
            case R.id.again:
                Intent intent1 = new Intent(this, RoomActivity.class);
                player.stop();
                startActivity(intent1);
                finish();
                break;
        }
    }
}
