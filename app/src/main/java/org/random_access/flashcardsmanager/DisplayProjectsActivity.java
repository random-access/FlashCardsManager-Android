package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.net.Uri;

import org.random_access.flashcardsmanager.adapter.ProjectCursorAdapter;
import org.random_access.flashcardsmanager.helpers.MyFileUtils;
import org.random_access.flashcardsmanager.provider.contracts.ProjectContract;
import org.random_access.flashcardsmanager.queries.ProjectQueries;
import org.random_access.flashcardsmanager.xmlImport.ProjectRootFinder;
import org.random_access.flashcardsmanager.xmlImport.UnzipHelper;
import org.random_access.flashcardsmanager.xmlImport.XMLExchanger;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
    public static final String TAG_PROJECT_ID = "project-id";
    public static final String TAG_PROJECT_IMPORT = "project-import";

    private static final int FILE_SELECT_REQUEST = 1000;
    private static final String IMPORT_DIR = "import";

    private ListView mProjectListView;
    private ProjectCursorAdapter mProjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            case R.id.action_download_zip:
                Intent downloadIntent = new Intent(this, XMLDownloadActivity.class);
                startActivity(downloadIntent);
                return true;
            case R.id.action_import_file:
                showFileChooser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            Intent chooser = Intent.createChooser(intent, "Select file");
            startActivityForResult(chooser, FILE_SELECT_REQUEST);
        } catch (ActivityNotFoundException e) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "No file manager found.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            new ImportXmlTask().execute(uri);
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
        mProjectAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProjectAdapter.swapCursor(null);
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
                        long[] currentSelections = mProjectListView.getCheckedItemIds();
                        mProjectAdapter.deleteSelectedProjects(DisplayProjectsActivity.this, currentSelections);
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

    private class ImportXmlTask extends AsyncTask<Uri, Void, ArrayList<String>> {
        ProgressDialog d;
        Exception exc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = ProgressDialog.show(DisplayProjectsActivity.this, getResources().getString(R.string.importing),getResources().getString(R.string.please_wait));
        }

        @Override
        protected ArrayList<String> doInBackground(Uri... uris) {
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uris[0]);
                UnzipHelper.unzip(is, getFilesDir().getAbsolutePath() + "/" + IMPORT_DIR, DisplayProjectsActivity.this);
                return new ProjectRootFinder(getFilesDir().getAbsolutePath() + "/" + IMPORT_DIR).findProjectRootDirs();
            } catch (IOException e) {
                exc = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            d.dismiss();
            if (exc != null) {
                exc.printStackTrace();
                Toast.makeText(DisplayProjectsActivity.this, getResources().getString(R.string.io_error), Toast.LENGTH_SHORT).show();
            } else {
                PrepareImportDialog d = PrepareImportDialog.newInstance(result, IMPORT_DIR);
                d.show(getFragmentManager(), TAG_PROJECT_IMPORT);

            }
        }
    }


}
