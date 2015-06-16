package org.random_access.flashcardsmanager.queries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.provider.contracts.DbJoins;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;
import org.random_access.flashcardsmanager.provider.contracts.LabelContract;
import org.random_access.flashcardsmanager.xmlImport.FlashCardParser;

import java.net.URI;
import java.util.ArrayList;
import android.net.Uri;

/**
 * Project: FlashCards Manager for Android
 * Date: 14.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class LabelQueries {

    private String[] labelProjection= new String[] {LabelContract.LabelEntry._ID, LabelContract.LabelEntry.COLUMN_NAME_TITLE, LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID};

    private Context context;

    public LabelQueries (Context context) {
        this.context = context;
    }

    public ArrayList<String> getLabelsFromProject(long projectId) {
        ArrayList<String> labels = new ArrayList<>();
        Cursor c = context.getContentResolver().query(LabelContract.CONTENT_URI, labelProjection, LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID + " = ? ",
                new String[] {projectId + ""}, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                labels.add(c.getString(1));
                c.moveToNext();
            }
        }
        c.close();
        return labels;
    }

    public long getLabelId(String labelName, long projectId) {
        long labelId = -1;
        Cursor c = context.getContentResolver().query(LabelContract.CONTENT_URI, labelProjection, LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID + " = ?  AND "
                + LabelContract.LabelEntry.COLUMN_NAME_TITLE + " = ? ",
                new String[] {projectId + "", labelName}, null);
        if (c.moveToFirst() && c.getCount() == 1) {
            labelId = c.getLong(0);
        }
        c.close();
        return labelId;
    }

    public Uri addLabel(long projectId, String labelTitle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID, projectId);
        contentValues.put(LabelContract.LabelEntry.COLUMN_NAME_TITLE, labelTitle);
        return context.getContentResolver().insert(LabelContract.CONTENT_URI, contentValues);
    }

    public boolean deleteLabel(long labelId, long projectId) {
        String name = "";
        Cursor cl  = context.getContentResolver().query(Uri.parse(LabelContract.CONTENT_URI + "/" + labelId), labelProjection, null, null, null);
        if (cl.moveToFirst()) {
           name = cl.getString(1);
        }
        cl.close();
        Cursor cf = getFlashcardsWithLabel(labelId);
        if (cf.moveToFirst()) {
            if (name.equals(context.getResources().getString(R.string.uncategorized))) {
                return false;
            }
            FlashCardQueries queries = new FlashCardQueries(context);
            long uncategorizedLabelId = findOrCreateUncategorizedLabel(projectId);
            while(!cf.isAfterLast()) {
                deleteLfRelation(cf.getLong(1));
                queries.assignLabelToCard(cf.getLong(0), uncategorizedLabelId);
                cf.moveToNext();
            }
        }
        cf.close();
        context.getContentResolver().delete(LabelContract.CONTENT_URI,
                LabelContract.LabelEntry._ID + "=?", new String[]{labelId + ""});
        return true;

    }

    public void deleteLfRelation (long lfRelId) {
        context.getContentResolver().delete(LFRelationContract.CONTENT_URI, LFRelationContract.LFRelEntry._ID + " = ? ", new String[] {lfRelId + "" });
    }

    public long findOrCreateUncategorizedLabel(long projectId) {
        Cursor c = context.getContentResolver().query(LabelContract.CONTENT_URI, labelProjection,
                LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID + " = ? ", new String [] {projectId + ""}, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                if (c.getString(1).equals(context.getResources().getString(R.string.uncategorized))) {
                    return c.getLong(0);
                }
                c.moveToNext();
            }
        }
        c.close();
        return Long.parseLong(addLabel(projectId, context.getResources().getString(R.string.uncategorized)).getLastPathSegment());
    }

    public Cursor getFlashcardsWithLabel(long labelId) {
        String[] LFREL_LIST_PROJECTION = {FlashCardContract.FlashCardEntry._ID,
                LFRelationContract.LFRelEntry._ID};
        return context.getContentResolver().query(DbJoins.CONTENT_URI_FLASHCARDS_JOIN_LFRELS, LFREL_LIST_PROJECTION,
                LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID + " = ? ", new String[]{labelId + ""}, null);
    }

}
