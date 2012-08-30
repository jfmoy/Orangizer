package com.orange.labs.uk.orangizer.event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Bundle;

import com.orange.labs.uk.orangizer.attendee.Attendee;
import com.orange.labs.uk.orangizer.utils.Constants;
import com.orange.labs.uk.orangizer.utils.Logger;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

public class Event {

	private static final Logger sLogger = Logger.getLogger(Event.class);

	private String mId;

	private String mName;

	private String mOrganizer;

	private String mDescription;

	private Date mStartDate;

	private Date mEndDate;

	private String mAddress;

	private RsvpStatus mStatus;

	private List<Attendee> mAttendees;


	private Event(String id, String name, String organizer, String description, Date startDate,
			Date endingDate, String address, RsvpStatus status) {
		mId = id;
		mName = name;
		mOrganizer = organizer;
		mDescription = description;
		mStartDate = startDate;
		mEndDate = endingDate;
		mAddress = address;
		mStatus = status;
	}

	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getOrganizer() {
		return mOrganizer;
	}

	public String getDescription() {
		return mDescription;
	}

	public Date getStartDate() {
		return mStartDate;
	}

	public Date getEndingDate() {
		return mEndDate;
	}

	public String getAddress() {
		return mAddress;
	}

	public RsvpStatus getStatus() {
		return mStatus;
	}

	public boolean hasId() {
		return (mId != null && mId.length() > 0);
	}

	public boolean hasName() {
		return (mName != null && mName.length() > 0);
	}

	public boolean hasOrganizer() {
		return (mOrganizer != null && mOrganizer.length() > 0);
	}

	public boolean hasDescription() {
		return (mDescription != null && mDescription.length() > 0);
	}

	public boolean hasStartingDate() {
		return (mStartDate != null);
	}

	public boolean hasEndingDate() {
		return (mEndDate != null);
	}

	public boolean hasAddress() {
		return (mAddress != null && mAddress.length() > 0);
	}

	public boolean hasStatus() {
		return (mStatus != null);
	}

	/**
	 * Set the Event ID
	 */
	public void setId(String id) {
		mId = id;
	}

	/**
	 * Set the list of attendees invited to the event.
	 */
	public void setAttendees(List<Attendee> attendees) {
		mAttendees = attendees;
	}

	public List<Attendee> getAttendees() {
		return mAttendees;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Event : [\n");
		if (hasName()) {
			builder.append(String.format("Name: %s, \n", getName()));
		}
		if (hasDescription()) {
			builder.append(String.format("Description: %s, \n", getDescription()));
		}
		if (hasAddress()) {
			builder.append(String.format("Address: %s, \n", getAddress()));
		}
		if (hasStartingDate()) {
			builder.append(String.format("Starting Date: %s, \n", getStartDate().toString()));
		}
		if (hasEndingDate()) {
			builder.append(String.format("Ending Date: %s, \n", getEndingDate().toString()));
		}
		if (hasId()) {
			builder.append(String.format("ID: %s, \n", getId()));
		}
		if (hasOrganizer()) {
			builder.append(String.format("Organizer: %s, \n", getOrganizer()));
		}
		if (hasStatus()) {
			builder.append(String.format("Status: %s, \n", getStatus()));
		}
		builder.append("]");
		return builder.toString();
	}

	public Bundle toFacebookBundle() {
		Bundle bundle = new Bundle();

		if (hasName()) {
			bundle.putString("name", getName());
		}
		if (hasAddress()) {
			bundle.putString("location", getAddress());
		}
		if (hasStartingDate()) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
			bundle.putString("start_time", df.format(getStartDate()));
		}
		if (hasEndingDate()) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
			bundle.putString("end_time", df.format(getEndingDate()));
		}
		if (hasDescription()) {
			bundle.putString("description", getDescription());
		}
		bundle.putString("privacy_type", Constants.FACEBOOK_DEFAULT_PRIVACY);

		return bundle;
	}

	public static class Builder {

		private String mId;

		private String mName;

		private String mOrganizer;

		private String mDescription;

		private Date mStartDate;

		private Date mEndDate;

		private String mAddress;

		private RsvpStatus mStatus;

		public Builder fromJsonObject(JSONObject object) {
			try {
				mId = object.has("id") ? object.getString("id") : null;
				mName = object.has("name") ? object.getString("name") : null;
				mAddress = object.has("location") ? object.getString("location") : null;
				mDescription = object.has("description") ? object.getString("description") : null;

				// Set status.
				String statusCode = object.has("rsvp_status") ? object.getString("rsvp_status")
						: null;
				if (statusCode != null) {
					mStatus = OrangizerUtils.valueToEnumValue(statusCode, RsvpStatus.class);
				}

				// Start time
				String date = object.has("start_time") ? object.getString("start_time") : null;
				if (date != null) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
					try {
						mStartDate = format.parse(date);
					} catch (ParseException e) {
						sLogger.w("Could not parse starting date: " + date);
					}
				}

				// End Time
				date = object.has("end_time") ? object.getString("end_time") : null;
				if (date != null) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
					try {
						mEndDate = format.parse(date);
					} catch (ParseException e) {
						sLogger.w("Could not parse starting date: " + date);
					}
				}

				// Owner
				if (object.has("owner")) {
					JSONObject owner = object.getJSONObject("owner");
					mOrganizer = owner.has("name") ? owner.getString("name") : null;
				}

				// TODO: format start time and end time
			} catch (JSONException e) {
				sLogger.w(String.format("This should never happen: %s", e));
			}

			return this;
		}

		public Builder fromCursor(Cursor cursor) {
			setId(OrangizerUtils.getString(cursor, EventsColumns.ID));
			setName(OrangizerUtils.getString(cursor, EventsColumns.NAME));
			setOrganizer(OrangizerUtils.getString(cursor, EventsColumns.ORGANIZER));
			setDescription(OrangizerUtils.getString(cursor, EventsColumns.DESCRIPTION));
			setAddress(OrangizerUtils.getString(cursor, EventsColumns.ADDRESS));

			Date startingDate = new Date(OrangizerUtils.getLong(cursor, EventsColumns.START_DATE));
			setStartingDate(startingDate);

			Date endingDate = new Date(OrangizerUtils.getLong(cursor, EventsColumns.END_DATE));
			setEndingDate(endingDate);

			String statusCode = OrangizerUtils.getString(cursor, EventsColumns.STATUS);
			RsvpStatus status = OrangizerUtils.valueToEnumValue(statusCode, RsvpStatus.class);
			setStatus(status);

			return this;
		}

		public Builder setId(String id) {
			mId = id;
			return this;
		}

		public Builder setName(String name) {
			mName = name;
			return this;
		}

		public Builder setOrganizer(String organizer) {
			mOrganizer = organizer;
			return this;
		}

		public Builder setDescription(String description) {
			mDescription = description;
			return this;
		}

		public Builder setStartingDate(Date startingDate) {
			mStartDate = startingDate;
			return this;
		}

		public Builder setEndingDate(Date endingDate) {
			mEndDate = endingDate;
			return this;
		}

		public Builder setAddress(String address) {
			mAddress = address;
			return this;
		}

		public Builder setStatus(RsvpStatus status) {
			mStatus = status;
			return this;
		}

		public Event build() {
			return new Event(mId, mName, mOrganizer, mDescription, mStartDate, mEndDate, mAddress,
					mStatus);
		}
	}

}
