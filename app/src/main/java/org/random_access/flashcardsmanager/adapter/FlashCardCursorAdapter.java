package org.random_access.flashcardsmanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.random_access.flashcardsmanager.R;

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
        setStatusDrawable(0, imgStatus);
        txtQuestion.setText(cardTitle);
    }

    private void setStatusDrawable(int status, ImageView view) {
        // TODO: replace this fake method with a real one
        switch(status) {
            case 0:
                view.setImageResource(R.drawable.shape_circle_red);
                break;
            case 1:
                view.setImageResource(R.drawable.shape_circle_yellow);
                break;
            default:
                view.setImageResource(R.drawable.shape_circle_green);
                break;
        }
    }
}
