package com.example.fourquadrantcontentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FourQuadrantDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fourquadrant.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    public FourQuadrantDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        databaseCleanup(mContext);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        TodoTable.onCreate(database);
        QuadrantsTable.onCreate(database, mContext);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion) {
        TodoTable.onUpgrade(database, oldVersion, newVersion);
    }

    // Deletes the database. Shouldn't be called normally.
    public void databaseCleanup(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
