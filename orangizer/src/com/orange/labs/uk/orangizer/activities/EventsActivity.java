package com.orange.labs.uk.orangizer.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;
import com.orange.labs.uk.orangizer.event.Event;
import com.orange.labs.uk.orangizer.fetch.EventFetcher;
import com.orange.labs.uk.orangizer.settings.SettingsManager;
import com.orange.labs.uk.orangizer.utils.Logger;

public class EventsActivity extends SherlockActivity {

	private static final Logger sLogger = Logger.getLogger(EventsActivity.class);

	/** Used to store access token */
	private SettingsManager mSettingsManager;

	private DependencyResolver mDependencyResolver;
	
	private Facebook mFacebook;
	
	private ListView mListView;

	private List<Event> mEvents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		mDependencyResolver = DependencyResolverImpl.getInstance();
		mSettingsManager = mDependencyResolver.getSettingsManager();
		mFacebook = mDependencyResolver.getFacebook();
		

		String accessToken = mSettingsManager.getFacebookAccessToken();
		long accessExpires = mSettingsManager.getFacebookAccessExpires();

		if (accessToken != null) {
			mFacebook.setAccessToken(accessToken);
		}

		if (accessExpires != 0) {
			mFacebook.setAccessExpires(accessExpires);
		}

		if (!mFacebook.isSessionValid()) {
			mFacebook.authorize(this, new String[] { "user_events" }, new FacebookDialogListener());
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mFacebook.shouldExtendAccessToken()) {
			mFacebook.extendAccessTokenIfNeeded(this, new Facebook.ServiceListener() {

				@Override
				public void onFacebookError(FacebookError e) {
					sLogger.w(e.toString());
				}

				@Override
				public void onError(Error e) {
					sLogger.w(e.toString());
				}

				@Override
				public void onComplete(Bundle values) {
					fetchFacebookEvents();
				}
			});
		} else {
			fetchFacebookEvents();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Authorization callback for Facebook authentication when coming back from their app.
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_events, menu);
		return true;
	}

	/** Grab events from user profile */
	private void fetchFacebookEvents() {
		mDependencyResolver.getFacebookEventsFetcher().fetch(new Callback<List<Event>>() {
			
			@Override
			public void onSuccess(List<Event> result) {
				mEvents = result;
			}
			
			@Override
			public void onFailure(Exception e) {
				//TODO show error message to the user
			}
		});
	}

	private class FacebookDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			sLogger.i("Facebook Authentication Succeeded");
			// Store access token and expires
			mSettingsManager.setFacebookToken(mFacebook.getAccessToken());
			mSettingsManager.setFacebookTokenExpires(mFacebook.getAccessExpires());

			fetchFacebookEvents();
		}

		@Override
		public void onFacebookError(FacebookError e) {
			sLogger.w(e.toString());
		}

		@Override
		public void onError(DialogError e) {
			sLogger.w(e.toString());
		}

		@Override
		public void onCancel() {
			
		}
	}

}
