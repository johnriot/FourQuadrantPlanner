package com.example.fourquadrantplanner;

public class TodoItem {
    private static int id;

    public final int mId;
    public final String mText;
    public final TodoBox mBox;

    public TodoItem(String text, TodoBox box) {
        mId = id++;
        mText = text + " " + mId;
        mBox = box;
    }

    public TodoItem(String text, int quadrant) {
        mId = id++;
        mText = text + " " + mId;
        mBox = Quadrants.quadrantToBox(quadrant);
    }

    public static void resetCount() {
        id = 0;
    }
}
