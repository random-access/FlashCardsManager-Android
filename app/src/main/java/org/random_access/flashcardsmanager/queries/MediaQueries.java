package org.random_access.flashcardsmanager.queries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.random_access.flashcardsmanager.MediaExchanger;
import org.random_access.flashcardsmanager.provider.contracts.MediaContract;

import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 14.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MediaQueries {

    private static final String[] MEDIA_PROJECTION = new String [] {
            MediaContract.MediaEntry._ID, MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID,
            MediaContract.MediaEntry.COLUMN_NAME_MEDIAPATH, MediaContract.MediaEntry.COLUMN_NAME_PICTYPE
    };

    private Context context;

    public MediaQueries (Context context) {
        this.context = context;
    }


    public Bitmap getMediaForFlashcard(long projectId, long flashcardId, String picType) {
        Bitmap bmp;
        Cursor cursor = context.getContentResolver().query(MediaContract.CONTENT_URI, MEDIA_PROJECTION, MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID + " = ? " +
                        "AND " + MediaContract.MediaEntry.COLUMN_NAME_PICTYPE + " = ? ",
                new String[]{flashcardId + "", picType}, null);
        if (cursor.moveToFirst()) {
            return MediaExchanger.getImage(context, projectId, cursor.getString(2));
        }
        cursor.close();
        return null;
    }

    public void insertMedia(long projectId, long cardId, String mediaType, String sourcePath) throws IOException {
        String mediaPath = MediaExchanger.importImage(context, projectId, cardId, mediaType, sourcePath);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID, cardId);
        contentValues.put(MediaContract.MediaEntry.COLUMN_NAME_MEDIAPATH, mediaPath);
        contentValues.put(MediaContract.MediaEntry.COLUMN_NAME_PICTYPE, mediaType);
        context.getContentResolver().insert(MediaContract.CONTENT_URI, contentValues);
    }

    public void deleteMedia(long projectId, long flashcardId) {
        ArrayList<Long> mediaIds = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaContract.CONTENT_URI, MEDIA_PROJECTION, MediaContract.MediaEntry.COLUMN_NAME_FK_F_ID + " = ? ",
                new String[]{flashcardId + ""}, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MediaExchanger.deleteImage(context, projectId, cursor.getString(2));
                mediaIds.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        for (Long l : mediaIds) {
            context.getContentResolver().delete(Uri.parse(MediaContract.CONTENT_URI + "/" + l), null, null);
        }
    }

}
