package org.random_access.flashcardsmanager.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.random_access.flashcardsmanager.provider.contracts.DbJoins;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;
import org.random_access.flashcardsmanager.provider.contracts.LabelContract;
import org.random_access.flashcardsmanager.provider.contracts.MediaContract;
import org.random_access.flashcardsmanager.provider.contracts.ProjectContract;

import java.util.HashMap;

/**
 * Project: FlashCards Manager for Android
 * Date: 10.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardsProvider extends ContentProvider {

    public static final String AUTHORITY = "org.random_access.flashcardsmanager.provider";

    private FlashCardDbOpenHelper flashCardDbOpenHelper;

    // ID's for URI matcher
    private static final int PROJECT_TABLE = 1;
    private static final int LABEL_TABLE = 2;
    private static final int FLASHCARD_TABLE = 3;
    private static final int LFREL_TABLE = 4;
    private static final int MEDIA_TABLE = 5;

    private static final int PROJECT_ROW = 10;
    private static final int LABEL_ROW = 11;
    private static final int FLASHCARD_ROW = 12;
    private static final int LFREL_ROW = 13;
    private static final int MEDIA_ROW = 14;

    private static final int FLASHCARDS_FROM_LABELS = 20;

    private static final int FLASHCARDS_FROM_LABELS_ROW = 30;

    private static HashMap<String, String> PROJECTION_MAP_PROJECTS;
    private static HashMap<String, String> PROJECTION_MAP_LABELS;
    private static HashMap<String, String> PROJECTION_MAP_FLASHCARDS;
    private static HashMap<String, String> PROJECTION_MAP_LFRELS;
    private static HashMap<String, String> PROJECTION_MAP_MEDIA;
    private static HashMap<String, String> PROJECTION_MAP_FLASHCARD_JOIN_LFRELATIONS;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, ProjectContract.ProjectEntry.TABLE_NAME, PROJECT_TABLE);
        uriMatcher.addURI(AUTHORITY, LabelContract.LabelEntry.TABLE_NAME, LABEL_TABLE);
        uriMatcher.addURI(AUTHORITY, FlashCardContract.FlashCardEntry.TABLE_NAME, FLASHCARD_TABLE);
        uriMatcher.addURI(AUTHORITY, LFRelationContract.LFRelEntry.TABLE_NAME, LFREL_TABLE);
        uriMatcher.addURI(AUTHORITY, MediaContract.MediaEntry.TABLE_NAME, MEDIA_TABLE);

        uriMatcher.addURI(AUTHORITY, ProjectContract.ProjectEntry.TABLE_NAME + "/#", PROJECT_ROW);
        uriMatcher.addURI(AUTHORITY, LabelContract.LabelEntry.TABLE_NAME + "/#", LABEL_ROW);
        uriMatcher.addURI(AUTHORITY, FlashCardContract.FlashCardEntry.TABLE_NAME + "/#", FLASHCARD_ROW);
        uriMatcher.addURI(AUTHORITY, LFRelationContract.LFRelEntry.TABLE_NAME + "/#", LFREL_ROW);
        uriMatcher.addURI(AUTHORITY, MediaContract.MediaEntry.TABLE_NAME + "/#", MEDIA_ROW);

        uriMatcher.addURI(AUTHORITY, DbJoins.NAME_FLASHCARDS_JOIN_LFRELS, FLASHCARDS_FROM_LABELS);

        uriMatcher.addURI(AUTHORITY, DbJoins.NAME_FLASHCARDS_JOIN_LFRELS + "/#", FLASHCARDS_FROM_LABELS_ROW);
    }

    static {
        PROJECTION_MAP_PROJECTS = new HashMap<>();
        PROJECTION_MAP_PROJECTS.put(ProjectContract.ProjectEntry._ID, ProjectContract.ProjectEntry.COLUMN_NAME_ID_FULLNAME);
        PROJECTION_MAP_PROJECTS.put(ProjectContract.ProjectEntry.COLUMN_NAME_TITLE, ProjectContract.ProjectEntry.COLUMN_NAME_TITLE_FULLNAME);
        PROJECTION_MAP_PROJECTS.put(ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION, ProjectContract.ProjectEntry.COLUMN_NAME_DESCRIPTION_FULLNAME);
        PROJECTION_MAP_PROJECTS.put(ProjectContract.ProjectEntry.COLUMN_NAME_STACKS, ProjectContract.ProjectEntry.COLUMN_NAMEß_STACKS_FULLNAME);

        PROJECTION_MAP_LABELS = new HashMap<>();
        PROJECTION_MAP_LABELS.put(LabelContract.LabelEntry._ID, LabelContract.LabelEntry.COLUMN_NAME_ID_FULLNAME);
        PROJECTION_MAP_LABELS.put(LabelContract.LabelEntry.COLUMN_NAME_TITLE, LabelContract.LabelEntry.COLUMN_NAME_TITLE_FULLNAME);
        PROJECTION_MAP_LABELS.put(LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID, LabelContract.LabelEntry.COLUMN_NAME_FK_P_ID_FULLNAME);

        PROJECTION_MAP_FLASHCARDS = new HashMap<>();
        PROJECTION_MAP_FLASHCARDS.put(FlashCardContract.FlashCardEntry._ID, FlashCardContract.FlashCardEntry.COLUMN_NAME_ID_FULLNAME);
        PROJECTION_MAP_FLASHCARDS.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION, FlashCardContract.FlashCardEntry.COLUMN_NAME_QUESTION_FULLNAME);
        PROJECTION_MAP_FLASHCARDS.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_ANSWER, FlashCardContract.FlashCardEntry.COLUMN_NAME_ANSWER_FULLNAME);
        PROJECTION_MAP_FLASHCARDS.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK, FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK_FULLNAME);
        PROJECTION_MAP_FLASHCARDS.put(FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID, FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID_FULLNAME);

        PROJECTION_MAP_LFRELS = new HashMap<>();
        PROJECTION_MAP_LFRELS.put(LFRelationContract.LFRelEntry._ID, LFRelationContract.LFRelEntry.COLUMN_NAME_ID_FULLNAME);
        PROJECTION_MAP_LFRELS.put(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID, LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID_FULLNAME);
        PROJECTION_MAP_LFRELS.put(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID, LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID_FULLNAME);

        PROJECTION_MAP_MEDIA = new HashMap<>();
        PROJECTION_MAP_MEDIA.put(MediaContract.MediaEntry._ID, MediaContract.MediaEntry.COLUMN_NAME_ID_FULLNAME);
        PROJECTION_MAP_MEDIA.put(MediaContract.MediaEntry.COLUMN_NAME_MEDIAPATH, MediaContract.MediaEntry.COLUMN_NAME_MEDIAPATH_FULLNAME);
        PROJECTION_MAP_MEDIA.put(MediaContract.MediaEntry.COLUMN_NAME_PICTYPE, MediaContract.MediaEntry.COLUMN_NAME_PICTYPE_FULLNAME);
        PROJECTION_MAP_MEDIA.put(MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID, MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID_FULLNAME);

        PROJECTION_MAP_FLASHCARD_JOIN_LFRELATIONS = new HashMap<>();
        PROJECTION_MAP_FLASHCARD_JOIN_LFRELATIONS.putAll(PROJECTION_MAP_FLASHCARDS);
        PROJECTION_MAP_FLASHCARD_JOIN_LFRELATIONS.putAll(PROJECTION_MAP_LFRELS);

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
            if (TextUtils.isEmpty(selection)) {
                numberOfUpdates = sqlDB.update(tableName, values, itemId + " = ? ", new String[]{id + ""});
            } else {
                numberOfUpdates = sqlDB.update(tableName, values, itemId + " = " +  id + " and "
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
        HashMap<String,String> pMap = getProjections(uriCode);
        queryBuilder.setTables(tableName);
        queryBuilder.setProjectionMap(pMap);
        checkColumnProjection(projection);
        String itemId = getTableIdColumn(uriCode);
        if (itemId != null) {
            queryBuilder.appendWhere(itemId + "="
                    + uri.getLastPathSegment());
        }
        SQLiteDatabase db = flashCardDbOpenHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // notify listeners
            cursor.setNotificationUri(getContext().getContentResolver(), getNotificationUri(uriCode));
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // TODO think about name of mime type
        return null;
    }

    private Uri getNotificationUri(int uriCode) {
        switch (uriCode) {
            case PROJECT_TABLE:
            case PROJECT_ROW:
                return ProjectContract.CONTENT_URI;
            case LABEL_TABLE:
            case LABEL_ROW:
                return LabelContract.CONTENT_URI;
            case FLASHCARD_TABLE:
            case FLASHCARD_ROW:
            case FLASHCARDS_FROM_LABELS:
            case FLASHCARDS_FROM_LABELS_ROW:
                return FlashCardContract.CONTENT_URI;
            case LFREL_TABLE:
            case LFREL_ROW:
                return LFRelationContract.CONTENT_URI;
            case MEDIA_TABLE:
            case MEDIA_ROW:
                return MediaContract.CONTENT_URI;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uriCode);
        }
    }

    /**
     * Helper method to get the projection map associated with the matched uriCode, if a valid Uri was given
     * @param uriCode code returned from URI matcher
     * @return projection map that maps all column requests to TableName.column
     * @throws IllegalArgumentException if we didn't get a request with a valid Uri
     */
    private HashMap<String,String> getProjections(int uriCode) {
        switch (uriCode) {
            case PROJECT_TABLE:
            case PROJECT_ROW:
                return PROJECTION_MAP_PROJECTS;
            case LABEL_TABLE:
            case LABEL_ROW:
                return PROJECTION_MAP_LABELS;
            case FLASHCARD_TABLE:
            case FLASHCARD_ROW:
                return PROJECTION_MAP_FLASHCARDS;
            case LFREL_TABLE:
            case LFREL_ROW:
                return PROJECTION_MAP_LFRELS;
            case MEDIA_TABLE:
            case MEDIA_ROW:
                return PROJECTION_MAP_MEDIA;
            case FLASHCARDS_FROM_LABELS:
            case FLASHCARDS_FROM_LABELS_ROW:
                return PROJECTION_MAP_FLASHCARD_JOIN_LFRELATIONS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uriCode);
        }
    }

    /**
     * Helper method to get the name of the table associated with the matched uriCode, if a valid Uri was given
     * @param uriCode code returned from URI matcher
     * @return name of the table associated with the matched uriCode
     * @throws IllegalArgumentException if we didn't get a request with a valid Uri
     */
    private String getTableName(int uriCode) {
        switch(uriCode) {
            case PROJECT_TABLE:
            case PROJECT_ROW:
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
            case FLASHCARDS_FROM_LABELS:
            case FLASHCARDS_FROM_LABELS_ROW:
                return DbJoins.TABLES_FLASHCARDS_JOIN_LFRELS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uriCode);
        }
    }

    /**
     * Helper method to get the name of the ID column of the table associated with the matched uriCode,
     * if the Uri specifies this
     * @param uriCode code returned from Uri matcher
     * @return column name for table associated with the given uriCode (currently always "_ID"), else null
     */
    private String getTableIdColumn(int uriCode) {
        switch(uriCode){
            case PROJECT_ROW:
                return ProjectContract.ProjectEntry._ID;
            case LABEL_ROW:
                return LabelContract.LabelEntry._ID;
            case FLASHCARD_ROW:
                return FlashCardContract.FlashCardEntry._ID;
            case LFREL_ROW:
                return LFRelationContract.LFRelEntry._ID;
            case MEDIA_ROW:
                return MediaContract.MediaEntry._ID;
            case FLASHCARDS_FROM_LABELS_ROW:
                return FlashCardContract.FlashCardEntry._ID;
            default:
                return null;
        }
    }

    private void checkColumnProjection(String[] projection) {
        // TODO check if requested columns in selection are valid
    }

}
