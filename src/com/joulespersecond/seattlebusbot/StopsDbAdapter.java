package com.joulespersecond.seattlebusbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StopsDbAdapter {
    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mCtx;
    
    public StopsDbAdapter(Context ctx) {
        mCtx = ctx;
    }
    
    public StopsDbAdapter open() /*throws SQLException*/ {
        mDbHelper = new DbHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * addStop will either add a stop to the database, or update the data and increment 
     * its use count.
     * 
     * @param stopId
     * @param code
     * @param name
     */
    public void addStop(String stopId, 
    		String code, 
    		String name, 
    		String direction,
    		boolean markAsUsed) {
    	final String where = DbHelper.KEY_STOPID + " = '" + stopId + "'";
        Cursor cursor =
        	mDb.query(DbHelper.STOPS_TABLE, 
        				new String[] { DbHelper.KEY_USECOUNT }, // rows
        				where, // selection (where)
        				null, // selectionArgs
        				null, // groupBy
        				null, // having
        				null, // order by
        				null); // limit
        
        ContentValues args = new ContentValues();
        args.put(DbHelper.KEY_CODE, code);
        args.put(DbHelper.KEY_NAME, name);
        args.put(DbHelper.KEY_DIRECTION, direction);
       
        if (cursor != null && cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	if (markAsUsed) {
        		args.put(DbHelper.KEY_USECOUNT, cursor.getInt(0) + 1);
        	} 
        	mDb.update(DbHelper.STOPS_TABLE, args, where, null);
        }
        else {
        	// Insert a new entry
        	args.put(DbHelper.KEY_STOPID, stopId);
        	if (markAsUsed) {
            	args.put(DbHelper.KEY_USECOUNT, 1);        		
        	}
        	else {
            	args.put(DbHelper.KEY_USECOUNT, 0);
        	}
        	mDb.insert(DbHelper.STOPS_TABLE, null, args);
        }
    	// Make sure the cursor is closed if it exists
    	if (cursor != null) {
    		cursor.close();
    	}
    }
    public static void addStop(Context ctx, String stopId, String code, 
    		String name, String direction, boolean markAsUsed) {
    	StopsDbAdapter adapter = new StopsDbAdapter(ctx);
    	adapter.open();
    	adapter.addStop(stopId, code, name, direction, markAsUsed);
    	adapter.close();
    }
    public static void clearFavorites(Context ctx) {
    	StopsDbAdapter adapter = new StopsDbAdapter(ctx);
    	adapter.open();
    	adapter.clearFavorites();
    	adapter.close();
    }
    
	private static final String WHERE = String.format("%s=?", 
			DbHelper.KEY_STOPID);
	private static final String[] COLS = new String[] { 
			DbHelper.KEY_STOPID, 
			DbHelper.KEY_CODE, 
			DbHelper.KEY_NAME, 
			DbHelper.KEY_DIRECTION, 
			DbHelper.KEY_USECOUNT
 	};
    
    public static final int STOP_COL_STOPID = 0;
    public static final int STOP_COL_CODE = 1;
    public static final int STOP_COL_NAME = 2;
    public static final int STOP_COL_DIRECTION = 3;
    public static final int STOP_COL_USECOUNT = 4;
    
    public Cursor getStop(String stopId) {
    	final String[] whereArgs = new String[] { stopId };
        Cursor cursor =
        	mDb.query(DbHelper.STOPS_TABLE, 
        				COLS, // rows
        				WHERE, // selection (where)
        				whereArgs, // selectionArgs
        				null, // groupBy
        				null, // having
        				null, // order by
        				null); // limit
        if (cursor != null) {
        	cursor.moveToFirst();
        }
        return cursor;    	
    }
    
    public Cursor getFavoriteStops() {
        Cursor cursor =
        	mDb.query(DbHelper.STOPS_TABLE, 
        				COLS,
        				DbHelper.KEY_USECOUNT + " > 0", // selection (where)
        				null, // selectionArgs
        				null, // groupBy
        				null, // having
        				DbHelper.KEY_USECOUNT + " desc", // order by
        				"20"); // limit
        if (cursor != null) {
        	cursor.moveToFirst();
        }
        return cursor;
    }
    public void clearFavorites() {
    	mDb.execSQL("delete from " + DbHelper.STOPS_TABLE);
    }
}