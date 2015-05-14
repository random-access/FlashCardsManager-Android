package org.random_access.flashcardsmanager.queries;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.random_access.flashcardsmanager.helpers.Status;

/**
 * Project: FlashCards Manager for Android
 * Date: 13.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class QueryHelper {

    public static int count(Context context, Uri uri,String selection,String[] selectionArgs) {
        Cursor cursor = context.getContentResolver().query(uri,new String[] {"count(*) AS count"},
                selection, selectionArgs, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return 0;
        } else {
            cursor.moveToFirst();
            int result = cursor.getInt(0);
            cursor.close();
            return result;
        }
    }

    public static Status getStatus (Cursor cursor, int maxStack) {
        Status status = null;
        if (cursor.getCount() == 0) {
            // no cards in result
            status = Status.RED;
        } else {
            // at least 1 card in result
            cursor.moveToFirst();
            if (cursor.getInt(0) == 1 && maxStack != 1) {
                // only cards in stack 1 and stack 1 is not the highest possible stack
                status = Status.RED;
            } else if (cursor.getInt(0) > 1 && cursor.getInt(0) != maxStack) {
                // we don't have any cards in the highest stack but not all cards are in stack 1
                status = Status.YELLOW;
            } else {
                // we need to check if all cards are in the highest stack
                while (!cursor.isAfterLast()) {
                    if (cursor.getInt(0) != maxStack) {
                        // we found a card which is not in the highest stack
                        status = Status.YELLOW;
                        break;
                    }
                    cursor.moveToNext();
                }
                if (status == null) {
                    status = Status.GREEN;
                }
            }
        }
        cursor.close();
        return status;
    }

}
