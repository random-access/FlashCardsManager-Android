package org.random_access.flashcardsmanager.storage;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.random_access.flashcardsmanager.storage.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.storage.contracts.LFRelationContract;
import org.random_access.flashcardsmanager.storage.contracts.LabelContract;
import org.random_access.flashcardsmanager.storage.contracts.MediaContract;
import org.random_access.flashcardsmanager.storage.contracts.ProjectContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 10.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardsProvider extends ContentProvider {

    private static final String AUTHORITY = "org.random_access.flashcardsmanager.provider";

    private FlashCardDbOpenHelper flashCardDbOpenHelper;

    // ID's for URI matcher
    private static final int PROJECT_TABLE = 1;
    private static final int LABEL_TABLE = 2;
    private static final int FLASHCARD_TABLE = 3;
    private static final int LFREL_TABLE = 4;
    private static final int MEDIA_TABLE = 5;

    private static final int PROJECT_ROw = 10;
    private static final int LABEL_ROW = 11;
    private static final int FLASHCARD_ROW = 12;
    private static final int LFREL_ROW = 13;
    private static final int MEDIA_ROW = 14;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, ProjectContract.ProjectEntry.TABLE_NAME, PROJECT_TABLE);
        uriMatcher.addURI(AUTHORITY, LabelContract.LabelEntry.TABLE_NAME, LABEL_TABLE);
        uriMatcher.addURI(AUTHORITY, FlashCardContract.FlashCardEntry.TABLE_NAME, FLASHCARD_TABLE);
        uriMatcher.addURI(AUTHORITY, LFRelationContract.LFRelEntry.TABLE_NAME, LFREL_TABLE);
        uriMatcher.addURI(AUTHORITY, MediaContract.MediaEntry.TABLE_NAME, MEDIA_TABLE);

        uriMatcher.addURI(AUTHORITY, ProjectContract.ProjectEntry.TABLE_NAME + "/#", PROJECT_ROw);
        uriMatcher.addURI(AUTHORITY, LabelContract.LabelEntry.TABLE_NAME + "/#", LABEL_ROW);
        uriMatcher.addURI(AUTHORITY, FlashCardContract.FlashCardEntry.TABLE_NAME + "/#", FLASHCARD_ROW);
        uriMatcher.addURI(AUTHORITY, LFRelationContract.LFRelEntry.TABLE_NAME + "/#", LFREL_ROW);
        uriMatcher.addURI(AUTHORITY, MediaContract.MediaEntry.TABLE_NAME + "/#", MEDIA_ROW);
    }

    @Override
    public boolean onCreate() {
        flashCardDbOpenHelper = new FlashCardDbOpenHelper(getContext());
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = flashCardDbOpenHelper.getWritableDatabase();
        int uriCode = uriMatcher.match(uri);
        String tableName = getTableName(uriCode);
        long id = sqlDB.insert(tableName, null, values);
        // notify observers
        getContext().getContentResolver().notifyChange(uri, null);
        return  Uri.parse(tableName + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = flashCardDbOpenHelper.getWritableDatabase();
        int numberOfUpdates;
        int uriCode = uriMatcher.match(uri);
        String tableName = getTableName(uriCode);
        String itemId = getTableIdColumn(uriCode);
        if (itemId == null) {
            numberOfUpdates = sqlDB.update(tableName, values, selection, selectionArgs);
        } else {
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(id)) {
                numberOfUpdates = sqlDB.update(tableName, values, itemId + "=" + id, null);
            } else {
                numberOfUpdates = sqlDB.update(tableName,values, itemId + "=" + id + " and "
                        + selection, selectionArgs);
            }
        }
        //notify observers
        getContext().getContentResolver().notifyChange(uri, null);
        return numberOfUpdates;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = flashCardDbOpenHelper.getWritableDatabase();
        int numberOfDeletions;
        int uriCode = uriMatcher.match(uri);
        String tableName = getTableName(uriCode);
        String itemId = getTableIdColumn(uriCode);
        if (itemId == null ) {
            numberOfDeletions = sqlDB.delete(tableName, selection, selectionArgs);
        } else {
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(id)) {
                numberOfDeletions = sqlDB.delete(tableName, itemId + "=" + id, null);
            } else {
                numberOfDeletions = sqlDB.delete(tableName, itemId + "=" + id + " and " + selection,
                        selectionArgs);
            }
        }
        // notify potential observers
        getContext().getContentResolver().notifyChange(uri,null);
        return numberOfDeletions;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriCode = uriMatcher.match(uri);
        String tableName = getTableName(uriCode);
        queryBuilder.setTables(tableName);
        checkColumnProjection(projection);
        String itemId = getTableIdColumn(uriCode);
        if (itemId != null) {
            queryBuilder.appendWhere(itemId + "="
                    + uri.getLastPathSegment());
        }
        SQLiteDatabase db = flashCardDbOpenHelper.getWritableDatabase();
        Cursor cursor = db.query(tableName, projection, selection,
                selectionArgs, null, null, sortOrder);
        // notify listeners
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // we don't specify a MIME type here, leaving this as is
        return null;
    }

    /**
     * Helper method to get the name of the table associated with the matched uriCode, if a valid URI was given
     * @param uriCode code returned from URI matcher
     * @return name of the table associated with the matched uriCode
     * @throws IllegalArgumentException if we didn't get a request with a valid URI
     */
    private String getTableName(int uriCode) {
        switch(uriCode) {
            case PROJECT_TABLE:
            case PROJECT_ROw:
                return ProjectContract.ProjectEntry.TABLE_NAME;
            case LABEL_TABLE:
            case LABEL_ROW:
                return LabelContract.LabelEntry.TABLE_NAME;
            case FLASHCARD_TABLE:
            case FLASHCARD_ROW:
                return FlashCardContract.FlashCardEntry.TABLE_NAME;
            case LFREL_TABLE:
            case LFREL_ROW:
                return LFRelationContract.LFRelEntry.TABLE_NAME;
            case MEDIA_TABLE:
            case MEDIA_ROW:
                return MediaContract.MediaEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uriCode);
        }
    }

    /**
     * Helper method to get the name of the ID column of the table associated with the matched uriCode,
     * if the URI specifies this
     * @param uriCode code returned from URI matcher
     * @return column name for table associated with the given uriCode (currently always "_ID"), else null
     */
    private String getTableIdColumn(int uriCode) {
        switch(uriCode){
            case PROJECT_ROw:
                return ProjectContract.ProjectEntry._ID;
            case LABEL_ROW:
                return LabelContract.LabelEntry._ID;
            case FLASHCARD_ROW:
                return FlashCardContract.FlashCardEntry._ID;
            case LFREL_ROW:
                return LFRelationContract.LFRelEntry._ID;
            case MEDIA_ROW:
                return MediaContract.MediaEntry._ID;
            default:
                return null;
        }
    }

    private void checkColumnProjection(String[] projection) {
        // TODO check if requested columns in selection are valid
    }

}
