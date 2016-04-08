package org.bienvenidoainternet.baiparser;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.bienvenidoainternet.baiparser.structure.BoardItem;
import org.bienvenidoainternet.baiparser.structure.BoardItemFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import layout.FragmentImage;
import pl.droidsonroids.gif.GifImageView;

public class ViewerActivity extends AppCompatActivity implements FragmentImage.OnFragmentInteractionListener {
    private static final String EXTRA_FILELIST = "fileList", EXTRA_RELATIVEPOSITION = "position";
    private ViewPager imagePager;
    public ProgressBar barDownload;
    private int relativePosition = 0;

    public ArrayList<BoardItemFile> fileList = new ArrayList<BoardItemFile>();
    private CustomFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null){
            fileList = getIntent().getParcelableArrayListExtra(EXTRA_FILELIST);
            relativePosition = getIntent().getIntExtra(EXTRA_RELATIVEPOSITION, 0);
        }
        setContentView(R.layout.activity_viewer);
        barDownload = (ProgressBar) findViewById(R.id.downloadProgressBar);
        imagePager = (ViewPager) findViewById(R.id.imagePager);
        this.pagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < fileList.size(); i++){
            Log.v("ImageViewer", fileList.get(i).toString());
            pagerAdapter.addFragment(FragmentImage.newInstance(fileList.get(i)));
        }
        imagePager.setAdapter(pagerAdapter);
        imagePager.setCurrentItem(relativePosition);
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSubtitle(fileList.get(position).file);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        File baiDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Bai/");
        if (!baiDir.exists()){
            baiDir.mkdir();
        }
        if (item.getItemId() == R.id.menu_save_img){
            BoardItemFile boardItemFile = fileList.get(imagePager.getCurrentItem());
            File to = new File(Environment.getExternalStorageDirectory().getPath() + "/Bai/" + boardItemFile.file);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("src", Context.MODE_PRIVATE);
            File fileSource = new File(directory, boardItemFile.boardDir + "_" + boardItemFile.file);
            try{
                InputStream in = new FileInputStream(fileSource);
                OutputStream out = new FileOutputStream(to);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Toast.makeText(getApplicationContext(), boardItemFile.file + " guardado.", Toast.LENGTH_LONG).show();
                MediaScannerConnection.scanFile(this, new String[]{to.getPath()}, new String[]{"image/jpeg"}, null);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
