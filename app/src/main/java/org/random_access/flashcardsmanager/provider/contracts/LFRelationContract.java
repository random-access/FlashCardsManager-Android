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
public class LFRelationContract {

    private static final String TAG = LFRelationContract.class.getSimpleName();
    public static final String TABLE_NAME = "_TBL_LFRELATIONS";
    public static final Uri CONTENT_URI = Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + TABLE_NAME);

    // prevent instantiation
    private LFRelationContract() {}

    /**
     * Table name: _TBL_LFRELATIONS
     * <br>
     * Columns:
     * <ul>
     *     <li>_ID: int, PK, NN, AI</li>
     *     <li>_FK_L_ID: int, references _TBL_LABELS._ID</li>
     *     <li>FK_F_ID: int, references _TBL_FLASHCARDS._ID</li>
     * </ul>
     */
    public static abstract class LFRelEntry implements BaseColumns {

        public static final String COLUMN_NAME_FK_L_ID = "_FK_L_ID";
        public static final String COLUMN_NAME_FK_F_ID = "_FK_F_ID";

        public static final String COLUMN_NAME_ID_FULLNAME = TABLE_NAME + "." + _ID;
        public static final String COLUMN_NAME_FK_L_ID_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_FK_L_ID;
        public static final String COLUMN_NAME_FK_F_ID_FULLNAME = TABLE_NAME + "." + COLUMN_NAME_FK_F_ID;
    }

    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_NAME
            + "("
            + LFRelEntry._ID + " integer primary key autoincrement, "
            + LFRelEntry.COLUMN_NAME_FK_L_ID + " integer, "
            + LFRelEntry.COLUMN_NAME_FK_F_ID + " integer, "
            + "foreign key (" + LFRelEntry.COLUMN_NAME_FK_L_ID + ") references "
            +  LabelContract.TABLE_NAME + " (" + LabelContract.LabelEntry._ID + "), "
            + "foreign key (" + LFRelEntry.COLUMN_NAME_FK_F_ID + ") references "
            +  FlashCardContract.TABLE_NAME + " (" + FlashCardContract.FlashCardEntry._ID + ")"
            + ");";

    public static void onCreate (SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.i(TAG, DATABASE_CREATE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add upgrade procedure if necessary
    }

}
