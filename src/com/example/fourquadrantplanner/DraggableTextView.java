package com.example.fourquadrantplanner;

import android.content.ClipData;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;

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
