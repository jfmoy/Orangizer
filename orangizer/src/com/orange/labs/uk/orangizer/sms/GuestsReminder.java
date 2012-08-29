package com.orange.labs.uk.orangizer.sms;

import java.text.DateFormat;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;

import com.orange.labs.uk.orangizer.attendee.Attendee;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.event.Event;
import com.orange.labs.uk.orangizer.utils.Logger;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

/**
 * This class is useful to send a reminder to a list of attendees. Typically, a SMS.
 * 
 */
public class GuestsReminder {

	private static final Logger sLogger = Logger.getLogger(GuestsReminder.class);

	private Context mContext;

	private SmsManager mSmsManager;

	/**
	 * Context is needed to access the phone's address book.
	 */
	public GuestsReminder(Context context) {
		mContext = context;
		mSmsManager = SmsManager.getDefault();
	}

	public void remind(Event event, Callback<Integer> callback) {
		// Get all attendees for that event.
		List<Attendee> attendees = event.getAttendees();

		int contactsContacted = 0;
		String smsContent = String.format(
				"Hey! You are invited to %s on %s, come, it will be aweomse!", event.getName(),
				DateFormat.getDateTimeInstance().format(event.getStartDate()));

		// Retrieve phone numbers of attendees.
		ContentResolver cr = mContext.getContentResolver();
		Cursor contacts = null;
		try {
			contacts = cr.query(ContactsContract.Contacts.CONTENT_URI, null, withPhone(attendees),
					null, null);

			sLogger.d("Cursor size : " + contacts.getCount());
			while (contacts.moveToNext()) {
				sLogger.d("Contact Name: "
						+ contacts.getString(contacts.getColumnIndex(Contacts.DISPLAY_NAME)));

				String id = contacts.getString(contacts
						.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor phones = null;
				try {
					phones = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + id, null,
							null);
					if (phones.moveToFirst()) {
						contactsContacted++;
						
						// Texting the number.
						String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
						sLogger.d("Found phone number: " + number);
						mSmsManager.sendTextMessage(number, null, smsContent, null, null);
					}
				} finally {
					OrangizerUtils.closeQuietly(phones);
				}
			}
		} finally {
			OrangizerUtils.closeQuietly(contacts);
		}

		if (contactsContacted > 0) {
			callback.onSuccess(contactsContacted);
		} else {
			callback.onFailure(null);
		}
	}

	private String withPhone(List<Attendee> attendees) {
		StringBuilder builder = new StringBuilder();
		builder.append(Contacts.HAS_PHONE_NUMBER);
		builder.append("=1");
		builder.append(" AND (");
		attendeesSelection(builder, attendees);
		builder.append(")");
		return builder.toString();
	}

	private void attendeesSelection(StringBuilder builder, List<Attendee> attendees) {
		for (int i = 0; i < attendees.size(); i++) {
			Attendee attendee = attendees.get(i);

			builder.append(DatabaseUtils.sqlEscapeString(attendee.getName()));
			builder.append(" LIKE ");
			builder.append(Contacts.DISPLAY_NAME);

			if ((i + 1) != attendees.size()) {
				builder.append(" OR ");
			}
		}
	}
}
