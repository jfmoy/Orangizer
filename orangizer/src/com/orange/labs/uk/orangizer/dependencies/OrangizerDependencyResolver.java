package com.orange.labs.uk.orangizer.dependencies;

import android.content.Context;

import com.orange.labs.uk.orangizer.settings.SettingsManager;

public interface OrangizerDependencyResolver {

	/** Returns the application {@link Context} */
	public Context getApplicationContext();
	
	/** Return the SettingsManager used to stores application settings and configuration */
	public SettingsManager getSettingsManager();

}
