package com.neoware.fourquadrantplanner;

import com.neoware.fourquadrantplanner.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements TodoDialogFragment.DialogListener {
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
        mQuadrants.writeToDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQuadrants.readFromDatabase();
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
        case R.id.about_app:
            createAboutDialog();
        default:
            return false;
        }
    }

    /**
     * Callback from TodoDialogFragment->Create which creates a new TodoItem in
     * the selected quadrant
     */
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

    /**
     * Creates a dialog to explain the app
     */
    private void createAboutDialog() {
        DialogFragment aboutDialog = new AboutDialogFragment();
        aboutDialog.show(getFragmentManager(), "About");
    }

}
