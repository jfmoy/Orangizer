package com.orange.labs.uk.orangizer.activities.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.orange.labs.uk.orangizer.friends.Friend;

public class AddressBookListFragment extends SherlockListFragment {

	private Cursor mCursor;
	private OnAddressBookFriendSelected mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnAddressBookFriendSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnAddressBook..");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ContentResolver cr = getActivity().getContentResolver();
		mCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME }, withPhone(), null, Contacts.DISPLAY_NAME + " ASC");
		getActivity().startManagingCursor(mCursor);

		setListAdapter(new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_multiple_choice, mCursor,
				new String[] { Contacts.DISPLAY_NAME }, new int[] { android.R.id.text1 }));

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	private String withPhone() {
		StringBuilder builder = new StringBuilder();
		builder.append(Contacts.HAS_PHONE_NUMBER);
		builder.append("=1");
		return builder.toString();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mCursor.moveToPosition(position);
		String name = mCursor.getString(mCursor.getColumnIndex(Contacts.DISPLAY_NAME));
		String id_friend = mCursor.getString(mCursor.getColumnIndex(Contacts._ID));

		mCallback.onAddressBookFriendSelected(new Friend(id_friend, name), l.isItemChecked(position));
	}

	public interface OnAddressBookFriendSelected {
		public void onAddressBookFriendSelected(Friend friend, boolean selected);
	}
}