package org.random_access.flashcardsmanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.random_access.flashcardsmanager.DisplayCardsActivity;
import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.helpers.Status;
import org.random_access.flashcardsmanager.queries.FlashCardQueries;

/**
 * Project: FlashCards Manager for Android
 * Date: 11.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class FlashCardCursorAdapter extends CursorAdapter {

    public FlashCardCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get view references
        ImageView imgStatus = (ImageView) view.findViewById(R.id.id_card_status);
        TextView txtQuestion = (TextView) view.findViewById(R.id.id_card_title);

        // get data
        String cardTitle = cursor.getString(1);

        // bind view to data
        setStatusDrawable(new FlashCardQueries(context).getFlashcardStatus(cursor.getLong(DisplayCardsActivity.COL_ID),
                cursor.getLong(DisplayCardsActivity.COL_FK_P_ID)), imgStatus);
        txtQuestion.setText(Html.fromHtml(cardTitle));
    }

    private void setStatusDrawable(Status status, ImageView view) {
        // TODO: replace this fake method with a real one
        switch(status) {
            case GREEN:
                view.setImageResource(R.drawable.shape_circle_green);
                break;
            case YELLOW:
                view.setImageResource(R.drawable.shape_circle_yellow);
                break;
            default:
                view.setImageResource(R.drawable.shape_circle_red);
                break;
        }
    }
}
