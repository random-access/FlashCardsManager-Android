package org.random_access.flashcardsmanager.provider.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.random_access.flashcardsmanager.provider.FlashCardsProvider;

/**
 * Project: FlashCards Manager for Android
 * Date: 09.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardContract {

    private static final String TAG = FlashCardContract.class.getSimpleName();
    public static final String TABLE_NAME = "_TBL_FLASHCARDS";
    public static final Uri CONTENT_URI = Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + TABLE_NAME);

    // prevent instantiation
    private FlashCardContract(){}

    /**
     * Table name: _TBL_FLASHCARDS
     * <br>
     * Columns:
     * <ul>
     *      <li>_ID: int, PK, AI -> inherited from BaseColumns</li>
     *      <li>_QUESTION: text</li>
     *      <li>_ANSWER: text</li>
     *      <li>_STACK: int</li>
     *      <li>_FK_P_ID: int, references _TBL_PROJECTS._ID</li>
     * </ul>
     */
    public static abstract class FlashCardEntry implements BaseColumns {

        public static final String COLUMN_NAME_QUESTION = "_QUESTION";
        public static final String COLUMN_NAME_ANSWER = "_ANSWER";
        public static final String COLUMN_NAME_STACK = "_STACK";
        public static final String COLUMN_NAME_FK_P_ID = "_FK_P_ID";
        public static final String COLUMN_NAME_LAST_MODIFIED = "_LAST_MODIFIED";

        public static final String COLUMN_NAME_ID_FULLNAME = TABLE_NAME + "." + _ID;
        public static final String COLUMN_NAME_QUESTION_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_QUESTION;
        public static final String COLUMN_NAME_ANSWER_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_ANSWER;
        public static final String COLUMN_NAME_STACK_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_STACK;
        public static final String COLUMN_NAME_FK_P_ID_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_FK_P_ID;
        public static final String COLUMN_NAME_LAST_MODIFIED_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_LAST_MODIFIED;
    }

   private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_NAME
            + "("
            + FlashCardEntry._ID + " integer primary key autoincrement, "
            + FlashCardEntry.COLUMN_NAME_QUESTION + " text not null, "
            + FlashCardEntry.COLUMN_NAME_ANSWER + " text not null, "
            + FlashCardEntry.COLUMN_NAME_STACK + " integer not null, "
            + FlashCardEntry.COLUMN_NAME_FK_P_ID + " integer, "
            + FlashCardEntry.COLUMN_NAME_LAST_MODIFIED + " integer default -1, "
            + "foreign key (" + FlashCardEntry.COLUMN_NAME_FK_P_ID + ") references "
            +  ProjectContract.TABLE_NAME + " (" + ProjectContract.ProjectEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.i(TAG, DATABASE_CREATE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("alter table " + TABLE_NAME + " add column " + FlashCardEntry.COLUMN_NAME_LAST_MODIFIED + " integer default -1");
            Log.d(TAG, TABLE_NAME + " updated: old version: " + oldVersion + ", new version: " + newVersion + "(added " + FlashCardEntry.COLUMN_NAME_LAST_MODIFIED + " column)");
        }
    }

}
