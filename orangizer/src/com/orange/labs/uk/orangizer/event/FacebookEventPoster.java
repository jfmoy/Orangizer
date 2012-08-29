package com.orange.labs.uk.orangizer.event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.utils.Logger;

public class FacebookEventPoster implements EventPoster {

	private static final Logger sLogger = Logger.getLogger(FacebookEventPoster.class);

	private final Facebook mFacebook;
	private final EventsDatabase mEventsDb;

	public FacebookEventPoster(Facebook facebook, EventsDatabase eventsDb) {
		mFacebook = facebook;
		mEventsDb = eventsDb;
	}

	/**
	 * Post the Event on Facebook and returns the result through the callback.
	 * 
	 * @param event
	 *            Event to be posted.
	 * @param callback
	 *            Callback to be invoked in case of success or failure.
	 */
	@Override
	public void post(Event event, Callback<Event> callback) {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(mFacebook);
		Bundle eventBundle = event.toFacebookBundle();

		sLogger.d(eventBundle.toString());

		runner.request("me/events/", eventBundle, "POST", new EventPosterRequestListener(event,
				callback), null);
	}

	private class EventPosterRequestListener implements RequestListener {

		private Event mEvent;
		private final Callback<Event> mCallback;

		public EventPosterRequestListener(Event event, Callback<Event> callback) {
			mEvent = event;
			mCallback = callback;
		}

		@Override
		public void onComplete(String response, Object state) {
			sLogger.d(response);
			try {
				JSONObject object = new JSONObject(response);
				String id = object.has("id") ? object.getString("id") : null;
				if (id != null) {
					mEvent.setId(id);
					mEventsDb.insert(mEvent);

					mCallback.onSuccess(mEvent);
				}
			} catch (JSONException e) {
				// Json error, should not happen.
			}

			mCallback.onFailure(null);
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
			sLogger.w(String.format("An error has occured: %s", e.getMessage()));
			mCallback.onFailure(e);
		}

	}
}
