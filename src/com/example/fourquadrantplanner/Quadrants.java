package com.example.fourquadrantplanner;

import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

enum TodoBox {
	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

public class Quadrants {
	private ArrayList<EditText> mTodoBoxes = new ArrayList<EditText>();

	public Quadrants(Activity activity) {
		mTodoBoxes
				.add((EditText) activity.findViewById(R.id.top_left_editText));
		mTodoBoxes.add((EditText) activity
				.findViewById(R.id.top_right_editText));
		mTodoBoxes.add((EditText) activity
				.findViewById(R.id.bottom_left_editText));
		mTodoBoxes.add((EditText) activity
				.findViewById(R.id.bottom_right_editText));
	}

	public EditText getBox(TodoBox todoBox) {
		switch (todoBox) {
		case TOP_LEFT:
			return mTodoBoxes.get(0);
		case TOP_RIGHT:
			return mTodoBoxes.get(1);
		case BOTTOM_LEFT:
			return mTodoBoxes.get(2);
		case BOTTOM_RIGHT:
			return mTodoBoxes.get(3);
		default:
			System.out.println("Error in getTextFromTodo");
			return null;
		}
	}

	public String getText(TodoBox todoBox) {
		return getBox(todoBox).getText().toString();
	}

	public void setText(TodoBox todoBox, String text) {
		getBox(todoBox).setText(text);
	}

	public void writeAllTextToDatabase() {

	}

	public void writeTextDatabase(int boxNumber) {

	}

}
