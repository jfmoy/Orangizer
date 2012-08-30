package com.orange.labs.uk.orangizer.friends;

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
import com.orange.labs.uk.orangizer.utils.Logger;

public class FacebookFriendsFetcher implements FriendsFetcher {
	
	private static final Logger sLogger = Logger.getLogger(FacebookFriendsFetcher.class);

	private Facebook mFacebook;
	
	public FacebookFriendsFetcher(Facebook facebook) {
		mFacebook = facebook;
	}
	
	@Override
	public void fetch(Callback<List<Friend>> callback) {
		AsyncFacebookRunner runner = new AsyncFacebookRunner(mFacebook);
		runner.request("me/friends/", new FacebookFriendsRequestListener(callback));
	}
	
	private class FacebookFriendsRequestListener implements RequestListener {

		private Callback<List<Friend>> mCallback;
		
		public FacebookFriendsRequestListener(Callback<List<Friend>> callback) {
			mCallback = callback;
		}
		
		@Override
		public void onComplete(String response, Object state) {
			List<Friend> friends = new ArrayList<Friend>();
			JSONObject object;
			JSONArray array;
			try {
				object = new JSONObject(response);
				array = object.has("data") ? object.getJSONArray("data") : null;
				if (array != null) {
					// Get friends
					for (int i = 0; i < array.length(); i++) {
						JSONObject friend = array.getJSONObject(i);
						
						String name = friend.has("name") ? friend.getString("name") : null;
						String id = friend.has("id") ? friend.getString("id") : null;
						
						if (name != null && id != null) {
							friends.add(new Friend(id, name));
						}
					}
				}
			} catch (JSONException e) {
				handleFailure(e);
				return;
			}
			
			sLogger.d(friends.toString());
			mCallback.onSuccess(friends);
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
