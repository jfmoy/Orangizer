package com.orange.labs.uk.orangizer.event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.orange.labs.uk.orangizer.attendee.Attendee;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;
import com.orange.labs.uk.orangizer.utils.Logger;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

public class EventCreator {

	private static final Logger sLogger = Logger.getLogger(EventCreator.class);

	private Event mEvent;
	private Callback<Event> mCallback;
	private Context mContext;
	private DependencyResolver mResolver;
	private ContentResolver mCr;

	private SmsManager mSmsManager;

	public EventCreator() {
		mResolver = DependencyResolverImpl.getInstance();
		mContext = mResolver.getApplicationContext();
		mCr = mContext.getContentResolver();
		mSmsManager = SmsManager.getDefault();
	}

	public void create(Event event, Callback<Event> callback) {
		mEvent = event;
		mCallback = callback;

		/** Invite friends on Facebook and send SMS to address book friends */
		mResolver.getEventPoster().post(event, new Callback<Event>() {

			@Override
			public void onSuccess(Event event) {
				AsyncFacebookRunner runner = new AsyncFacebookRunner(mResolver.getFacebook());
				List<Attendee> attendees = mEvent.getAttendees();
				if (attendees != null && attendees.size() > 0) {
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < attendees.size(); i++) {
						Attendee attendee = attendees.get(i);
						if (attendee.hasFacebookId()) {
							builder.append(attendee.getFacebookId());
							if (i + 1 < attendees.size()) {
								builder.append(",");
							}
						} else if (attendee.hasAddressBookId()) {
							sendSms(attendee.getAddressBookId(), event);
						}
					}

					Bundle bundle = new Bundle();
					bundle.putString("users", builder.toString());

					String path = mEvent.getId() + "/invited/";
					runner.request(path, bundle, "POST", new RequestListener() {

						@Override
						public void onMalformedURLException(MalformedURLException e, Object state) {
							handleFailure(e);
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
						public void onFacebookError(FacebookError e, Object state) {
							handleFailure(e);
						}

						@Override
						public void onComplete(String response, Object state) {
							sLogger.i(response);
							mCallback.onSuccess(mEvent);
						}

						private void handleFailure(Exception e) {
							sLogger.e(e.getMessage());
							mCallback.onFailure(e);
						}

					}, null);
				} else {
					mCallback.onSuccess(mEvent);
				}
			}

			@Override
			public void onFailure(Exception e) {
				mCallback.onFailure(e);
			}
		});
	}

	private boolean sendSms(String addressBookId, Event event) {
		String smsContent = String.format(
				"Hey! You are invited to %s on %s, come, it will be aweomse!", event.getName(),
				DateFormat.getDateTimeInstance().format(event.getStartDate()));
		Cursor phones = null;
		try {
			phones = mCr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + addressBookId,
					null, null);
			if (phones.moveToFirst()) {

				// Texting the number.
				String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
				sLogger.d("Texting phone number: " + number);
				mSmsManager.sendTextMessage(number, null, smsContent, null, null);
				return true;
			}
		} finally {
			OrangizerUtils.closeQuietly(phones);
		}

		return false;
	}
}
