package com.orange.labs.uk.orangizer.event;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orange.labs.uk.orangizer.db.DatabaseHelper;
import com.orange.labs.uk.orangizer.utils.OrangizerUtils;

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
		List<Event> events = new ArrayList<Event>();
		Cursor cursor = null;
		try {
			cursor = getEventsCursor();
			while (cursor.moveToNext()) {
				Event event = new Event.Builder().fromCursor(cursor).build();
				events.add(event);
			}
		} finally {
			OrangizerUtils.closeQuietly(cursor);
		}

		return events;
	}
	
	/**
	 * Returns a Cursor pointing on all events stored in the database.
	 */
	public Cursor getEventsCursor() {
		SQLiteDatabase database = mDbHelper.getReadableDatabase();
		return database.query(EVENTS_TABLE_NAME, null, null, null, null, null, null);
	}

	/**
	 * Retrieve the event specified with its ID from the database. Returns null if it does not
	 * exist.
	 */
	public Event getEvent(String id) {
		SQLiteDatabase database = mDbHelper.getReadableDatabase();

		String query = getWhereClause(EventsColumns.ID, id);

		Event event = null;
		Cursor cursor = null;
		try {
			cursor = database.query(EVENTS_TABLE_NAME, null, query, null, null, null, null);
			if (cursor.moveToFirst()) {
				event = new Event.Builder().fromCursor(cursor).build();
			}
		} finally {
			OrangizerUtils.closeQuietly(cursor);
		}

		return event;
	}

	/** Insert the provided {@link Event} in the database. */
	public boolean insert(Event event) {
		Event existingEvent = getEvent(event.getId());
		if (existingEvent != null) {
			return update(event);
		}
		
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		return (database.insert(EVENTS_TABLE_NAME, null, getContentValues(event)) != -1);
	}

	/** Update the provided {@link Event} in the database */
	public boolean update(Event event) {
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		String whereClause = getWhereClause(EventsColumns.ID, event.getId());
		return (database.update(EVENTS_TABLE_NAME, getContentValues(event), whereClause, null) == 1);
	}

	/** Delete the specified {@link Event} from the databse */
	public boolean delete(Event event) {
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		String query = getWhereClause(EventsColumns.ID, event.getId());
		return (database.delete(EVENTS_TABLE_NAME, query, null) == 1);
	}
	
	/** Delete all the stored events */
	public void deleteAll() {
		SQLiteDatabase database = mDbHelper.getWritableDatabase();
		database.delete(EVENTS_TABLE_NAME, null, null);
	}

	/**
	 * Get a {@link ContentValues} used for inserting or updating a table row based on the provided
	 * event.
	 */
	private ContentValues getContentValues(Event event) {
		ContentValues values = new ContentValues();
		if (event.hasId()) {
			values.put(EventsColumns.ID.getName(), event.getId());
		}
		if (event.hasName()) {
			values.put(EventsColumns.NAME.getName(), event.getName());
		}
		if (event.hasOrganizer()) {
			values.put(EventsColumns.ORGANIZER.getName(), event.getOrganizer());
		}
		if (event.hasDescription()) {
			values.put(EventsColumns.DESCRIPTION.getName(), event.getDescription());
		}
		if (event.hasAddress()) {
			values.put(EventsColumns.ADDRESS.getName(), event.getAddress());
		}
		if (event.hasStartingDate()) {
			values.put(EventsColumns.STARTING_DATE.getName(), event.getStartingDate().getTime());
		}
		if (event.hasEndingDate()) {
			values.put(EventsColumns.ENDING_DATE.getName(), event.getEndingDate().getTime());
		}
		if (event.hasStatus()) {
			values.put(EventsColumns.STATUS.getName(), event.getStatus().getCode());
		}

		return values;
	}

	/** Generates a WHERE clause using the provided column and value */
	private String getWhereClause(EventsColumns column, String value) {
		StringBuilder builder = new StringBuilder();
		builder.append(column.getName());
		builder.append("='");
		builder.append(value);
		builder.append("'");
		return builder.toString();
	}
}
