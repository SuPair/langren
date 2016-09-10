package com.jinhanyu.jack.langren;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RecordActivity extends Activity {

    Socket socket;
    TextView tv_state;
    Button bt_finishSpeak;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        tv_state = (TextView) findViewById(R.id.tv_state);
        bt_finishSpeak = (Button) findViewById(R.id.bt_finishSpeak);

        try {
            socket = IO.socket("http://172.168.0.10:3000/msg");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on("cmd", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });
        socket.connect();

    }

    public void finishSpeak(View view) {

    }
}