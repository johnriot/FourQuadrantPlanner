package com.example.fourquadrantplanner;

public class TodoItem {

    private String mText;
    private TodoBox mBox;
    private int mPriority;

    // Constructor using text and the TodoBox quadrant
    public TodoItem(String text, TodoBox box, int priority) {
        mText = text;
        mBox = box;
        mPriority = priority;
    }

    // Constructor using text and an integer representing the quadrant [1,4]
    public TodoItem(String text, int quadrant, int priority) {
        this(text, Quadrants.quadrantToBox(quadrant), priority);
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
}
