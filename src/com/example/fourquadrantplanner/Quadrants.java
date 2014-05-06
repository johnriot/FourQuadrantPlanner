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
    int mOffsetX = 0;
    int mOffsetY = 0;

    private ArrayList<EditText> mTodoBoxes = new ArrayList<EditText>();

    private ArrayList<TodoTextView> mTodoViews = new ArrayList<TodoTextView>();

    public static Context mContext;

    public Quadrants(Activity activity) {
        /* Removing these dues to removing EditTexts from layout file
        mTodoBoxes
                .add((EditText) activity.findViewById(R.id.top_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.top_right_editText));
                */

        mContext = activity;
        // TODO: Remove these two lines, only here to stop crashing.
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_right_editText));
        //
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_right_editText));
        addOnDragListeners();
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
    public void writeAllTextToDatabase() {
        for (TodoBox box : TodoBox.values()) {
            writeTextToDatabase(box);
        }
    }

    public void writeTextToDatabase(TodoBox box) {

        ContentResolver contentResolver = mContext.getContentResolver();

        ContentValues values = new ContentValues();

        // Insert first record
        DataRecord dataRecord = new DataRecord(getText(box));
        values.put(DataContract.TODO_TEXT, dataRecord.getData());
        String updateClause = DataContract._ID + "=" + (box.ordinal() + 1);
        int numChanges = contentResolver.update(DataContract.CONTENT_URI,
                values, updateClause, null);
        values.clear();
    }

    public void readAllTextFromDatabase() {
        Cursor cursor = mContext.getContentResolver().query(
                DataContract.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() == mTodoBoxes.size()) {
            for (TodoBox box : TodoBox.values()) {
                cursor.moveToNext();
                String record = cursor.getString(cursor
                        .getColumnIndex(DataContract.TODO_TEXT));
                setText(box, record);
            }
        }
        cursor.close();
    }

    // Code for TodoItems
    public void addTodoView(TodoTextView todoView) {
        mTodoViews.add(todoView);
        todoView.makeDraggable();
    }

    // Means that quadrants can be targets for drag actions
    private void addOnDragListeners() {
        ((Activity) mContext).findViewById(R.id.top_left_layout)
                .setOnDragListener(new QuadrantDragListener());
        ((Activity) mContext).findViewById(R.id.top_right_layout)
                .setOnDragListener(new QuadrantDragListener());
    }
}