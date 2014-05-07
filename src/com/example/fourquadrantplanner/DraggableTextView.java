package com.example.fourquadrantplanner;

import android.content.ClipData;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;

// A TextView with a border that can be dragged and dropped
// in different locations on the UI
public class DraggableTextView extends TextView {

    public DraggableTextView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.back);
        makeDraggable();
    }

    public void makeDraggable() {
        super.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    // Makes the original TextView invisible and creates
                    // a copy that follows your finger as you drag
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
                case MotionEvent.ACTION_DOWN:
                    return true;
                default:
                    return false;
                }
            }

        });
    }

}
