package com.orange.labs.uk.orangizer.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

	private static final String SETTINGS_NAME = "Orangizer Settings";
	
	private static final String FACEBOOK_TOKEN = "facebook_token";
	
	private static final String FACEBOOK_TOKEN_EXPIRES = "facebook_expires";
	
	private SharedPreferences mPreferences;
	
	private SharedPreferences.Editor mEditor;
	
	public SettingsManager(Context context) {
		mPreferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}
	
	/**
	 * Stores the Facebook token in the SharedPreferences.
	 * @param accessToken
	 * 			Facebook Access Token provided by the Facebook Authentication Callback.
	 */
	public void setFacebookToken(String accessToken) {
		mEditor.putString(FACEBOOK_TOKEN, accessToken);
		mEditor.commit();
	}
	
	public void setFacebookTokenExpires(long expires) {
		mEditor.putLong(FACEBOOK_TOKEN_EXPIRES, expires);
		mEditor.commit();
	}
	
	/** Return the Facebook token if it exists, null otherwise */
	public String getFacebookAccessToken() {
		return mPreferences.getString(FACEBOOK_TOKEN, null);
	}

	public long getFacebookAccessExpires() {
		return mPreferences.getLong(FACEBOOK_TOKEN_EXPIRES, 0);
	}
	
}
