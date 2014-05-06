package com.example.fourquadrantplanner;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fourquadrantcontentprovider.*;

public class MainActivity extends Activity {

    private Quadrants mQuadrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuadrants = new Quadrants(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQuadrants.writeAllTextToDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQuadrants.readAllTextFromDatabase();
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
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.top_left_layout);
            TodoTextView view = new TodoTextView(this);
            view.setText("Todo Item");
            view.setBackgroundResource(R.drawable.back);
            linearLayout.addView(view);
            mQuadrants.addTodoView(view);
            return true;
        default:
            return false;
        }
    }
}
