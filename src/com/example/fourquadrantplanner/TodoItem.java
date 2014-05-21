package com.example.fourquadrantplanner;

public class TodoItem {

    private String mText;
    private TodoBox mBox;
    private int mPriority;
    private boolean mIsTicked;

    // Constructor using text and the TodoBox quadrant
    public TodoItem(String text, TodoBox box, int priority, int ticked) {
        mText = text;
        mBox = box;
        mPriority = priority;
        mIsTicked = isTickedBool(ticked);
    }

    // Constructor using text and an integer representing the quadrant [1,4]
    public TodoItem(String text, int quadrant, int priority, int ticked) {
        this(text, Quadrants.quadrantToBox(quadrant), priority, ticked);
    }

    public String getText() {
        return mText; // + " " + mPriority;
    }

    public void setText(String text) {
        mText = text;
    }

    public TodoBox getBox() {
        return mBox;
    }

    public void setBox(TodoBox box) {
        mBox = box;
    }

    /*
     * Sets the priority of a TodoItem based on its position in
     * its quadrant.
     */
    public void setPriority(int priority) {
        mPriority = priority;
    }

    /*
     * Gets the priority of a TodoItem
     */
    public int getPriority() {
        return mPriority;
    }

    /*
     * Sets a boolean for the todoItem to indicate if the Checkbox is
     * checked or not.
     */

    public void setTicked(boolean isTicked) {
        mIsTicked = isTicked;
    }

    public boolean isTicked() {
        return mIsTicked;
    }

    public int getIsTickedInt() {
        if (mIsTicked == true) {
            return 1;
        } else {
            return 0;
        }
    }

    private static boolean isTickedBool(int isTicked) {
        if (isTicked == 0) {
            return false;
        } else {
            return true;
        }

    }
}
