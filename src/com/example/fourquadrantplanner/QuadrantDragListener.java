package com.example.fourquadrantplanner;

import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnDragListener;
import android.widget.LinearLayout;

public class QuadrantDragListener implements OnDragListener {
    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
            break;
        case DragEvent.ACTION_DRAG_ENTERED:
            break;
        case DragEvent.ACTION_DRAG_EXITED:
            break;
        case DragEvent.ACTION_DROP:
            // Dropped, reassign View to ViewGroup
            View view = (View) event.getLocalState();
            ViewGroup owner = (ViewGroup) view.getParent();
            owner.removeView(view);
            LinearLayout container = (LinearLayout) v;
            container.addView(view);
            view.setVisibility(View.VISIBLE);
            break;
        case DragEvent.ACTION_DRAG_ENDED:
            if (!event.getResult()) {
                View view2 = (View) event.getLocalState();
                view2.setVisibility(View.VISIBLE);
            }
        default:
            break;
        }
        return true;
    }
}
