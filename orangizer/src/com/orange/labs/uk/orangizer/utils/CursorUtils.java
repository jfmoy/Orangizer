package com.orange.labs.uk.orangizer.utils;

import java.io.Closeable;
import java.io.IOException;

import com.orange.labs.uk.orangizer.db.DatabaseColumn;

import android.database.Cursor;

public class CursorUtils {
	
	/** Return the string at the specified column of the cursor */
	public static String getString(Cursor cursor, DatabaseColumn column) {
		return cursor.getString(cursor.getColumnIndex(column.getColumnName()));
	}
	
	/** Return the long at the specified column of the cursor */
	public static long getLong(Cursor cursor, DatabaseColumn column) {
		return cursor.getLong(cursor.getColumnIndex(column.getColumnName()));
	}
	
	public static void finallyClose(Closeable closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
	
}
