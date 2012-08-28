package com.orange.labs.uk.orangizer.attendee;

import java.util.List;

import com.orange.labs.uk.orangizer.callback.Callback;
import com.orange.labs.uk.orangizer.event.Event;

/**
 * Interface defining the method for fetching attendees for a list of events. Once the attendees
 * have been fetched, a callback is executed provided the list back including the attendees.
 */
public interface AttendeeFetcher {

	public abstract void fetch(List<Event> events, Callback<List<Event>> callback);

	public abstract void fetch(Event event, Callback<Event> callback);

}
