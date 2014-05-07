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
    private ArrayList<TodoItem> mTodoItems = new ArrayList<TodoItem>();

    // private ArrayList<EditText> mTodoBoxes = new ArrayList<EditText>();

    // private ArrayList<DraggableTextView> mTodoViews = new
    // ArrayList<DraggableTextView>();

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
        /*
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_right_editText));
        //
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_left_editText));
        mTodoBoxes.add((EditText) activity
                .findViewById(R.id.bottom_right_editText));
                */
        addOnDragListeners();
    }

    /*
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
    */

    // Perhaps these next methods should be in a class like DatabaseClient
    /*
    public void writeAllTextToDatabase() {
        for (TodoBox box : TodoBox.values()) {
            writeTextToDatabase(box);
        }
    }
    */

    public void writeTextToDatabase() {
        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        for (TodoItem item : mTodoItems) {
            // DataRecord dataRecord = new DataRecord(item.mText);
            values.put(DataContract._ID, item.mId);
            values.put(DataContract.TODO_TEXT, item.mText);
            values.put(DataContract.REF_QUADRANTS_ID, item.mBox.ordinal() + 1);
            Uri recordUri = contentResolver.insert(DataContract.CONTENT_URI,
                    values);
            values.clear();
        }
    }

    public void readAllTextFromDatabaseTodoTextViewImplementation() {
        Cursor c = mContext.getContentResolver().query(
                DataContract.CONTENT_URI, null, null, null, null);
        mTodoItems.clear();
        TodoItem.resetCount();
        if (c != null && c.moveToFirst()) {
            do {
                String record = c.getString(c
                        .getColumnIndex(DataContract.TODO_TEXT));
                int refQuadrants = Integer.parseInt(c.getString(c
                        .getColumnIndex(DataContract.REF_QUADRANTS_ID)));
                // TodoItem item = new TodoItem("Todo Item", refQuadrants);
                addTodo(quadrantToBox(refQuadrants));
                Toast.makeText(mContext, record, Toast.LENGTH_SHORT).show();

            } while (c.moveToNext());
            c.close();
        }
    }

    /*
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
    */

    /*
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
    */

    // Code for TodoItems
    public void addTodo(TodoBox box) {
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
        TodoItem item = new TodoItem("Todo Item", box);
        DraggableTextView view = new DraggableTextView(mContext);
        view.setText(item.mText);
        linearLayout.addView(view);
        mTodoItems.add(item);
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

    public static TodoBox quadrantToBox(int quadrant) {
        return TodoBox.values()[quadrant - 1];
    }
}