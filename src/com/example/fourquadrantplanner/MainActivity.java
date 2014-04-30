package com.example.fourquadrantplanner;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.example.fourquadrantcontentprovider.*;

public class MainActivity extends Activity {

	private Quadrants mQuadrants;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mQuadrants = new Quadrants(this);

		mQuadrants.getBox(TodoBox.TOP_LEFT).setOnFocusChangeListener(
				new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mQuadrants.setText(TodoBox.TOP_LEFT, "");
						}
					}
				});

		mQuadrants.getBox(TodoBox.TOP_RIGHT).setOnFocusChangeListener(
				new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mQuadrants.setText(TodoBox.TOP_RIGHT, "");
						}
					}
				});

		mQuadrants.getBox(TodoBox.BOTTOM_LEFT).setOnFocusChangeListener(
				new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mQuadrants.setText(TodoBox.BOTTOM_LEFT, "");
						}
					}
				});

		mQuadrants.getBox(TodoBox.BOTTOM_RIGHT).setOnFocusChangeListener(
				new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							mQuadrants.setText(TodoBox.BOTTOM_RIGHT, "");

							testContentProvider();
						}
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();
		// mQuadrants.writeAllTextToDatabase();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void testContentProvider() {
		ContentResolver contentResolver = getContentResolver();

		ContentValues values = new ContentValues();

		// Insert first record
		DataRecord dataRecord = new DataRecord(
				mQuadrants.getText(TodoBox.TOP_LEFT));
		values.put(DataContract._ID, dataRecord.getID());
		values.put(DataContract.TODO_TEXT, dataRecord.getData());
		values.put(DataContract.REF_QUADRANTS_ID, 1); // Hardcoded quadrant 1
		Uri firstRecordUri = contentResolver.insert(DataContract.CONTENT_URI,
				values);

		values.clear();

		Cursor c = contentResolver.query(DataContract.CONTENT_URI, null, null,
				null, null);
		c.moveToFirst();
		String record = c.getString(c.getColumnIndex(DataContract.TODO_TEXT));
		mQuadrants.setText(TodoBox.BOTTOM_RIGHT, record);
		c.close();
	}
}
