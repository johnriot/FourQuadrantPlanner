package com.example.fourquadrantplanner;

import java.util.*;

import com.example.fourquadrantcontentprovider.DataContract;
import com.example.fourquadrantcontentprovider.DataRecord;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Layout;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

enum TodoBox {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

public class Quadrants {
    private Map<Integer, DraggableTodoView> mTodoViews = new HashMap<Integer, DraggableTodoView>();
    public static Context mContext;

    // Constructor, sets all quadrants as drag sinks
    public Quadrants(Activity activity) {
        mContext = activity;
        addOnDragListeners();
    }

    // Writes all TodoItem records to the database
    public void writeTextToDatabase() {
        ContentResolver contentResolver = mContext.getContentResolver();

        // First Delete everything from the database
        contentResolver.delete(DataContract.CONTENT_URI, null, null);

        // Then rewrite everything back to the database
        ContentValues values = new ContentValues();
        for (DraggableTodoView view : mTodoViews.values()) {
            TodoItem item = view.getItem();
            values.put(DataContract.TODO_TEXT, item.getText());
            values.put(DataContract.REF_QUADRANTS_ID, boxToQuadrant(item.getBox()));
            Uri recordUri = contentResolver.insert(DataContract.CONTENT_URI, values);
            values.clear();
        }
    }

    // Reads all TodoItem records from the database
    public void readTextFromDatabase() {
        Cursor c = mContext.getContentResolver().query(DataContract.CONTENT_URI, null, null, null, null);
        mTodoViews.clear();
        if (c != null && c.moveToFirst()) {
            do {
                String text = c.getString(c.getColumnIndex(DataContract.TODO_TEXT));
                int refQuadrants = Integer.parseInt(c.getString(c.getColumnIndex(DataContract.REF_QUADRANTS_ID)));
                addTodo(quadrantToBox(refQuadrants), text);
                // Toast.makeText(mContext, record, Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
            c.close();
        }
    }

    // Add a todoView to the UI and the todoItem to the List
    public void addTodo(TodoBox box, String text) {
        LinearLayout linearLayout = getLayout(box);
        TodoItem item = new TodoItem(text, box);
        DraggableTodoView view = new DraggableTodoView(mContext, item);
        linearLayout.addView(view);
        mTodoViews.put(view.mId, view);
    }

    // Change a todo based on values entered in the dialog
    public void editTodo(TodoBox box, String text, int viewKey) {
        DraggableTodoView view = mTodoViews.get(viewKey);

        // Change the box
        if (box != view.getItem().getBox()) {
            changeBox(view, box);
        }

        // Change the text
        view.updateText(text);
    }

    // Change the box for a DraggableTodoView both visually and on the TodoItem
    public void changeBox(DraggableTodoView view, TodoBox box) {
        // Remove view from old box
        LinearLayout layout = getLayout(view.getItem().getBox());
        layout.removeView(view);

        // Add view to new box
        layout = getLayout(box);
        layout.addView(view);
        view.getItem().setBox(box);
    }

    // Pops up a dialog for a new Todo Item
    public void createTodoDialog() {
        DialogFragment todoDialog = new TodoDialogFragment();
        todoDialog.show(((Activity) mContext).getFragmentManager(), "todoDialog");
    }

    // Means that quadrants can be targets for drag actions
    private void addOnDragListeners() {
        ((Activity) mContext).findViewById(R.id.top_left_layout).setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.top_right_layout).setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.bottom_left_layout).setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.bottom_right_layout).setOnDragListener(new QuadrantDragListener());
    }

    // Converts an integer representing a quadrant to an enum representing the
    // TodoBox
    public static TodoBox quadrantToBox(int quadrant) {
        return TodoBox.values()[quadrant - 1];
    }

    // Converts an integer representing a quadrant to an enum representing the
    // TodoBox
    public static int boxToQuadrant(TodoBox box) {
        return box.ordinal() + 1;
    }

    // Gets the layout for the given box
    private LinearLayout getLayout(TodoBox box) {
        LinearLayout linearLayout = null;
        switch (box) {
        case TOP_LEFT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.top_left_layout);
            break;
        case TOP_RIGHT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.top_right_layout);
            break;
        case BOTTOM_LEFT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.bottom_left_layout);
            break;
        case BOTTOM_RIGHT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.bottom_right_layout);
            break;
        default:
            break;
        }
        return linearLayout;
    }
}

/**
 * Implements dragging logic across Quadrants
 */
class QuadrantDragListener implements OnDragListener {
    @Override
    public boolean onDrag(View targetQuadrant, DragEvent event) {
        switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
            break;
        case DragEvent.ACTION_DRAG_ENTERED:
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            break;
        case DragEvent.ACTION_DROP:
            DraggableTodoView movingView = (DraggableTodoView) event.getLocalState();

            // Check if View has been dropped in its original location
            if (isPointInsideView(event.getX(), event.getY(), movingView)) {
                // Returning false means view will be made visible again
                // in ACTION_DRAG_ENDED below
                return false;
            }

            // Otherwise redraw in the new loaction
            movingView.redrawInNewLocation((ViewGroup) targetQuadrant, null);
            return true;
        case DragEvent.ACTION_DRAG_ENDED:
            // If the drag is a failure, then make the view visible in the
            // original location.
            if (!event.getResult()) {
                ((DraggableTodoView) event.getLocalState()).setVisible();
            }
            break;

        default:
            break;
        }
        return true;
    }

    /**
     * Checks if the view has contains a given point. Used to check if the view
     * has been dropped in its original location.
     */
    private boolean isPointInsideView(float x, float y, View view) {
        float viewX = view.getX();
        float viewY = view.getY();

        // point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }
}