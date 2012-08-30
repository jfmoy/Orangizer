package com.orange.labs.uk.orangizer.friends;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Comparable<Friend>, Parcelable {

	private String mId;
	private String mName;

	public Friend(String id, String name) {
		mId = id;
		mName = name;
	}

	public String getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String toString() {
		return mName;
	}

	@Override
	public int compareTo(Friend oFriend) {
		return getName().compareTo(oFriend.getName()) + getId().compareTo(oFriend.getId());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeString(mName);
	}

	private Friend(Parcel in) {
		mId = in.readString();
		mName = in.readString();
	}

	public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
		public Friend createFromParcel(Parcel in) {
			return new Friend(in);
		}

		public Friend[] newArray(int size) {
			return new Friend[size];
		}
	};

}
