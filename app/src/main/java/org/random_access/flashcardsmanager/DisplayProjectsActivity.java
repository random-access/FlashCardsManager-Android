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
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.random_access.flashcardsmanager.adapter.ProjectCursorAdapter;
import org.random_access.flashcardsmanager.provider.contracts.ProjectContract;
import org.random_access.flashcardsmanager.queries.ProjectQueries;

/**
 * Project: FlashCards Manager for Android
 * Date: 09.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class DisplayProjectsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = DisplayProjectsActivity.class.getSimpleName();
    private static final String TAG_ADD_PROJECT = "add-project";
    private static final String TAG_IMPORT_PROJECT = "import-project";

    public static final String TAG_PROJECT_ID = "project-id";

    private ListView mProjectListView;
    private ProjectCursorAdapter mProjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_display_projects);
        mProjectListView = (ListView) findViewById(R.id.list_projects);
        mProjectListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mProjectAdapter = new ProjectCursorAdapter(this, null);
        mProjectListView.setAdapter(mProjectAdapter);
        setListActions();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_projects, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_add_project:
                ProjectDialogFragment addProjectFragment = ProjectDialogFragment.newInstance(true, -1);
                addProjectFragment.show(getFragmentManager(), TAG_ADD_PROJECT);
                return true;
            case R.id.action_import:
                Intent intent = new Intent(this, XMLImportActivity.class);
                startActivity(intent);
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
        String[] P_LIST_PROJECTION = { ProjectContract.ProjectEntry._ID,
                ProjectContract.ProjectEntry.COLUMN_NAME_TITLE,
                ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION,
                ProjectContract.ProjectEntry.COLUMN_NAME_STACKS};
        return new CursorLoader(this, ProjectContract.CONTENT_URI, P_LIST_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "in onLoadFinished");
        mProjectAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProjectAdapter.swapCursor(null);
    }

    private void deleteSelectedProjects() {
        long[] currentSelections = mProjectListView.getCheckedItemIds();
        OnDeleteProjectsDialogListener dialogClickListener = new OnDeleteProjectsDialogListener(currentSelections);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setNeutralButton(getResources().getString(R.string.no), dialogClickListener)
                .setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setTitle(getResources().getString(R.string.delete))
                .setMessage(getResources().getQuantityString(R.plurals.really_delete_project, currentSelections.length, currentSelections.length))
                .setCancelable(false);
        builder.show();
    }

    class OnDeleteProjectsDialogListener implements DialogInterface.OnClickListener {

        long[] currentSelection;

        OnDeleteProjectsDialogListener(long[] currentSelection) {
            this.currentSelection = currentSelection;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    int selCount = currentSelection.length;
                    for (long l : currentSelection) {
                        new ProjectQueries(DisplayProjectsActivity.this).deleteProjectWithId(l);
                    }
                    Toast.makeText(DisplayProjectsActivity.this, getResources().
                            getQuantityString(R.plurals.deleted_project, selCount, selCount), Toast.LENGTH_SHORT).show();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // user cancelled
                    break;
            }
        }
    }

    private void setListActions () {
        mProjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mProjectAdapter.getmCurrentDetailPosition() == -1) {
                    mProjectAdapter.setmCurrentDetailPosition(position);
                } else if (mProjectAdapter.getmCurrentDetailPosition() == position){
                    mProjectAdapter.setmCurrentDetailPosition(-1);
                } else {
                    mProjectAdapter.setmCurrentDetailPosition(position);
                }
                Log.d(TAG, "Set mCurrentDetailView to " + mProjectAdapter.getmCurrentDetailPosition());
            }
        });

        mProjectListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mProjectListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_project_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_delete_project:
                        deleteSelectedProjects();
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
