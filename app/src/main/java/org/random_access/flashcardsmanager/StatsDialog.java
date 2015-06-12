package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 12.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class StatsDialog extends DialogFragment {

    public static final String KEY_RIGHT_CARDS = "key-right-cards";
    public static final String KEY_WRONG_CARDS = "key-wrong-cards";
    public static final String KEY_NEUTRAL_CARDS = "key-neutral-cards";

    private int right, wrong, neutral;

    private Resources res;
    private View dialogView;

    private TextView txtRightAnswers, txtWrongAnswers, txtNeutralAnswers;

    public static StatsDialog newInstance (int right, int wrong, int neutral) {
        StatsDialog d = new StatsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RIGHT_CARDS, right);
        bundle.putInt(KEY_WRONG_CARDS, wrong);
        bundle.putInt(KEY_NEUTRAL_CARDS, neutral);
        d.setArguments(bundle);
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        res = getActivity().getResources();
        right = getArguments().getInt(KEY_RIGHT_CARDS);
        wrong = getArguments().getInt(KEY_WRONG_CARDS);
        neutral = getArguments().getInt(KEY_NEUTRAL_CARDS);
        dialogView =  getActivity().getLayoutInflater().inflate(R.layout.view_learning_stats, null);
        getViewElems();
        setupView();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setTitle(res.getString(R.string.statistics))
                .setNeutralButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return  builder.create();
    }

    private void getViewElems () {
        txtRightAnswers = (TextView)dialogView.findViewById(R.id.txt_right_answers);
        txtWrongAnswers = (TextView)dialogView.findViewById(R.id.txt_wrong_answers);
        txtNeutralAnswers = (TextView)dialogView.findViewById(R.id.txt_not_answered);
    }

    private void setupView () {
        txtRightAnswers.setText(right+"");
        txtWrongAnswers.setText(wrong+"");
        txtNeutralAnswers.setText(neutral+"");
    }

}
