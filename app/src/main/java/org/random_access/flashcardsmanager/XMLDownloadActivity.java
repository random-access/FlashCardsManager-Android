package org.random_access.flashcardsmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.random_access.flashcardsmanager.helpers.MyFileUtils;
import org.random_access.flashcardsmanager.xmlImport.ProjectRootFinder;
import org.random_access.flashcardsmanager.xmlImport.UnzipHelper;
import org.random_access.flashcardsmanager.xmlImport.XMLExchanger;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 11.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class XMLDownloadActivity extends AppCompatActivity {

    private static final String TAG = XMLDownloadActivity.class.getSimpleName();
    private static final String TAG_PROJECT_IMPORT = "project-import";

    private static final String IMPORT_DIR = "import";

    private EditText txtUrl;
    private Button btnStartDownload;
    private TextView tvShowDownload;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_xml);
        getViewElems();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download_xml, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getViewElems() {
        txtUrl = (EditText) findViewById(R.id.download_url);
        btnStartDownload = (Button) findViewById(R.id.btn_start_download);
        tvShowDownload = (TextView) findViewById(R.id.tv_show_download);
        progressBar = (ProgressBar) findViewById(R.id.progress_wheel);
    }

    private void setListeners() {
        btnStartDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl = txtUrl.getText().toString();
                if (isOnline() && !TextUtils.isEmpty(downloadUrl)) {
                    tvShowDownload.setVisibility(View.GONE);
                    new DownloadXmlTask().execute(downloadUrl);
                } else {
                    tvShowDownload.setVisibility(View.VISIBLE);
                    if (!isOnline()) {
                        tvShowDownload.setText(getResources().getString(R.string.network_error));
                    } else {
                        tvShowDownload.setText(R.string.download_link_missing);
                    }
                }
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, ArrayList<String>> {

        private Exception exc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            try {
                loadXmlFromNetwork(urls[0]);
                return new ProjectRootFinder(getFilesDir().getAbsolutePath() + "/" + IMPORT_DIR).findProjectRootDirs();
            } catch (IOException e) {
                exc = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (exc != null) {
                exc.printStackTrace();
                tvShowDownload.setText(getResources().getString(R.string.connection_error));
            } else {
                progressBar.setVisibility(View.GONE);
                tvShowDownload.setVisibility(View.VISIBLE);
                tvShowDownload.setText(getResources().getString(R.string.success_download));
                PrepareImportDialog d = PrepareImportDialog.newInstance(result, IMPORT_DIR);
                d.show(getFragmentManager(), TAG_PROJECT_IMPORT);
            }
        }
    }

    private void loadXmlFromNetwork(String urlString) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = downloadUrl(urlString);
            InputStream stream = conn.getInputStream();
            UnzipHelper.unzip(stream, getFilesDir().getAbsolutePath() + "/" + IMPORT_DIR, XMLDownloadActivity.this);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private HttpURLConnection downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn;
    }


}
