package com.neoware.fourquadrantplanner;

import com.neoware.fourquadrantplanner.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TodoDialogFragment extends DialogFragment {
    private static final String KEY_DIALOG_TODO_TEXT = "DIALOG_TODO_TEXT";
    private static final String KEY_DIALOG_TODO_QUADRANT = "DIALOG_TODO_QUADRANT";
    private static final String KEY_DIALOG_TODO_INDEX = "DIALOG_TODO_INDEX";
    private String mDisplayText;
    private int mQuadrant = -1;
    private int mViewKey = -1;

    /**
     * Create a new instance of TodoDialogFragment, providing displayText as an
     * argument.
     */
    static TodoDialogFragment newInstance(String displayText, int quadrant, int index) {
        TodoDialogFragment f = new TodoDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_DIALOG_TODO_TEXT, displayText);
        args.putInt(KEY_DIALOG_TODO_QUADRANT, quadrant);
        args.putInt(KEY_DIALOG_TODO_INDEX, index);
        f.setArguments(args);
        return f;
    }

    /**
     * A class creating this dialog must implement its method
     */
    public interface DialogListener {
        public void onDialogPositiveClick(TodoBox box, String text, int ViewKey);
    }

    // Use this instance of the interface to deliver action events
    DialogListener mListener;

    /**
     * Override the Fragment.onAttach() method to instantiate the
     * NoticeDialogListener
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the view to allow us to play write text in the edit text
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_todo, null);

        // Retrieve the displayText from the bundle and display it on the
        // editText, in the case of an 'edit note' operation
        final Bundle bundle = getArguments();
        final EditText eText = (EditText) v.findViewById(R.id.todo_text);
        if (bundle != null && (mDisplayText = bundle.getString(KEY_DIALOG_TODO_TEXT)) != null) {
            mDisplayText = bundle.getString(KEY_DIALOG_TODO_TEXT);
            mQuadrant = bundle.getInt(KEY_DIALOG_TODO_QUADRANT);
            mViewKey = bundle.getInt(KEY_DIALOG_TODO_INDEX);
            eText.setText(mDisplayText);

            // Make the cursor visible once the user touches the text
            eText.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        eText.setCursorVisible(true);
                        Layout layout = ((EditText) v).getLayout();
                        // Find the cursor offset vertically and horizontally
                        // and put the cursor in the correct position.
                        float x = event.getX() + eText.getScrollX();
                        float y = event.getY() + eText.getScrollY();
                        int line = layout.getLineForVertical((int) y);
                        int offset = layout.getOffsetForHorizontal(line, x);
                        if (offset > 0)
                            if (x > layout.getLineMax(0))
                                eText.setSelection(offset); // touch was at end
                                                            // of text
                            else
                                eText.setSelection(offset - 1);

                        break;
                    }
                    return false;
                }
            });
        }

        // If this is a create new dialog action, put the cursor in the edit
        // text
        else {
            eText.setCursorVisible(true);
        }

        // Select the appropriate radio button if we are editing a TodoItem
        if (mQuadrant != -1) {
            RadioButton button = null;
            switch (mQuadrant) {
            case 1:
                button = (RadioButton) v.findViewById(R.id.quadrant_radio1);
                break;
            case 2:
                button = (RadioButton) v.findViewById(R.id.quadrant_radio2);
                break;
            case 3:
                button = (RadioButton) v.findViewById(R.id.quadrant_radio3);
                break;
            case 4:
                button = (RadioButton) v.findViewById(R.id.quadrant_radio4);
                break;
            default:
                break;
            }
            // Turn on the selected RadioButton
            button.toggle();
        }

        // Build the alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
        // Create button:
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Get the text entered in the dialog
                        EditText eText = (EditText) TodoDialogFragment.this.getDialog().findViewById(R.id.todo_text);
                        String text = eText.getText().toString();

                        // Get selected radio button from radioGroup
                        RadioGroup rGroup = (RadioGroup) TodoDialogFragment.this.getDialog().findViewById(
                                R.id.quadrant_radio_group);
                        int selectedId = rGroup.getCheckedRadioButtonId();
                        TodoBox box = null;
                        switch (selectedId) {
                        case R.id.quadrant_radio1:
                            box = TodoBox.TOP_LEFT;
                            break;
                        case R.id.quadrant_radio2:
                            box = TodoBox.TOP_RIGHT;
                            break;
                        case R.id.quadrant_radio3:
                            box = TodoBox.BOTTOM_LEFT;
                            break;
                        case R.id.quadrant_radio4:
                            box = TodoBox.BOTTOM_RIGHT;
                            break;
                        default:
                            break;
                        }
                        mListener.onDialogPositiveClick(box, text, mViewKey);
                    }
                    // Cancel Button:
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TodoDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
