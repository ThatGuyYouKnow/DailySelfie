package com.example.dailyselfie;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelfieViewAdapter extends BaseAdapter {
	private static final String TAG = "DailySelfieAdapter";

	private ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();
	private static LayoutInflater inflater = null;
	private Context mContext;
		
	public SelfieViewAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}
	
	public int getCount() {
		return list.size();
	}
	
	public Object getItem(int position) {
		return list.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View newView = convertView;
		ViewHolder holder;
		
		SelfieRecord curr = list.get(position);
		
		
		
		if (null == convertView)  {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.activity_selfie_view, null);
			holder.selfieName = (TextView) newView.findViewById(R.id.image_date);
			holder.selfieView =  (ImageView) newView.findViewById(R.id.thumbnail);
			newView.setTag(holder);
		} else {
			holder = (ViewHolder) newView.getTag();
		}
		
		String date = curr.getLastModified().toGMTString();
		holder.selfieName.setText("" + date);
		
		holder.selfieView.setImageBitmap(setPic(curr, holder.selfieView));
		
		return newView;
	}
	
	static class ViewHolder {
		TextView selfieName;
		ImageView selfieView;
	}
	
	public void add(SelfieRecord listItem) {
		Log.i(TAG, "Adding selfie to list");
		list.add(listItem);
		notifyDataSetChanged();
	}
	
	public ArrayList<SelfieRecord> getList(){
		return list;
	}
	
	public void removeAllViews() {
		list.clear();
		this.notifyDataSetChanged();
	}
	
	public void addAllViews() {
		Log.i(TAG,"Adding all views");
		// Remove all the views to make sure we can start fresh.
		list.clear();
	
		// Get a list of files
		File[] allSelfies = getAllFiles();
		
		//If we don't have any files in that directory, let the user know
		if (null == allSelfies || allSelfies.length == 0) {
			Log.i(TAG, "No selfies on record");
			Toast.makeText(mContext, "Sorry, there are no photos yet", Toast.LENGTH_SHORT).show();
		} else {
			for (int i = 0; i < allSelfies.length; i++) {
				SelfieRecord selfie = new SelfieRecord("file:" + allSelfies[i].getPath(), new Date(allSelfies[i].lastModified()));
				add(selfie);
			}
		}
		
		this.notifyDataSetChanged();
	
	}
	
	public File[] getAllFiles() {
		Log.i(TAG, "Getting list of all files");
		File[] fileList = null;
		
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DailySelfie/";
		File f = new File(path);
		
		fileList = f.listFiles();
		
		Log.i(TAG, "There were " + fileList.length + " files in this directory");
		return fileList;
	}
	
	private Bitmap setPic(SelfieRecord currentSelfie, ImageView imageView) {
		Resources resources = mContext.getResources();
		String path = (currentSelfie.getFilePath()).substring(5);
		
		// Get the dimensions of the View
		int targetW = (int) resources.getDimension(R.dimen.selfie_image_width);
		int targetH = (int) resources.getDimension(R.dimen.selfie_image_height);
		
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		//bmOptions.inJustDecodeBounds = true;
		//BitmapFactory.decodeFile(path, bmOptions);
		//int photoW = bmOptions.outWidth;
		//int photoH = bmOptions.outHeight;
		Bitmap selfie = BitmapFactory.decodeFile(path);
		int photoW = selfie.getWidth();
		int photoH = selfie.getHeight();
		
		
		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
		
		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		//imageView.setImageBitmap(bitmap);
		
		return bitmap;
	}
	
	

}
