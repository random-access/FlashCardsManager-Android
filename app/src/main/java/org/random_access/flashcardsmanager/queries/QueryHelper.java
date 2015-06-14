package org.random_access.flashcardsmanager.queries;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.random_access.flashcardsmanager.helpers.Status;
import org.random_access.flashcardsmanager.provider.contracts.FlashCardContract;
import org.random_access.flashcardsmanager.provider.contracts.LFRelationContract;

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

    public static String[] buildFlashcardFilterArgumentString(long projectId, int[] stacks, long[] labelIds) {
        String[] result = new String [labelIds.length + stacks.length + 1];
        result[0] = Long.toString(projectId);
        for (int i = 0; i < stacks.length; i++) {
            result[1+i] = Integer.toString(stacks[i]);
        }
        for (int i = 0; i < labelIds.length; i++) {
            result[1+stacks.length+i] = Long.toString(labelIds[i]);
        }
        return  result;
    }

    public static String buildFlashcardFilterWhereString(int labelCount, int stacksCount, String conjunction) {
        if (labelCount == 0 && stacksCount == 0) {
            return FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID + " = ? ";
        }
        StringBuilder sb = new StringBuilder();

        // add project constraint
        sb.append(FlashCardContract.FlashCardEntry.COLUMN_NAME_FK_P_ID).append(" = ? AND ");

        // add stacks constraints
        if (stacksCount > 1) sb.append("(");
        for (int i = 0; i < stacksCount; i++) {
            sb.append(FlashCardContract.FlashCardEntry.COLUMN_NAME_STACK).append(" = ?  OR ");
        }
        if (stacksCount > 0) sb.replace(sb.length() - 3, sb.length(), "");
        if (stacksCount > 1) sb.append(")");

        // add conjunction (AND or OR)
        if (labelCount > 0 && stacksCount > 0) {
            sb.append(" ").append(conjunction).append(" ");
        }

        // add label constraints
        if (labelCount > 1) sb.append("(");
        for (int i = 0; i < labelCount; i++) {
            sb.append(LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID).append(" = ? OR ");
        }
        if (labelCount > 0)  sb.replace(sb.length() - 3, sb.length(), "");
        if (labelCount > 1)  sb.append(")");
        return sb.toString();
    }

}
