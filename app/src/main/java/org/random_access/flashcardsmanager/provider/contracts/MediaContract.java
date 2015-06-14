package org.random_access.flashcardsmanager.provider.contracts;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.random_access.flashcardsmanager.provider.FlashCardsProvider;

/**
 * Project: FlashCards Manager for Android
 * Date: 10.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class MediaContract {

    private static final String TAG = MediaContract.class.getSimpleName();
    public static final String TABLE_NAME = "_TBL_MEDIA";
    public static final Uri CONTENT_URI = Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + TABLE_NAME);

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

        public static final String COLUMN_NAME_MEDIAPATH = "_MEDIA_PATH";
        public static final String COLUMN_NAME_PICTYPE = "_PIC_TYPE";
        public static final String COLUMN_NAME_FK_F_ID = "_FK_F_ID";

        public static final String COLUMN_NAME_ID_FULLNAME = TABLE_NAME + "." + _ID;
        public static final String COLUMN_NAME_MEDIAPATH_FULLNAME = TABLE_NAME  + "." + COLUMN_NAME_MEDIAPATH;
        public static final String COLUMN_NAME_PICTYPE_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_PICTYPE;
        public static final String COLUMN_NAME_FK_F_ID_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_FK_F_ID;
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_NAME
            + "("
            + MediaEntry._ID + " integer primary key autoincrement, "
            + MediaEntry.COLUMN_NAME_MEDIAPATH + " text not null, "
            + MediaEntry.COLUMN_NAME_PICTYPE + " text not null, "
            + MediaEntry.COLUMN_NAME_FK_F_ID + " integer, "
            + "foreign key (" + MediaEntry.COLUMN_NAME_FK_F_ID + ") references "
            + FlashCardContract.TABLE_NAME + " (" + FlashCardContract.FlashCardEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.i(TAG, DATABASE_CREATE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add upgrade procedure if necessary
    }

}
