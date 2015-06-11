package org.random_access.flashcardsmanager.queries;

import android.content.Context;
import android.database.Cursor;

import org.random_access.flashcardsmanager.provider.contracts.LabelContract;

import java.util.ArrayList;

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

}
