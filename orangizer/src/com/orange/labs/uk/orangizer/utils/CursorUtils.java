package com.orange.labs.uk.orangizer.utils;

import java.io.Closeable;
import java.io.IOException;

import com.orange.labs.uk.orangizer.db.DatabaseColumn;

import android.database.Cursor;

public class CursorUtils {

	/** Empty / Private Constructor */
	private CursorUtils() {
	}
	
	/** Return the string at the specified column of the cursor */
	public static String getString(Cursor cursor, DatabaseColumn column) {
		return cursor.getString(cursor.getColumnIndex(column.getName()));
	}
	
	/** Return the long at the specified column of the cursor */
	public static long getLong(Cursor cursor, DatabaseColumn column) {
		return cursor.getLong(cursor.getColumnIndex(column.getName()));
	}
	
	/** Close the closeable object provided if needed. Should be executed in final blocks only as
	 * exceptions are ignored
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
	
	/** Close the cursor if it exists */
	public static void closeQuietly(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}
	
}
