package com.orange.labs.uk.orangizer.dependencies;

import android.content.Context;

import com.orange.labs.uk.orangizer.event.EventsDatabase;
import com.orange.labs.uk.orangizer.settings.SettingsManager;

public interface DependencyResolver {

	/** Returns the application {@link Context} */
	public Context getApplicationContext();
	
	/** Return the SettingsManager used to stores application settings and configuration */
	public SettingsManager getSettingsManager();
	
	/** Return the Events Database used */
	public EventsDatabase getEventsDatabase();

}
