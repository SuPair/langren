package com.jinhanyu.jack.langren.receiver;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AudioReceiver{
	String LOG = "AudioReceiver";


	private Socket socket;
	AudioDecoder decoder;


	public AudioReceiver(Socket socket){
		  this.socket = socket;
		  decoder = AudioDecoder.getInstance();
	}

	/*
	 * 开始接收数据
	 */
	public void startRecieving() {
		// 在接收前，要先启动解码器
		decoder.startDecoding();
		socket.on("blob",new Emitter.Listener(){

			@Override
			public void call(Object... args) {
				JSONObject obj = (JSONObject) args[0];
				try {
					byte[] bytes = (byte[]) obj.get("binary");
					decoder.addData(bytes, bytes.length);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * 停止接收数据
	 */
	public void stopRecieving() {
		decoder.stopDecoding();
		release();
		Log.e(LOG, "stop recieving");
	}

	/*
	 * 释放资源
	 */
	private void release() {
		socket.off("blob");
	}

}
