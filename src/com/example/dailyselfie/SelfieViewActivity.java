package com.example.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SelfieViewActivity extends ListActivity {
	private static final String TAG = "DailySelfieActivity";
	
	public final static String FILE_URL = "com.example.dailyselfie.FILE_URL";
	
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent, mLoggerReceiverPendingIntent;
	private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;
	protected static final long JITTER = 5000L;
	private SelfieViewAdapter mAdapter;
	String mCurrentPhotoPath;
	
	//static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView selfiesListView = getListView();
		
		// Get the AlarmManager service
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationReceiverIntent = new Intent(SelfieViewActivity.this, AlarmNotificationReceiver.class);
		
		// Create a PendingIntent that holds the NotificationReceiverIntentt
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(SelfieViewActivity.this, 0, mNotificationReceiverIntent, 0);
		
		// Create an Intent to broadcast to the AlarmLoggerReceiver
		mLoggerReceiverIntent = new Intent(SelfieViewActivity.this, AlarmLoggerReceiver.class);
		
		// Create PendingIntent that holds the mLoggerReceiverPendingIntent
		mLoggerReceiverPendingIntent = PendingIntent.getBroadcast(SelfieViewActivity.this, 0, mLoggerReceiverIntent, 0);
		
		// Set repeating alarm
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
						INITIAL_ALARM_DELAY,
						mNotificationReceiverPendingIntent);
		
		// Set repeating alarm to fire shortly after previous alarm
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY + JITTER,
				INITIAL_ALARM_DELAY,
				mLoggerReceiverPendingIntent);
		
		mAdapter = new SelfieViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);
		
		selfiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(SelfieViewActivity.this, SelfieDetail.class);
				SelfieRecord selfie = (SelfieRecord) mAdapter.getItem(position);
				i.putExtra(FILE_URL, selfie.getFilePath());
				startActivity(i);
			}
		});
		//setContentView(R.layout.activity_selfie_view);
	}
	
	@Override
	protected void onResume() {
		mAdapter.addAllViews();
		
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.selfie_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.i(TAG, "Menu item selected");

		switch (item.getItemId()) {
			case R.id.action_camera:
				Log.i(TAG, "Camera selected");
				dispatchTakePictureIntent();
				return true;
			//case R.id.action_settings:
			//	Log.i(TAG, "Setting selected");
			//	return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void addNewSelfie(SelfieRecord selfie) {
		Log.i(TAG, "Entered addNewSelfie()");
		
		if (null == selfie) {
			Log.i(TAG, "There is no selfie!");
			Toast.makeText(this, "Selfie could not be read", Toast.LENGTH_SHORT).show();
		} else {
			Log.i(TAG, "We have a valid selfie");
			Toast.makeText(this, "You look great!", Toast.LENGTH_SHORT).show();
			mAdapter.add(selfie);
		}
	}
	
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// This if statement is to verify there is an activity that can handle the intent.
		// If this statement wasn't there, and we tried to call the intent without an activity to handle it,
		// The app would crash!
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Log.i(TAG, "An error occurred creating the file");
				Log.i(TAG, ex.toString());
			}
			
			// Continue only if the file was successfully created
			if (photoFile != null) {
				Log.i(TAG, "photo successfully created!");
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
				
				
			}
		}
	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		Log.i(TAG, "Creating an image file");
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timestamp + "_";
		//File storageDir = Environment.getExternalStorageDirectory();
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/DailySelfie/");
		//File storageDir = getExternalFilesDir("DailySelfie/");

		
		//If the directory doesn't exist, make it!
		storageDir.mkdirs();
		File image = File.createTempFile(
			imageFileName, 	// prefix
			".jpg",			// suffix
			storageDir		// Directory
		);
		
		// Save a file: path for use with ACTION_VIEW intents
		Log.i(TAG, "Saving the file path");
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
			Log.i(TAG, "Photo canceled!");
			File file = new File(mCurrentPhotoPath);
			file.delete();
			Toast.makeText(this, "Selfie canceled!", Toast.LENGTH_SHORT).show();
		} else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			Log.i(TAG, "Successful selfie!");
			Toast.makeText(this, "You look great!", Toast.LENGTH_SHORT).show();
			//Bundle extras = output.getExtras();
			File file = new File(mCurrentPhotoPath);
			//String path = mCurrentPhotoPath;
			SelfieRecord selfie = new SelfieRecord(file.getPath(), new Date(file.lastModified()));
			
			addNewSelfie(selfie);
		}
	}
		
}
