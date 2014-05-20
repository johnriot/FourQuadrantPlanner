package com.example.fourquadrantplanner;

import java.util.*;

import com.example.fourquadrantcontentprovider.DataContract;
import com.example.fourquadrantcontentprovider.DataRecord;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Layout;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

enum TodoBox {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

public class Quadrants {
    private static SparseArray<DraggableTodo> mTodos = new SparseArray<DraggableTodo>();
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
        for (int i = 0, nsize = mTodos.size(); i < nsize; i++) {
            TodoItem item = mTodos.valueAt(i).getItem();
            values.put(DataContract.TODO_TEXT, item.getText());
            values.put(DataContract.PRIORITY, item.getPriority());
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
        if (c != null && c.moveToFirst()) {
            do {
                String text = c.getString(c.getColumnIndex(DataContract.TODO_TEXT));
                int refQuadrants = Integer.parseInt(c.getString(c.getColumnIndex(DataContract.REF_QUADRANTS_ID)));
                addDraggableTodo(quadrantToBox(refQuadrants), text);
                // Toast.makeText(mContext, record, Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
            c.close();
        }
    }

    /*
    // Add a todoView to the UI and the todoItem to the List
    // By default place it at the end
    public void addTodo(TodoBox box, String text) {
        LinearLayout linearLayout = getLayout(box);
        int priority = linearLayout.getChildCount();
        TodoItem item = new TodoItem(text, box, priority);
        DraggableTodoView view = new DraggableTodoView(mContext, item);
        linearLayout.addView(view);
        mTodoViews.put(view.mId, view);
    }

    // Change a todo based on values entered in the dialog
    public void editTodo(TodoBox box, String text, int viewKey) {
        DraggableTodoView view = mTodoViews.get(viewKey);

        // Change the box
        if (box != view.getItem().getBox()) {
            changeBox(view, box);
        }

        // Change the text
        view.updateText(text);
    }
    */

    /*
    // Change the box for a DraggableTodoView both visually and on the TodoItem
    public void changeBox(DraggableTodo view, TodoBox box) {
        // Remove view from old box
        LinearLayout layout = getLayout(view.getItem().getBox());
        layout.removeView(view);

        // Add view to new box
        layout = getLayout(box);
        layout.addView(view);
        view.getItem().setBox(box);
    }
    */

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
    private LinearLayout getLayout(TodoBox box) {
        LinearLayout linearLayout = null;
        switch (box) {
        case TOP_LEFT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.top_left_layout);
            break;
        case TOP_RIGHT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.top_right_layout);
            break;
        case BOTTOM_LEFT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.bottom_left_layout);
            break;
        case BOTTOM_RIGHT:
            linearLayout = (LinearLayout) ((Activity) mContext).findViewById(R.id.bottom_right_layout);
            break;
        default:
            break;
        }
        return linearLayout;
    }

    /**
     * Sets the priorities of todoItems based on their position in their
     * quadrant
     */
    private void setPriorities() {
        for (TodoBox box : TodoBox.values()) {
            LinearLayout linearLayout = getLayout(box);

            for (int index = 0; index < linearLayout.getChildCount(); index++) {
                // *** TODO: Remove the class checking code (if statement) once
                // we have a layout that doesn't have a text view at the top
                Class cl = linearLayout.getChildAt(index).getClass();
                if (cl == RelativeLayout.class) {
                    RelativeLayout view = (RelativeLayout) linearLayout.getChildAt(index);
                    DraggableTodo todo = Quadrants.getDraggableTodo(view);
                    if (todo != null) {
                        todo.getItem().setPriority(index);
                    }

                }
            }
        }
    }

    /**
     * Add custom view to the layout
     */
    public void addDraggableTodo(TodoBox box, String text) {
        LinearLayout linearLayout = getLayout(box);
        int priority = linearLayout.getChildCount();
        TodoItem item = new TodoItem(text, box, priority);
        DraggableTodo todo = new DraggableTodo(mContext, item);
        todo.initialiseInContainer(linearLayout);
        todo.getView().setId(todo.mId);
        mTodos.put(todo.mId, todo);
    }

    // Change a todo based on values entered in the dialog
    public void editDraggableTodo(TodoBox newBox, String text, int viewKey) {
        DraggableTodo todo = mTodos.get(viewKey);

        // Change the box
        TodoBox oldBox = todo.getItem().getBox();
        if (newBox != oldBox) {
            // changeBox(todo, box);
            getLayout(oldBox).removeView(todo.getView());
            todo.initialiseInContainer(this.getLayout(newBox));
            todo.getView().setId(todo.mId);
        }

        // Change the text on the Todo (data and displayed text)
        todo.setText(text);
    }

    /**
     * Find the DraggableTodo based on its associated ViewGroup
     */
    public static DraggableTodo getDraggableTodo(ViewGroup view) {
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
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            break;
        case DragEvent.ACTION_DROP: {

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
            // Point is in View
            String record = "event: (" + x + ", " + y + "). ";
            record += " viewTL: (" + viewX + ", " + viewY + "). ";
            record += "viewBR: (" + (viewX + view.getWidth()) + ", " + (viewY + view.getHeight()) + "). ";
            record += " viewWidth: " + view.getWidth() + ", view.getHeight()" + view.getHeight();
            Toast.makeText(Quadrants.mContext, record, Toast.LENGTH_LONG).show();
            System.out.println(record);
            return true;
        } else {
            return false;
        }
    }

}