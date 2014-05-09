package com.example.fourquadrantplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioGroup;

public class TodoDialogFragment extends DialogFragment {

    // A class creating this dialog must implement its method
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(TodoBox box, String text);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the
    // NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout
        builder.setView(inflater.inflate(R.layout.dialog_todo, null))
        // Create button:
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
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
                        mListener.onDialogPositiveClick(box, text);
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
