package com.orange.labs.uk.orangizer.event;

import com.orange.labs.uk.orangizer.db.DatabaseColumn;

/**
 * Enumeration defining columns needed to store an {@link Event} object in the database. 
 * 
 * <p>
 * It associates the name of the column to its SQL type and the version of the database it has been
 * introduced at.
 */
public enum EventsColumns implements DatabaseColumn {
	ID("_id", "TEXT PRIMARY KEY", 1),
	NAME("name", "TEXT", 1),
	ORGANIZER("organizer", "TEXT", 1),
	DESCRIPTION("description", "TEXT", 1),
	ADDRESS("address", "TEXT", 1),
	STARTING_DATE("starting_date", "INTEGER", 1),
	ENDING_DATE("ending_date", "INTEGER", 1),
	STATUS("status", "TEXT", 1);

	private final String mName;
	private final String mSqlType;
	private final int mSinceVersion;

	private EventsColumns(String name, String sqlType, int sinceVersion) {
		mName = name;
		mSqlType = sqlType;
		mSinceVersion = sinceVersion;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getType() {
		return mSqlType;
	}

	@Override
	public int getSinceVersion() {
		return mSinceVersion;
	}

}
