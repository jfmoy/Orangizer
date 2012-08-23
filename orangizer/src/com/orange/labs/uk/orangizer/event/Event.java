package com.orange.labs.uk.orangizer.event;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.orange.labs.uk.orangizer.utils.Logger;

public class Event {

	private static final Logger sLogger = Logger.getLogger(Event.class);
	
	private final String mId;

	private final String mName;

	private final String mDescription;

	private final Date mStartingDate;

	private final Date mEndingDate;

	private final String mAddress;

	private final RsvpStatus mStatus;

	private Event(String id, String name, String description, Date startDate, Date endingDate,
			String address, RsvpStatus status) {
		if (id == null || name == null) {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Event: ");
		builder.append(String.format("ID: %s, ", mId));
		builder.append(String.format("Name: %s", mName));
		return builder.toString();
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
				//TODO: format start time and end time
			} catch (JSONException e) {
				sLogger.w(String.format("This should never happen: %s", e));
			}
			
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
