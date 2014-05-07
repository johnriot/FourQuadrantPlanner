package com.example.fourquadrantplanner;

public class TodoItem {
    private static int id;

    public final int mId;
    public final String mText;
    public final TodoBox mBox;

    // Constructor using text and the TodoBox quadrant
    public TodoItem(String text, TodoBox box) {
        mId = id++;
        mText = text + " " + mId;
        mBox = box;
    }

    // Constructor using text and an integer representing the quadrant [1,4]
    public TodoItem(String text, int quadrant) {
        this(text, Quadrants.quadrantToBox(quadrant));
    }

    // Resets the static counting id for TodoItems
    public static void resetCount() {
        id = 0;
    }
}
