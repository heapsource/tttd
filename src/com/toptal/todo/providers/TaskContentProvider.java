package com.toptal.todo.providers;

import com.toptal.todo.Content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Content provider for Tasks.
 * @author thepumpkin
 *
 */
public class TaskContentProvider extends ContentProvider {

	private SQLiteDatabase sqlDB;

	private DatabaseHelper dbHelper;

	private static final String DATABASE_NAME = "Tasks.db";

	private static final int DATABASE_VERSION = 5;

	private static final String TABLE_NAME = "Tasks";

	/**
	 * An internal helper to create and upgrade the internal SQLLite Database.
	 * @author thepumpkin
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// create table to store user names
			db.execSQL("Create table "
					+ TABLE_NAME
					+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DETAILS TEXT, COMPLETED INTEGER, PRIORITY INTEGER);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = db.delete(TABLE_NAME, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		// get database to insert records
		sqlDB = dbHelper.getWritableDatabase();
		// insert record in user table and get the row number of recently inserted record
		long rowId = sqlDB.insert(TABLE_NAME, "", contentvalues);
		if (rowId > 0) {
			Uri rowUri = ContentUris.appendId(
					Content.Task.CONTENT_URI.buildUpon(), rowId).build();
			getContext().getContentResolver().notifyChange(rowUri, null);
			return rowUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		qb.setTables(TABLE_NAME);
		Cursor c = qb.query(db, projection, selection, null, null, null,
				sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String where,
			String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = db.update(TABLE_NAME, contentvalues, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}