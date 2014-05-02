package com.example.fourquadrantplanner;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fourquadrantcontentprovider.*;

public class MainActivity extends Activity {

    private Quadrants mQuadrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuadrants = new Quadrants(this);
        /*
        mQuadrants.getBox(TodoBox.TOP_LEFT).setOnFocusChangeListener(
        		new OnFocusChangeListener() {
        			@Override
        			public void onFocusChange(View v, boolean hasFocus) {
        				if (hasFocus) {
        					mQuadrants.setText(TodoBox.TOP_LEFT, "");
        				}
        			}
        		});
        		*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQuadrants.writeAllTextToDatabase(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQuadrants.readAllTextFromDatabase(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_todo:
            // Replace this with functionality to pop up a dialog for note
            // insertion
            Toast.makeText(getApplicationContext(),
                    "Add Note: Not Implemented!", Toast.LENGTH_SHORT).show();
            return true;
        default:
            return false;
        }
    }
}
