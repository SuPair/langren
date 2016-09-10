package com.jinhanyu.jack.langren;


import com.jinhanyu.jack.langren.receiver.AudioReceiver;
import com.jinhanyu.jack.langren.sender.AudioRecorder;
import com.jinhanyu.jack.langren.sender.AudioSender;

import io.socket.client.Socket;

public class AudioWrapper {

	private AudioRecorder audioRecorder;
	private AudioReceiver audioReceiver;

	private static AudioWrapper instanceAudioWrapper;

	private AudioWrapper() {
	}

	public static AudioWrapper getInstance() {
		if (null == instanceAudioWrapper) {
			instanceAudioWrapper = new AudioWrapper();
		}
		return instanceAudioWrapper;
	}

	public void startRecord(Socket socket) {
		if (null == audioRecorder) {
			AudioSender.setSocket(socket);
			audioRecorder = new AudioRecorder();
		}
		audioRecorder.startRecording();
	}

	public void stopRecord() {
		if (audioRecorder != null)
			audioRecorder.stopRecording();
	}

	public void startListen(Socket socket) {
		if (null == audioReceiver) {
			audioReceiver = new AudioReceiver(socket);
		}
		audioReceiver.startRecieving();
	}

	public void stopListen() {
		if (audioRecorder != null)
			audioRecorder.stopRecording();
	}
}
