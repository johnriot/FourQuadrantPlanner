package com.example.fourquadrantcontentprovider;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;

// Contract Class for accessing ContentResolver 

public final class DataContract {

	public static final String TODO_TABLE = "todo_table";
	public static final String _ID = "_id";
	public static final String TODO_TEXT = "todo_item";
	public static final String REF_QUADRANTS_ID = "quadrants_id";

	public static final String QUADRANTS_TABLE = "quadrants_table";
	public static final String QUADRANTS_ID = "_id";
	public static final String QUADRANTS_CATEGORY = "category";

	private static final Uri BASE_URI = Uri
			.parse("content://com.example.fourquadrantcontentprovider/");

	// The URI for this table.
	public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,
			TODO_TABLE);

	// Mime type for a directory of data items
	public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/FourQuadrantContentProvider.data.text";

	// Mime type for a single data item
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/FourQuadrantContentProvider.data.text";

	// All columns of todo table
	public static final String[] ALL_TODO_COLUMNS = { _ID, TODO_TEXT,
			REF_QUADRANTS_ID };

	// All columns of quadrants table
	public static final String[] ALL_QUADRANT_COLUMNS = { QUADRANTS_ID,
			QUADRANTS_CATEGORY };

}