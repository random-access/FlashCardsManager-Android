package org.random_access.flashcardsmanager.storage.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.random_access.flashcardsmanager.storage.FlashCardsProvider;

/**
 * Project: FlashCards Manager for Android
 * Date: 09.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardContract {

    private static final String TAG = FlashCardContract.class.getSimpleName();

    public static final Uri CONTENT_URI = Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + FlashCardContract.FlashCardEntry.TABLE_NAME);

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

        public static final String TABLE_NAME = "_TBL_FLASHCARDS";

        public static final String COLUMN_NAME_FLASHCARD_QUESTION = "_QUESTION";
        public static final String COLUMN_NAME_FLASHCARD_ANSWER = "_ANSWER";
        public static final String COLUMN_NAME_FLASHCARD_STACK = "_STACK";

        public static final String COLUMN_NAME_FK_P_ID = "_FK_P_ID";
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + FlashCardEntry.TABLE_NAME
            + "("
            + FlashCardEntry._ID + " integer primary key autoincrement, "
            + FlashCardEntry.COLUMN_NAME_FLASHCARD_QUESTION + " text not null, "
            + FlashCardEntry.COLUMN_NAME_FLASHCARD_ANSWER + " text not null, "
            + FlashCardEntry.COLUMN_NAME_FLASHCARD_STACK + " integer not null, "
            + FlashCardEntry.COLUMN_NAME_FK_P_ID + " integer, "
            + "foreign key (" + FlashCardEntry.COLUMN_NAME_FK_P_ID + ") references "
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
