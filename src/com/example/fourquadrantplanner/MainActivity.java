package com.example.fourquadrantplanner;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText tlTodoBox = (EditText) findViewById(R.id.top_left_editText);
		final EditText trTodoBox = (EditText) findViewById(R.id.top_right_editText);
		final EditText blTodoBox = (EditText) findViewById(R.id.bottom_left_editText);
		final EditText brTodoBox = (EditText) findViewById(R.id.bottom_right_editText);

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

}
