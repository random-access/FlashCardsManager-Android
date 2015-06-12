package org.random_access.flashcardsmanager.queries;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 14.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardQueries {

    private final String TAG = FlashCardQueries.class.getSimpleName();

    private Context context;

    public FlashCardQueries(Context context) {
        this.context = context;
    }

    public Uri insertCard(String question, String answer, int stack, int projectId) {
        ContentValues values = new ContentValues();
        values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION, question);
        values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_ANSWER, answer);
        values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK, stack);
        values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID, projectId);
        Uri insertUri = context.getContentResolver().insert(FlashCardContract.CONTENT_URI, values);
        Log.d(TAG, insertUri.getPath());
        return insertUri;
    }

    public Uri assignLabelToCard(int cardId, int labelId) {
        ContentValues values = new ContentValues();
        values.put(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID, cardId);
        values.put(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID, labelId);
        Uri insertUri = context.getContentResolver().insert(LFRelationContract.CONTENT_URI, values);
        Log.d(TAG, insertUri.getPath());
        return insertUri;
    }

    public void deleteCardWithId(long cardId){
        context.getContentResolver().delete(LFRelationContract.CONTENT_URI,
                LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID + "=?", new String[]{cardId + ""});
        context.getContentResolver().delete(FlashCardContract.CONTENT_URI,
                FlashCardContract.FlashCardEntry._ID + "=?", new String[]{cardId + ""});
    }

    public boolean updateStackOfCard(long cardId, int stack) {
        ContentValues values = new ContentValues();
        values.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK, stack);
        int noOfRowsModified = context.getContentResolver().update(FlashCardContract.CONTENT_URI, values,
                FlashCardContract.FlashCardEntry._ID + "= ? ", new String[]{cardId + ""});
        return noOfRowsModified > 0;
    }

}
