package com.orange.labs.uk.orangizer.activities;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.orange.labs.uk.orangizer.R;
import com.orange.labs.uk.orangizer.activities.fragment.AddressBookListFragment;
import com.orange.labs.uk.orangizer.activities.fragment.AddressBookListFragment.OnAddressBookFriendSelected;
import com.orange.labs.uk.orangizer.activities.fragment.FriendsListFragment;
import com.orange.labs.uk.orangizer.activities.fragment.FriendsListFragment.OnFacebookFriendSelected;
import com.orange.labs.uk.orangizer.friends.Friend;
import com.orange.labs.uk.orangizer.utils.Logger;

public class ChooseGuestsActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener, OnFacebookFriendSelected, OnAddressBookFriendSelected {

	private static final Logger sLogger = Logger.getLogger(ChooseGuestsActivity.class);

	public static final String FACEBOOK_FRIENDS_KEY = "facebook_friends";
	public static final String ADDRESSBOOK_FRIENDS_KEY = "addressbook_friends";

	public static final int PICK_GUESTS = 0;
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best to
	 * switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	Set<Friend> mFacebookFriends = new TreeSet<Friend>();
	
	Set<Friend> mAddressBookFriends = new TreeSet<Friend>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_guests);

		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getSherlock().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding tab.
		// We can also use ActionBar.Tab#select() to do this if we have a reference to the
		// Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_choose_guests, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.choose_guests_menu_done) {
			Intent intent = generateResultIntent();
			setResult(RESULT_OK, intent);
			finish();
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private Intent generateResultIntent() {
		Intent intent = new Intent();
		intent.putExtra(FACEBOOK_FRIENDS_KEY, new ArrayList<Friend>(mFacebookFriends));
		intent.putExtra(ADDRESSBOOK_FRIENDS_KEY, new ArrayList<Friend>(mAddressBookFriends));
		return intent;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			if (i == 0) {
				fragment = new AddressBookListFragment();
			} else {
				fragment = new FriendsListFragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onAddressBookFriendSelected(Friend friend, boolean selected) {
		if (selected) {
			mAddressBookFriends.add(friend);
		} else {
			mAddressBookFriends.remove(friend);
		}
		
		sLogger.d(mAddressBookFriends.toString());
	}

	@Override
	public void onFacebookFriendSelected(Friend friend, boolean selected) {
		if (selected) {
			mFacebookFriends.add(friend);
		} else {
			mFacebookFriends.remove(friend);
		}
		
		sLogger.d(mFacebookFriends.toString());
	}
}
