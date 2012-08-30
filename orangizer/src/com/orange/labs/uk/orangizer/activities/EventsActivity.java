package com.orange.labs.uk.orangizer.activities;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;
import com.orange.labs.uk.orangizer.event.Event;
import com.orange.labs.uk.orangizer.event.EventsColumns;
import com.orange.labs.uk.orangizer.event.EventsDatabase;
import com.orange.labs.uk.orangizer.settings.SettingsManager;
import com.orange.labs.uk.orangizer.utils.Logger;

public class EventsActivity extends SherlockListActivity {

	private static final Logger sLogger = Logger.getLogger(EventsActivity.class);

	/** Used to store access token */
	private SettingsManager mSettingsManager;

	private DependencyResolver mDependencyResolver;

	private Facebook mFacebook;

	private EventsDatabase mEventsDb;

	/** Cursor providing data for the events list */
	private Cursor mCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		mDependencyResolver = DependencyResolverImpl.getInstance();
		mSettingsManager = mDependencyResolver.getSettingsManager();
		mFacebook = mDependencyResolver.getFacebook();
		mEventsDb = mDependencyResolver.getEventsDatabase();
		mCursor = mEventsDb.getEventsCursor();
		startManagingCursor(mCursor);

		ListAdapter adapter = new SimpleCursorAdapter(EventsActivity.this,
				R.layout.event_list_item, mCursor, new String[] { EventsColumns.NAME.getName(),
						EventsColumns.ORGANIZER.getName() }, new int[] {
						R.id.event_list_item_title, R.id.event_list_item_subtitle });
		setListAdapter(adapter);

		String accessToken = mSettingsManager.getFacebookAccessToken();
		long accessExpires = mSettingsManager.getFacebookAccessExpires();

		if (accessToken != null) {
			mFacebook.setAccessToken(accessToken);
		}

		if (accessExpires != 0) {
			mFacebook.setAccessExpires(accessExpires);
		}

		if (!mFacebook.isSessionValid()) {
			sLogger.i("Session is invalid, authorizing");
			mFacebook.authorize(this, new String[] { "user_events", "create_event" },
					new FacebookDialogListener());
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
					sLogger.i("Extending token");
					fetchFacebookEvents();
				}
			});
		} else if (mFacebook.isSessionValid()){
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.events_menu_refresh) {
			fetchFacebookEvents();
			return true;
		} else if (item.getItemId() == R.id.events_menu_add) {
			Intent createIntent = new Intent(this, CreateEventActivity.class);
			startActivity(createIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mCursor.moveToPosition(position);
		Event clickedEvent = new Event.Builder().fromCursor(mCursor).build();
		if (clickedEvent != null) {
			// Start the view activity providing the Event id.
			Intent viewIntent = new Intent(this, EventActivity.class);
			viewIntent.putExtra(EventActivity.EVENT_FIELD, clickedEvent.getId());
			startActivity(viewIntent);
		} else {
			super.onListItemClick(l, v, position, id);
		}
	}

	/** Grab events from user profile */
	private void fetchFacebookEvents() {
		sLogger.i("Fetching Events");
		mDependencyResolver.getEventsFetcher().fetch(new Callback<List<Event>>() {

			@Override
			public void onSuccess(List<Event> result) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mCursor.requery();
					}
				});
			}

			@Override
			public void onFailure(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(EventsActivity.this,
								getString(R.string.events_fetch_failed), Toast.LENGTH_SHORT).show();
					}

				});
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
