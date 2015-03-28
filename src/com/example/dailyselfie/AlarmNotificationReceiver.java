package com.example.dailyselfie;

import java.text.DateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmNotificationReceiver extends BroadcastReceiver {
	// Notification ID to allow for future updates
	private static final int MY_NOTIFICATION_ID = 1;
	private static final String TAG = "AlarmNotificationReceiver";
	
	// Notification Text Elements
	private final CharSequence tickerText = "Time for another selfie!";
	private final CharSequence contentTitle = "DailySelfie";
	private final CharSequence contentText = "Time for another selfie!";
	
	// Notification Action Elements
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;
	
	private final long[] mVibratePattern = { 0, 200, 200, 300 };
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// The Intent to be used when the user clicks on the Notification View
		mNotificationIntent = new Intent(context, SelfieViewActivity.class);
		
		// The PendingIntent that wraps around the underlying Intent
		mContentIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Build the Notification
		Notification.Builder notificationBuilder = new Notification.Builder(context).setTicker(tickerText)
				.setSmallIcon(R.drawable.ic_menu_camera)
				.setAutoCancel(true).setContentTitle(contentTitle)
				.setContentText(contentText).setContentIntent(mContentIntent)
				.setVibrate(mVibratePattern);
		
		// Get the NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Pass the notification to the NotificationManager
		mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
		
		// Log occurence of notify() call
		Log.i(TAG, "Sending Notification as: " + DateFormat.getDateTimeInstance().format(new Date()));
	}

}
