package com.jinhanyu.jack.langren;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.components.DraweeEventTracker;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.Parse;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        progressDialog = new ProgressDialog(getApplicationContext());

        Fresco.initialize(getApplicationContext());


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("langrensha")
                .clientKey("")
                .server(ServerHost + "/parse")
                .enableLocalDataStore()
                .build()
        );

        initSocket();

        SoundEffectManager.init(this);
    }

    private ProgressDialog progressDialog;

    private Handler handler=new Handler();

    private void initSocket() {
        try {
            if (socket == null) {
                socket = IO.socket(ServerHost + "/msg");
            }
              socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.i("connected to socket io", "nice");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainApplication.this, "成功连上服务器", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    })
                      .on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                          @Override
                          public void call(Object... args) {
                              handler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      progressDialog.setMessage("正在连接服务器...");
                                      progressDialog.show();
                                  }
                              });

                          }
                      })
                      .on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                          @Override
                          public void call(Object... args) {
                              handler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(MainApplication.this, "连接超时", Toast.LENGTH_SHORT).show();
                                  }
                              });

                          }
                      })
                      .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                          @Override
                          public void call(Object... args) {
                              handler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(MainApplication.this, "连接错误", Toast.LENGTH_SHORT).show();
                                  }
                              });
                          }
                      })
                    .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            Log.i("disconnected to socket", "sorry");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainApplication.this, "连接不稳定", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    })
                    .on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                          @Override
                          public void call(Object... args) {
                              handler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      progressDialog.setMessage("正在重连服务器...");
                                      progressDialog.show();
                                  }
                              });
                          }
                      })
                     .on(Socket.EVENT_RECONNECT,new Emitter.Listener(){

                         @Override
                         public void call(Object... args) {
                             handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     progressDialog.dismiss();

                                     Toast.makeText(MainApplication.this, "重连成功", Toast.LENGTH_SHORT).show();

                                 }
                             });

                         }
                     })
                    .on("serverError", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Looper.prepare();
                            Toast.makeText(MainApplication.this, "服务器错误：" + args[0], Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    })
                    .on(Socket.EVENT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Looper.prepare();
                            Toast.makeText(MainApplication.this, "socket error", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static UserInfo userInfo = new UserInfo();
    public static RoomInfo roomInfo;
    public static Socket socket;


    private static final String ServerHost = "http://172.168.0.10:3000";
//    private static final String ServerHost = "http://langren.applinzi.com";

}
