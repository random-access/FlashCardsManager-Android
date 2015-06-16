package org.random_access.flashcardsmanager.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ClipDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.random_access.flashcardsmanager.DisplayLabelsActivity;
import org.random_access.flashcardsmanager.DisplayProjectsActivity;
import org.random_access.flashcardsmanager.PrepareLearningDialog;
import org.random_access.flashcardsmanager.ProjectDialogFragment;
import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.queries.ProjectQueries;
import org.random_access.flashcardsmanager.helpers.Status;
import org.random_access.flashcardsmanager.xmlImport.ProjectParser;


/**
 * Project: FlashCards Manager for Android
 * Date: 11.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class ProjectCursorAdapter extends CursorAdapter{

    private static final String TAG = ProjectCursorAdapter.class.getSimpleName();
    private static final String TAG_EDIT_PROJECT = "edit-project";
    private static final String TAG_PREPARE_LEARNING = "prepare-learning";

    private static final int COLLAPSED = 0;
    private static final int EXTENDED = 1;

    private int mCurrentDetailPosition = -1;

    private AlertDialog deleteDialog;
    private Context context;

    public ProjectCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mCurrentDetailPosition) ? EXTENDED : COLLAPSED; // call notifyDataSetChanged when you modify isListSingleColumn
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int position = cursor.getPosition();
        final int type = getItemViewType(position);
        if (type == COLLAPSED) {
            return LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        } else {
            return LayoutInflater.from(context).inflate(R.layout.item_project_extended, parent,false);
        }
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Resources res = context.getResources();
        final int position = cursor.getPosition();
        final int type = getItemViewType(position);
        if (type == COLLAPSED) {
            // get views
            ImageView imgStatus = (ImageView) view.findViewById(R.id.id_project_status);
            TextView tvProjectTitle = (TextView) view.findViewById(R.id.id_project_title);

            // get data
            long projectId = cursor.getLong(0);
            String projectTitle = cursor.getString(1);
            int projectStacks = cursor.getInt(3);

            // bind data to view
            setStatusDrawable(new ProjectQueries(context).getProjectStatus(projectId, projectStacks), imgStatus);
            tvProjectTitle.setText(projectTitle);
        } else {
            // get views
            TextView tvProjectTitle = (TextView) view.findViewById(R.id.p_ext_title);
            ImageView imgProgress = (ImageView) view.findViewById(R.id.image_progress);
            ClipDrawable progressClip = (ClipDrawable) imgProgress.getBackground();
            TextView tvProjectDescription = (TextView) view.findViewById(R.id.p_ext_description);
            TextView tvStackInfo = (TextView) view.findViewById(R.id.text_stackinfo);
            TextView tvCardInfo = (TextView) view.findViewById(R.id.text_cardinfo);
            ImageButton btnEdit = (ImageButton) view.findViewById(R.id.btn_edit);
            ImageButton btnOpen = (ImageButton) view.findViewById(R.id.btn_open);
            ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btn_delete);
            ImageButton btnLearn = (ImageButton) view.findViewById(R.id.btn_learn);

            // get data
            final long projectId = cursor.getLong(0);
            String projectTitle = cursor.getString(1);
            String projectDescription = cursor.getString(2);
            int projectStacks = cursor.getInt(3);

            ProjectQueries query = new ProjectQueries(context);
            tvProjectTitle.setText(projectTitle);
            tvProjectDescription.setText(projectDescription);
            tvStackInfo.setText(res.getString(R.string.stacks_plural) + ": " + projectStacks);
            int noOfCardsTotal = query.getFlashcardCount(projectId);
            int noOfCardsCompleted  = query.getCompletedCardCount(projectId, projectStacks);
            tvCardInfo.setText(res.getString(R.string.cards) + ": " + noOfCardsTotal);
            Log.d(TAG, "no of cards completed: " + noOfCardsCompleted + ", noOfCardsTotal: " + noOfCardsTotal);
            progressClip.setLevel((int) (((double) noOfCardsCompleted / noOfCardsTotal) * 10000));
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Project id: " + projectId);
                    ProjectDialogFragment addProjectFragment = ProjectDialogFragment.newInstance(false, projectId);
                    addProjectFragment.show(getFragmentManager(context), TAG_EDIT_PROJECT);
                }
            });
            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(context, DisplayLabelsActivity.class);
                intent.putExtra(DisplayProjectsActivity.TAG_PROJECT_ID, projectId);
                context.startActivity(intent);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteSelectedProjects(context, new long[]{projectId});
                }
            });
            btnLearn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Learn function - not yet implemented..", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Open prepare learning dialog");
                    PrepareLearningDialog d = PrepareLearningDialog.newInstance(projectId);
                    d.show(getFragmentManager(context), TAG_PREPARE_LEARNING);
                }
            });

        }
    }


    private void setStatusDrawable(Status status, ImageView view) {
        switch(status) {
            case RED:
                view.setImageResource(R.drawable.shape_circle_red);
                break;
            case YELLOW:
                view.setImageResource(R.drawable.shape_circle_yellow);
                break;
            case GREEN:
                view.setImageResource(R.drawable.shape_circle_green);
                break;
            default:
                Log.d(TAG, "Status " + status.toString() + " doesn't exist");
        }
    }

    public int getmCurrentDetailPosition() {
        return mCurrentDetailPosition;
    }

    public void setmCurrentDetailPosition(int mCurrentDetailPosition) {
        this.mCurrentDetailPosition = mCurrentDetailPosition;
        notifyDataSetChanged();
    }

    private FragmentManager getFragmentManager(Context context) {
        Activity activity = null;
        try{
            activity = (Activity) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "Can't get fragment manager from a non-activity context");
        }
        return activity.getFragmentManager();
    }

    public void deleteSelectedProjects(Context context, long[] projectIds) {
        this.context = context;
        OnDeleteProjectsDialogListener dialogClickListener = new OnDeleteProjectsDialogListener(context, projectIds);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setNeutralButton(context.getResources().getString(R.string.no), dialogClickListener)
                .setPositiveButton(context.getResources().getString(R.string.yes), dialogClickListener)
                .setTitle(context.getResources().getString(R.string.delete))
                .setMessage(context.getResources().getQuantityString(R.plurals.really_delete_project, projectIds.length, projectIds.length))
                .setCancelable(false);
        deleteDialog = builder.show();
    }

    class OnDeleteProjectsDialogListener implements DialogInterface.OnClickListener {


        long[] projectIds;

        OnDeleteProjectsDialogListener(Context context, long[] projectId) {
            this.projectIds = projectId;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    new DeleteProjectsTask().execute(projectIds);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    // user cancelled
                    break;
            }
        }
    }

    class DeleteProjectsTask extends AsyncTask<long[], Void, Integer> {
        ProgressDialog d;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = ProgressDialog.show(context, context.getString(R.string.delete), context.getString(R.string.please_wait));
        }

        @Override
        protected Integer doInBackground(long[]... projectIds) {
            ProjectQueries projectQueries = new ProjectQueries(context);
            for (long l : projectIds[0]) {
                projectQueries.deleteProjectWithId(l);
            }
            return projectIds[0].length;
        }

        @Override
        protected void onPostExecute(Integer numberOfProjectsDeleted) {
            super.onPostExecute(numberOfProjectsDeleted);
            d.dismiss();
            Toast.makeText(context, context.getResources().
                    getQuantityString(R.plurals.deleted_project, numberOfProjectsDeleted, numberOfProjectsDeleted), Toast.LENGTH_SHORT).show();
        }
    }

}
