package com.example.dailyselfie;

import java.util.Date;

public class SelfieRecord {
	private String mFilePath;
	private Date mLastModified;
	
	public SelfieRecord(String filePath, Date lastModified) {
		this.mFilePath = filePath;
		this.mLastModified = lastModified;
	}
	
	public SelfieRecord() {
		
	}
	
	public void setFilePath(String filePath) {
		this.mFilePath = filePath;
	}
	
	public String getFilePath() {
		return mFilePath;
	}

	public Date getLastModified() {
		return mLastModified;
	}

	public void setLastModified(Date mLastModified) {
		this.mLastModified = mLastModified;
	}
}
