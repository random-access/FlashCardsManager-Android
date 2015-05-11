package org.random_access.flashcardsmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.random_access.flashcardsmanager.adapter.ProjectCursorAdapter;
import org.random_access.flashcardsmanager.storage.contracts.ProjectContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 09.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_ADD_PROJECT = "add-project";

    private static final String BASE_URI = "content://org.random_access.flashcardsmanager.provider";

    private String[] mProjection = {ProjectContract.ProjectEntry._ID, ProjectContract.ProjectEntry.COLUMN_NAME_PROJECT_TITLE,
            ProjectContract.ProjectEntry.COLUMN_NAME_PROJECT_STACKS};

    private ListView mProjectList;
    private ProjectCursorAdapter mProjectAdapter;

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getProjectsFromDatabase();
        mProjectList = (ListView) findViewById(R.id.list_projects);
        mProjectAdapter = new ProjectCursorAdapter(this,mCursor);
        mProjectList.setAdapter(mProjectAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                AddProjectFragment addProjectFragment = AddProjectFragment.newInstance();
                addProjectFragment.show(getFragmentManager(), TAG_ADD_PROJECT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCursor.close();
    }

    private void getProjectsFromDatabase () {
        /*getContentResolver().delete(Uri.parse(BASE_URI + "/" + ProjectContract.ProjectEntry.TABLE_NAME), null, null);
        Log.d(TAG, "Successfully deleted test data");
        ContentValues mNewValues = new ContentValues();
        for (int i = 0; i<7; i++) {
            mNewValues.put(ProjectContract.ProjectEntry.COLUMN_NAME_PROJECT_TITLE, "Testprojekt " + (i+1));
            mNewValues.put(ProjectContract.ProjectEntry.COLUMN_NAME_PROJECT_STACKS, "3");
            getContentResolver().insert(Uri.parse(BASE_URI + "/" + ProjectContract.ProjectEntry.TABLE_NAME), mNewValues);
            Log.d(TAG, "Successfully inserted test data");
        }*/
        mCursor = getContentResolver().query(Uri.parse(BASE_URI + "/" + ProjectContract.ProjectEntry.TABLE_NAME), mProjection, null, null, null);
        Log.d(TAG, "Successfully queried content provider");
    }

    public void updateUI() {
        mCursor = getContentResolver().query(Uri.parse(BASE_URI + "/" + ProjectContract.ProjectEntry.TABLE_NAME), mProjection, null, null, null);
        mProjectAdapter.swapCursor(mCursor);
    }
}
