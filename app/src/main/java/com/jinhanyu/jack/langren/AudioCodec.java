package com.jinhanyu.jack.langren;

import android.util.Log;

/**
 * Created by anzhuo on 2016/9/8.
 */
public class AudioCodec {

    static {
        System.loadLibrary("AudioCodec");
        Log.e("AudioCodec", " audioWraper库加载完毕");
    }

    public static native int audio_codec_init(int mode);

    // encode
    public static native int audio_encode(byte[] sample, int sampleOffset,
                                          int sampleLength, byte[] data, int dataOffset);

    // decode
    public static native int audio_decode(byte[] data, int dataOffset,
                                          int dataLength, byte[] sample, int sampleOffset);
}
