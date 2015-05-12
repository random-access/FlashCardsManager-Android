package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.random_access.flashcardsmanager.adapter.LabelCursorAdapter;
import org.random_access.flashcardsmanager.storage.contracts.LabelContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 11.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class DisplayLabelsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DisplayLabelsActivity.class.getSimpleName();
    private static final String TAG_ADD_LABEL = "add-label";

    public static final String TAG_LABEL_ID = "show-cards";

    private ListView mLabelListView;
    private LabelCursorAdapter mLabelAdapter;

    private long mCurrentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentProject = getIntent().getExtras().getLong(DisplayProjectsActivity.TAG_PROJECT_ID);
        setTitle("Labels");
        setContentView(R.layout.activity_display_labels);
        mLabelListView = (ListView) findViewById(R.id.list_labels);
        mLabelListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mLabelAdapter = new LabelCursorAdapter(this, null);
        mLabelListView.setAdapter(mLabelAdapter);
        setListActions();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_labels, menu);
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
            case R.id.action_add_label:
                AddLabelFragment addLabelFragment = AddLabelFragment.newInstance(mCurrentProject);
                addLabelFragment.show(getFragmentManager(), TAG_ADD_LABEL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Starts a new or restarts an existing Loader
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] L_LIST_PROJECTION = { LabelContract.LabelEntry._ID,
                LabelContract.LabelEntry.COLUMN_NAME_TITLE};
        return new CursorLoader(this, LabelContract.CONTENT_URI, L_LIST_PROJECTION,
                LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID + "=?", new String[] {mCurrentProject + ""}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLabelAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLabelAdapter.swapCursor(null);
    }

    private void deleteSelectedLabels() {
        long[] currentSelections = mLabelListView.getCheckedItemIds();
        OnDeleteLabelsDialogListener dialogClickListener = new OnDeleteLabelsDialogListener(currentSelections);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setNeutralButton(getResources().getString(R.string.no), dialogClickListener)
                .setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setTitle(getResources().getString(R.string.delete))
                .setMessage(getResources().getQuantityString(R.plurals.really_delete_label, currentSelections.length, currentSelections.length))
                .setCancelable(false);
        builder.show();
    }

    class OnDeleteLabelsDialogListener implements DialogInterface.OnClickListener {

        long[] currentSelection;

        OnDeleteLabelsDialogListener(long[] currentSelection) {
            this.currentSelection = currentSelection;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    int selCount = currentSelection.length;
                    for (long l : currentSelection) {
                        getContentResolver().delete(LabelContract.CONTENT_URI,
                                LabelContract.LabelEntry._ID + "=?", new String[]{l + ""});
                    }
                    Toast.makeText(DisplayLabelsActivity.this, getResources().
                            getQuantityString(R.plurals.deleted_label, selCount, selCount), Toast.LENGTH_SHORT).show();
                    // set count for deleting multiple projects
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // user cancelled
                    break;
            }
        }
    };



    private void setListActions() {
        mLabelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(DisplayLabelsActivity.this, DisplayCardsActivity.class);
                intent.putExtra(DisplayProjectsActivity.TAG_PROJECT_ID, mCurrentProject);
                intent.putExtra(TAG_LABEL_ID, id);
                startActivity(intent);
            }
        });

        mLabelListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mLabelListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_label_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_edit_label:
                        Toast.makeText(DisplayLabelsActivity.this, "Edit selected labels", Toast.LENGTH_SHORT).show();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.action_delete_label:
                        deleteSelectedLabels();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }
}
