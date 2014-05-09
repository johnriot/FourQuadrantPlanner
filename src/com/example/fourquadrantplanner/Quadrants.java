package com.example.fourquadrantplanner;

import java.util.*;

import com.example.fourquadrantcontentprovider.DataContract;
import com.example.fourquadrantcontentprovider.DataRecord;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
    private ArrayList<DraggableTodoView> mTodoViews = new ArrayList<DraggableTodoView>();
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
        for (DraggableTodoView view : mTodoViews) {
            TodoItem item = view.getItem();
            values.put(DataContract.TODO_TEXT, item.mText);
            values.put(DataContract.REF_QUADRANTS_ID,
                    boxToQuadrant(item.getBox()));
            Uri recordUri = contentResolver.insert(DataContract.CONTENT_URI,
                    values);
            values.clear();
        }
    }

    // Reads all TodoItem records from the database
    public void readTextFromDatabase() {
        Cursor c = mContext.getContentResolver().query(
                DataContract.CONTENT_URI, null, null, null, null);
        mTodoViews.clear();
        TodoItem.resetCount();
        if (c != null && c.moveToFirst()) {
            do {
                String text = c.getString(c
                        .getColumnIndex(DataContract.TODO_TEXT));
                int refQuadrants = Integer.parseInt(c.getString(c
                        .getColumnIndex(DataContract.REF_QUADRANTS_ID)));
                addTodo(quadrantToBox(refQuadrants), text);
                // Toast.makeText(mContext, record, Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
            c.close();
        }
    }

    // Add a todoView to the UI and the todoItem to the List
    public void addTodo(TodoBox box, String text) {
        LinearLayout linearLayout = null;
        switch (box) {
        case TOP_LEFT:
            linearLayout = (LinearLayout) ((Activity) mContext)
                    .findViewById(R.id.top_left_layout);
            break;
        case TOP_RIGHT:
            linearLayout = (LinearLayout) ((Activity) mContext)
                    .findViewById(R.id.top_right_layout);
            break;
        case BOTTOM_LEFT:
            linearLayout = (LinearLayout) ((Activity) mContext)
                    .findViewById(R.id.bottom_left_layout);
            break;
        case BOTTOM_RIGHT:
            linearLayout = (LinearLayout) ((Activity) mContext)
                    .findViewById(R.id.bottom_right_layout);
            break;
        default:
            break;
        }
        TodoItem item = new TodoItem(text, box);
        DraggableTodoView view = new DraggableTodoView(mContext, item);
        // view.setText(item.mText);
        linearLayout.addView(view);
        mTodoViews.add(view);
    }

    // Means that quadrants can be targets for drag actions
    private void addOnDragListeners() {
        ((Activity) mContext).findViewById(R.id.top_left_layout)
                .setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.top_right_layout)
                .setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.bottom_left_layout)
                .setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.bottom_right_layout)
                .setOnDragListener(new QuadrantDragListener());
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
}

/**
 * Implements dragging logic across Quadrants
 */
class QuadrantDragListener implements OnDragListener {
    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
            break;
        case DragEvent.ACTION_DRAG_ENTERED:
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            break;
        case DragEvent.ACTION_DROP:
            // Dropped, reassign View to ViewGroup
            DraggableTodoView view = (DraggableTodoView) event.getLocalState();
            ViewGroup owner = (ViewGroup) view.getParent();
            owner.removeView(view);
            LinearLayout container = (LinearLayout) v;
            container.addView(view);
            view.changeQuadrant(container.getId());
            view.setVisibility(View.VISIBLE);
            break;
        case DragEvent.ACTION_DRAG_ENDED:
            // If the drag is a failure, then make the view visible in the
            // original location
            if (!event.getResult()) {
                View view2 = (View) event.getLocalState();
                view2.setVisibility(View.VISIBLE);
            }
        default:
            break;
        }
        return true;
    }
}