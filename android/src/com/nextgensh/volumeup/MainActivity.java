package com.nextgensh.volumeup;

import java.util.UUID;

import src.com.getpebble.android.kit.PebbleKit;
import src.com.getpebble.android.kit.util.PebbleDictionary;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

interface Constants {
	String TAG = "VOLUP";
	
	/* Same constants as the ones used in the pebble 
	 * watch app.
	 */
	int KEY = 111;
	long DOWN = 601;
	long MUTE = 602;
	long UP = 603;
}

public class MainActivity extends Activity implements Constants {

	AudioManager am = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		am = (AudioManager)this.getSystemService(AUDIO_SERVICE);
		
		/* Check to see if the pebble application is connected. */
		boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
		Log.d(TAG, "Pebble connection status : "+(connected ? "Connected":"Not connected"));
		
		/* Get the text view and update it accordingly to indicate
		 * if the watch is connected to it or not.
		 */
		TextView tv = (TextView)findViewById(R.id.textView1);
		if(connected)
			tv.setText("Pebble is connected");
		else
			tv.setText("Pebble not connected");
		
		/* Register a Broadcast receiver through pebble's wrapper functions,
		 * to accept the volume change intents.
		 */
		final UUID PEBBLE_APP_UUID = UUID.fromString("9bda448d-52ed-4785-ab15-09eaf3e51c48");
		
		PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID){

			@Override
			public void receiveData(Context context, int transactionId,
					PebbleDictionary data) {
				// TODO Auto-generated method stub
				if(data.getInteger(KEY) == DOWN){
					am.adjustVolume(AudioManager.ADJUST_LOWER, 0);
					Log.d(TAG, "Volume mute requested");
				}
				else if(data.getInteger(KEY) == UP){
					am.adjustVolume(AudioManager.ADJUST_RAISE, 0);
					Log.d(TAG, "Volume up requested");
				}
				else if(data.getInteger(KEY) == MUTE){
					/* Working, go slow. */
				}
				
				/* Send the ack back to my nice little watch app. */
				PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
			}
		});
	}
}
