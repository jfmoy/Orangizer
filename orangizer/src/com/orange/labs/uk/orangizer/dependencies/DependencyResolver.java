package com.orange.labs.uk.orangizer.dependencies;

import android.content.Context;

import com.facebook.android.Facebook;
import com.orange.labs.uk.orangizer.attendee.AttendeeFetcher;
import com.orange.labs.uk.orangizer.event.EventsDatabase;
import com.orange.labs.uk.orangizer.fetch.EventFetcher;
import com.orange.labs.uk.orangizer.settings.SettingsManager;

public interface DependencyResolver {

	/** Returns the application {@link Context} */
	public Context getApplicationContext();
	
	/** Return the SettingsManager used to stores application settings and configuration */
	public SettingsManager getSettingsManager();
	
	/** Return the Events Database used */
	public EventsDatabase getEventsDatabase();
	
	/** Return the Facebook object used to communicating with their platform */
	public Facebook getFacebook();
	
	/** Return the Facebook Events Fetcher */
	public EventFetcher getFacebookEventsFetcher();

	/** Return the Facebook Attendees Fetcher */
	public AttendeeFetcher getFacebookAttendeesFetcher();

}
