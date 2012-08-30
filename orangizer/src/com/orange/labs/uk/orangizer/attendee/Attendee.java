package com.orange.labs.uk.orangizer.attendee;

import org.json.JSONException;
import org.json.JSONObject;

import com.orange.labs.uk.orangizer.event.RsvpStatus;
import com.orange.labs.uk.orangizer.utils.Logger;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

/**
 * Attendee for an Event.
 */
public class Attendee {

	private static final Logger sLogger = Logger.getLogger(Attendee.class);

	private String mName;

	private RsvpStatus mStatus;

	private String mFacebookId;

	private String mAddressBookId;

	private String mEmail;

	private String mPhoneNumber;

	private String mTwitterUsername;

	private Attendee(String name, String email, String phoneNumber, RsvpStatus status,
			String facebookId, String twitterUsername, String addressBookId) {
		mName = name;
		mStatus = status;
		mFacebookId = facebookId;
		mEmail = email;
		mPhoneNumber = phoneNumber;
		mTwitterUsername = twitterUsername;
		mAddressBookId = addressBookId;
	}

	public boolean hasName() {
		return (mName != null);
	}

	public boolean hasStatus() {
		return (mStatus != null);
	}

	public String getName() {
		return mName;
	}

	public RsvpStatus getStatus() {
		return mStatus;
	}

	public String getFacebookId() {
		return mFacebookId;
	}

	public String getEmail() {
		return mEmail;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public String getTwitterUsername() {
		return mTwitterUsername;
	}

	public void setAddressBookId(String id) {
		mAddressBookId = id;
	}

	public String getAddressBookId() {
		return mAddressBookId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Attendee: [\n");
		builder.append(String.format("Name: %s, \n", mName));
		builder.append(String.format("Facebook ID: %s, \n", mFacebookId));
		builder.append(String.format("Address Book ID: %s, \n", mAddressBookId));
		builder.append(String.format("Status: %s, \n", mStatus));
		builder.append("]");
		return builder.toString();
	}

	public static class Builder {

		private String mName;

		private RsvpStatus mStatus;

		private String mFacebookId;

		private String mPhoneNumber;

		private String mEmail;

		private String mTwitterUsername;

		private String mAddressBookId;

		public Builder setName(String name) {
			mName = name;
			return this;
		}

		public Builder setAddressBookId(String addressBookId) {
			mAddressBookId = addressBookId;
			return this;
		}

		public Builder setStatus(RsvpStatus status) {
			mStatus = status;
			return this;
		}

		public Builder setFacebookId(String facebookId) {
			mFacebookId = facebookId;
			return this;
		}

		public Builder setEmail(String email) {
			mEmail = email;
			return this;
		}

		public Builder setPhoneNumber(String phoneNumber) {
			mPhoneNumber = phoneNumber;
			return this;
		}

		public Builder setTwitterUsername(String twitterUsername) {
			mTwitterUsername = twitterUsername;
			return this;
		}

		public Attendee build() {
			return new Attendee(mName, mEmail, mPhoneNumber, mStatus, mFacebookId,
					mTwitterUsername, mAddressBookId);
		}

		public Builder fromJsonObject(JSONObject object) {
			try {
				mFacebookId = object.has("id") ? object.getString("id") : null;
				mName = object.has("name") ? object.getString("name") : null;

				// Convert status key to enum value
				String statusKey = object.has("rsvp_status") ? object.getString("rsvp_status")
						: null;
				mStatus = OrangizerUtils.valueToEnumValue(statusKey, RsvpStatus.class);
			} catch (JSONException e) {
				sLogger.w(String.format("This should never happen: %s", e));
			}

			return this;
		}

	}

	public boolean hasFacebookId() {
		return (mFacebookId != null && mFacebookId.length() >0);
	}

	public boolean hasAddressBookId() {
		return (mAddressBookId != null && mAddressBookId.length() > 0);
	}

}
