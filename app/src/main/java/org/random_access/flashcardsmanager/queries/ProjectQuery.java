package org.random_access.flashcardsmanager.queries;

import android.content.ContentValues;
import android.net.Uri;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.ProjectContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 13.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class ProjectQuery {

    private static final String TAG = ProjectQuery.class.getSimpleName();

    private Context context;

    private String[] projection = { ProjectContract.ProjectEntry._ID,
            ProjectContract.ProjectEntry.COLUMN_NAME_TITLE,
            ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION,
            ProjectContract.ProjectEntry.COLUMN_NAME_STACKS};

    public ProjectQuery(Context context) {
        this.context = context;
    }

    public int getFlashcardCount(long projectId) {
        return QueryHelper.count(context, FlashCardContract.CONTENT_URI,
                FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + " = ? ", new String[]{projectId + ""});
    }

    public Cursor getProjectWithId(long projectId) {
        return context.getContentResolver().query(Uri.parse(ProjectContract.CONTENT_URI + "/" + projectId), projection, null, null, null);
    }

    public int updateProjectWithId(long projectId, String title, String description, int stacks) {
        ContentValues values = new ContentValues();
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE, title);
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_STACKS, stacks);
        int noOfUpdates =  context.getContentResolver().update(Uri.parse(ProjectContract.CONTENT_URI + "/" + projectId), values, null, null);
        Log.d(TAG, noOfUpdates + " rows updated");
        return noOfUpdates;
    }


    public Uri insertProject(String title, String description, int stacks) {
        ContentValues values = new ContentValues();
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE, title);
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(ProjectContract.ProjectEntry.COLUMN_NAME_STACKS, stacks);
        Uri insertUri = context.getContentResolver().insert(ProjectContract.CONTENT_URI, values);
        Log.d(TAG, insertUri.toString());
        return insertUri;
    }



}
