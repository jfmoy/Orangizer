package com.orange.labs.uk.orangizer.friends;

import java.util.List;

import com.orange.labs.uk.orangizer.callback.Callback;

/**
 * Defines methods required for class allowing to fetch friends from a various source.
 */
public interface FriendsFetcher {

	/** Fetch all friends from Facebook */
	public void fetch(Callback<List<Friend>> callback);
	
}
