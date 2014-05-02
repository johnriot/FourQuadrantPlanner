package com.example.fourquadrantplanner;

import java.util.*;

import com.example.fourquadrantcontentprovider.DataContract;
import com.example.fourquadrantcontentprovider.DataRecord;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

enum TodoBox {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

public class Quadrants {
    private ArrayList<EditText> mTodoBoxes = new ArrayList<EditText>();

    private ArrayList<TodoTextView> mTodoViews = new ArrayList<TodoTextView>();

    private Context mContext;

    public Quadrants(Activity activity) {
        /* Removing these dues to removing EditTexts from layout file
        mTodoBoxes
                .add((EditText) activity.findViewById(R.id.top_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.top_right_editText));
                */

        mContext = activity;
        // TODO: Remove these tow lines, only here to stop crashing.
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_right_editText));
        //
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_right_editText));
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
    }

    // Code for TodoItems
    public void addTodoView(TodoTextView todoView) {
        mTodoViews.add(todoView);
        addOnTouchListeners();
    }

    private void addOnTouchListeners() {
        for (TodoTextView view : mTodoViews) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_MOVE:
                        Toast.makeText(mContext, "Move Detected",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(mContext, "Press Detected",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                    }
                }

            });
        }
    }
}
