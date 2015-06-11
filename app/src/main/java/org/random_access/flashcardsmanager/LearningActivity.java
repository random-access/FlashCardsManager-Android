package org.random_access.flashcardsmanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.random_access.flashcardsmanager.provider.contracts.DbJoins;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.queries.QueryHelper;

import java.util.Arrays;


public class LearningActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = LearningActivity.class.getSimpleName();

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

    enum Direction {
        PREVIOUS, NEXT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        setContentView(R.layout.activity_learning);
        getViewElems();
        setListeners();
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
        btnRight.setColorFilter(Color.GREEN);
        btnWrong = (ImageButton)findViewById(R.id.btn_wrong);
        btnWrong.setColorFilter(Color.RED);
        btnSwitch = (ImageButton)findViewById(R.id.btn_switch);
        btnSwitch.setColorFilter(Color.BLACK);
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
               txtAnswer.setVisibility(txtAnswer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
               lblAnswer.setVisibility(txtAnswer.getVisibility());
                btnSwitch.setColorFilter(txtAnswer.getVisibility() == View.VISIBLE ? Color.BLUE : Color.BLACK);
            }
        });
    }

    private void initView() {
        cardCursor.moveToFirst();
        fillFields();
        manageNavigationButtonActivation();
    }

    private void navigate(Direction d) {
        if (d == Direction.PREVIOUS) {
            cardCursor.moveToPrevious();
        } else {
            cardCursor.moveToNext();
        }
        manageNavigationButtonActivation();
        fillFields();
    }

    private void manageNavigationButtonActivation() {
        btnPrevious.setEnabled(!cardCursor.isFirst());
        btnPrevious.setColorFilter(btnPrevious.isEnabled() ? Color.BLACK : Color.LTGRAY);
        btnNext.setEnabled(!cardCursor.isLast());
        btnNext.setColorFilter(btnNext.isEnabled() ? Color.BLACK : Color.LTGRAY);
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
