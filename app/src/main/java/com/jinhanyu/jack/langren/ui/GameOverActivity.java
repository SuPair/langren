package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.adapter.GameOverAdapter;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {
    private Button back_hall;//返回大厅
    private Button again;//再来一局
    private ListView listView;
    private GameOverAdapter adapter;
    private TextView game_win;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        listView= (ListView) findViewById(R.id.game_over_listView);
        adapter = new GameOverAdapter(this,MainApplication.roomInfo.getUsers());
        listView.setAdapter(adapter);
        game_win = (TextView) findViewById(R.id.game_win);
        game_win.setText(getIntent().getStringExtra("victory"));

        back_hall = (Button) findViewById(R.id.back_hall);
        again = (Button) findViewById(R.id.again);

        back_hall.setOnClickListener(this);
        again.setOnClickListener(this);
        SoundEffectManager.play(R.raw.gameover);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_hall:
                SoundEffectManager.stop();
                MainApplication.socket.emit("leaveRoom",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId());
                startActivity(new Intent(this,SelectRoomActivity.class));
                finish();
                break;
            case R.id.again:
                Intent intent1 = new Intent(this, RoomActivity.class).putExtra("alreadyInRoom",true);
                SoundEffectManager.stop();
                startActivity(intent1);
                finish();
                break;
        }
    }
}
