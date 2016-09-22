package com.jinhanyu.jack.langren;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by anzhuo on 2016/9/22.
 */
public class SoundEffectManager {
    private MediaPlayer mediaPlayer;
    private Context context;


    private static SoundEffectManager instance;

    public  static SoundEffectManager getInstance(Context context){
        if(instance==null)
            instance = new SoundEffectManager(context);
        return instance;
    }

    public void play(int resId){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://"+context.getPackageName()+"/"+resId));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        mediaPlayer.stop();
    }

    public SoundEffectManager(Context context){
        this.context=context;
        mediaPlayer = new MediaPlayer();
    }



}
