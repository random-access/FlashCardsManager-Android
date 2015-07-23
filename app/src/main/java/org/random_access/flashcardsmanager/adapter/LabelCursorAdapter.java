package org.random_access.flashcardsmanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.random_access.flashcardsmanager.DisplayLabelsActivity;
import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.helpers.Status;
import org.random_access.flashcardsmanager.provider.contracts.LabelContract;
import org.random_access.flashcardsmanager.queries.LabelQueries;

/**
 * Project: FlashCards Manager for Android
 * Date: 12.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class LabelCursorAdapter extends CursorAdapter {

    public LabelCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_label, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get view references
        ImageView imgStatus = (ImageView) view.findViewById(R.id.id_label_status);
        TextView tvLabelTitle = (TextView) view.findViewById(R.id.id_label_text);

        // get data
        String labelTitle = cursor.getString(1);

        // bind data to view
        setStatusDrawable(new LabelQueries(context).getLabelStatus(cursor.getLong(DisplayLabelsActivity.COL_LABEL_ID),
                cursor.getLong(DisplayLabelsActivity.COL_FK_P_ID)), imgStatus);
        tvLabelTitle.setText(labelTitle);
    }

    private void setStatusDrawable(Status status , ImageView view) {
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
