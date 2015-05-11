package org.random_access.flashcardsmanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.storage.contracts.ProjectContract;

/**
 * Project: FlashCards Manager for Android
 * Date: 11.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class ProjectCursorAdapter extends CursorAdapter{

    public ProjectCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_project_show, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get views
        ImageView imgStatus = (ImageView) view.findViewById(R.id.id_project_status);
        TextView tvProjectTitle = (TextView) view.findViewById(R.id.id_project_title);
        ImageView imgBtnPlay = (ImageView) view.findViewById(R.id.id_project_play);

        // get data
        String projectTitle = cursor.getString(1);

        // bind data to view
        setStatusDrawable(0, imgStatus);
        tvProjectTitle.setText(projectTitle);
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
