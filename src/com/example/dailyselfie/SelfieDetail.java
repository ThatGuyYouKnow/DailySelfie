package com.example.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class SelfieDetail extends Activity {

	private static final String TAG = "SelfieDetail";
	//private static LayoutInflater inflater = null;
	//private Context mContext;
	//View newView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Entering Selfie Detail");		
		
		Intent intent = getIntent();
		String filePath = intent.getStringExtra(SelfieViewActivity.FILE_URL);
		filePath = filePath.substring(5);
		
		setContentView(R.layout.selfie_detail_view);
		ImageView preview = (ImageView) findViewById(R.id.selfie_detail);

		//Bundle extras = getIntent().getExtras();
		//Log.i(TAG, "Extras are: " + extras);
		if (filePath == null || filePath.isEmpty() || filePath.equalsIgnoreCase("")) {
			Log.i(TAG,"No extra info");
		} else {
			Bitmap selfie = BitmapFactory.decodeFile(filePath);
			preview.setImageBitmap(selfie);
		}
		
	}

}
