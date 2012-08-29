package com.orange.labs.uk.orangizer.utils;

import java.io.Closeable;
import java.io.IOException;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.orange.labs.uk.orangizer.db.DatabaseColumn;

public class OrangizerUtils {

	/** Empty / Private Constructor */
	private OrangizerUtils() {
	}

	/** Return the string at the specified column of the cursor */
	public static String getString(Cursor cursor, DatabaseColumn column) {
		return cursor.getString(cursor.getColumnIndex(column.getName()));
	}

	/** Return the long at the specified column of the cursor */
	public static long getLong(Cursor cursor, DatabaseColumn column) {
		return cursor.getLong(cursor.getColumnIndex(column.getName()));
	}

	/**
	 * Close the closeable object provided if needed. Should be executed in final blocks only as
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

	/**
	 * Used to get the Enum value for its code. Useful when we want to convert a facebook json value
	 * into an enum.
	 */
	public static <T extends Enum<T> & OrangizerEnum> T valueToEnumValue(String stringValue,
			Class<T> enumClass) {
		for (T enumValue : enumClass.getEnumConstants()) {
			if (enumValue.getCode().equals(stringValue)) {
				return enumClass.cast(enumValue);
			}
		}
		return null;
	}

	public static Intent getMapIntent(String address) {
		return new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?q=" + address));
	}
}
