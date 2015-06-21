package org.random_access.flashcardsmanager.provider.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.random_access.flashcardsmanager.provider.FlashCardsProvider;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 14.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class StatsContract {

    private static final String TAG = FlashCardContract.class.getSimpleName();

    public static final String TABLE_NAME = "_TBL_STATS";

    public static final Uri CONTENT_URI = Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + TABLE_NAME);

    // prevent instantiation
    private StatsContract(){}

    /**
     * Table name: _TBL_STATS
     * <br>
     * Columns:
     * <ul>
     *      <li>_ID: int, PK, AI -> inherited from BaseColumns</li>
     *      <li>_DATE: int</li>
     *      <li>_WRONG_ANSWERS: int</li>
     *      <li>_RIGHT_ANSWERS: int</li>
     *      <li>_NEUTRAL_ANSWERS: int</li>
     *      <li>_FK_P_ID: int, references _TBL_PROJECTS._ID</li>
     * </ul>
     */

    public static abstract class StatsEntry implements BaseColumns {

        public static final String COLUMN_NAME_DATE = "_QUESTION";
        public static final String COLUMN_NAME_WRONG_ANSWERS = "_WRONG_ANSWERS";
        public static final String COLUMN_NAME_RIGHT_ANSWERS = "_RIGHT_ANSWERS";
        public static final String COLUMN_NAME_NEUTRAL_ANSWERS = "_NEUTRAL_ANSWERS";
        public static final String COLUMN_NAME_FK_P_ID = "_FK_P_ID";
        public static final String COLUMN_NAME_LAST_MODIFIED = "_LAST_MODIFIED";

        public static final String COLUMN_NAME_ID_FULLNAME = TABLE_NAME + "." + _ID;
        public static final String COLUMN_NAME_DATE_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_DATE;
        public static final String COLUMN_NAME_WRONG_ANSWERS_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_WRONG_ANSWERS;
        public static final String COLUMN_NAME_RIGHT_ANSWERS_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_RIGHT_ANSWERS;
        public static final String COLUMN_NAME_NEUTRAL_ANSWERS_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_NEUTRAL_ANSWERS;
        public static final String COLUMN_NAME_FK_P_ID_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_FK_P_ID;
        public static final String COLUMN_NAME_LAST_MODIFIED_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_LAST_MODIFIED;
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_NAME
            + "("
            + StatsContract.StatsEntry._ID + " integer primary key autoincrement, "
            + StatsEntry.COLUMN_NAME_DATE + " integer not null, "
            + StatsEntry.COLUMN_NAME_WRONG_ANSWERS + " integer not null, "
            + StatsEntry.COLUMN_NAME_RIGHT_ANSWERS + " integer not null, "
            + StatsEntry.COLUMN_NAME_NEUTRAL_ANSWERS + " integer not null, "
            + StatsEntry.COLUMN_NAME_FK_P_ID + " integer, "
            + StatsEntry.COLUMN_NAME_LAST_MODIFIED + " integer default -1, "
            + "foreign key (" + StatsEntry.COLUMN_NAME_FK_P_ID + ") references "
            +  ProjectContract.TABLE_NAME + " (" + ProjectContract.ProjectEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.i(TAG, DATABASE_CREATE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("alter table " + TABLE_NAME + " add column " + StatsEntry.COLUMN_NAME_LAST_MODIFIED + " integer default -1");
            Log.d(TAG, TABLE_NAME + " updated: old version: " + oldVersion + ", new version: " + newVersion + "(added "+ StatsEntry.COLUMN_NAME_LAST_MODIFIED + " column)");
        }
    }
}
