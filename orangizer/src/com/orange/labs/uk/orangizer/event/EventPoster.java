package com.orange.labs.uk.orangizer.event;

import com.orange.labs.uk.orangizer.callback.Callback;

public interface EventPoster {

	public void post(Event event, Callback<Event> callback);
	
}
