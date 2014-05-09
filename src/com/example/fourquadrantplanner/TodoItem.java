package com.example.fourquadrantplanner;

public class TodoItem {

    private String mText;
    private TodoBox mBox;

    // Constructor using text and the TodoBox quadrant
    public TodoItem(String text, TodoBox box) {
        mText = text;
        mBox = box;
    }

    // Constructor using text and an integer representing the quadrant [1,4]
    public TodoItem(String text, int quadrant) {
        this(text, Quadrants.quadrantToBox(quadrant));
    }

    public String getText() {
        return mText;
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
    // Resets the static counting id for TodoItems
    public static void resetCount() {
        id = 0;
    }
    */
}
