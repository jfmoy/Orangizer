package com.orange.labs.uk.orangizer.models;

public enum RsvpStatus {
	ATTENDING("attending");
	
	//TODO: Add other status.
	private String mCode;
	
	private RsvpStatus(final String code) {
		mCode = code;
	}
	
	public String getCode() {
		return mCode;
	}
}
