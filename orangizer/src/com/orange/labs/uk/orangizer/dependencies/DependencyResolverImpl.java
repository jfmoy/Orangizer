package com.orange.labs.uk.orangizer.dependencies;

import android.content.Context;

import com.facebook.android.Facebook;
import com.orange.labs.uk.orangizer.attendee.InvitedFetcher;
import com.orange.labs.uk.orangizer.attendee.FacebookInvitedFetcher;
import com.orange.labs.uk.orangizer.db.DatabaseHelper;
import com.orange.labs.uk.orangizer.event.EventCreator;
import com.orange.labs.uk.orangizer.event.EventPoster;
import com.orange.labs.uk.orangizer.event.EventsDatabase;
import com.orange.labs.uk.orangizer.event.FacebookEventFetcher;
import com.orange.labs.uk.orangizer.event.FacebookEventPoster;
import com.orange.labs.uk.orangizer.fetch.EventFetcher;
import com.orange.labs.uk.orangizer.friends.FacebookFriendsFetcher;
import com.orange.labs.uk.orangizer.friends.FriendsFetcher;
import com.orange.labs.uk.orangizer.settings.SettingsManager;
import com.orange.labs.uk.orangizer.utils.Constants;

public class DependencyResolverImpl implements DependencyResolver {

	private static DependencyResolver sInstance;

	private Context mAppContext;

	private SettingsManager mSettingsManager;
	private DatabaseHelper mDbHelper;
	private EventsDatabase mEventsDatabase;
	
	private final Facebook mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);

	public static void initialize(Context appContext) {
		if (sInstance == null) {
			sInstance = new DependencyResolverImpl(appContext);
		}
	}

	public static DependencyResolver getInstance() {
		if (sInstance == null) {
			throw new IllegalStateException(
					"Dependency resolver should be initialized before being accessed");
		}
		return sInstance;
	}

	private DependencyResolverImpl(Context appContext) {
		mAppContext = appContext;
	}

	@Override
	public Context getApplicationContext() {
		return mAppContext;
	}

	@Override
	public SettingsManager getSettingsManager() {
		if (mSettingsManager == null) {
			mSettingsManager = new SettingsManager(getApplicationContext());
		}
		return mSettingsManager;
	}
	
	@Override
	public EventsDatabase getEventsDatabase() {
		if (mEventsDatabase == null) {
			mEventsDatabase = new EventsDatabase(getDatabaseHelper());
		}
		return mEventsDatabase;
	}
	
	@Override
	public Facebook getFacebook() {
		return mFacebook;
	}
	
	@Override
	public EventFetcher getEventsFetcher() {
		return new FacebookEventFetcher(getFacebook(), getEventsDatabase());
	}
	
	@Override
	public InvitedFetcher getAttendeesFetcher() {
		return new FacebookInvitedFetcher(mFacebook);
	}
	
	private DatabaseHelper getDatabaseHelper() {
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(getApplicationContext());
		}
		return mDbHelper;
	}

	@Override
	public EventPoster getEventPoster() {
		return new FacebookEventPoster(getFacebook(), getEventsDatabase());
	}

	@Override
	public FriendsFetcher getFriendsFetcher() {
		return new FacebookFriendsFetcher(getFacebook());
	}

	@Override
	public EventCreator getEventCreator() {
		return new EventCreator();
	}


}
