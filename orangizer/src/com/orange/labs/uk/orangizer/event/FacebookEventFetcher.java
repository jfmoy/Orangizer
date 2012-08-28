package com.orange.labs.uk.orangizer.event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
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

	/**
	 * Listener that handles the result of the fetching request and provide the results back to the
	 * caller. If an error occurs, it notifies of the error through the callback as well.
	 */
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
				handleFailure(e);
				return;
			}

			// insert events in the db.
			for (Event event : events) {
				mEventsDb.insert(event);
			}

			// Insert all events in the database.
			mCallback.onSuccess(events);
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
}
