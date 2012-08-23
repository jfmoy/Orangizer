package com.orange.labs.uk.orangizer.dependencies;

import android.content.Context;

import com.orange.labs.uk.orangizer.settings.SettingsManager;

public class OrangizerDependencyResolverImpl implements OrangizerDependencyResolver {

	private static OrangizerDependencyResolver sInstance;

	private Context mAppContext;

	private SettingsManager mSettingsManager;

	public static void initialize(Context appContext) {
		if (sInstance == null) {
			sInstance = new OrangizerDependencyResolverImpl(appContext);
		}
	}

	public static OrangizerDependencyResolver getInstance() {
		if (sInstance == null) {
			throw new IllegalStateException(
					"Dependency resolver should be initialized before being accessed");
		}
		return sInstance;
	}

	private OrangizerDependencyResolverImpl(Context appContext) {
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

}
