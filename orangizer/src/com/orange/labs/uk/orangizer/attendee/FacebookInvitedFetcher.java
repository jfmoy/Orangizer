package com.orange.labs.uk.orangizer.attendee;

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
import com.orange.labs.uk.orangizer.event.Event;
import com.orange.labs.uk.orangizer.utils.Logger;

public class FacebookInvitedFetcher implements InvitedFetcher {

	private static final Logger sLogger = Logger.getLogger(FacebookInvitedFetcher.class);

	private final Facebook mFacebook;

	public FacebookInvitedFetcher(Facebook facebook) {
		mFacebook = facebook;
	}

	@Override
	public void fetch(final Event event, final Callback<Event> callback) {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(mFacebook);
		runner.request(String.format("%s/invited", event.getId()), new AttendeesRequestListener(
				event, callback));
	}

	@Override
	public void fetch(List<Event> event, Callback<List<Event>> callback) {
		// not implemented yet.
	}

	/**
	 * {@link RequestListener} for Attendees request for a particular events. It parses the
	 * attendees and inserts them along the event.
	 */
	private class AttendeesRequestListener implements RequestListener {

		/** Callback to execute when everything has been done or an error has occured */
		private final Callback<Event> mCallback;

		/** Event we request the attendees of */
		private final Event mEvent;

		public AttendeesRequestListener(Event event, Callback<Event> callback) {
			mEvent = event;
			mCallback = callback;
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
				mEvent.setAttendees(attendees);
			} catch (JSONException e) {
				sLogger.w(e.getMessage());
				handleFailure(e);
				return;
			}

			mCallback.onSuccess(mEvent);
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
			sLogger.w(e.getMessage());
			mCallback.onFailure(e);
		}

	}

}
