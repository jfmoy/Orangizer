package com.orange.labs.uk.orangizer.event;

import com.orange.labs.uk.orangizer.utils.OrangizerEnum;

public enum RsvpStatus implements OrangizerEnum {
	ATTENDING("attending");
	
	//TODO: Add other status.
	private String mCode;
	
	private RsvpStatus(final String code) {
		mCode = code;
	}
	
	@Override
	public String getCode() {
		return mCode;
	}
}
