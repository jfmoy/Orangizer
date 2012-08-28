package com.orange.labs.uk.orangizer.fetch;

import java.util.List;

import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.event.Event;

/**
 *	Interface that defines classes that fetch events from a source. 
 */
public interface EventFetcher {

	/**
	 * Fetch events from the source.
	 */
	public abstract void fetch(Callback<List<Event>> callback);
	
}
