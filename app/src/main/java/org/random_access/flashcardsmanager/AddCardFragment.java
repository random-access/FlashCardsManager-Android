package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 13.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class AddCardFragment extends DialogFragment {

    private static final String TAG_PROJECT_ID = "project-id";
    private static final String TAG_LABEL_ID = "label-id";

    private long mProjectId;
    private long mLabelId;

    private Resources res;
    private LayoutInflater inflater;
    private View dialogView;
    private EditText mQuestion;
    private EditText mAnswer;

    public static AddCardFragment newInstance(long projectId, long labelId) {
        Bundle bundle = new Bundle();
        bundle.putLong(TAG_PROJECT_ID, projectId);
        bundle.putLong(TAG_LABEL_ID, labelId);
        AddCardFragment fragment = new AddCardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mProjectId = getArguments().getLong(TAG_PROJECT_ID);
        mLabelId = getArguments().getLong(TAG_LABEL_ID);
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_card, null);
        res = getResources();
        mQuestion = (EditText)dialogView.findViewById(R.id.c_add_questiontext);
        mQuestion.requestFocus();
        mAnswer = (EditText)dialogView.findViewById(R.id.c_add_answertext);
        MyAlertDialog d = new MyAlertDialog(getActivity(), getResources().getString(R.string.c_add), dialogView);
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
                    String qText = mQuestion.getText().toString();
                    String aText = mAnswer.getText().toString();
                    handleDialogInput(qText, aText);
                }
            });
        }

        private void handleDialogInput(String question, String answer) {
            if (TextUtils.isEmpty(question)) {
                mQuestion.setError(res.getString(R.string.error_empty_field));
            } else if (TextUtils.isEmpty(answer)){
                mAnswer.setError(res.getString(R.string.error_empty_field));
            } else {
                // add flashcard
                ContentValues values = new ContentValues();
                values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION, question);
                values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_ANSWER, answer);
                values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK, 1);
                values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID, mProjectId);
                Uri uri = getActivity().getContentResolver().insert(FlashCardContract.CONTENT_URI, values);

                int cardId = Integer.parseInt(uri.getLastPathSegment());

                // add label to flashcard
                values = new ContentValues();
                values.put(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID, cardId);
                values.put(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID, mLabelId);
                getActivity().getContentResolver().insert(LFRelationContract.CONTENT_URI, values);
                Toast.makeText(getActivity(), res.getString(R.string.c_add_success), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }


}
