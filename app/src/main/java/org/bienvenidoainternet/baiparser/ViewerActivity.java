package org.bienvenidoainternet.baiparser;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.bienvenidoainternet.baiparser.structure.BoardItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ViewerActivity extends AppCompatActivity {
    private SubsamplingScaleImageView imageView;
    private GifImageView gifView;
    private BoardItem bi;
    File imagePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            bi = savedInstanceState.getParcelable("boardItem");
        }
        if (getIntent().getExtras() != null){
            bi = getIntent().getParcelableExtra("boardItem");
        }
        setContentView(R.layout.activity_viewer);
        imageView = (SubsamplingScaleImageView)findViewById(R.id.imageView);
        gifView = (GifImageView) findViewById(R.id.gifView);
        setTitle(bi.getFile());
        downloadFile();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        File baiDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Bai/");
        if (!baiDir.exists()){
            baiDir.mkdir();
        }
        if (item.getItemId() == R.id.menu_save_img){
            File to = new File(Environment.getExternalStorageDirectory().getPath() + "/Bai/" + bi.getFile());
            try{
                InputStream in = new FileInputStream(imagePath);
                OutputStream out = new FileOutputStream(to);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Toast.makeText(getApplicationContext(), bi.getFile() + " guardado.", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    private void downloadFile(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("src", Context.MODE_PRIVATE);
        final File filePath = new File(directory, bi.getParentBoard().getBoardDir() + "_" + bi.getFile());
        final ProgressBar downloadBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        if (filePath.exists()){
            downloadBar.setVisibility(View.GONE);
            if (bi.getFile().endsWith(".gif")){
                try {
                    GifDrawable gifFromFile = new GifDrawable(filePath);
                    gifView.setImageDrawable(gifFromFile);
                    imageView.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                imageView.setImage(ImageSource.uri(filePath.toURI().getPath()));
                gifView.setVisibility(View.GONE);
            }
        }
        Ion.with(getApplicationContext())
                .load("http://bienvenidoainternet.org/" + bi.getParentBoard().getBoardDir() + "/src/" + bi.getFile())
                .progressBar(downloadBar)
                .asInputStream()
                .setCallback(new FutureCallback<InputStream>() {
                    @Override
                    public void onCompleted(Exception e, InputStream result) {
                        downloadBar.setVisibility(View.GONE);
                        if (e != null){
                            e.printStackTrace();
                        }else{
                            FileOutputStream fout;
                            try {
                                fout = new FileOutputStream(filePath);
                                final byte data[] = new byte[1024];
                                int count;
                                while ((count = result.read(data, 0, 1024)) != -1) {
                                    fout.write(data, 0, count);
                                }
                            }catch(Exception e1) {
                                e1.printStackTrace();
                            }
                            if (bi.getFile().endsWith(".gif")){
                                try {
                                    GifDrawable gifFromFile = new GifDrawable(filePath);
                                    gifView.setImageDrawable(gifFromFile);
                                    imageView.setVisibility(View.GONE);
                                }catch(Exception e2){
                                    e2.printStackTrace();
                                }
                            }else{
                                imageView.setImage(ImageSource.uri(filePath.toURI().getPath()));
                                gifView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    class TaskDownloadFile extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... params) {
            Bitmap downloadedBitmap = null;
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("src", Context.MODE_PRIVATE);
            File mypath = new File(directory, bi.getParentBoard().getBoardDir() + "_" + bi.getFile());
            if (mypath.exists()){
                System.out.println("[Viewer] resource exist!");
                return mypath;
            }
            try {
                String sUrl = "http://bienvenidoainternet.org/" + bi.getParentBoard().getBoardDir() + "/src/" + bi.getFile();
                System.out.println("[Viewer]dwonloading " + sUrl);
//                System.out.println(sUrl);
                InputStream in = new java.net.URL(sUrl).openStream();
//                downloadedBitmap = BitmapFactory.decodeStream(in);

//                if (downloadedBitmap != null){
                FileOutputStream fout = null;
                try {
//                    in = new BufferedInputStream(new URL(urlString).openStream());
                     fout = new FileOutputStream(mypath);

                    final byte data[] = new byte[1024];
                    int count;
                    while ((count = in.read(data, 0, 1024)) != -1) {
                        fout.write(data, 0, count);
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if (in != null) {
                        in.close();
                    }
                    if (fout != null) {
                        fout.close();
                    }
                }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mypath;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            imagePath = file;
//            iv.setImageBitmap(bitmap);
            if (bi.getFile().endsWith(".gif")){
                try {
                    GifDrawable gifFromFile = new GifDrawable(file);
                    gifView.setImageDrawable(gifFromFile);
                    imageView.setVisibility(View.GONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                imageView.setImage(ImageSource.uri(file.toURI().getPath()));
                gifView.setVisibility(View.GONE);
//                imageView.setImage(ImageSource.resource(R.drawable.bai));
//                System.out.println("not a gif file: " + file.toURI().getPath());
            }

        }
    }
}
