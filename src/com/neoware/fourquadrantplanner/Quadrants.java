package com.neoware.fourquadrantplanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.neoware.fourquadrantplanner.R;
import com.neoware.fourquadrantcontentprovider.DataContract;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;
import android.view.*;
import android.view.View.OnDragListener;
import android.widget.*;

enum TodoBox {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

public class Quadrants {
    private static Map<Integer, DraggableTodo> mTodos = new ConcurrentHashMap<Integer, DraggableTodo>();
    public static Context mContext;

    // Constructor, sets all quadrants as drag sinks
    public Quadrants(Activity activity) {
        mContext = activity;
        addOnDragListeners();
    }

    // Writes all TodoItem records to the database
    public void writeToDatabase() {
        // Set Priorities based on order in quadrants
        setPriorities();

        ContentResolver contentResolver = mContext.getContentResolver();

        // First Delete everything from the database
        contentResolver.delete(DataContract.CONTENT_URI, null, null);

        // Then rewrite everything back to the database
        ContentValues values = new ContentValues();
        for (DraggableTodo todo : mTodos.values()) {
            TodoItem item = todo.getItem();
            values.put(DataContract.TODO_TEXT, item.getText());
            values.put(DataContract.PRIORITY, item.getPriority());
            values.put(DataContract.TICKED, item.getIsTickedInt());
            values.put(DataContract.REF_QUADRANTS_ID, boxToQuadrant(item.getBox()));
            Uri recordUri = contentResolver.insert(DataContract.CONTENT_URI, values);
            values.clear();
        }
    }

