package org.random_access.flashcardsmanager.storage.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Project: FlashCards Manager for Android
 * Date: 10.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class MediaContract {

    private static final String TAG = MediaContract.class.getSimpleName();

    // prevent instantiation
    private MediaContract() {}

    /**
     * Table name: _TBL_MEDIA
     * <br>
     * Columns:
     * <ul>
     *     <li>_ID: int, PK, NN, AI</li>
     *     <li>_MEDIA_PATH: text</li>
     *     <li>_PIC_TYPE: text</li>
     *     <li>_FK_F_ID: int, references _TBL_FLASHCARDS._ID</li>
     * </ul>
     */
    public static abstract class MediaEntry implements BaseColumns {

        public static final String TABLE_NAME = "_TBL_MEDIA";

        public static final String COLUMN_NAME_PATH_TO_MEDIA = "_MEDIA_PATH";
        public static final String COLUMN_NAME_PIC_TYPE = "_PIC_TYPE";

        public static final String COLUMN_NAME_FK_F_ID = "_FK_F_ID";
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + MediaEntry.TABLE_NAME
            + "("
            + MediaEntry._ID + " integer primary key autoincrement, "
            + MediaEntry.COLUMN_NAME_PATH_TO_MEDIA + " text not null, "
            + MediaEntry.COLUMN_NAME_PIC_TYPE + " text not null, "
            + MediaEntry.COLUMN_NAME_FK_F_ID + " integer, "
            + "foreign key (" + MediaEntry.COLUMN_NAME_FK_F_ID + ") references "
            + FlashCardContract.FlashCardEntry.TABLE_NAME + " (" + FlashCardContract.FlashCardEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(TAG, DATABASE_CREATE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add upgrade procedure if necessary
    }

}
