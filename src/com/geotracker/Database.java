package com.geotracker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Database {

	private static final String DATABASE_NAME = "geoevent.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "points";

	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + TABLE_NAME + "(lat, lng) values (?, ?)";

	public Database(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}

	public long save(GeoPoint point) {
		this.insertStmt.bindLong(1, point.getLatitudeE6());
		this.insertStmt.bindLong(2, point.getLongitudeE6());
		return this.insertStmt.executeInsert();
	}

	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}

	public List<GeoPoint> selectAll() {
		List<GeoPoint> list = new ArrayList<GeoPoint>();
		Cursor cursor = this.db.query(TABLE_NAME, new String[] { "lat", "lng" }, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				list.add(new GeoPoint((int) cursor.getLong(0), (int) cursor.getLong(1)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + "(lat LONG, lng LONG)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Example", "Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
}