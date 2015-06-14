package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.random_access.flashcardsmanager.helpers.MyListUtils;
import org.random_access.flashcardsmanager.provider.contracts.DbJoins;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;
import org.random_access.flashcardsmanager.provider.contracts.LabelContract;
import org.random_access.flashcardsmanager.queries.LabelQueries;
import org.random_access.flashcardsmanager.queries.ProjectQueries;
import org.random_access.flashcardsmanager.queries.QueryHelper;

import java.util.ArrayList;
import java.util.Arrays;


public class PrepareLearningDialog extends DialogFragment {

    private static final String TAG = PrepareLearningDialog.class.getSimpleName();

    public static final String KEY_PROJECT_ID = "project-id";

   private String[] C_LIST_PROJECTION = { FlashCardContract.FlashCardEntry._ID,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_ANSWER,
            LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID
    };

    private Resources res;
    private long projectId;

    private TextView tvNumberOfMatches;
    private ListView lvStacks, lvLabels;
    private ImageButton btnShowStacklist, btnShowLabellist;
    private RadioGroup rgSearchCriteria;
    private CheckBox chkRandomize;

    private View dialogView;
    private Dialog dialog;

    private long[] checkedLabelList;
    private int[] checkedStacksList;

    public static PrepareLearningDialog newInstance (long projectId) {
        PrepareLearningDialog d = new PrepareLearningDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_PROJECT_ID, projectId);
        d.setArguments(bundle);
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        res = getActivity().getResources();
        projectId = getArguments().getLong(KEY_PROJECT_ID);
        dialogView =  getActivity().getLayoutInflater().inflate(R.layout.dialog_prepare_learning, null);
        getViewElems();
        setupView();
        setListeners();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton(res.getText(R.string.learn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(),LearningActivity.class);
                        intent.putExtra(LearningActivity.KEY_PROJECT, projectId);
                        intent.putExtra(LearningActivity.KEY_STACKS, checkedStacksList);
                        intent.putExtra(LearningActivity.KEY_LABELS, checkedLabelList);
                        intent.putExtra(LearningActivity.KEY_RANDOM, chkRandomize.isChecked()); // TODO implement random mode
                        intent.putExtra(LearningActivity.KEY_CONJUNCTION, getConjunctionString());
                        startActivity(intent);
                    }
                })
                .setNeutralButton(res.getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user cancelled
                    }
                });
        dialog = builder.create();
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayNumberOfMatches();
        if (rgSearchCriteria.getCheckedRadioButtonId() == -1)
            rgSearchCriteria.check(R.id.choice_or);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        }
    }

    private String getConjunctionString() {
        int id = rgSearchCriteria.getCheckedRadioButtonId();
        switch (id) {
            case R.id.choice_and:
                return "AND";
            case R.id.choice_or:
                return "OR";
            default:
                return ""; // TODO exc. handling
        }
    }

    private void getViewElems () {
        lvLabels = (ListView) dialogView.findViewById(R.id.list_label_selection);
        lvStacks = (ListView) dialogView.findViewById(R.id.list_stack_selection);
        rgSearchCriteria = (RadioGroup)dialogView.findViewById(R.id.choice_search_criteria);
        chkRandomize = (CheckBox) dialogView.findViewById(R.id.choice_random);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnShowLabellist = (ImageButton) dialogView.findViewById(R.id.btn_show_labellist);
            btnShowStacklist = (ImageButton) dialogView.findViewById(R.id.btn_show_stacklist);
        }
        tvNumberOfMatches = (TextView) dialogView.findViewById(R.id.number_of_matches);
    }

    private void setListeners() {
        View.OnClickListener btnListener = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_show_labellist:
                            lvLabels.setVisibility(lvLabels.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                            btnShowLabellist.setImageResource(lvLabels.getVisibility() == View.VISIBLE ? R.drawable.ic_down_grey : R.drawable.ic_up_grey);
                            if (lvLabels.getVisibility() == View.VISIBLE && lvStacks.getVisibility() == View.VISIBLE) {
                                lvStacks.setVisibility(View.GONE);
                                btnShowStacklist.setImageResource(R.drawable.ic_up_grey);
                            }
                            break;
                        case R.id.btn_show_stacklist:
                            lvStacks.setVisibility(lvStacks.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                            btnShowStacklist.setImageResource(lvStacks.getVisibility() == View.VISIBLE ? R.drawable.ic_down_grey : R.drawable.ic_up_grey);
                            if (lvStacks.getVisibility() == View.VISIBLE && lvLabels.getVisibility() == View.VISIBLE) {
                                lvLabels.setVisibility(View.GONE);
                                btnShowLabellist.setImageResource(R.drawable.ic_up_grey);
                            }
                            break;
                    }
                }
            };
        }

        AdapterView.OnItemClickListener selectionChangeListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayNumberOfMatches();
            }
        };
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnShowLabellist.setOnClickListener(btnListener);
            btnShowStacklist.setOnClickListener(btnListener);
        }
        lvStacks.setOnItemClickListener(selectionChangeListener);
        lvLabels.setOnItemClickListener(selectionChangeListener);
        rgSearchCriteria.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                displayNumberOfMatches();
            }
        });
    }

    private void setupView() {
        ArrayList<String> labels = new LabelQueries(getActivity()).getLabelsFromProject(projectId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, labels);
        lvLabels.setAdapter(adapter);
        lvLabels.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        /*for (int i = 0; i < labels.size(); i++){
            lvLabels.setItemChecked(i,true);
        }*/
        ArrayList<String> stacks = new ArrayList<>();
        for (int i = 1; i <= new ProjectQueries(getActivity()).getNumberOfStacks(projectId); i++) {
            stacks.add(res.getString(R.string.stacks) + " " + i);
        }
        ArrayAdapter<String> stackAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, stacks);
        lvStacks.setAdapter(stackAdapter);
        lvStacks.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        /*for (int i= 0; i < stacks.size(); i++) {
            lvStacks.setItemChecked(i,true);
        }*/
    }

    private void displayNumberOfMatches() {
        LabelQueries labelQueries = new LabelQueries (getActivity());
        // get current selection of labels
        ArrayList<Long> checkedLabelIds = new ArrayList<>();
        for (int i = 0; i < lvLabels.getAdapter().getCount(); i++) {
            if (lvLabels.isItemChecked(i)) {
                checkedLabelIds.add(labelQueries.getLabelId((String) lvLabels.getAdapter().getItem(i), projectId));
            }
        }
        checkedLabelList = MyListUtils.buildLongArray(checkedLabelIds);

        // get current selection of items
        ArrayList<Integer> checkedItems = new ArrayList<>();
        for (int i = 1; i <= lvStacks.getAdapter().getCount(); i++) {
            if (lvStacks.isItemChecked(i-1) ) {
                checkedItems.add(i);
            }
        }
        checkedStacksList = MyListUtils.buildIntArray(checkedItems);

        // query database how many cards match this criteria
        String where = QueryHelper.buildFlashcardFilterWhereString(checkedLabelList.length, checkedStacksList.length, getConjunctionString());
        Log.d(TAG, where);
        String[] arguments = QueryHelper.buildFlashcardFilterArgumentString(projectId, checkedStacksList, checkedLabelList);
        Log.d(TAG, Arrays.toString(arguments));
        int numberOfMatches = 0;
        if (arguments.length > 1) {
            Cursor c = getActivity().getContentResolver().query(DbJoins.CONTENT_URI_FLASHCARDS_JOIN_LFRELS, C_LIST_PROJECTION, where, arguments, null);
            numberOfMatches = c.getCount();
            c.close();
        }
        tvNumberOfMatches.setText(res.getQuantityString(R.plurals.cards, numberOfMatches, numberOfMatches));
        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(numberOfMatches > 0);
    }

}
