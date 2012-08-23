package com.orange.labs.uk.orangizer.activities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.models.Event;
import com.orange.labs.uk.orangizer.utils.Constants;
import com.orange.labs.uk.orangizer.utils.Logger;

public class EventsActivity extends SherlockActivity {

	private static final Logger sLogger = Logger.getLogger(EventsActivity.class);

	private Facebook mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);

	private AsyncFacebookRunner mAsyncRunner;
	
	private ListView mListView;
	
	private List<Event> mEvents = new ArrayList<Event>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		mFacebook.authorize(this, new String[] { "user_events" }, new FacebookDialogListener());
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
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

	private class FacebookDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			sLogger.i("Facebook Authentication Succeeded");
			// Grab events from user profile
			mAsyncRunner.request("me/events", new EventsRequestListener());
		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

	}

	private class EventsRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			sLogger.d(response);
			try {
				JSONObject json = new JSONObject(response);
				JSONArray jsonArray = json.getJSONArray("data");

				int eventsNumber = jsonArray.length();
				for (int i = 0; i < eventsNumber; i++) {
					Event event = new Event.Builder().fromJsonObject(jsonArray.getJSONObject(i))
							.getEvent();
					mEvents.add(event);
				}
				sLogger.d(mEvents.toString());
			} catch (JSONException e) {
				sLogger.w(e.getMessage());
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

	}
}
