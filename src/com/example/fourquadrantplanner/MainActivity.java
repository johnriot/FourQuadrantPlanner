package com.example.fourquadrantplanner;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fourquadrantcontentprovider.*;

public class MainActivity extends Activity implements TodoDialogFragment.DialogListener {
    private Quadrants mQuadrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        mQuadrants = new Quadrants(this);
        // Moved readTextFromDatabase() here. When it was in onResume()
        // then multiple copies of the same DraggableTodoView were being drawn
        // in cases of pause-and-resume (e.g. press the home button and then
        // resume the application). Correct behaviour achieved having the read
        // method called here.
        mQuadrants.readFromDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQuadrants.writeToDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mQuadrants.readTextFromDatabase();
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
        // Plus button is pressed
        case R.id.action_todo:
            mQuadrants.createTodoDialog();
            return true;
        case R.id.action_delete:
            mQuadrants.promptConfirmDeletions();
            return true;
        default:
            return false;
        }
    }

    // Callback from TodoDialogFragment->Create which creates
    // a new TodoItem in the selected quadrant
    @Override
    public void onDialogPositiveClick(TodoBox box, String text, int viewKey) {
        if (viewKey == -1) {
            mQuadrants.addDraggableTodo(box, text);
        } else {
            mQuadrants.editDraggableTodo(box, text, viewKey);
        }
    }

    /**
     * Callback from ConfirmDeletionFragment to delete ticked Todos
     */
    public void onConfirmDeletions() {
        mQuadrants.deleteTickedTodos();
    }

}
