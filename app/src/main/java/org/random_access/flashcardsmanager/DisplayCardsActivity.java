package org.random_access.flashcardsmanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import org.random_access.flashcardsmanager.adapter.FlashCardCursorAdapter;
import org.random_access.flashcardsmanager.storage.contracts.FlashCardContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 11.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class DisplayCardsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DisplayCardsActivity.class.getSimpleName();
    private static final String TAG_ADD_CARD = "add-card";

    private ListView mCardListView;
    private FlashCardCursorAdapter mCardAdapter;

    private long mCurrentProject;
    private long mCurrentLabel;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Bundle extras = getIntent().getExtras();
        mCurrentProject = extras.getLong(DisplayProjectsActivity.TAG_PROJECT_ID);
        mCurrentLabel = extras.getLong(DisplayLabelsActivity.TAG_LABEL_ID);
        setTitle("Cards");
        setContentView(R.layout.activity_display_cards);
        mCardListView = (ListView) findViewById(R.id.list_cards);
        mCardListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mCardAdapter = new FlashCardCursorAdapter(this, null);
        mCardListView.setAdapter(mCardAdapter);
        setListActions();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_add_card:
                // TODO
               /* AddCardFragment addLabelFragment = AddCardFragment.newInstance(mCurrentProject);
                addLabelFragment.show(getFragmentManager(), TAG_ADD_CARD)*/;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Starts a new or restarts an existing Loader
        getLoaderManager().restartLoader(0, null, this);;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] C_LIST_PROJECTION = { FlashCardContract.FlashCardEntry._ID,
                FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION};
        return new CursorLoader(this, FlashCardContract.CONTENT_URI, C_LIST_PROJECTION,
                null, null, null); // TODO change
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCardAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);
    }

    private void setListActions() {
        // TODO
    }
}
