package com.orange.labs.uk.orangizer.event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.attendee.Attendee;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.fetch.EventFetcher;
import com.orange.labs.uk.orangizer.utils.Logger;

/**
 * Class that can be used to fetch Facebook events and insert them into the database.
 */
public class FacebookEventFetcher implements EventFetcher {

	private static final Logger sLogger = Logger.getLogger(FacebookEventFetcher.class);

	private final Facebook mFacebook;
	private final EventsDatabase mEventsDb;

	public FacebookEventFetcher(Facebook facebook, EventsDatabase eventsDatabase) {
		mFacebook = facebook;
		mEventsDb = eventsDatabase;
	}

	@Override
	public void fetch(Callback<List<Event>> callback) {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(mFacebook);
		runner.request("me/events", new EventsRequestListener(callback));
	}

	private class EventsRequestListener implements RequestListener {

		private final Callback<List<Event>> mCallback;

		private EventsRequestListener(final Callback<List<Event>> callback) {
			mCallback = callback;
		}

		@Override
		public void onComplete(String response, Object state) {
			List<Event> events = new ArrayList<Event>();

			sLogger.d(response);
			try {
				JSONObject json = new JSONObject(response);
				JSONArray jsonArray = json.getJSONArray("data");

				int eventsNumber = jsonArray.length();

				for (int i = 0; i < eventsNumber; i++) {
					Event event = new Event.Builder().fromJsonObject(jsonArray.getJSONObject(i))
							.build();
					events.add(event);
				}
				sLogger.d(events.toString());
			} catch (JSONException e) {
				sLogger.w(e.getMessage());
			}

			// Insert all events in the database.
			if (events != null) {
				AtomicInteger remaining = new AtomicInteger(events.size());
				for (Event event : events) {
					// Insert event locally
					mEventsDb.insert(event);

					// Prepare path
					String path = String.format("%s/attending", event.getId());

					// Fetch attendees
					AsyncFacebookRunner runner = new AsyncFacebookRunner(mFacebook);
					runner.request(path, new AttendeesRequestListener(event, mCallback, remaining));
				}
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			handleFailure(e);
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			handleFailure(e);
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			handleFailure(e);
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			handleFailure(e);
		}

		private void handleFailure(Exception e) {
			sLogger.w(e.toString());
			mCallback.onFailure(e);
		}

	}

	/**
	 * {@link RequestListener} for Attendees request for a particular events. It parses the
	 * attendees and inserts them along the event.
	 */
	private class AttendeesRequestListener implements RequestListener {

		/** Callback to execute when everything has been done or an error has occured */
		private final Callback<List<Event>> mCallback;

		/** Indicates how many requests are left */
		private final AtomicInteger mRemaining;

		/** Event we request the attendees of */
		private final Event mEvent;

		public AttendeesRequestListener(Event event, Callback<List<Event>> callback,
				AtomicInteger remaining) {
			mEvent = event;
			mCallback = callback;
			mRemaining = remaining;
		}

		@Override
		public void onComplete(String response, Object state) {
			try {
				JSONObject json = new JSONObject(response);
				JSONArray jsonArray = json.getJSONArray("data");

				int attendeesNumber = jsonArray.length();
				List<Attendee> attendees = new ArrayList<Attendee>(attendeesNumber);
				for (int i = 0; i < attendeesNumber; i++) {
					Attendee attendee = new Attendee.Builder().fromJsonObject(
							jsonArray.getJSONObject(i)).build();
					attendees.add(attendee);
				}
				
				sLogger.i(attendees.toString());
			} catch (JSONException e) {
				sLogger.w(e.getMessage());
			}
			
			if (mRemaining.decrementAndGet() == 0) {
				mCallback.onSuccess(null);
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
