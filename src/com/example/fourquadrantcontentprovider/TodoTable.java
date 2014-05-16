package com.example.fourquadrantcontentprovider;

import com.example.fourquadrantplanner.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + DataContract.TODO_TABLE + " (" + DataContract._ID
            + " integer primary key autoincrement, " + DataContract.TODO_TEXT + " text not null, "
            + DataContract.PRIORITY + " integer not null, " + DataContract.REF_QUADRANTS_ID + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TodoTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + DataContract.TODO_TABLE);
        onCreate(database);
    }
}
