package org.random_access.flashcardsmanager.provider.contracts;

import android.net.Uri;

import org.random_access.flashcardsmanager.provider.FlashCardsProvider;

/**
 * Project: FlashCards Manager for Android
 * Date: 13.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class DbJoins {

    public static final String NAME_FLASHCARDS_JOIN_LFRELS = "FLASHCARDS+LFRELS";
    public static final Uri CONTENT_URI_FLASHCARDS_JOIN_LFRELS =  Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + NAME_FLASHCARDS_JOIN_LFRELS);
    public static final String TABLES_FLASHCARDS_JOIN_LFRELS = FlashCardContract.FlashCardEntry.TABLE_NAME + " inner join " + LFRelationContract.LFRelEntry.TABLE_NAME
            + " on " + FlashCardContract.FlashCardEntry.COLUMN_NAME_ID_FULLNAME + " = " + LFRelationContract.LFRelEntry.COLUMN_NAME_FK_F_ID_FULLNAME;

    public static final String NAME_LABELS_JOIN_LFRELS = "LABELS+LFRELS";
    public static final Uri CONTENT_URI_LABELS_JOIN_LFRELS = Uri.parse("content://" + FlashCardsProvider.AUTHORITY + "/" + NAME_LABELS_JOIN_LFRELS);
    public static final String TABLES_LABELS_JOIN_LFRELS = LabelContract.LabelEntry.TABLE_NAME + " inner join " + LFRelationContract.LFRelEntry.TABLE_NAME
            + " on " + LabelContract.LabelEntry.COLUMN_NAME_ID_FULLNAME + " = " + LFRelationContract.LFRelEntry.COLUMN_NAME_FK_L_ID_FULLNAME;
}
