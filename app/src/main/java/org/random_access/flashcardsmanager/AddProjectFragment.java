package org.random_access.flashcardsmanager;

/**
 * Project: FlashCards Manager for Android
 * Date: 11.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.random_access.flashcardsmanager.storage.contracts.ProjectContract;

public class AddProjectFragment extends DialogFragment {
    Resources res;
    LayoutInflater inflater;
    View dialogView;
    EditText title;
    EditText stacks;

    public static AddProjectFragment newInstance() {
        return new AddProjectFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_project, null);
        res = getResources();
        title = (EditText)dialogView.findViewById(R.id.p_add_title);
        stacks = (EditText)dialogView.findViewById(R.id.p_add_stack);
        title.requestFocus();
        MyAlertDialog d = new MyAlertDialog(getActivity(), getResources().getString(R.string.p_add), dialogView);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return d;
    }

    /**
     * Hack to keep dialog open when input is wrong. Needs improvement, but at least it works like expected.
     */
    private class MyAlertDialog extends AlertDialog {

        public MyAlertDialog(Context context, String title, View view) {
            super(context);
            setTitle(title);
            setView(view);
            setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), (new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // this will never be called
                }
            }));
            setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.cancel), (new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            }));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pTitle = title.getText().toString();
                    String pStacks = stacks.getText().toString();
                    handleDialogInput(pTitle, pStacks);
                }
            });
        }

        private void handleDialogInput(String pTitle, String pStacks) {
            if (TextUtils.isEmpty(pTitle)) {
                title.setError(res.getString(R.string.error_empty_field));
            } else if (TextUtils.isEmpty(pStacks)) {
                stacks.setError(res.getString(R.string.error_enter_number));
            } else {
                int noOfStacks = Integer.parseInt(pStacks);
                if (noOfStacks > 15 || noOfStacks < 1) {
                    stacks.setError(res.getString(R.string.error_invalid_stacks) + " (1 - 15)");
                } else {
                    ContentValues values = new ContentValues();
                    values.put(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE, pTitle);
                    values.put(ProjectContract.ProjectEntry.COLUMN_NAME_STACKS, noOfStacks);
                    getActivity().getContentResolver().insert(ProjectContract.CONTENT_URI, values);
                    Toast.makeText(getActivity(), res.getString(R.string.p_add_success), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        }
    }

}
