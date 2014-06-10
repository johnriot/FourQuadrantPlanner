package com.neoware.fourquadrantplanner;

import com.neoware.fourquadrantplanner.R;
import com.neoware.fourquadrantplanner.TodoDialogFragment.DialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmDeletionFragment extends DialogFragment {
    public static String KEY_NUMBER_TODOS_TICKED = "NUMBER_TODOS_TICKED";

    public static ConfirmDeletionFragment newInstance(int numberTicked) {
        ConfirmDeletionFragment frag = new ConfirmDeletionFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_NUMBER_TODOS_TICKED, numberTicked);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String message = getActivity().getString(R.string.delete_ticked);
        if (args != null) {
            int numTicked = args.getInt(KEY_NUMBER_TODOS_TICKED);
            message = String.format(message, numTicked);
        }
        return new AlertDialog.Builder(getActivity()).setMessage(message).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).onConfirmDeletions();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfirmDeletionFragment.this.getDialog().cancel();
                    }
                }).create();
    }
}
