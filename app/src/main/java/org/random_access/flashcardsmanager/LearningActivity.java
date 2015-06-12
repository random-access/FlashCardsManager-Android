package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.random_access.flashcardsmanager.provider.contracts.DbJoins;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.queries.FlashCardQueries;
import org.random_access.flashcardsmanager.queries.ProjectQueries;
import org.random_access.flashcardsmanager.queries.QueryHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LearningActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = LearningActivity.class.getSimpleName();
    private static final String SAVE_PROGRESS_TAG = "save-progress-tag";
    private static final String SHOW_STATS_TAG = "show-stats-tag";

    public static final String KEY_PROJECT = "tag-project";
    public static final String KEY_STACKS = "tag-stacks";
    public static final String KEY_LABELS = "tag-labels";
    public static final String KEY_RANDOM = "tag-random";

    private long projectId;
    private int[] stacks;
    private boolean random;
    private String[] labels;

    private TextView lblAnswer;
    private TextView txtQuestion, txtAnswer;
    private ImageButton btnPrevious, btnNext, btnRight, btnWrong, btnSwitch;

    private Cursor cardCursor;

    private String[] C_LIST_PROJECTION = { FlashCardContract.FlashCardEntry._ID,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION,
            FlashCardContract.FlashCardEntry.COLUMN_NAME_ANSWER};

    private final int COL_ID = 0;
    private final int COL_PROJECT_ID = 1;
    private final int COL_STACK = 2;
    private final int COL_QUESTION = 3;
    private final int COL_ANSWER = 4;

    private Map<Long, Integer> progressChanges = new HashMap<>();
    private Map<Long, LearningActivity.Result> statsTracking = new HashMap<>();
    private int cursorPosition = -1;

    private LearningFragment learningFragment;

    enum Direction {
        PREVIOUS, NEXT;
    }

    enum Result {
        RIGHT, WRONG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        setContentView(R.layout.activity_learning);
        getViewElems();
        setListeners();
        loadSavedConfiguration();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_learning, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Starts a new or restarts an existing Loader
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (learningFragment != null) {
            learningFragment.setProgressChanges(progressChanges);
            learningFragment.setStatsTracking(statsTracking);
            learningFragment.setCursorPosition(cursorPosition);
        }
    }

    @Override
    public void onBackPressed() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.save_results_title))
                .setMessage(getResources().getString(R.string.save_results))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveProgressInDatabase();
                        LearningActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LearningActivity.super.onBackPressed();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();

    }

    private void loadSavedConfiguration() {
        FragmentManager fragmentManager = getFragmentManager();
        learningFragment = (LearningFragment) fragmentManager.findFragmentByTag(SAVE_PROGRESS_TAG);
        if (learningFragment == null) {
            learningFragment = new LearningFragment();
            fragmentManager.beginTransaction().add(learningFragment, SAVE_PROGRESS_TAG).commit();
        } else {
            progressChanges = learningFragment.getProgressChanges();
            statsTracking = learningFragment.getStatsTracking();
            cursorPosition = learningFragment.getCursorPosition();
        }
    }

    private void getExtras() {
        projectId = getIntent().getExtras().getLong(KEY_PROJECT);
        stacks = getIntent().getExtras().getIntArray(KEY_STACKS);
        random = getIntent().getExtras().getBoolean(KEY_RANDOM);
        labels = getIntent().getExtras().getStringArray(KEY_LABELS);
    }

    private void getViewElems () {
        lblAnswer = (TextView)findViewById(R.id.label_answer);
        txtQuestion = (TextView)findViewById(R.id.text_question);
        txtAnswer = (TextView)findViewById(R.id.text_answer);
        btnPrevious = (ImageButton)findViewById(R.id.btn_previous);
        btnNext = (ImageButton)findViewById(R.id.btn_next);
        btnRight = (ImageButton)findViewById(R.id.btn_right);
        btnRight.setColorFilter(getResources().getColor(R.color.green));
        btnWrong = (ImageButton)findViewById(R.id.btn_wrong);
        btnWrong.setColorFilter(getResources().getColor(R.color.red));
        btnSwitch = (ImageButton)findViewById(R.id.btn_switch);
        btnSwitch.setColorFilter(getResources().getColor(R.color.dark_grey));
    }


    private void setListeners () {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(Direction.PREVIOUS);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(Direction.NEXT);
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageAnswerVisibility();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResult(Result.RIGHT);
            }
        });
        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResult(Result.WRONG);
            }
        });
    }

    private void initView() {
        if (cursorPosition == -1) {
            cardCursor.moveToFirst();
        } else {
            cardCursor.moveToPosition(cursorPosition);
        }
        fillFields();
        manageNavigationButtonActivation();
    }

    private void saveProgressInDatabase () {
        FlashCardQueries queries = new FlashCardQueries(this);
        for (Map.Entry<Long,Integer> entry : progressChanges.entrySet()) {
            queries.updateStackOfCard(entry.getKey(), entry.getValue());
        }
        Toast.makeText(this, getResources().getString(R.string.save_results_success), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void handleResult(Result result) {
        long currentId = cardCursor.getLong(COL_ID);
        int currentStack = cardCursor.getInt(COL_STACK);
        String msg = "";
        statsTracking.put(currentId, result);
        switch (result) {
            case RIGHT:
                if (currentStack < new ProjectQueries(this).getNumberOfStacks(projectId)) {
                    progressChanges.put(currentId, ++currentStack);
                    msg = getResources().getString(R.string.answer_marked_right);
                }
                break;
            case WRONG:
                if (currentStack > 1) {
                    progressChanges.put(currentId, --currentStack);
                    msg = getResources().getString(R.string.answer_marked_wrong);
                }
                break;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if (!cardCursor.isLast()) {
            navigate(Direction.NEXT);
        } else {
            showStatistics();
        }
    }

    private void  showStatistics () {
        int right = 0, wrong = 0, neutral = 0;
        for (Map.Entry<Long, Result> entry : statsTracking.entrySet()) {
            if (entry.getValue() != null) {
                switch (entry.getValue()) {
                    case RIGHT:
                        right++;
                        break;
                    case WRONG:
                        wrong++;
                        break;
                }
            }
        }
        neutral = cardCursor.getCount() - statsTracking.size();
        StatsDialog d = StatsDialog.newInstance(right,wrong,neutral);
        d.show(getFragmentManager(), SHOW_STATS_TAG);
    }

    private void manageAnswerVisibility() {
        txtAnswer.setVisibility(txtAnswer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        lblAnswer.setVisibility(txtAnswer.getVisibility());
        btnSwitch.setColorFilter(txtAnswer.getVisibility() == View.VISIBLE ? getResources().getColor(R.color.light_blue) : getResources().getColor(R.color.dark_grey));
    }

    private void navigate(Direction d) {
        if (d == Direction.PREVIOUS) {
            cardCursor.moveToPrevious();
        } else {
            cardCursor.moveToNext();
        }
        cursorPosition = cardCursor.getPosition();
        manageNavigationButtonActivation();
        fillFields();
    }

    private void manageNavigationButtonActivation() {
        btnPrevious.setEnabled(!cardCursor.isFirst());
        btnPrevious.setColorFilter(btnPrevious.isEnabled() ? getResources().getColor(R.color.dark_grey) : Color.LTGRAY);
        btnNext.setEnabled(!cardCursor.isLast());
        btnNext.setColorFilter(btnNext.isEnabled() ? getResources().getColor(R.color.dark_grey) : Color.LTGRAY);
    }

    private void fillFields() {
        setTitle((cardCursor.getPosition()+1) + " / " + cardCursor.getCount());
        txtQuestion.setText(cardCursor.getString(COL_QUESTION));
        txtAnswer.setText(cardCursor.getString(COL_ANSWER));
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String where = QueryHelper.buildFlashcardFilterWhereString(labels.length, stacks.length);
        Log.d(TAG,where);
        String[] arguments = QueryHelper.buildFlashcardFilterArgumentString(projectId, stacks, labels);
        Log.d(TAG, Arrays.toString(arguments));
        return new CursorLoader(this, DbJoins.CONTENT_URI_FLASHCARDS_JOIN_LFRELS, C_LIST_PROJECTION,
                where, arguments, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cardCursor = data;
        initView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cardCursor = null;
    }


}
