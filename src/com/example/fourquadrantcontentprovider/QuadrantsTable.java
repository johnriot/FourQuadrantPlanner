package com.example.fourquadrantcontentprovider;

import com.example.fourquadrantplanner.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuadrantsTable {

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ DataContract.QUADRANTS_TABLE + " (" + DataContract.QUADRANTS_ID
			+ " integer primary key autoincrement, "
			+ DataContract.QUADRANTS_CATEGORY + " text not null" + ");";

	/*
	 * private static final String FIRST_QUADRANT_ROW_INSERT = "insert into " +
	 * DataContract.QUADRANTS_TABLE + " values " + "(1, " +
	 * getString(R.string.first_quadrant_heading) + ");";
	 * 
	 * private static final String SECOND_QUADRANT_ROW_INSERT = "insert into " +
	 * DataContract.QUADRANTS_TABLE + " values " + "(2, " +
	 * R.string.second_quadrant_heading + ");";
	 * 
	 * private static final String THIRD_QUADRANT_ROW_INSERT = "insert into " +
	 * DataContract.QUADRANTS_TABLE + " values " + "(3, " +
	 * R.string.third_quadrant_heading + ");";
	 * 
	 * private static final String FOURTH_QUADRANT_ROW_INSERT = "insert into " +
	 * DataContract.QUADRANTS_TABLE + " values " + "(4, " +
	 * R.string.fourth_quadrant_heading + ");";
	 */

	public static void onCreate(SQLiteDatabase database, Context context) {
		database.execSQL(DATABASE_CREATE);

		final String FIRST_QUADRANT_ROW_INSERT = "insert into "
				+ DataContract.QUADRANTS_TABLE + " values " + "(1, \""
				+ context.getString(R.string.first_quadrant_heading) + "\");";
		final String SECOND_QUADRANT_ROW_INSERT = "insert into "
				+ DataContract.QUADRANTS_TABLE + " values " + "(2, \""
				+ context.getString(R.string.second_quadrant_heading) + "\");";
		final String THIRD_QUADRANT_ROW_INSERT = "insert into "
				+ DataContract.QUADRANTS_TABLE + " values " + "(3, \""
				+ context.getString(R.string.third_quadrant_heading) + "\");";
		final String FOURTH_QUADRANT_ROW_INSERT = "insert into "
				+ DataContract.QUADRANTS_TABLE + " values " + "(4, \""
				+ context.getString(R.string.fourth_quadrant_heading) + "\");";

		database.execSQL(FIRST_QUADRANT_ROW_INSERT);
		database.execSQL(SECOND_QUADRANT_ROW_INSERT);
		database.execSQL(THIRD_QUADRANT_ROW_INSERT);
		database.execSQL(FOURTH_QUADRANT_ROW_INSERT);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TodoTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + DataContract.QUADRANTS_TABLE);
	}
}