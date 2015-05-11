package org.random_access.flashcardsmanager;

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

import org.random_access.flashcardsmanager.storage.contracts.LabelContract;
import org.random_access.flashcardsmanager.storage.contracts.ProjectContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 12.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class AddLabelFragment extends DialogFragment {

    private static final String TAG_PROJECT_ID = "project-id";

    private long mProjectId;

    Resources res;
    LayoutInflater inflater;
    View dialogView;
    EditText title;

    public static AddLabelFragment newInstance(long projectId) {
        Bundle bundle = new Bundle();
        bundle.putLong(TAG_PROJECT_ID, projectId);
        AddLabelFragment fragment = new AddLabelFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProjectId = getArguments().getLong(TAG_PROJECT_ID);
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_label, null);
        res = getResources();
        title = (EditText)dialogView.findViewById(R.id.l_add_title);
        title.requestFocus();
        MyAlertDialog d = new MyAlertDialog(getActivity(), getResources().getString(R.string.l_add), dialogView);
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
                    String lTitle = title.getText().toString();
                    handleDialogInput(lTitle);
                }
            });
        }

        private void handleDialogInput(String lTitle) {
            if (TextUtils.isEmpty(lTitle)) {
                title.setError(res.getString(R.string.error_empty_field));
            } else {
                ContentValues values = new ContentValues();
                values.put(LabelContract.LabelEntry.COLUMN_NAME_LABEL_TITLE, lTitle);
                values.put(LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID, mProjectId);
                getActivity().getContentResolver().insert(LabelContract.CONTENT_URI, values);
                Toast.makeText(getActivity(), res.getString(R.string.l_add_success), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }

}
