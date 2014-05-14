package com.example.fourquadrantplanner;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        setBackgroundResource(R.drawable.back_blue);
        makeDraggable();
        makeReorderable();
    }

    public DraggableTodoView(DraggableTodoView copyFrom) {
        super(copyFrom.mContext);
        mContext = copyFrom.mContext;
        mTodoItem = copyFrom.mTodoItem;
        mId = copyFrom.mId;
        setText(mTodoItem.getText());
        setBackgroundResource(R.drawable.back_blue);
        makeDraggable();
        makeReorderable();
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

    /**
     * Allows views to reorder as a view from the same group is moved up and
     * down that group
     */
    public void makeReorderable() {
        super.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View stationaryView, DragEvent event) {
                final int action = event.getAction();

                switch (action) {
                case DragEvent.ACTION_DRAG_ENTERED: {
                    stationaryView.setBackgroundResource(R.drawable.back_red);
                    break;
                }

                case DragEvent.ACTION_DRAG_EXITED: {
                    stationaryView.setBackgroundResource(R.drawable.back_blue);
                    break;
                }

                case DragEvent.ACTION_DROP: {
                    // Move the DraggableView to the new location
                    DraggableTodoView movingView = (DraggableTodoView) event.getLocalState();
                    movingView.redrawInNewLocation((ViewGroup) stationaryView.getParent(),
                            (DraggableTodoView) stationaryView);
                    return true;
                }

                case DragEvent.ACTION_DRAG_ENDED:
                    // If the drag is a failure, then make the view visible in
                    // the original location
                    if (!event.getResult()) {
                        ((DraggableTodoView) event.getLocalState()).setVisible();
                    }

                default:
                    break;
                }
                return true;
            }
        });

    }

    /**
     * Remove a View from one owner and redraw in the new location. A targetView
     * of null can be passed when dragging into blank space in a quadrant
     */
    public void redrawInNewLocation(ViewGroup targetContainer, DraggableTodoView targetView) {
        // Remove the moving View from its owner
        ViewGroup oldOwner = (ViewGroup) getParent();
        oldOwner.removeView(this);

        // Add a clone of the moving view to the parent of the stationary view
        DraggableTodoView clonedView = new DraggableTodoView(this);
        if (targetView != null) {
            targetContainer.addView(clonedView, targetContainer.indexOfChild(targetView));
            targetView.setBackgroundResource(R.drawable.back_blue);
        } else {
            targetContainer.addView(clonedView);
        }
        clonedView.changeQuadrant(targetContainer.getId());
    }

    /**
     * Make the view visible again (in its original location) Called on another
     * thread to avoid ConcurrentModificationException
     */
    public void setVisible() {
        post(new Runnable() {
            public void run() {
                setVisibility(View.VISIBLE);
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
