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
    private int trunkSize = 480;


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
        AudioCodec.audio_codec_init(30);
    }

    public int getTrunkSize(){
        return trunkSize;
    }

    public void speak(byte[] data){
        audioTrack.write(data,0,data.length);
    }

    public void startRecord(){
        audioRecord.startRecording();
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[trunkSize];
                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0, trunkSize);

                    socket.emit("blob",MainApplication.roomInfo.getRoomId(),copyBuffer(buffer,bufferReadResult));
                    //Log.i("record","编码前: "+bufferReadResult+"   编码后: "+encodeSize);
                }
                audioRecord.stop();
                audioRecord.release();
            }
        }).start();
    }

    public byte[] copyBuffer(byte[] inputData,int actualSize){
        byte[] outputData = new byte[actualSize];
        System.arraycopy(inputData, 0, outputData, 0, actualSize);
        return outputData;
    }

    private byte[] encode(byte[] inputData,int actualSize){
        byte[] encodeData = new byte[actualSize];
        int encodeSize = AudioCodec.audio_encode(inputData,0,actualSize,encodeData,0);
        byte[] outputData = new byte[encodeSize];
        System.arraycopy(encodeData, 0, outputData, 0, encodeSize);
        return outputData;
    }





    public byte[] decode(byte[] inputData){
        byte[] decodeData = new byte[getTrunkSize()*2];
        int decodeSize = AudioCodec.audio_decode(inputData, 0,inputData.length, decodeData, 0);
        byte[] outputData = new byte[decodeSize];
        System.arraycopy(decodeData,0,outputData,0,decodeSize);
        return outputData;
    }

    public void stopRecord(){
        isRecording = false;
    }

    public void startPlay(){
        audioTrack.play();
    }

    public void stopPlay(){
        audioTrack.stop();
    }
}
