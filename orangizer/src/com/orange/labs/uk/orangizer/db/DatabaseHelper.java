package com.orange.labs.uk.orangizer.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orange.labs.uk.orangizer.event.EventsColumns;
import com.orange.labs.uk.orangizer.event.EventsDatabase;

/**
 * Helper class to create a database helper for a set of columns.
 * <p>
 * This class simply wraps several {@link TableCreator} instances for each of the table of the
 * application. It also contains the global properties of the database such as its name, version
 * and principle tables.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
    private static final String DB_NAME = "omtpstack.db";
    private static final int DB_VERSION = 1;
    private static final HashMap<String, DatabaseColumn[]> DB_COLUMNS = 
    		new HashMap<String, DatabaseColumn[]>();
    		
    static {
    	DB_COLUMNS.put(EventsDatabase.EVENTS_TABLE_NAME, EventsColumns.values());
    }
    		
	
    /** The version of the database to create. */
    private final int mVersion;
    /** A list of helper objects to create the various tables. */
    private List<TableCreator> mTableCreators;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        mVersion = DB_VERSION;
        mTableCreators = new ArrayList<TableCreator>();
        
        for (String tableName : DB_COLUMNS.keySet()) {
        	mTableCreators.add(new TableCreator(tableName, DB_COLUMNS.get(tableName)));
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	for (TableCreator tableCreator : mTableCreators) {
    		db.execSQL(tableCreator.getCreateTableQuery(mVersion));
    	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (TableCreator tableCreator : mTableCreators) {
        	db.execSQL(tableCreator.getUpgradeTableQuery(oldVersion, newVersion));
        }
    }
}
