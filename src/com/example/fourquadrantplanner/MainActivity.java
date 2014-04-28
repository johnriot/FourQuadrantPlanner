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

	private EditText tlTodoBox;
	private EditText trTodoBox;
	private EditText blTodoBox;
	private EditText brTodoBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tlTodoBox = (EditText) findViewById(R.id.top_left_editText);
		trTodoBox = (EditText) findViewById(R.id.top_right_editText);
		blTodoBox = (EditText) findViewById(R.id.bottom_left_editText);
		brTodoBox = (EditText) findViewById(R.id.bottom_right_editText);

		tlTodoBox.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					tlTodoBox.setHint("");
				}
			}
		});

		trTodoBox.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					trTodoBox.setHint("");
				}
			}
		});

		blTodoBox.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					blTodoBox.setHint("");
				}
			}
		});

		brTodoBox.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					brTodoBox.setHint("");

					testContentProvider();
				}
			}
		});
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
		// values.put(DataContract.DATA, tlTodoBox.getText().toString());
		DataRecord dataRecord = new DataRecord(tlTodoBox.getText().toString());
		values.put(DataContract._ID, dataRecord.getID());
		values.put(DataContract.DATA, dataRecord.getData());
		Uri firstRecordUri = contentResolver.insert(DataContract.CONTENT_URI,
				values);

		values.clear();

		Cursor c = contentResolver.query(DataContract.CONTENT_URI, null, null,
				null, null);
		c.moveToFirst();
		String record = c.getString(c.getColumnIndex(DataContract.DATA));
		brTodoBox.setText(record);
		c.close();
	}
}
