package com.jinhanyu.jack.langren;

import android.media.AudioTrack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anzhuo on 2016/9/8.
 */
public class VoiceList{

    private List<byte[]> list;
    private AudioTrack audioTrack;

    private boolean isPlaying;

    public void addData(byte[] data){
         list.add(data);
    }

    public VoiceList(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
        list = Collections.synchronizedList(new LinkedList<byte[]>());
    }

    public void startPlaying() {
          audioTrack.play();
          isPlaying = true;
          new Thread(new Runnable() {
              @Override
              public void run() {
                  while (isPlaying){
                      while (list.size() > 0) {
                          byte[] data= list.remove(0);
                          audioTrack.write(data,0,data.length);
                      }
                  }
                  audioTrack.stop();
                  audioTrack.release();
              }
          }).start();

    }

    public void stopPlaying(){
          isPlaying =false;
    }
}
