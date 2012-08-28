package com.orange.labs.uk.orangizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;
import com.orange.labs.uk.orangizer.event.Event;
import com.orange.labs.uk.orangizer.event.EventsDatabase;
import com.orange.labs.uk.orangizer.sms.GuestsReminder;
import com.orange.labs.uk.orangizer.utils.Logger;

public class EventActivity extends SherlockActivity {

	private static final Logger sLogger = Logger.getLogger(EventActivity.class);
	
	/** Intent should contain that field to indicate which event the user wants to get details of */
	public static final String EVENT_FIELD = "event_id";
	
	/** Used to retrieve the event that we want to display. Its id is provided through the Intent */
	private EventsDatabase mEventsDb;
	
	/** Displayed Event */
	private Event mEvent;

	/** Used to get application wide components */
	private DependencyResolver mDependencyResolver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		// Allow up button to go back to Events list.
		getSherlock().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mDependencyResolver = DependencyResolverImpl.getInstance(); 
		mEventsDb = mDependencyResolver.getEventsDatabase(); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent = getIntent();
		
		String eventId = intent.getStringExtra(EVENT_FIELD);
		
		mEvent = (eventId != null) ? mEventsDb.getEvent(eventId) : null;
		if (mEvent != null) {
			// Fetch attendees for that event.
			mDependencyResolver.getFacebookAttendeesFetcher().fetch(mEvent, new Callback<Event>() {
				
				@Override
				public void onSuccess(Event result) {
					final int attending = result.getAttendees().size();
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							TextView attendeesTv = (TextView) findViewById(R.id.event_attendees_tv);
							attendeesTv.setText(getString(R.string.event_attending, attending));
						}
					});
				}
				
				@Override
				public void onFailure(Exception e) {
					TextView attendeesTv = (TextView) findViewById(R.id.event_attendees_tv);
					attendeesTv.setText(getString(R.string.event_attending_error));
				}
			});
			
			
			// display the view
			sLogger.i(String.format("Showing Event: %s", mEvent.toString()));
			setTitle(mEvent.getName());
			
			TextView addressTv = (TextView) findViewById(R.id.event_address_tv);
			addressTv.setText(mEvent.getAddress());
			
			ImageButton remindButton = (ImageButton) findViewById(R.id.event_remind_b);
			remindButton.setOnClickListener(new ReminderButtonClickListener());
		}
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent eventsIntent = new Intent(this, EventsActivity.class);
			startActivity(eventsIntent);
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private class ReminderButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// Implement logic to notify guests.
			GuestsReminder reminder = new GuestsReminder(getApplicationContext());
			reminder.remind(mEvent.getAttendees(), new Callback<Void>() {

				@Override
				public void onSuccess(Void result) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onFailure(Exception e) {
					// TODO Auto-generated method stub
					
				}
			});
			
		}
		
	}
	
}