    // Reads all TodoItem records from the database
    public void readFromDatabase() {
        Cursor c = mContext.getContentResolver().query(DataContract.CONTENT_URI, null, null, null,
                DataContract.PRIORITY);
        mTodos.clear();
        removeAllViews();
        if (c != null && c.moveToFirst()) {
            do {
                String text = c.getString(c.getColumnIndex(DataContract.TODO_TEXT));
                int refQuadrants = Integer.parseInt(c.getString(c.getColumnIndex(DataContract.REF_QUADRANTS_ID)));
                int ticked = Integer.parseInt(c.getString(c.getColumnIndex(DataContract.TICKED)));
                addDraggableTodo(quadrantToBox(refQuadrants), text, ticked);
                // Toast.makeText(mContext, record, Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
            c.close();
        }
    }

    /**
     * Removes all views from the UI
     */
    private void removeAllViews() {
        for (TodoBox box : TodoBox.values()) {
            ViewGroup layout = getLayout(box);
            layout.removeAllViews();
        }
    }

    // Pops up a dialog for a new Todo Item
    public void createTodoDialog() {
        DialogFragment todoDialog = new TodoDialogFragment();
        todoDialog.show(((Activity) mContext).getFragmentManager(), "todoDialog");
    }

    // Means that quadrants can be targets for drag actions
    private void addOnDragListeners() {
        ((Activity) mContext).findViewById(R.id.top_left_layout).setOnDragListener(
                new QuadrantDragListener(TodoBox.TOP_LEFT));
        ((Activity) mContext).findViewById(R.id.top_right_layout).setOnDragListener(
                new QuadrantDragListener(TodoBox.TOP_RIGHT));
        ((Activity) mContext).findViewById(R.id.bottom_left_layout).setOnDragListener(
                new QuadrantDragListener(TodoBox.BOTTOM_LEFT));
        ((Activity) mContext).findViewById(R.id.bottom_right_layout).setOnDragListener(
                new QuadrantDragListener(TodoBox.BOTTOM_RIGHT));
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
    private ViewGroup getLayout(TodoBox box) {
        ViewGroup group = null;
        switch (box) {
        case TOP_LEFT:
            group = (ViewGroup) ((Activity) mContext).findViewById(R.id.top_left_layout);
            break;
        case TOP_RIGHT:
            group = (ViewGroup) ((Activity) mContext).findViewById(R.id.top_right_layout);
            break;
        case BOTTOM_LEFT:
            group = (ViewGroup) ((Activity) mContext).findViewById(R.id.bottom_left_layout);
            break;
        case BOTTOM_RIGHT:
            group = (ViewGroup) ((Activity) mContext).findViewById(R.id.bottom_right_layout);
            break;
        default:
            break;
        }
        return group;
    }

    /**
     * Sets the priorities of todoItems based on their position in their
     * quadrant
     */
    private void setPriorities() {
        for (TodoBox box : TodoBox.values()) {
            ViewGroup layout = getLayout(box);
            for (int index = 0; index < layout.getChildCount(); index++) {
                RelativeLayout view = (RelativeLayout) getLayout(box).getChildAt(index);
                DraggableTodo todo = Quadrants.getDraggableTodo(view);
                if (todo != null) {
                    todo.getItem().setPriority(index);
                }
            }
        }
    }

    /**
     * Add custom view to the layout
     */
    public void addDraggableTodo(TodoBox box, String text, int ticked) {
        ViewGroup layout = getLayout(box);
        int priority = layout.getChildCount();
        TodoItem item = new TodoItem(text, box, priority, ticked);
        DraggableTodo todo = new DraggableTodo(mContext, item);
        todo.initialiseInContainer(layout);
        todo.getView().setId(todo.mId);
        mTodos.put(todo.mId, todo);
    }

    /**
     * Used to add a Todo from the dialog. TodoItems are unticked by default.
     */
    public void addDraggableTodo(TodoBox box, String text) {
        addDraggableTodo(box, text, 0);
    }

    // Change a todo based on values entered in the dialog
    public void editDraggableTodo(TodoBox newBox, String text, int viewKey) {
        DraggableTodo todo = mTodos.get(viewKey);

        // Change the box
        TodoBox oldBox = todo.getItem().getBox();
        if (newBox != oldBox) {
            getLayout(oldBox).removeView(todo.getView());
            todo.initialiseInContainer(this.getLayout(newBox));
            todo.getView().setId(todo.mId);
        }

        // Change the text on the Todo (data and displayed text)
        todo.setText(text);
    }

    /**
     * Pop up a confirmation dialog to confirm the deletion of the ticked Todos
     */
    public void promptConfirmDeletions() {
        DialogFragment confirmDialog = ConfirmDeletionFragment.newInstance(countTickedTodos());
        confirmDialog.show(((Activity) mContext).getFragmentManager(), "confirmDialog");
    }

    /**
     * Count the number of ticked todos from the mTodos Map
     * 
     */
    private int countTickedTodos() {
        int tickedTodos = 0;
        for (DraggableTodo todo : mTodos.values()) {
            if (todo.getItem().isTicked()) {
                tickedTodos++;
            }
        }
        return tickedTodos;
    }

    /**
     * Delete each Todo that is ticked and update the UI
     */
    public void deleteTickedTodos() {
        for (DraggableTodo todo : mTodos.values()) {
            if (todo != null && todo.getItem().isTicked()) {
                // Remove the view from the collection
                mTodos.remove(todo.mId);
                // Remove the View from its UI Layout
                View view = todo.getView();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
            }
        }
    }

    /**
     * Find the DraggableTodo based on its associated ViewGroup
     */
    public static DraggableTodo getDraggableTodo(View view) {
        return mTodos.get(view.getId());
    }

    /**
     * We must update the TodosMap when a new view is created when we move a
     * view's position or location
     */
    public static void updateTodosMap(ViewGroup oldTodoView, ViewGroup newTodoView, DraggableTodo todo) {
        if (oldTodoView != null) {
            mTodos.remove(oldTodoView.getId());
        }
        newTodoView.setId(todo.mId);
        mTodos.put(newTodoView.getId(), todo);
    }

    /**
     * Cycles through every DraggableTodo and restores the background if it has
     * changed during the drag-and-drop action
     */
    public static void restoreAllOriginalBackgrounds() {
        for (DraggableTodo todo : mTodos.values()) {
            todo.restoreOriginalBackground();
        }
    }
}

/**
 * Implements dragging logic across Quadrants
 */
class QuadrantDragListener implements OnDragListener {

    private final TodoBox mBox;

    public QuadrantDragListener(TodoBox box) {
        mBox = box;
    }

    @Override
    public boolean onDrag(View targetQuadrant, DragEvent event) {
        switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
            break;
        case DragEvent.ACTION_DRAG_ENTERED:
            targetQuadrant.setBackgroundResource(R.drawable.quadrant_to_drop);
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            targetQuadrant.setBackgroundResource(R.drawable.quadrant);
            break;
        case DragEvent.ACTION_DROP: {
            // Restore the quadrant colour and all todo borders
            targetQuadrant.setBackgroundResource(R.drawable.quadrant);

            // Find the moving view and its associated DraggableTodo
            ViewGroup movingView = (ViewGroup) event.getLocalState();
            DraggableTodo todo = Quadrants.getDraggableTodo(movingView);

            // Check if View has been dropped in its original location
            boolean sameQuadrant = todo.getItem().getBox() == mBox;
            if (sameQuadrant && isPointInView((int) event.getX(), (int) event.getY(), movingView)) {
                // Returning false means view will be made visible again
                // in ACTION_DRAG_ENDED below
                return false;
            }

            // The View has been moved, so draw in the new location
            todo.redrawInNewLocation((ViewGroup) targetQuadrant, null);
            return true;
        }
        case DragEvent.ACTION_DRAG_ENDED:
            // If the drag is a failure, then make the view visible in the
            // original location.
            if (!event.getResult()) {
                ViewGroup movingView = (ViewGroup) event.getLocalState();
                DraggableTodo movingTodo = Quadrants.getDraggableTodo(movingView);
                movingTodo.setVisible();
            }
            break;

        default:
            break;
        }
        return true;
    }

    /**
     * Find if a Point is within a specified view
     * 
     * @param fx
     *            - x coordinate of the point
     * @param fy
     *            - y coordinate of the point
     * @param view
     *            - the view to be tested against
     * @return true if point is within the view; false otherwise
     */
    public static boolean isPointInView(int x, int y, View view) {
        int viewX = (int) view.getX();
        int viewY = (int) view.getY();

        // Check if point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

}