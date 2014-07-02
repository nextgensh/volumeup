package com.nextgensh.volumeup;

import java.util.UUID;

import src.com.getpebble.android.kit.PebbleKit;
import src.com.getpebble.android.kit.util.PebbleDictionary;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		
		/* Start the volumeup service. */
		Intent servicestart = new Intent(this, MainService.class);
		this.startService(servicestart);
	}
}
