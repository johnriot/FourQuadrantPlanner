package com.example.fourquadrantplanner;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
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
    private CheckBox mCheckBox;
    private Drawable mOldBackground = null;

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
        mCheckBox = (CheckBox) mTodoView.findViewById(R.id.checkBox);
        captureCheckBoxClicks();
        mCheckBox.setChecked(mTodoItem.isTicked());
        makeDraggable();
        makeReorderable();
        Quadrants.updateTodosMap(oldTodoView, mTodoView, this);
    }

    // Allow the text view to be dragged across the UI
    public void makeDraggable() {
        mTodoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE: {
                    // Makes the original TextView invisible and creates
                    // a copy that follows your finger as you drag
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    // Draw the top border on the element below the one being
                    // moved
                    redrawViewBelowMovingView(view);
                    return false;
                }
                case MotionEvent.ACTION_DOWN:
                    view.setBackgroundResource(R.drawable.border_green);
                    return true;
                case MotionEvent.ACTION_UP: {
                    int quadrant = Quadrants.boxToQuadrant(mTodoItem.getBox());
                    DialogFragment newFragment = TodoDialogFragment.newInstance(mTodoItem.getText(), quadrant, mId);
                    Activity activity = (Activity) mContext;
                    newFragment.show(activity.getFragmentManager(), "todoEditDialog");
                    view.setBackgroundResource(R.drawable.bottom_border_black);
                    return false;
                }
                default:
                    view.setBackgroundResource(R.drawable.bottom_border_black);
                    return false;
                }
            }

        });

        /*
        mTodoView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                // Makes the original TextView invisible and creates
                // a copy that follows your finger as you drag
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                Toast.makeText(mContext, "onLongClick", Toast.LENGTH_SHORT).show();
                return false;
            }

        });
        */
    }

    /**
     * Draws the top border onto the child below the moving view when such a
     * child exists
     */
    private static void redrawViewBelowMovingView(View movingView) {
        ViewGroup quadrant = (ViewGroup) movingView.getParent();
        int movingViewIndx = quadrant.indexOfChild(movingView);
        View nextView = quadrant.getChildAt(movingViewIndx + 1);
        if (nextView != null) {
            drawViewWithBackground(nextView, R.drawable.top_bottom_border_black);
        }
    }

    /**
     * Remove the border from the view above the entered View
     */
    private static void redrawViewAboveEnteredView(View enteredView) {
        ViewGroup quadrant = (ViewGroup) enteredView.getParent();
        int enteredViewIndx = quadrant.indexOfChild(enteredView);
        View prevView = quadrant.getChildAt(enteredViewIndx - 1);
        if (prevView != null) {
            drawViewWithBackground(prevView, R.drawable.white_background);
        }
    }

    /**
     * Redraws a view with a specified new background and saves the old
     * background
     */
    private static void drawViewWithBackground(View view, int drawableId) {
        DraggableTodo todo = Quadrants.getDraggableTodo(view);
        todo.drawNewBackground(drawableId);
    }

    private static void restoreBackground(View view) {
        DraggableTodo todo = Quadrants.getDraggableTodo(view);
        todo.restoreBackground();
    }

    private static void restoreBackgroundAbove(View enteredView) {
        ViewGroup quadrant = (ViewGroup) enteredView.getParent();
        int enteredViewIndx = quadrant.indexOfChild(enteredView);
        View prevView = quadrant.getChildAt(enteredViewIndx - 1);
        if (prevView != null) {
            restoreBackground(prevView);
        }
    }

    /**
     * Draw a new background and save the old background for this DraggableTodo
     */
    private void drawNewBackground(int backgroundId) {
        mOldBackground = mTodoView.getBackground();
        mTodoView.setBackgroundResource(backgroundId);
    }

    /**
     * Restores the old background for a View
     */
    @SuppressWarnings("deprecation")
    public void restoreBackground() {
        if (mOldBackground != null) {
            mTodoView.setBackgroundDrawable(mOldBackground);
            mOldBackground = null;
        }
    }

    /**
     * Reset background to be the original (bottom_border_black) if it has
     * changed
     */
    public void restoreOriginalBackground() {
        mTodoView.setBackgroundResource(R.drawable.bottom_border_black);
        mTodoView.invalidate();
        mOldBackground = null;
    }

    /**
     * Allows views to reorder as a view from the same group is moved up and
     * down that group
     */
    public void makeReorderable() {
        mTodoView.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View stationaryView, DragEvent event) {
                switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED: {
                    ViewGroup quadrant = (ViewGroup) stationaryView.getParent();
                    quadrant.setBackgroundResource(R.drawable.quadrant);
                    // stationaryView.setBackgroundResource(R.drawable.top_border_mint);
                    redrawViewAboveEnteredView(stationaryView);
                    drawViewWithBackground(stationaryView, R.drawable.top_border_mint);
                    break;
                }

                case DragEvent.ACTION_DRAG_EXITED: {
                    ViewGroup quadrant = (ViewGroup) stationaryView.getParent();
                    quadrant.setBackgroundResource(R.drawable.quadrant_to_drop);
                    restoreBackgroundAbove(stationaryView);
                    restoreBackground(stationaryView);
                    break;
                }

                case DragEvent.ACTION_DROP: {
                    // Redraw the moving view in its new location
                    ViewGroup movingView = (ViewGroup) event.getLocalState();
                    DraggableTodo movingTodo = Quadrants.getDraggableTodo(movingView);
                    ViewGroup quadrant = (ViewGroup) stationaryView.getParent();
                    quadrant.setBackgroundResource(R.drawable.quadrant);
                    movingTodo.redrawInNewLocation(quadrant, (ViewGroup) stationaryView);
                    Quadrants.restoreAllOriginalBackgrounds();
                    return true;
                }

                case DragEvent.ACTION_DRAG_ENDED: {
                    // If the drag is a failure, then make the view visible in
                    // the original location
                    if (!event.getResult()) {
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
            targetView.setBackgroundResource(R.drawable.bottom_border_black);
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
     * Implement code for the checking and unchecking of the checkboxes of the
     * CheckedTextView
     */
    public void captureCheckBoxClicks() {

        mCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mCheckBox.isChecked()) {
                    mTodoItem.setTicked(true);
                } else {
                    mTodoItem.setTicked(false);
                }

                // Toast.makeText(mContext, "ChckBox clicked",
                // Toast.LENGTH_SHORT).show();

            }
        });
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
