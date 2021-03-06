package org.random_access.flashcardsmanager.queries;

import android.content.ContentValues;
import android.net.Uri;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.random_access.flashcardsmanager.MediaExchanger;
import org.random_access.flashcardsmanager.helpers.Status;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;
import org.random_access.flashcardsmanager.provider.contracts.LabelContract;
import org.random_access.flashcardsmanager.provider.contracts.MediaContract;
import org.random_access.flashcardsmanager.provider.contracts.ProjectContract;
import org.random_access.flashcardsmanager.provider.contracts.StatsContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 13.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class ProjectQueries {

    private static final String TAG = ProjectQueries.class.getSimpleName();

    private Context context;

    private String[] projection = { ProjectContract.ProjectEntry._ID,
            ProjectContract.ProjectEntry.COLUMN_NAME_TITLE,
            ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION,
            ProjectContract.ProjectEntry.COLUMN_NAME_STACKS};

    public ProjectQueries(Context context) {
        this.context = context;
    }

    public int getFlashcardCount(long projectId) {
        return QueryHelper.count(context, FlashCardContract.CONTENT_URI,
                FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + " = ? ", new String[]{projectId + ""});
    }

    public int getNumberOfStacks(long projectId) {
        int numberOfStacks = 0;
        Cursor c = getProjectWithId(projectId);
        if (c.moveToFirst()) {
            numberOfStacks = c.getInt(3);
        }
        c.close();
        return numberOfStacks;
    }

    public Cursor getProjectWithId(long projectId) {
        return context.getContentResolver().query(Uri.parse(ProjectContract.CONTENT_URI + "/" + projectId), projection, null, null, null);
    }

    public int updateProjectWithId(long projectId, String title, String description, int stacks) {
        ContentValues values = new ContentValues();
        values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK, stacks);
        int noOfChangedCards = context.getContentResolver().update(FlashCardContract.CONTENT_URI, values,
                FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK  + "> ? and " + FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + " = ?",
                new String[]{stacks + "", projectId + ""});
        Log.d(TAG, noOfChangedCards + " cards modified");
        values = new ContentValues();
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
        long projectId = Long.parseLong(insertUri.getLastPathSegment());
        Log.d(TAG, insertUri.getPath());
        MediaExchanger.createProjectDirectory(context, projectId);
        return insertUri;
    }

    public int[] deleteProjectWithId (long projectId) {
        int[] deleteResult = new int[6];
        String[] FLASHCARDS_ID_PROJECTION = {FlashCardContract.FlashCardEntry._ID};
        Cursor cursor = context.getContentResolver().query(FlashCardContract.CONTENT_URI, FLASHCARDS_ID_PROJECTION, FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + " = ?",
                new String[]{projectId + ""},null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String[] currentFlashcardId = {cursor.getLong(0) + ""};
                deleteResult[4] = context.getContentResolver().delete(MediaContract.CONTENT_URI, MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID + " = ?", currentFlashcardId);
                deleteResult[3] = context.getContentResolver().delete(LFRelationContract.CONTENT_URI, LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID + " = ?", currentFlashcardId);
                deleteResult[2] = context.getContentResolver().delete(FlashCardContract.CONTENT_URI, FlashCardContract.FlashCardEntry._ID + " = ? ", currentFlashcardId);
                cursor.moveToNext();
            }
        }
        cursor.close();
        String[] project = {projectId + ""};
        MediaExchanger.deleteProjectDirectory(context, projectId);
        deleteResult[5] = context.getContentResolver().delete(StatsContract.CONTENT_URI, StatsContract.StatsEntry.COLUMN_NAME_FK_P_ID + " = ? ", project);
        deleteResult[1] = context.getContentResolver().delete(LabelContract.CONTENT_URI, LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID + "  = ? ", project);
        deleteResult[0] = context.getContentResolver().delete(ProjectContract.CONTENT_URI,
                ProjectContract.ProjectEntry._ID + " = ?", project);
        Log.d(TAG, deleteResult[0] + " projects, " + deleteResult[1] + " labels, " + deleteResult[2] + " flashcards, " + deleteResult[3] + " lfrels and " + deleteResult[4] +  " media deleted.");
        return deleteResult;
    }

    public Status getProjectStatus(long projectId, int maxStack) {
        String[] FLASHCARDS_STACK_PROJECTION = {FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK};
        Cursor cursor = context.getContentResolver().query(FlashCardContract.CONTENT_URI, FLASHCARDS_STACK_PROJECTION,
                FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + " = ?", new String[]{projectId + ""},
                FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK + " desc");
        return QueryHelper.getStatus(cursor,maxStack);
    }


    public int getCompletedCardCount(long projectId, int maxStack) {
        return QueryHelper.count(context, FlashCardContract.CONTENT_URI,
                FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + "= ? and " + FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK + " = ? ",
                new String[]{projectId + "", maxStack + ""});

    }



}
