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
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.random_access.flashcardsmanager.queries.ProjectQueries;

public class ProjectDialogFragment extends DialogFragment {

    private static final String TAG = ProjectDialogFragment.class.getSimpleName();

    private static final String TAG_IS_NEW_PROJECT = "is-new-project";
    private static final String TAG_PROJECT_ID = "project-id";

    private boolean mIsNewProject;
    private long mProjectId;

    private Resources res;
    private EditText title, stacks, description;

    public static ProjectDialogFragment newInstance(boolean isNewProject, long projectId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TAG_IS_NEW_PROJECT, isNewProject);
        bundle.putLong(TAG_PROJECT_ID, projectId);
        ProjectDialogFragment fragment = new ProjectDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIsNewProject = getArguments().getBoolean(TAG_IS_NEW_PROJECT);
        mProjectId = getArguments().getLong(TAG_PROJECT_ID);
        Log.d(TAG, "bundle project id = " + mProjectId);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_project, null);
        res = getResources();
        title = (EditText)dialogView.findViewById(R.id.p_add_title);
        stacks = (EditText)dialogView.findViewById(R.id.p_add_stack);
        description = (EditText)dialogView.findViewById(R.id.p_add_description);
        if (!mIsNewProject) {
            Cursor cursor = new ProjectQueries(getActivity()).getProjectWithId(mProjectId);
            cursor.moveToFirst();
            title.setText(cursor.getString(1));
            Log.d(TAG, "current title = " + cursor.getString(1));
            description.setText(cursor.getString(2));
            stacks.setText(cursor.getInt(3) + "");
            cursor.close();
        }
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
                    String pDescription = description.getText().toString();
                    String pStacks = stacks.getText().toString();
                    handleDialogInput(pTitle, pDescription, pStacks);
                }
            });
        }

        private void handleDialogInput(String pTitle, String pDescription, String pStacks) {
            if (TextUtils.isEmpty(pTitle)) {
                title.setError(res.getString(R.string.error_empty_field));
            } else if (TextUtils.isEmpty(pStacks)) {
                stacks.setError(res.getString(R.string.error_enter_number));
            } else {
                int noOfStacks = Integer.parseInt(pStacks);
                if (noOfStacks > 15 || noOfStacks < 1) {
                    stacks.setError(res.getString(R.string.error_invalid_stacks) + " (1 - 15)");
                } else {
                    if (mIsNewProject) {
                        new ProjectQueries(getActivity()).insertProject(pTitle, pDescription, noOfStacks);
                        Toast.makeText(getActivity(), res.getString(R.string.p_add_success), Toast.LENGTH_SHORT).show();
                    } else {
                        new ProjectQueries(getActivity()).updateProjectWithId(mProjectId, pTitle, pDescription, noOfStacks);
                        Toast.makeText(getActivity(), res.getString(R.string.p_edit_success), Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            }
        }
    }

}
