package com.jinhanyu.jack.langren;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by anzhuo on 2016/9/8.
 */
public class VoiceManager {

    private static VoiceManager manager;
    private final Socket socket;
    private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    private int frequency;
    private int audioEncoding;
    private int channel;
    private boolean isRecording;
    private int bufferSize;
    private VoiceList vl;

    public static VoiceManager getInstance(Socket socket) {
        if (manager == null)
            manager = new VoiceManager(socket);
        return manager;
    }


    public VoiceManager(Socket socket) {
        this.socket = socket;
        this.frequency = 11025;
        this.channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        this.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        this.bufferSize = AudioRecord.getMinBufferSize(frequency, channel, audioEncoding);
        this.audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channel, audioEncoding, bufferSize);
        this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,frequency,channel,audioEncoding,bufferSize,AudioTrack.MODE_STREAM);
        this.vl = new VoiceList(audioTrack);

    }

    public void startRecord(){
        audioRecord.startRecording();
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[bufferSize];
                AudioCodec.audio_codec_init(30);
                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    byte[] encodeData = new byte[bufferReadResult];
                    int encodeSize = AudioCodec.audio_encode(buffer,0,bufferReadResult,encodeData,0);
                    byte[] data = new byte[encodeSize];
                    System.arraycopy(encodeData, 0, data, 0, encodeSize);
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("binary", data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("blob", obj);

                    Log.i("record","编码前: "+bufferReadResult+"   编码后: "+encodeSize);
                }
                audioRecord.stop();
                audioRecord.release();
            }
        }).start();
    }

    public void stopRecord(){
        isRecording = false;
    }


    public void startPlay(){
        AudioCodec.audio_codec_init(30);
        socket.on("blob", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                try {
                    byte[] buffer = (byte[]) obj.get("binary");
                    byte[] decodeData = new byte[bufferSize];
                    int decodeSize = AudioCodec.audio_decode(buffer, 0,buffer.length, decodeData, 0);
                    byte[] data = new byte[decodeSize];
                    System.arraycopy(decodeData,0,data,0,decodeSize);
                    vl.addData(data);
                    Log.i("play","解码前: "+ buffer.length+"   解码后: "+decodeSize);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        vl.startPlaying();
    }

    public void stopPlay(){
        socket.off("blob");
        vl.stopPlaying();
    }
}
