package org.bienvenidoainternet.baiparser;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UpdaterActivity extends AppCompatActivity {
    private float lastVersion = 1.0F;
    Button btnUpdate;
    ProgressBar barUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        setTheme(R.style.AppTheme_Black_Activity);
        btnUpdate = (Button) findViewById(R.id.btnDownloadLastVersion);
        barUpdate = (ProgressBar) findViewById(R.id.barUpdateProgress);
        TextView txtCurrentVersion = (TextView) findViewById(R.id.txtCurrentVersion);
        btnUpdate.setEnabled(false);
        txtCurrentVersion.setText("Versión actual: " + MainActivity.CURRENT_VERSION);
        getVersionData();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadApk();
            }
        });
    }

    private void getVersionData(){
        Ion.with(getApplicationContext())
                .load("http://ahri.xyz/bai/version.php")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            JSONObject version = null;
                            try {
                                version = new JSONObject(result);
                                lastVersion = (float) version.getDouble("version");
                                TextView txtLastVersion = (TextView) findViewById(R.id.txtLastVersion);
                                txtLastVersion.setText("Última versión: " + lastVersion);

                                if (lastVersion > MainActivity.CURRENT_VERSION) {
                                    getChangelog();
                                    btnUpdate.setEnabled(true);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void getChangelog(){
        Ion.with(getApplicationContext())
                .load("http://ahri.xyz/bai/lastChangelog.txt")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null){
                            e.printStackTrace();
                        }else{
                            TextView txtChangelog = (TextView) findViewById(R.id.txtChangelog);
                            txtChangelog.setText(Html.fromHtml(result));
                        }
                    }
                });
    }

    private void downloadApk(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("src", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        final File filePath = new File(directory, "last.apk");
        if (filePath.exists()) {
            filePath.delete();
        }
        Ion.with(getApplicationContext())
                .load("http://ahri.xyz/bai/" + lastVersion + "/last.apk")
                .setLogging("Updater", Log.VERBOSE)
                .progressBar(barUpdate)
                .write(filePath)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                    .setDataAndType(Uri.fromFile(filePath),
                                            "application/vnd.android.package-archive");
                            startActivity(promptInstall);
                        }
                    }
                });
    }
}


