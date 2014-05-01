package com.example.fourquadrantcontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

// Note: Currently, this data does not persist across device reboot
public class FourQuadrantContentProvider extends ContentProvider {

	// Data storage
	private FourQuadrantDatabaseHelper database;

	@SuppressWarnings("unused")
	private static final String TAG = "FourQuadrantPlannerContentProvider";

	// Delete some or all data items
	@Override
	public synchronized int delete(Uri uri, String selection,
			String[] selectionArgs) {

		int rowsDeleted = 0;
		SQLiteDatabase sqlDB = database.getWritableDatabase();

		if (isTableUri(uri)) {
			rowsDeleted = sqlDB.delete(DataContract.TODO_TABLE, null, null);
		} else if (isItemUri(uri)) {
			Integer requestId = Integer.parseInt(uri.getLastPathSegment());
			rowsDeleted = sqlDB.delete(DataContract.TODO_TABLE,
					DataContract._ID + "=" + requestId + " and " + selection,
					selectionArgs);
		}

		// return number of items deleted
		return rowsDeleted;
	}

	// Return MIME type for given uri
	@Override
	public synchronized String getType(Uri uri) {
		String contentType = DataContract.CONTENT_ITEM_TYPE;

		if (isTableUri(uri)) {
			contentType = DataContract.CONTENT_DIR_TYPE;
		}
		return contentType;
	}

	// Insert specified value into ContentProvider
	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		if (values.containsKey(DataContract._ID)
				&& values.containsKey(DataContract.TODO_TEXT)
				&& values.containsKey(DataContract.REF_QUADRANTS_ID)) {

			long id = sqlDB.insert(DataContract.TODO_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.withAppendedPath(DataContract.CONTENT_URI,
					String.valueOf(id));
		}
		return null;
	}

	// return all or some rows from ContentProvider based on specified Uri
	// all other parameters are ignored

	@Override
	public synchronized Cursor query(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Set the table
		queryBuilder.setTables(DataContract.TODO_TABLE);

		SQLiteDatabase localDb = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(localDb,
				DataContract.ALL_TODO_COLUMNS, selection, selectionArgs, null,
				null, sortOrder);

		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	// Update
	@Override
	public synchronized int update(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {

		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int numUpdates = 0;
		if (// values.containsKey(DataContract._ID) &&
		values.containsKey(DataContract.TODO_TEXT)
		// && values.containsKey(DataContract.REF_QUADRANTS_ID)
		) {

			numUpdates = sqlDB.update(DataContract.TODO_TABLE, values,
					selection, null);
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return numUpdates;
	}

	// Initialize ContentProvider
	@Override
	public boolean onCreate() {
		database = new FourQuadrantDatabaseHelper(getContext());
		return false;
	}

	// Does last segment of the Uri match a string of digits?
	private boolean isItemUri(Uri uri) {
		return uri.getLastPathSegment().matches("\\d+");
	}

	// Is the last segment of the Uri the name of the data table?
	private boolean isTableUri(Uri uri) {
		return uri.getLastPathSegment().equals(DataContract.TODO_TABLE);
	}

}
