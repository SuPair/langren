package com.jinhanyu.jack.langren.sender;

import com.jinhanyu.jack.langren.data.AudioData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.socket.client.Socket;


public class AudioSender implements Runnable {
	String LOG = "AudioSender ";

	private boolean isSendering = false;
	private List<AudioData> dataList;
	private static Socket socket;
	private JSONObject obj;


	public static void setSocket(Socket s)
	{
		socket =s;
	}

	public AudioSender() {
		dataList = Collections.synchronizedList(new LinkedList<AudioData>());
		this.obj = new JSONObject();
	}

	public void addData(byte[] data, int size) {
		AudioData encodedData = new AudioData();
		encodedData.setSize(size);
		byte[] tempData = new byte[size];
		System.arraycopy(data, 0, tempData, 0, size);
		encodedData.setRealData(tempData);
		dataList.add(encodedData);
	}

	/*
	 * send data to server
	 */
	private void sendData(byte[] data, int size) {
		try {
			obj.put("binary",data);
			socket.emit("blob",obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * start sending data
	 */
	public void startSending() {
		new Thread(this).start();
	}

	/*
	 * stop sending data
	 */
	public void stopSending() {
		this.isSendering = false;
	}

	// run
	public void run() {
		this.isSendering = true;
		System.out.println(LOG + "start....");
		while (isSendering) {
			if (dataList.size() > 0) {
				AudioData encodedData = dataList.remove(0);
				sendData(encodedData.getRealData(), encodedData.getSize());
			}
		}
		System.out.println(LOG + "stop!!!!");
	}
}