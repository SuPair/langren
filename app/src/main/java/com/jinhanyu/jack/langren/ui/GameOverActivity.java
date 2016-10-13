package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.adapter.GameOverAdapter;
import com.jinhanyu.jack.langren.entity.GameResult;
import com.jinhanyu.jack.langren.entity.UserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.emitter.Emitter;

public class GameOverActivity extends CommonActivity implements View.OnClickListener {
    private Button back_hall;//返回大厅
    private Button again;//再来一局
    private ListView listView;
    private GameOverAdapter adapter;
    private TextView game_win,gameOver_myScore;
    private List<GameResult> gameResults = new ArrayList<>();


    @Override
    protected void prepareViews() {
        setContentView(R.layout.game_over);
        listView= (ListView) findViewById(R.id.game_over_listView);
        adapter = new GameOverAdapter(this,gameResults);
        listView.setAdapter(adapter);
        game_win = (TextView) findViewById(R.id.game_win);
        gameOver_myScore = (TextView) findViewById(R.id.game_over_myScore);
        back_hall = (Button) findViewById(R.id.back_hall);
        again = (Button) findViewById(R.id.again);
        back_hall.setOnClickListener(this);
        again.setOnClickListener(this);
        SoundEffectManager.play(R.raw.gameover);
        SoundEffectManager.looping(true);

    }

    @Override
    protected void prepareSocket() {
        MainApplication.socket.on("gameOverResult", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject res = (JSONObject) args[0];
                            int victory = res.getInt("victory");
                            if (victory == 1) {
                                game_win.setText("狼人胜利");
                            } else {
                                game_win.setText("好人胜利");
                            }
                            JSONArray array = res.getJSONArray("returnResults");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                String userId = obj.getString("userId");
                                int type = obj.getInt("type");
                                int score = obj.getInt("score");
                                GameResult gameResult = new GameResult();
                                UserInfo user = MainApplication.roomInfo.findUserInRoom(userId);
                                gameResult.setHead(user.getHead());
                                gameResult.setNickname(user.getNickname());
                                gameResult.setType(type);
                                gameResult.setScore(score);
                                gameResults.add(gameResult);
                                if(userId.equals(Me.getUserId())){
                                    gameOver_myScore.setText(Me.getScore()+score+"");
                                }
                            }
                            Collections.sort(gameResults);
                            Collections.reverse(gameResults);
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void unbindSocket() {
        MainApplication.socket.off("gameOverResult");
    }


    private void backToHall(){
        SoundEffectManager.looping(false);
        SoundEffectManager.stop();
        MainApplication.socket.emit("leaveRoom",MainApplication.roomInfo.getRoomId(), Me.getUserId());
        MainApplication.roomInfo.resetRoom();
        startActivity(new Intent(this,SelectRoomActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_hall:
                backToHall();
                break;
            case R.id.again:
                SoundEffectManager.looping(false);
                SoundEffectManager.stop();
                MainApplication.roomInfo.resetRoom();
                startActivity(new Intent(this, RoomActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
       backToHall();
    }
}
