package com.jinhanyu.jack.langren;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    public static final  String myserver = "http://172.168.0.10:3000/msg";
    String dirPath;
    Socket socket;
    TextView tv_content;
    TextView tv_state;
    EditText et_msg;

    StringBuilder sb;

    AudioWrapper audioWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        tv_content = (TextView) findViewById(R.id.tv_content);
        et_msg = (EditText) findViewById(R.id.et_msg);
        tv_state = (TextView) findViewById(R.id.tv_state);
        sb = new StringBuilder();
        dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Blob";
        initSocket();
        audioWrapper = AudioWrapper.getInstance();

    }

    private void initSocket(){
        try {
            socket = IO.socket(myserver);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("connected", "nice!");
                    tv_state.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_state.setText("connected");
                        }
                    });
                    socket.emit("pass", "hi");

                }

            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    try {
                        Log.i("received data", obj.getString("some"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).on("msg", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    String msg = (String) args[0];
                    sb.append(msg).append("\n");
                    tv_content.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_content.setText(sb.toString());
                        }
                    });

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    tv_state.post(new Runnable() {
                        @Override
                        public void run() {
                            tv_state.setText("disconnected");
                        }
                    });
                }

            }).on("cmd",new Emitter.Listener(){

                @Override
                public void call(Object... args) {
                    startActivity(new Intent(MainActivity.this,ParseActivity.class));
                }
            });
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private File byte2File(byte[] buf, String filePath, String fileName)
    {
        BufferedOutputStream bos = null;
        File file = null;
        try
        {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory())
            {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(buf);
            bos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }

    private  byte[] file2Byte(String filePath)
    {
        byte[] buffer = null;
        try
        {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return buffer;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.close();
    }

    public void sendMsg(View view) {
        String msg = et_msg.getText().toString();
        if (!TextUtils.isEmpty(msg)) {
            socket.emit("msg", msg);
            et_msg.setText("");
        }
    }

    public void sendBlob(View view){
        String file = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Download"+File.separator+"haha.jpg";
        JSONObject obj = new JSONObject();
        try {
            obj.put("binary",file2Byte(file));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("blob",obj);
    }

    public void gotoparse(View view) {
        startActivity(new Intent(this,ParseActivity.class));
    }

    public void startRecord(View view) {
//        audioWrapper.startListen(socket);
//        audioWrapper.startRecord(socket);
        startActivity(new Intent(this,RecordActivity.class));
    }

    public void stopRecord(View view) {
        audioWrapper.stopRecord();
        audioWrapper.stopListen();
    }
}
