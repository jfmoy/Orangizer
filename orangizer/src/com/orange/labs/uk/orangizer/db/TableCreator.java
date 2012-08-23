package com.orange.labs.uk.orangizer.db;

/**
 * A helper class to create and upgrade an SQLite table using the provided {@link DatabaseColumn}
 * objects.
 */
public class TableCreator {
	private final String mName;
	private final DatabaseColumn[] mColumns;

	public TableCreator(String name, DatabaseColumn[] columns) {
		mName = name;
		mColumns = columns;
	}

	public String getCreateTableQuery(int version) {
		return String.format("CREATE TABLE %s (%s);", mName, getColumns(version));
	}

	public String getUpgradeTableQuery(int oldVersion, int newVersion) {
		StringBuilder builder = new StringBuilder();
		for (DatabaseColumn column : mColumns) {
			int sinceVersion = column.getSinceVersion();
			if (sinceVersion > oldVersion && sinceVersion <= newVersion) {
				builder.append("ALTER TABLE ");
				builder.append(mName);
				builder.append(" ADD COLUMN ");
				builder.append(column.getColumnName());
				builder.append(" ");
				builder.append(column.getColumnType());
				builder.append(";");
			}
		}
		return builder.toString();
	}

	private String getColumns(int version) {
		StringBuilder builder = new StringBuilder();
		for (DatabaseColumn column : mColumns) {
			if (column.getSinceVersion() <= version) {
				if (builder.length() != 0) {
					builder.append(", ");
				}
				builder.append(column.getColumnName());
				builder.append(" ");
				builder.append(column.getColumnType());
			}
		}
		return builder.toString();
	}
}
