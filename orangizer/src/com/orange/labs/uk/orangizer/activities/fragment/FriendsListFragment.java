package com.orange.labs.uk.orangizer.activities.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolver;
import com.orange.labs.uk.orangizer.dependencies.DependencyResolverImpl;
import com.orange.labs.uk.orangizer.friends.Friend;
import com.orange.labs.uk.orangizer.utils.Logger;

public class FriendsListFragment extends SherlockListFragment {

	private List<Friend> mFriends;
	private OnFacebookFriendSelected mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnFacebookFriendSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFriendSelected");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		DependencyResolver resolver = DependencyResolverImpl.getInstance();
		resolver.getFriendsFetcher().fetch(new Callback<List<Friend>>() {

			@Override
			public void onSuccess(List<Friend> result) {
				mFriends = result;
				if (mFriends != null) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							setListAdapter(new ArrayAdapter<Friend>(getActivity(),
									android.R.layout.simple_list_item_multiple_choice, mFriends));

							getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
						}
					});
				}
			}

			@Override
			public void onFailure(Exception e) {
				// Do nothing
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCallback.onFacebookFriendSelected(mFriends.get(position), l.isItemChecked(position));
	}
	
	public interface OnFacebookFriendSelected {
		public void onFacebookFriendSelected(Friend friend, boolean selected);
	}
}