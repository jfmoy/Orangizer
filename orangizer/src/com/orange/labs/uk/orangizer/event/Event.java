package com.orange.labs.uk.orangizer.event;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.orange.labs.uk.orangizer.attendee.Attendee;
import com.orange.labs.uk.orangizer.utils.Logger;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

public class Event {

	private static final Logger sLogger = Logger.getLogger(Event.class);

	private String mId;

	private String mName;

	private String mDescription;

	private Date mStartingDate;

	private Date mEndingDate;

	private String mAddress;

	private RsvpStatus mStatus;

	private List<Attendee> mAttendees;

	private Event(String id, String name, String description, Date startDate, Date endingDate,
			String address, RsvpStatus status) {
		if (id == null) {
			throw new IllegalArgumentException("Required parameters are missing");
		}
		mId = id;
		mName = name;
		mDescription = description;
		mStartingDate = startDate;
		mEndingDate = endingDate;
		mAddress = address;
		mStatus = status;
	}

	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public Date getStartingDate() {
		return mStartingDate;
	}

	public Date getEndingDate() {
		return mEndingDate;
	}

	public String getAddress() {
		return mAddress;
	}

	public RsvpStatus getStatus() {
		return mStatus;
	}

	public boolean hasId() {
		return (mId != null);
	}

	public boolean hasName() {
		return (mName != null);
	}

	public boolean hasDescription() {
		return (mDescription != null);
	}

	public boolean hasStartingDate() {
		return (mStartingDate != null);
	}

	public boolean hasEndingDate() {
		return (mEndingDate != null);
	}

	public boolean hasAddress() {
		return (mAddress != null);
	}

	public boolean hasStatus() {
		return (mStatus != null);
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
		return mName;
	}

	public static class Builder {

		private String mId;

		private String mName;

		private String mDescription;

		private Date mStartingDate;

		private Date mEndingDate;

		private String mAddress;

		private RsvpStatus mStatus;

		public Builder fromJsonObject(JSONObject object) {
			try {
				mId = object.has("id") ? object.getString("id") : null;
				mName = object.has("name") ? object.getString("name") : null;
				mAddress = object.has("location") ? object.getString("location") : null;
				
				
				// Set status.
				String statusCode = object.has("rsvp_status") ? object.getString("rsvp_status")
						: null;
				if (statusCode != null) {
					mStatus = OrangizerUtils.valueToEnumValue(statusCode, RsvpStatus.class);
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
			setDescription(OrangizerUtils.getString(cursor, EventsColumns.DESCRIPTION));
			setAddress(OrangizerUtils.getString(cursor, EventsColumns.ADDRESS));

			Date startingDate = new Date(
					OrangizerUtils.getLong(cursor, EventsColumns.STARTING_DATE));
			setStartingDate(startingDate);

			Date endingDate = new Date(OrangizerUtils.getLong(cursor, EventsColumns.ENDING_DATE));
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

		public Builder setDescription(String description) {
			mDescription = description;
			return this;
		}

		public Builder setStartingDate(Date startingDate) {
			mStartingDate = startingDate;
			return this;
		}

		public Builder setEndingDate(Date endingDate) {
			mEndingDate = endingDate;
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
			return new Event(mId, mName, mDescription, mStartingDate, mEndingDate, mAddress,
					mStatus);
		}
	}

}
