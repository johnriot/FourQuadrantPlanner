package com.example.fourquadrantplanner;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;

// A TextView with a border that can be dragged and dropped
// in different locations on the UI
public class DraggableTodoView extends TextView {
    private static int ID = 0;

    private final TodoItem mTodoItem;
    private final Context mContext;

    public final int mId;

    // Create the TodoView, set the text of the item,
    // give it a border and make it draggable
    public DraggableTodoView(Context context, TodoItem item) {
        super(context);
        mContext = context;
        mTodoItem = item;
        mId = ID++;
        setText(item.getText());
        setBackgroundResource(R.drawable.back);
        makeDraggable();
    }

    // Return the encapsulated TodoItem
    public TodoItem getItem() {
        return mTodoItem;
    }

    // Updates text on the view and in the TodoItem
    public void updateText(String text) {
        mTodoItem.setText(text);
        setText(text);
    }

    // Allow the text view to be dragged across the UI
    public void makeDraggable() {
        super.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    // Makes the original TextView invisible and creates
                    // a copy that follows your finger as you drag
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return false;
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_UP:
                    int quadrant = Quadrants.boxToQuadrant(mTodoItem.getBox());
                    DialogFragment newFragment = TodoDialogFragment.newInstance(mTodoItem.getText(), quadrant, mId);
                    Activity activity = (Activity) mContext;
                    newFragment.show(activity.getFragmentManager(), "todoEditDialog");
                    return false;
                default:
                    return false;
                }
            }

        });
    }

    // Changes the TodoBox of the TodoItem based on the id of the
    // target in with it was dropped
    public void changeQuadrant(int targetId) {
        switch (targetId) {
        case R.id.top_left_layout:
            mTodoItem.setBox(TodoBox.TOP_LEFT);
            break;
        case R.id.top_right_layout:
            mTodoItem.setBox(TodoBox.TOP_RIGHT);
            break;
        case R.id.bottom_left_layout:
            mTodoItem.setBox(TodoBox.BOTTOM_LEFT);
            break;
        case R.id.bottom_right_layout:
            mTodoItem.setBox(TodoBox.BOTTOM_RIGHT);
            break;
        default:
            break;
        }
    }
}
