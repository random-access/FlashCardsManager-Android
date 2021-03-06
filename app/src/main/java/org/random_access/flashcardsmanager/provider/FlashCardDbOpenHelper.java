package org.random_access.flashcardsmanager.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;
import org.random_access.flashcardsmanager.provider.contracts.LabelContract;
import org.random_access.flashcardsmanager.provider.contracts.MediaContract;
import org.random_access.flashcardsmanager.provider.contracts.ProjectContract;
import org.random_access.flashcardsmanager.provider.contracts.StatsContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 10.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardDbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = FlashCardDbOpenHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "FLASHCARDSMANAGER.db";

    public FlashCardDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates all databases if not existing
     * @param db the sqlite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        ProjectContract.onCreate(db);
        FlashCardContract.onCreate(db);
        LabelContract.onCreate(db);
        LFRelationContract.onCreate(db);
        MediaContract.onCreate(db);
        StatsContract.onCreate(db);
        Log.d(TAG, "Finished onCreate in FlashCardDbOpenHelper");
    }

    /**
     * Starts update routines in all tables
     * @param db the sqlite database
     * @param oldVersion current version of database
     * @param newVersion new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ProjectContract.onUpdate(db, oldVersion, newVersion);
        LabelContract.onUpdate(db, oldVersion, newVersion);
        FlashCardContract.onUpdate(db, oldVersion, newVersion);
        LFRelationContract.onUpdate(db, oldVersion, newVersion);
        MediaContract.onUpdate(db, oldVersion, newVersion);
        StatsContract.onUpdate(db, oldVersion, newVersion);
    }

}
