package org.random_access.flashcardsmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.random_access.flashcardsmanager.xmlImport.FlashCardParser;
import org.random_access.flashcardsmanager.xmlImport.UnzipHelper;
import org.random_access.flashcardsmanager.xmlImport.XMLExchanger;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
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
public class XMLImportActivity extends AppCompatActivity {

    private static final String TAG = XMLImportActivity.class.getSimpleName();

    private static final String IMPORT_DIR = "import";

    private EditText txtUrl;
    private Button btnStartDownload;
    private TextView tvShowDownload;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_xml);
        txtUrl = (EditText) findViewById(R.id.download_url);
        btnStartDownload = (Button) findViewById(R.id.btn_start_download);
        tvShowDownload = (TextView) findViewById(R.id.tv_show_download);
        progressBar = (ProgressBar) findViewById(R.id.progress_wheel);

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
                        tvShowDownload.setText(R.string.download_url_missing);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import_xml, menu);
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

    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                loadXmlFromNetwork(urls[0]);
                XMLExchanger xmlExchanger = new XMLExchanger(XMLImportActivity.this, IMPORT_DIR);
                xmlExchanger.importProjects();
                deleteRecursive(new File(getFilesDir().getAbsolutePath(), IMPORT_DIR));
                return getResources().getString(R.string.finished);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            tvShowDownload.setVisibility(View.VISIBLE);
            tvShowDownload.setText(result);
        }
    }

    private void loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        try {
            stream = downloadUrl(urlString);
            UnzipHelper.unzip(stream, getFilesDir().getAbsolutePath() + "/" + IMPORT_DIR, XMLImportActivity.this);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private boolean deleteRecursive(File path) {
        boolean success = true;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                success &= deleteRecursive(f);
            }
        }  else {
            success &= path.delete();
        }
        return success;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }


}
