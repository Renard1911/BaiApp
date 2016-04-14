package org.bienvenidoainternet.baiparser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.bienvenidoainternet.baiparser.structure.Board;
import org.bienvenidoainternet.baiparser.structure.BoardItem;
import org.w3c.dom.Document;

import java.io.File;

import utils.ContentProviderUtils;


public class ResponseActivity extends AppCompatActivity {

    private BoardItem theReply = null;
    private SharedPreferences  settings;
    private String password;
    private String selectedFile = "";
    private final int PICK_IMAGE = 1;
    private boolean quoting = false;
    EditText filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager tm = new ThemeManager(this);
        this.setTheme(tm.getThemeForActivity());
        setContentView(R.layout.activity_response);
        getSupportActionBar().setTitle("Respondiendo");
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        password = settings.getString("pref_password", "12345678");

        if (savedInstanceState != null){
            this.theReply = savedInstanceState.getParcelable("theReply");
            this.quoting = savedInstanceState.getBoolean("quoting");
        }
        if (getIntent().getExtras() != null){
            this.theReply = getIntent().getParcelableExtra("theReply");
            this.quoting = getIntent().getBooleanExtra("quoting", false);
        }
        if (theReply != null && quoting){
            TextView txtMessage = (TextView) findViewById(R.id.txtResponse);
            if (theReply.getParentBoard().getBoardType() == 1){ // BBS
                txtMessage.setText(">>" + theReply.getBbsId());
            }else{
                txtMessage.setText(">>" + theReply.getId());
            }
        }

        LinearLayout layoutProcess = (LinearLayout)findViewById(R.id.layoutPostProcess);
        layoutProcess.setVisibility(View.GONE);
        filePath = (EditText) findViewById(R.id.txtFilePath);

        Button bBold = (Button) findViewById(R.id.buttonBold);
        bBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtMessage = (TextView) findViewById(R.id.txtResponse);
                if (txtMessage.getSelectionStart() == -1){
                    txtMessage.setText(txtMessage.getText() + "<b></b>");
                }else{
                    String s = txtMessage.getText().toString();
                    String a = s.substring(0, txtMessage.getSelectionStart());
                    String b = s.substring(txtMessage.getSelectionStart(), txtMessage.getSelectionEnd());
                    String c = s.substring(txtMessage.getSelectionEnd(), txtMessage.getText().length());
                    txtMessage.setText(a + "<b>" + b + "</b>" + c);
                }
            }
        });
        Button bItalic = (Button) findViewById(R.id.buttonItalic);
        bItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtMessage = (TextView) findViewById(R.id.txtResponse);
                if (txtMessage.getSelectionStart() == -1){
                    txtMessage.setText(txtMessage.getText() + "<i></i>");
                }else{
                    String s = txtMessage.getText().toString();
                    String a = s.substring(0, txtMessage.getSelectionStart());
                    String b = s.substring(txtMessage.getSelectionStart(), txtMessage.getSelectionEnd());
                    String c = s.substring(txtMessage.getSelectionEnd(), txtMessage.getText().length());
                    txtMessage.setText(a + "<i>" + b + "</i>" + c);
                }
            }
        });

        Button select = (Button) findViewById(R.id.btnSelectFiles);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar Archivo"), PICK_IMAGE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_replyform, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_form_reply){
            TextView txtName = (TextView) findViewById(R.id.txtPosterName);
            TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
            TextView txtMessage = (TextView) findViewById(R.id.txtResponse);
            makePost(txtName.getText().toString(), txtEmail.getText().toString(), txtMessage.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String picturePath = ContentProviderUtils.getPath(getApplicationContext(), selectedImage);
            selectedFile = picturePath;
            filePath.setText(picturePath);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void makePost(String name, String email, String message){
        int parentId = theReply.getParentId();
        if (theReply.getParentId() == 0 || theReply.getParentId() == -1){
            parentId = theReply.getId();
        }
        LinearLayout layoutProcess = (LinearLayout)findViewById(R.id.layoutPostProcess);
        layoutProcess.setVisibility(View.VISIBLE);
        final RelativeLayout formSendPost = (RelativeLayout) findViewById(R.id.layoutForm);
        formSendPost.setVisibility(View.GONE);
        ProgressBar progess = (ProgressBar) findViewById(R.id.barPosting);
        final TextView err = (TextView)findViewById(R.id.txtPostingState);
        err.setText("");
        File up = new File(selectedFile);
        if (selectedFile.isEmpty()) Ion.with(getApplicationContext())
                .load("http://bienvenidoainternet.org/cgi/post")
                .setLogging("posting", Log.VERBOSE)
                .uploadProgressBar(progess)
                .setMultipartParameter("board", theReply.getParentBoard().getBoardDir())
                .setMultipartParameter("parent", String.valueOf(theReply.realParentId()))
                .setMultipartParameter("password", password)
                .setMultipartParameter("fielda", name)
                .setMultipartParameter("fieldb", email)
                .setMultipartParameter("name", "")
                .setMultipartParameter("email", "")
                .setMultipartParameter("message", message)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.v("sendPost", result);
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Ha ocurrido un error! ;_;", Toast.LENGTH_LONG).show();
                            formSendPost.setVisibility(View.VISIBLE);
                            err.setText("Error: " + e.getMessage());
                            e.printStackTrace();
                        } else {
                            Toast.makeText(getApplicationContext(), "Post enviado", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
        else{
            Ion.with(getApplicationContext())
                    .load("http://bienvenidoainternet.org/cgi/post")
                    .uploadProgressBar(progess)
                    .setMultipartParameter("board", theReply.getParentBoard().getBoardDir())
                    .setMultipartParameter("parent", String.valueOf(parentId))
                    .setMultipartParameter("password", password)
                    .setMultipartParameter("fielda", name)
                    .setMultipartParameter("fieldb", email)
                    .setMultipartParameter("name", "")
                    .setMultipartParameter("email", "")
                    .setMultipartParameter("message", message)
                    .setMultipartFile("file", up)
                    .asDocument()
                    .setCallback(new FutureCallback<Document>() {
                        @Override
                        public void onCompleted(Exception e, Document result) {
                            if (e != null){
                                Toast.makeText(getApplicationContext(), "Ha ocurrido un error! ;_;", Toast.LENGTH_LONG).show();
                                formSendPost.setVisibility(View.VISIBLE);
                                err.setText("Error: " + e.getMessage());
                                e.printStackTrace();
                            }else{
                                Toast.makeText(getApplicationContext(), "Post enviado", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
        }
    }
}
