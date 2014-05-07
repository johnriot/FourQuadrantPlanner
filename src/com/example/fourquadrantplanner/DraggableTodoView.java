package com.example.fourquadrantplanner;

import android.content.ClipData;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;

// A TextView with a border that can be dragged and dropped
// in different locations on the UI
public class DraggableTodoView extends TextView {

    private final TodoItem mTodoItem;

    // Create the TodoView, set the text of the item,
    // give it a border and make it draggable
    public DraggableTodoView(Context context, TodoItem item) {
        super(context);
        mTodoItem = item;
        setText(item.mText);
        setBackgroundResource(R.drawable.back);
        makeDraggable();
    }

    // Return the encapsulated TodoItem
    public TodoItem getItem() {
        return mTodoItem;
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
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
                case MotionEvent.ACTION_DOWN:
                    return true;
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
