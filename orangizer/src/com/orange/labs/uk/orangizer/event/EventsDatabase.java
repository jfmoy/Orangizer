package com.orange.labs.uk.orangizer.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orange.labs.uk.orangizer.db.DatabaseHelper;
import com.orange.labs.uk.orangizer.event.Event.Builder;
import com.orange.labs.uk.orangizer.utils.CursorUtils;

public class EventsDatabase {
	/** Table name */
	public static final String EVENTS_TABLE_NAME = "events";

	private final DatabaseHelper mDbHelper;

	public EventsDatabase(final DatabaseHelper dbHelper) {
		mDbHelper = dbHelper;
	}

	/**
	 * Retrieves and return all the {@link Event} currently stored in the database using a
	 * {@link List}.
	 */
	public List<Event> getAllEvents() {
		SQLiteDatabase database = mDbHelper.getReadableDatabase();
		Cursor cursor = database.query(EVENTS_TABLE_NAME, null, null, null, null, null, null);

		List<Event> events = new ArrayList<Event>(cursor.getCount());
		if (cursor.moveToFirst()) {
			Event event = getEventFromCursor(cursor);
			events.add(event);
		}

		return events;
	}

	private Event getEventFromCursor(Cursor cursor) {
		Builder builder = new Event.Builder();
		builder.setId(CursorUtils.getString(cursor, EventsColumns.ID));
		builder.setName(CursorUtils.getString(cursor, EventsColumns.NAME));
		builder.setDescription(CursorUtils.getString(cursor, EventsColumns.DESCRIPTION));
		builder.setAddress(CursorUtils.getString(cursor, EventsColumns.ADDRESS));
		
		Date startingDate = new Date(CursorUtils.getLong(cursor, EventsColumns.STARTING_DATE));
		builder.setStartingDate(startingDate);
		
		Date endingDate = new Date(CursorUtils.getLong(cursor, EventsColumns.ENDING_DATE));
		builder.setEndingDate(endingDate);
		
		//TODO add status
		
		return builder.build();
	}
}
