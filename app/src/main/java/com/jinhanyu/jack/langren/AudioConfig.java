package com.jinhanyu.jack.langren;

import android.media.AudioFormat;
import android.media.MediaRecorder;

public class AudioConfig {

	/**
	 * Player Configure
	 */
	public static final int SAMPLERATE = 11025;// 8KHZ
	public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	/**
	 * Recorder Configure
	 */
	public static final int AUDIO_RESOURCE = MediaRecorder.AudioSource.MIC;

}
