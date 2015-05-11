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
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(res.getString(R.string.p_add))
                .setView(dialogView)
                .setPositiveButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pTitle = title.getText().toString();
                        int pStacks = Integer.parseInt(stacks.getText().toString());
                        // TODO add data
                        ContentValues values = new ContentValues();
                        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_PROJECT_TITLE, pTitle);
                        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_PROJECT_STACKS, pStacks);
                        getActivity().getContentResolver().insert(Uri.parse(Keys.BASE_URI + "/" + ProjectContract.ProjectEntry.TABLE_NAME), values);
                        ((MainActivity)getActivity()).updateUI();
                        Toast.makeText(getActivity(), res.getString(R.string.p_add_success), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddProjectFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
