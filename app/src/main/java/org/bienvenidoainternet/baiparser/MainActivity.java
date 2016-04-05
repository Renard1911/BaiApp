package org.bienvenidoainternet.baiparser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.bienvenidoainternet.baiparser.structure.Board;
import org.bienvenidoainternet.baiparser.structure.BoardItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import layout.fragmentThreadList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, fragmentThreadList.OnFragmentInteractionListener {

    public static final float CURRENT_VERSION = 1.5F;
    private ViewPager pager; // variable del ViewPager
    CustomFragmentPagerAdapter pagerAdapter; // Adaptador del ViewPager
    NavigationView navigationView;
    DrawerLayout drawer;
    FloatingActionButton fab;
    public ThemeManager themeManager;
    fragmentThreadList childFragment; // Segunda página del ViewPager, se muestra un solo hilo (selecionado del catálogo)
    fragmentThreadList mainFragment; // Primera página del ViewPager, se muestra una lista de hilos. (catálogo)
//    fragmentThreadList recentFragment;
    Toolbar toolbar = null;
    public int currentThemeId = 0, themeId = 0; // Id del recurso, Id del tema
    public ArrayList<Board> boardList = new ArrayList<>();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentThemeId", currentThemeId);
        outState.putInt("themeId", themeId);
        outState.putParcelableArrayList("boardList", boardList);
        if (getSupportFragmentManager().getFragments() != null) {
            if (getSupportFragmentManager().getFragments().size() != 0) {
                try {
                    getSupportFragmentManager().putFragment(outState, "mainFragment", mainFragment);
                    getSupportFragmentManager().putFragment(outState, "childFragment", childFragment);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public int getCurrentThemeId() {
        return currentThemeId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentThemeId = savedInstanceState.getInt("currentThemeId");
            boardList = savedInstanceState.getParcelableArrayList("boardList");
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        themeId = Integer.valueOf(settings.getString("pref_theme", "1"));

        if (settings.getString("pref_password", "").isEmpty()){
            SharedPreferences.Editor edit = settings.edit();
            edit.putString("pref_password", makePassword());
            edit.commit();
        }

        switch (themeId) {
            case 1:
                currentThemeId = R.style.AppTheme_NoActionBar;
                break;
            case 2:
                currentThemeId = R.style.AppTheme_Dark;
                break;
            case 3:
                currentThemeId = R.style.AppTheme_HeadLine;
                setTheme(R.style.AppTheme_HeadLine_Activity);
                break;
            case 4:
                currentThemeId = R.style.AppTheme_Black;
                setTheme(R.style.AppTheme_Black_Activity);
                break;
        }

        themeManager = new ThemeManager(this);
        Log.d("ThemeManager", "isDarkTheme: " + themeManager.isDarkTheme());

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bievenido a internet");
        this.setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (childFragment.currentBoard != null) {
                    if (!childFragment.boardItems.isEmpty()) {
                        try {
                            Intent in = new Intent(getApplicationContext(), ResponseActivity.class);
                            Bundle b = new Bundle();
                            b.putParcelable("theReply", childFragment.boardItems.get(0));
                            b.putParcelable("theBoard", childFragment.currentBoard);
                            in.putExtras(b);
                            startActivity(in);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        fab.setVisibility(View.GONE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            mainFragment = (fragmentThreadList) getSupportFragmentManager().getFragment(savedInstanceState, "mainFragment");
            childFragment = (fragmentThreadList) getSupportFragmentManager().getFragment(savedInstanceState, "childFragment");
//            recentFragment = (fragmentThreadList) getSupportFragmentManager().getFragment(savedInstanceState, "recentFragment");
        } else {
            mainFragment = fragmentThreadList.newInstance(true, null, null);
            childFragment = fragmentThreadList.newInstance(false, null, null);
//            recentFragment = fragmentThreadList.newInstance(false, null, -1);
        }

        this.pager = (ViewPager) findViewById(R.id.pager);
        this.pagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(mainFragment);
        pagerAdapter.addFragment(childFragment);
//        pagerAdapter.addFragment(recentFragment);
        this.pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    if (mainFragment.currentBoard != null) {
                        toolbar.setTitle("Catálogo");
                        toolbar.setSubtitle(mainFragment.currentBoard.getBoardName());
                    }
                    if (mainFragment.getMode()){
                        toolbar.setTitle("Post recientes");
                        toolbar.setSubtitle("");
                    }
                    fab.setVisibility(View.INVISIBLE);
                }else if (position == 1){
                    if (childFragment.currentBoard != null) {
                        toolbar.setTitle(childFragment.currentBoard.getBoardName());
                        if (!childFragment.boardItems.isEmpty()){
                            toolbar.setSubtitle(childFragment.boardItems.get(0).getSubject());
                        }
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (boardList.isEmpty()){
            getBoardList();
        }else{
            Menu menu = navigationView.getMenu();
            SubMenu sub = menu.addSubMenu("Lista de Boards");
            for (Board b : boardList) {
                sub.add(b.getBoardName());
            }
            refreshNavigator();
        }

        // TODO: Aplicar tema al navigator
//        navigationView.setBackgroundColor(themeManager.getPrimaryDarkColor());
        // TODO: Arreglar el servidor
//        checkForUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                boolean result = data.getBooleanExtra("result", false);
                if (result){
                    this.recreate();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_exit:
                System.exit(0);
                break;
            case R.id.action_refresh:
                if (pager.getCurrentItem() == 0) {
                    mainFragment.refresh();
                } else {
                    childFragment.refresh();
                }
                if (boardList.isEmpty()){
                    getBoardList();
                }
                break;
            case R.id.action_settings:
                Intent in2 = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(in2, 1);
                break;
            case R.id.action_to_bot:
                if (pager.getCurrentItem() == 0) {
                    mainFragment.scrollToBotton();
                } else {
                    childFragment.scrollToBotton();
                }
                break;
            case R.id.action_to_top:
                if (pager.getCurrentItem() == 0) {
                    mainFragment.scrollToTop();
                } else {
                    childFragment.scrollToTop();
                }
                break;
            case R.id.action_update:
                Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_SHORT).show();
//                Intent updater = new Intent(getApplicationContext(), UpdaterActivity.class);
//                startActivity(updater);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        toolbar.setSubtitle(item.getTitle());
        if (id == R.id.nav_recent_post){
            toolbar.setTitle("Post recientes");
            toolbar.setSubtitle("");
            pager.setCurrentItem(0);
            mainFragment.loadRecentPost();
        }
        for (Board b : boardList){
            if (b.getBoardName() == item.getTitle()){
                System.out.println("Updating mainfragment to " + b.getBoardName() + " d: " + b.getBoardDir());
                mainFragment.setCatalogMode();
                mainFragment.updateBoardItems(b, null);
                pager.setCurrentItem(0);
                navigationView.getMenu().findItem(R.id.nav_recent_post).setChecked(false);
            }
        }
        return true;
    }

    public Board getBoardFromDir(String dir){
        for (Board b : boardList){
            if (b.getBoardDir().equals(dir)){
                return b;
            }
        }
        System.out.println("[MainActivity] Board not found " + dir);
        return null;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void showThread(Board board, BoardItem thread) {
        childFragment.updateBoardItems(board, thread);
        pager.setCurrentItem(1);
    }


    @Override
    public void updateToolbar(Board cBoard, BoardItem btem) {
        if (pager.getCurrentItem() == 1){
            toolbar.setTitle(cBoard.getBoardName());
            toolbar.setSubtitle(btem.getSubject());
        }
    }

    @Override
    public void updateToolbar(String s) {
        toolbar.setTitle(s);
        toolbar.setSubtitle("");
    }

    @Override
    public void hideActionButton() {
        if (pager.getCurrentItem() == 1){
            fab.hide();
        }
    }

    @Override
    public void showActionButton() {
        if (pager.getCurrentItem() == 1){
            fab.show();
        }
    }

    private void getBoardList(){
        Menu menu = navigationView.getMenu();
        final SubMenu sub = menu.addSubMenu("Lista de Boards");
        Ion.with(getApplicationContext())
                .load("http://bienvenidoainternet.org/cgi/api/boards")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            JsonArray boards = result.get("boards").getAsJsonArray();
                            for (int i = 0; i < boards.size(); i++) {
                                try {
                                    JSONObject board = new JSONObject(boards.get(i).toString());
                                    Board parsedBoard = new Board(board.getString("name"), board.getString("dir"), board.getInt("board_type"));
                                    sub.add(parsedBoard.getBoardName());
                                    boardList.add(parsedBoard);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Error parsing JSON", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
        refreshNavigator();
    }

    public void refreshNavigator(){
        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
            final View child = navigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }
    /*
            Crea una secuencia de caracteres de 8 digitos aleatorios (incluye mayusculas, minisculas y numeros).
    */

    public String makePassword(){
        Random r = new Random();
        String rnd = "";
        for (int i = 0; i < 8; i++){
            int a = r.nextInt(3);
            char b;
            if (a == 0){
                b = (char)(66 + r.nextInt(25));
            }else if (a == 1){
                b = (char)(97 + r.nextInt(25));
            }else{
                b = (char) (48 + r.nextInt(9));
            }
            rnd = rnd + b;
        }
        return rnd;
    }

    public void checkForUpdates(){
        Ion.with(getApplicationContext())
                .load("http://ahri.xyz/bai/version.php")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null){
                            e.printStackTrace();
                        }else{
                            try {
                                JSONObject version = new JSONObject(result);
                                float lastVersion = (float) version.getDouble("version");
                                if (CURRENT_VERSION == lastVersion){
                                    Log.v("Updater", "Up to date");
                                }else{
                                    Log.v("Updater", "New version available : " + lastVersion);
                                    Snackbar.make(getCurrentFocus(), "Nueva versión disponible", Snackbar.LENGTH_LONG)
                                            .setAction("Actualizar", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent updater = new Intent(getApplicationContext(), UpdaterActivity.class);
                                                    startActivity(updater);
                                                }
                                            })
                                            .setActionTextColor(Color.rgb(255,127,0))
                                            .show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
}