package com.sharpaiexample;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TrafficLightActivity extends Activity implements MqttCallback {
	GLSurfaceView mGLView;
	TrafficLightRenderer mRenderer;
	private MqttClient mqttClient;

	public static final String BROKER_URL = "tcp://192.168.0.2:1883";


	private final static UUID TLC_UUID = UUID.fromString("10d4fbe9-fdae-407f-8696-80130bafbd92");
	public static final String TOPIC = "rt_message";

	private boolean hasGLES20() {
		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		Log.i("Prosectura", "Gles Version: " + (info.reqGlEsVersion >> 16) + "." + (info.reqGlEsVersion & 0xffff));
		return info.reqGlEsVersion >= 0x20000;
	}

	@Override
	public void connectionLost(Throwable cause) {
		Log.d("Prosectura","connectionLost");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Log.d("Prosectura",message.toString());

		mRenderer.GetTL().SetGreen();
		Timer myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mRenderer.GetTL().SetRed();
				this.cancel();
			}

		}, 4000, 1000);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			MemoryPersistence persistence = new MemoryPersistence();
			mqttClient = new MqttClient(BROKER_URL,MqttClient.generateClientId(),persistence);
			mqttClient.setCallback(this);
			mqttClient.connect();
			mqttClient.subscribe(TOPIC);
			mqttClient.subscribe("test");
			final MqttMessage message = new MqttMessage("testing".getBytes());
			mqttClient.publish("test",message);
		} catch (MqttException e) {
			e.printStackTrace();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		if (hasGLES20()) {
			mGLView = new GLSurfaceView(this);
			mGLView.setEGLContextClientVersion(2);
			mGLView.setPreserveEGLContextOnPause(true);
			mGLView.setRenderer(mRenderer = new TrafficLightRenderer(getAssets()));
		} else {
			Log.e("Prosectura", "Gles 2.0 not supported.");
			return;
		}

		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN)
		{
			Log.w("prosectura_action", "ACTION_POINTER_DOWN");
		} else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			Log.w("prosectura_action", "ACTION_POINTER_UP");
			int iaxisY = 800 >> 2;
			if (event.getAxisValue(MotionEvent.AXIS_Y) <= (iaxisY*1)) {
				mRenderer.GetTL().SetRed();
			} else if (event.getAxisValue(MotionEvent.AXIS_Y) <= (iaxisY*2)) {
				mRenderer.GetTL().SetBlinking();
			} else if (event.getAxisValue(MotionEvent.AXIS_Y) <= (iaxisY*3)) {
				mRenderer.GetTL().SetGreen();
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
