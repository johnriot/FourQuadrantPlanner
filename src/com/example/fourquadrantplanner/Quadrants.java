package com.example.fourquadrantplanner;

import java.util.*;

import com.example.fourquadrantcontentprovider.DataContract;
import com.example.fourquadrantcontentprovider.DataRecord;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.EditText;

enum TodoBox {
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

public class Quadrants {
	private ArrayList<EditText> mTodoBoxes = new ArrayList<EditText>();

	public Quadrants(Activity activity) {
		mTodoBoxes
				.add((EditText) activity.findViewById(R.id.top_left_editText));
		mTodoBoxes.add((EditText) activity
				.findViewById(R.id.top_right_editText));
		mTodoBoxes.add((EditText) activity
				.findViewById(R.id.bottom_left_editText));
		mTodoBoxes.add((EditText) activity
				.findViewById(R.id.bottom_right_editText));
	}

	public EditText getBox(TodoBox todoBox) {
		switch (todoBox) {
		case TOP_LEFT:
			return mTodoBoxes.get(TodoBox.TOP_LEFT.ordinal());
		case TOP_RIGHT:
			return mTodoBoxes.get(TodoBox.TOP_RIGHT.ordinal());
		case BOTTOM_LEFT:
			return mTodoBoxes.get(TodoBox.BOTTOM_LEFT.ordinal());
		case BOTTOM_RIGHT:
			return mTodoBoxes.get(TodoBox.BOTTOM_RIGHT.ordinal());
		default:
			System.out.println("Error in getTextFromTodo");
			return null;
		}
	}

	public String getText(TodoBox todoBox) {
		return getBox(todoBox).getText().toString();
	}

	public void setText(TodoBox todoBox, String text) {
		getBox(todoBox).setText(text);
	}

	// Perhaps these next methods should be in a class like DatabaseClient
	public void writeAllTextToDatabase(Context context) {
		for (TodoBox box : TodoBox.values()) {
			writeTextToDatabase(context, box);
		}
	}

	public void writeTextToDatabase(Context context, TodoBox box) {
		// getText(box)
		ContentResolver contentResolver = context.getContentResolver();

		ContentValues values = new ContentValues();

		// Insert first record
		DataRecord dataRecord = new DataRecord(getText(TodoBox.TOP_LEFT));
		values.put(DataContract._ID, dataRecord.getID());
		values.put(DataContract.TODO_TEXT, dataRecord.getData());
		values.put(DataContract.REF_QUADRANTS_ID, box.ordinal());
		Uri firstRecordUri = contentResolver.insert(DataContract.CONTENT_URI,
				values);

		values.clear();
	}

	public void readAllTextFromDatabase(Context context) {
		for (TodoBox box : TodoBox.values()) {
			readTextFromDatabase(context, box);
		}
	}

	public void readTextFromDatabase(Context context, TodoBox box) {
		Cursor cursor = context.getContentResolver().query(
				DataContract.CONTENT_URI, null, null, null, null);
		if (cursor.getCount() > box.ordinal()) {
			// Bit of a hack, should only return the required row
			cursor.moveToPosition(box.ordinal());
			String record = cursor.getString(cursor
					.getColumnIndex(DataContract.TODO_TEXT));
			setText(box, record);
		}
		cursor.close();
	}
}
