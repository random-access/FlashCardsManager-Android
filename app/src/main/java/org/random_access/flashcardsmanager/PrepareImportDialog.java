package org.random_access.flashcardsmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.random_access.flashcardsmanager.helpers.MyFileUtils;
import org.random_access.flashcardsmanager.xmlImport.XMLExchanger;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <b>Project:</b> FlashcardsManager for Android <br>
 * <b>Date:</b> 01.02.16 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class PrepareImportDialog extends DialogFragment {

    private static final String KEY_IMPORT_FOLDERS = "import-folders";
    private static final String KEY_IMPORT_DIR = "import-dir";
    private static final String TAG = PrepareImportDialog.class.getSimpleName();

    private Resources res;
    private ArrayList<String> projectRoots;
    private String importDir;
    private View dialogView;
    private Dialog dialog;
    private ListView lvProjects;

    public static PrepareImportDialog newInstance (ArrayList<String> importFolders, String importRootDir) {
        PrepareImportDialog d = new PrepareImportDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_IMPORT_FOLDERS, importFolders);
        bundle.putString(KEY_IMPORT_DIR, importRootDir);
        d.setArguments(bundle);
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        res = getActivity().getResources();
        projectRoots = getArguments().getStringArrayList(KEY_IMPORT_FOLDERS);
        importDir = getArguments().getString(KEY_IMPORT_DIR);
        dialogView =  getActivity().getLayoutInflater().inflate(R.layout.dialog_prepare_import, null);
        setupView();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton(res.getText(R.string.learn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean[] checkedProjectRoots = new Boolean[lvProjects.getAdapter().getCount()];
                        for (int i = 0; i < checkedProjectRoots.length; i++) {
                            checkedProjectRoots[i] = lvProjects.isItemChecked(i);
                        }
                        new ImportXMLTask().execute(checkedProjectRoots);
                    }
                })
                .setNeutralButton(res.getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "User cancelled");
                        // user cancelled
                    }
                });
        dialog = builder.create();
        return dialog;
    }

    private void setupView () {
        lvProjects = (ListView) dialogView.findViewById(R.id.list_projects);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, getDirectoryShortNames());
        lvProjects.setAdapter(adapter);
        lvProjects.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    private ArrayList<String> getDirectoryShortNames() {
        ArrayList<String> shortNames = new ArrayList<>();
        for (String s : projectRoots) {
            shortNames.add(s.substring(s.lastIndexOf('/') + 1));
        }
        return shortNames;
    }


    private class ImportXMLTask extends AsyncTask<Boolean, Void, String> {

        @Override
        protected String doInBackground(Boolean... params) {
            try {
                for (int i = 0; i < params.length; i++) {
                    if (params[i]) {
                        Log.d(TAG, "Importing " + projectRoots.get(i) + "...");
                        XMLExchanger xmlExchanger = new XMLExchanger(dialog.getContext(), projectRoots.get(i));
                        xmlExchanger.importProjects();
                    }
                }
                MyFileUtils.deleteRecursive(new File(dialog.getContext().getFilesDir().getAbsolutePath(), importDir));
                if (params.length > 0) {
                    return dialog.getContext().getResources().getString(R.string.success_import);
                } else {
                    return dialog.getContext().getResources().getString(R.string.no_import);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return dialog.getContext().getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return dialog.getContext().getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(dialog.getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
