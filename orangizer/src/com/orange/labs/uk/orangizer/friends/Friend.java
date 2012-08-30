package com.orange.labs.uk.orangizer.friends;

public class Friend implements Comparable<Friend> {

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
	
}
