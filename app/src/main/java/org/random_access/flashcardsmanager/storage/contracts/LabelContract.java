package org.random_access.flashcardsmanager.storage.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Project: FlashCards Manager for Android
 * Date: 09.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class LabelContract {

    private static final String TAG = LabelContract.class.getSimpleName();

    // prevent instantiation
    private LabelContract() {}

    /**
     * Table name: _TBL_LABELS
     * <br>
     * Columns:
     * <ul>
     *     <li>_ID: int, PK, NN, AI: inherited from BaseColumns</li>
     *     <li>_TITLE: text</li>
     *     <li>_FK_P_ID: int, references _TBL_PROJECTS._ID</li>
     * </ul>
     */
    public static abstract class LabelEntry implements BaseColumns {

        public static final String TABLE_NAME = "_TBL_LABELS";

        public static final String COLUMN_NAME_LABEL_TITLE = "_TITLE";

        public static final String COLUMN_NAME_FK_P_ID = "_FK_P_ID";
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + LabelEntry.TABLE_NAME
            + "("
            + LabelEntry._ID + " integer primary key autoincrement, "
            + LabelEntry.COLUMN_NAME_LABEL_TITLE + " text not null, "
            + LabelEntry.COLUMN_NAME_FK_P_ID + " integer, "
            + "foreign key (" + LabelEntry.COLUMN_NAME_FK_P_ID + ") references "
            +  ProjectContract.ProjectEntry.TABLE_NAME + " (" + ProjectContract.ProjectEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(TAG, DATABASE_CREATE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add upgrade procedure if necessary
    }

}
