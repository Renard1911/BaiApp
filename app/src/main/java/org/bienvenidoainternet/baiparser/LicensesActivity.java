package org.bienvenidoainternet.baiparser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager tm = new ThemeManager(this);
        this.setTheme(tm.getThemeForActivity());
        setContentView(R.layout.activity_licenses);
        getSupportActionBar().setTitle("Acerca de BaI App");
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/licenses.html");
    }
}
