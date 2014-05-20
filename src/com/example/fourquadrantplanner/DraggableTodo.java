package com.example.fourquadrantplanner;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DraggableTodo {
    private static int ID = 0;

    private TodoItem mTodoItem;
    private final Context mContext;
    public final int mId;
    private ViewGroup mTodoView;
    private TextView mTextView;

    /**
     * Constructor for the composition version of DraggableTodo
     */
    public DraggableTodo(Context context, TodoItem item) {
        mContext = context;
        mTodoItem = item;
        mId = ID++;
    }

    /**
     * Initialise the DraggableTodo. Must be called after successful
     * construction
     */
    public void initialiseInContainer(ViewGroup targetGroup) {
        setupCustomView();
        targetGroup.addView(mTodoView);
        // Change the quadrant info for the TodoItem
        changeQuadrant(targetGroup.getId());
    }

    /**
     * Initialise the DraggableTodo. Must be called after successful
     * construction
     */
    public void initialiseInContainer(ViewGroup targetGroup, ViewGroup targetView) {
        setupCustomView();
        targetGroup.addView(mTodoView, targetGroup.indexOfChild(targetView));
        // Change the quadrant info for the TodoItem
        changeQuadrant(targetGroup.getId());
    }

    /**
     * Get the ViewGroup for the DraggableTodo
     */
    public ViewGroup getView() {
        return mTodoView;
    }

    /**
     * Return the encapsulated TodoItem
     */
    public TodoItem getItem() {
        return mTodoItem;
    }

    /**
     * Sets the text in the TodoItem and on the mTextView
     */
    public void setText(String text) {
        mTodoItem.setText(text);
        mTextView.setText(text);
    }

    /**
     * Puts the text on the custom view and sets draggable and reorderable
     * properties
     */
    private void setupCustomView() {
        ViewGroup oldTodoView = mTodoView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTodoView = (RelativeLayout) inflater.inflate(R.layout.draggable_todo_view, null, false);
        mTextView = (TextView) mTodoView.findViewById(R.id.textView);
        mTextView.setText(mTodoItem.getText());
        makeDraggable();
        makeReorderable();
        Quadrants.updateTodosMap(oldTodoView, mTodoView, this);
    }

    // Allow the text view to be dragged across the UI
    public void makeDraggable() {
        // mTextView.setOnTouchListener(new View.OnTouchListener() {
        mTodoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    // Makes the original TextView invisible and creates
                    // a copy that follows your finger as you drag
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    // int[] viewPos = new int[2];
                    // view.getLocationOnScreen(viewPos);
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
        // mTextView.setOnDragListener(new View.OnDragListener() {
        mTodoView.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View stationaryView, DragEvent event) {
                switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED: {
                    stationaryView.setBackgroundResource(R.drawable.back_red);
                    break;
                }

                case DragEvent.ACTION_DRAG_EXITED: {
                    stationaryView.setBackgroundResource(R.drawable.back_blue);
                    break;
                }

                case DragEvent.ACTION_DROP: {
                    // Redraw the moving view in its new location
                    ViewGroup movingView = (ViewGroup) event.getLocalState();
                    DraggableTodo movingTodo = Quadrants.getDraggableTodo(movingView);
                    movingTodo.redrawInNewLocation((ViewGroup) stationaryView.getParent(), (ViewGroup) stationaryView);
                    return true;
                }

                case DragEvent.ACTION_DRAG_ENDED: {
                    // If the drag is a failure, then make the view visible in
                    // the original location
                    if (!event.getResult()) {
                        // mTodoView = (RelativeLayout) event.getLocalState();
                        ViewGroup movingView = (ViewGroup) event.getLocalState();
                        DraggableTodo movingTodo = Quadrants.getDraggableTodo(movingView);
                        movingTodo.setVisible();
                    }
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
    public void redrawInNewLocation(ViewGroup targetContainer, ViewGroup targetView) {
        // Remove the moving View from its owner
        ViewGroup oldOwner = (ViewGroup) mTodoView.getParent();
        oldOwner.removeView(mTodoView);

        if (targetView != null) {
            initialiseInContainer(targetContainer, targetView);
            targetView.setBackgroundResource(R.drawable.back_blue);
        } else {
            initialiseInContainer(targetContainer);
        }
    }

    /**
     * Changes the TodoBox of the TodoItem based on the id of the target in with
     * it was dropped
     */
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

    /**
     * Make the view visible again (in its original location) Called on another
     * thread to avoid ConcurrentModificationException
     */
    public void setVisible() {
        mTodoView.post(new Runnable() {
            public void run() {
                mTodoView.setVisibility(View.VISIBLE);
            }
        });
    }

}