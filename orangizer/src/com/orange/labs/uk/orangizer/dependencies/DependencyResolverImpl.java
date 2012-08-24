package com.orange.labs.uk.orangizer.dependencies;

import android.content.Context;

import com.orange.labs.uk.orangizer.db.DatabaseHelper;
import com.orange.labs.uk.orangizer.event.EventsDatabase;
import com.orange.labs.uk.orangizer.settings.SettingsManager;

public class DependencyResolverImpl implements DependencyResolver {

	private static DependencyResolver sInstance;

	private Context mAppContext;

	private SettingsManager mSettingsManager;
	private DatabaseHelper mDbHelper;
	private EventsDatabase mEventsDatabase;

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
	
	private DatabaseHelper getDatabaseHelper() {
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(getApplicationContext());
		}
		return mDbHelper;
	}

}
