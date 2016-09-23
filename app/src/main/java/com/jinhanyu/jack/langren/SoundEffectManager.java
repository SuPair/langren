package com.jinhanyu.jack.langren;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by anzhuo on 2016/9/22.
 */
public class SoundEffectManager {
    private static MediaPlayer mediaPlayer;
    private static Context context;



    public static void init(Context ctx){
        mediaPlayer = new MediaPlayer();
        context = ctx;
    }


    public static void play(int resId){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://"+context.getPackageName()+"/"+resId));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void stop(){
        mediaPlayer.stop();
    }

    public static void looping(boolean choose){
        mediaPlayer.setLooping(true);
    }



}
