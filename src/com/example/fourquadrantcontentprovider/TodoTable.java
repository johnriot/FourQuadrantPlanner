package com.example.fourquadrantcontentprovider;

import com.example.fourquadrantplanner.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ DataContract.TODO_TABLE + " (" + DataContract._ID
			+ " integer primary key autoincrement, " + DataContract.TODO_TEXT
			+ " text not null, " + DataContract.REF_QUADRANTS_ID
			+ " integer not null" + ");";
	private static final String FIRST_TODO_BLANK_INSERT = "insert into "
			+ DataContract.TODO_TABLE + " values " + "(1, \"\", 1);";
	private static final String SECOND_TODO_BLANK_INSERT = "insert into "
			+ DataContract.TODO_TABLE + " values " + "(2, \"\", 2);";
	private static final String THIRD_TODO_BLANK_INSERT = "insert into "
			+ DataContract.TODO_TABLE + " values " + "(3, \"\", 3);";
	private static final String FOURTH_TODO_BLANK_INSERT = "insert into "
			+ DataContract.TODO_TABLE + " values " + "(4, \"\", 4);";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		// Enter 4 empty strings to the database initially.
		database.execSQL(FIRST_TODO_BLANK_INSERT);
		database.execSQL(SECOND_TODO_BLANK_INSERT);
		database.execSQL(THIRD_TODO_BLANK_INSERT);
		database.execSQL(FOURTH_TODO_BLANK_INSERT);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TodoTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + DataContract.TODO_TABLE);
		onCreate(database);
	}
}
