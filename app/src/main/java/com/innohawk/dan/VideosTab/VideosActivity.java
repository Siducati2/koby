package com.innohawk.dan.VideosTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activityDropMenu.ActivityDropMenu;
import com.innohawk.dan.activitymaps.ActivityMapsDispensaries;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.friends.FriendsActivity;
import com.innohawk.dan.friends.FriendsHomeActivity;
import com.innohawk.dan.helps.HelpActivity;
import com.innohawk.dan.location.LocationActivity;
import com.innohawk.dan.mail.MailComposeActivity;
import com.innohawk.dan.mail.MailHomeActivity;
import com.innohawk.dan.media.AddImageActivity;
import com.innohawk.dan.media.AddVideoActivity;
import com.innohawk.dan.media.ImagesAlbumsActivity;
import com.innohawk.dan.media.ImagesGallery;
import com.innohawk.dan.media.SoundsAlbumsActivity;
import com.innohawk.dan.media.SoundsFilesActivity;
import com.innohawk.dan.media.VideosAlbumsActivity;
import com.innohawk.dan.media.VideosFilesActivity;
import com.innohawk.dan.profile.ProfileActivity;
import com.innohawk.dan.profile.ProfileMyInfoActivity;
import com.innohawk.dan.search.SearchHeaderActivity;
import com.innohawk.dan.search.SearchHomeActivity;
import com.innohawk.dan.sqlite.SQLiteActivity;
import com.innohawk.dan.status.StatusMessageActivity;

/**
 * Created by innohawk on 9/10/17.: Is the new videos
 */

public class VideosActivity extends ActivityBase {
    private static final String TAG = "OO TimeLineAct";
    private WebView webView;
    String isGuest;
    Button goMyVideos,goAddVideo;
    //Custom Header
    private SQLiteActivity db; //logout
    int flag_menu;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Button btn_menu,btn_menu_close;
    EditText edit_search;
    String search = " ";
    Button goStore;
    RelativeLayout CloseDropMenu,CloseDropMenuBg;
    //Normal Header
    protected TextView textTitle;
    Button reload;
    Button Back;
    Typeface tt;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.tabvideos);
        //textTitle = (TextView) findViewById(R.id.textTitle);
        //textTitle.setText(R.string.TabVideo);
        //Fx para saber si es guest!
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);
        //Config header
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        initconfigheader();
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.TabNewsFeed);
        textTitle.setTypeface(tt);
        reload = (Button) findViewById(R.id.buttonRefresh);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });
        goMyVideos = (Button) findViewById(R.id.buttonMy);
        goAddVideo = (Button) findViewById(R.id.buttonAdd);
        goMyVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    if(isGuest.equals("yes"))
                        Toast.makeText(VideosActivity.this,getResources().getString(R.string.TabVideoException), Toast.LENGTH_LONG).show();
                    else
                        LaunchActivityVideos();
                }catch (NullPointerException e) {
                    // TODO: handle exception
                }*/

            }
        });
        goAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    if(isGuest.equals("yes"))
                        Toast.makeText(VideosActivity.this,getResources().getString(R.string.TabVideoException), Toast.LENGTH_LONG).show();
                    else
                        LaunchAddActivityVideos();
                }catch (NullPointerException e) {
                    // TODO: handle exception
                }*/

            }
        });
        getintent();
    }

    private void initconfigheader() {
        // TODO Auto-generated method stub
        //De momento ocultamos la opacidad
        CloseDropMenu = (RelativeLayout)findViewById(R.id.rl_background_closeDropmenu);
        CloseDropMenuBg = (RelativeLayout)findViewById(R.id.rl_background_closeDropmenu_image);
        CloseDropMenu.setVisibility(View.INVISIBLE);
        CloseDropMenuBg.setVisibility(View.INVISIBLE);
        btn_menu_close  = (Button) findViewById(R.id.btn_menu_close); //only when drap its open
        //Menu Drop menu
        flag_menu = 0; //A 0 significa que la accion de menu se abrirá pq esta cerrado!
        btn_menu = (Button) findViewById(R.id.btn_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setVisibility(View.INVISIBLE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // close drawer
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(VideosActivity.this, R.anim.lefttoright);
                    CloseDropMenuBg.setVisibility(View.VISIBLE);
                    mDrawerLayout.setAnimation(anim);
                    flag_menu = 1;
                    CloseDropMenu.setVisibility(View.VISIBLE);
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }

            }

        });
        btn_menu_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDropMenuBg.setVisibility(View.INVISIBLE);
                CloseDropMenu.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Animation anim = AnimationUtils.loadAnimation(VideosActivity.this, R.anim.righttoleft);
                mDrawerLayout.setAnimation(anim);
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }

        });
        goStore = (Button) findViewById(R.id.btn_gostore);
        goStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                    i.putExtra("action", "store");
                    startActivityForResult(i, 0);
                    VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                }catch (NullPointerException e) {
                    // TODO: handle exception
                }
            }
        });
        //Search
        edit_search = (EditText) findViewById(R.id.Search_Edit);
        edit_search.setTypeface(tt);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) { // TODO
                search = s.toString();
            }
        });
        edit_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (search.equals("")) {
                        search = " ";
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                        Intent i = new Intent(VideosActivity.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    } else {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                        Intent i = new Intent(VideosActivity.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
    }
    private void getintent() {
        // TODO Auto-generated method stub
        drawer();
        reloadRemoteData();
    }

    private void drawer() {
        // TODO Auto-generated method stub
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.icon, R.string.toolbar_open, R.string.toolbar_close) {
            /** Called when drawer is closed */
            @Override
            public void onDrawerClosed(View view) {
                CloseDropMenu.setVisibility(View.INVISIBLE);
                CloseDropMenuBg.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mDrawerLayout.setVisibility(View.INVISIBLE);
                flag_menu = 0;
            }
            /** Called when a drawer is opened */
            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                mDrawerLayout.setVisibility(View.VISIBLE);
                flag_menu = 1;
            }
        };
        // Setting DrawerToggle on DrawerLayout
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //Typificamos _todas las letras
        TextView _budName = (TextView)findViewById(R.id.txt_name);
        _budName.setTypeface(tt);
        TextView _profile = (TextView)findViewById(R.id.ll_profile_text);
        _profile.setTypeface(tt);
        TextView _timeline = (TextView)findViewById(R.id.ll_timeline_text);
        _timeline.setTypeface(tt);
        TextView _buddees = (TextView)findViewById(R.id.ll_buddees_text);
        _buddees.setTypeface(tt);
        TextView _budw = (TextView)findViewById(R.id.ll_world_text);
        _budw.setTypeface(tt);
        TextView _buds = (TextView)findViewById(R.id.ll_strains_text);
        _buds.setTypeface(tt);
        TextView _bude = (TextView)findViewById(R.id.ll_events_text);
        _bude.setTypeface(tt);
        TextView _budc = (TextView)findViewById(R.id.ll_ads_text);
        _budc.setTypeface(tt);
        TextView _budg = (TextView)findViewById(R.id.ll_games_text);
        _budg.setTypeface(tt);
        TextView _config = (TextView)findViewById(R.id.ll_settings_text);
        _config.setTypeface(tt);
        TextView _about = (TextView)findViewById(R.id.ll_contact_text);
        _about.setTypeface(tt);
        TextView _exit = (TextView)findViewById(R.id.ll_logout_text);
        _exit.setTypeface(tt);
        //Bud Title: Go website
        LinearLayout ll_goweb = (LinearLayout) findViewById(R.id.layoutmenu_website);
        ll_goweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                //Go to web
                Uri uri = Uri.parse("https://social.budbuddee.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        // Profile
        LinearLayout ll_profile = (LinearLayout) findViewById(R.id.layoutmenu_profile);
        ll_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isGuest.equals("no")) {
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    if(flag_menu == 0){
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                        mDrawerLayout.setVisibility(View.VISIBLE);
                        flag_menu = 1;
                    }else{
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        mDrawerLayout.setVisibility(View.INVISIBLE);
                        CloseDropMenu.setVisibility(View.INVISIBLE);
                        CloseDropMenuBg.setVisibility(View.INVISIBLE);
                        flag_menu = 0;
                    }
                    Intent i = new Intent(VideosActivity.this, ProfileMyInfoActivity.class);
                    i.putExtra("username", m_sUsername);
                    i.putExtra("user_title", m_sUserTitle);
                    i.putExtra("thumb", m_sThumb);
                    i.putExtra("info", m_sInfo);
                    i.putExtra("status", m_sStatus);
                    startActivityForResult(i, 0);
                    VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                }else{
                    Toast.makeText(VideosActivity.this,getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
                }
            }
        });
        LinearLayout ll_timeline = (LinearLayout) findViewById(R.id.layoutmenu_timeline);
        ll_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "timeline");
                startActivityForResult(i, 0);
                //VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                //No hace falta cambiar!
            }
        });
        LinearLayout ll_buddees = (LinearLayout) findViewById(R.id.layoutmenu_buddees);
        ll_buddees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "buddees");
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        /*LinearLayout ll_stats = (LinearLayout) findViewById(R.id.layoutmenu_stats);
        ll_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(NewsActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "stats");
                startActivityForResult(i, 0);
                NewsActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });*/
        LinearLayout ll_BudW = (LinearLayout) findViewById(R.id.layoutmenu_world);
        ll_BudW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudW");
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudS = (LinearLayout) findViewById(R.id.layoutmenu_strains);
        ll_BudS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudS");
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudE = (LinearLayout) findViewById(R.id.layoutmenu_events);
        ll_BudE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudE");
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudC = (LinearLayout) findViewById(R.id.layoutmenu_ads);
        ll_BudC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudC");
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudG = (LinearLayout) findViewById(R.id.layoutmenu_games);
        ll_BudG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(VideosActivity.this,getResources().getString(R.string.toolbar_menu_games_coming), Toast.LENGTH_LONG).show();
            }
        });
        LinearLayout ll_Settings = (LinearLayout) findViewById(R.id.layoutmenu_settings);
        ll_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "Settings");
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_Contact = (LinearLayout) findViewById(R.id.layoutmenu_contact);
        ll_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(VideosActivity.this, HelpActivity.class);
                startActivityForResult(i, 0);
                VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });

        LinearLayout ll_logout = (LinearLayout) findViewById(R.id.layoutmenu_logout);
        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView logout = (TextView)findViewById(R.id.ll_logout_text);
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(flag_menu == 0){
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                //innoHawk:: Start Code db
                db = new SQLiteActivity(getApplicationContext());
                Bundle b = new Bundle();
                b.putInt("index", 0);
                Intent i = new Intent();
                i.putExtras(b);
                db.deleteUser();
                LoginManager.getInstance().logOut();
                setResult(0, i);
                //Borramos preferencias
                SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
                prefs.edit().remove("site").commit();
                prefs.edit().remove("member_id").commit();
                prefs.edit().remove("username").commit();
                prefs.edit().remove("password").commit();
                prefs.edit().remove("protocol").commit();
                prefs.edit().remove("index").commit();
                prefs.edit().remove("isGuest").commit();
                //Al loro! que no borramos el name del guest.. BASICAMENTE pq asi no repetimos username y creamos siempre user nuevo por si vuelve a entrar!!!! :)
                Intent intent = new Intent(VideosActivity.this, Main.class);
                intent.putExtra("member_id", "");
                intent.putExtra("username", "");
                intent.putExtra("password", "");
                intent.putExtra("index", 0);
                startActivityForResult(intent, 1);
                finish();
            }
        });
    }
    protected void reloadRemoteData(){
        //WebView
        Connector o = Main.getConnector();
        String password = o.getPassword();
        String username = o.getUsername();
        int id_member = o.getMemberId();
        String sMethod;
        sMethod = "";


        //sMethod = "modules%2Fpcint%2Fmobilehotnews%2Fclasses%2FPcintMobHotNewsRequestVideo.php%3Faction%3Dmenu";
        sMethod = AppConfig.URL_WALL;

        String sUrl = AppConfig.URL_METHODS + "r.php?user="+username+"&pwd="+password+"&url="+sMethod+"%26mid%3D"+id_member+"%26isguest%3D"+isGuest+"%26user%3D"+username+"%26pwd%3D"+password;


        webView = (WebView) findViewById(R.id.webViews);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        //Brower niceties -- pinch / zoom, follow links in place
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        // Below required for geolocation
        webView.getSettings().setGeolocationEnabled(true);
        //HTML5 API flags
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        //Load WebView
        webView.loadUrl(sUrl, Main.getHeadersForLoggedInUser());
        webView.setWebViewClient(new com.innohawk.dan.VideosTab.VideosActivity.WebPageViewClient(this));
        webView.setWebChromeClient(new com.innohawk.dan.VideosTab.VideosActivity.WebPageChromeClient(this));
    }

    //innoHawk:: Varios para webview, permisos de localización, etc...
    interface ThirdPartyOnClickListener extends View.OnClickListener {
        public void setUrl(String s);
        public void setAction(int iAction);
        public void setTitle(String s);
    }
    private class WebPageChromeClient extends WebChromeClient {
        public WebPageChromeClient(com.innohawk.dan.VideosTab.VideosActivity act) {
            super();
        }
        public void onProgressChanged(WebView view, int progress) {
            getActionBarHelper().setRefreshActionItemState(progress >= 100 ? false : true);
        }
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            final GeolocationPermissions.Callback callbackF = callback;
            final String originF = origin;
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(m_actThis);
            String Location;
            Location = getString(R.string.google_location_text);
            String Share;
            Share = getString(R.string.google_location_textShare);
            String Accept;
            Accept = getString(R.string.google_location_textAccept);
            String Decline;
            Decline = getString(R.string.google_location_textDecline);

            builder.setTitle(Location);
            builder.setMessage(Share)
                    .setCancelable(true)
                    .setPositiveButton(Accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            callbackF.invoke(originF, true, false);
                        }
                    }).setNegativeButton(Decline, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    callbackF.invoke(originF, false, false);
                }
            });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }
    @Override
    public void setContentView(int iLayoutResID) {
        super.setContentView(iLayoutResID);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the BACK key and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the BACK key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    private class WebPageViewClient extends WebViewClient {
        private static final String TAG = "dropmenu";
        protected com.innohawk.dan.VideosTab.VideosActivity m_actWebPage;
        public WebPageViewClient(com.innohawk.dan.VideosTab.VideosActivity act) {
            super();
            m_actWebPage = act;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String sScheme = Uri.parse(url).getScheme();
            Log.d(TAG, "URL scheme: " + Uri.parse(url).getScheme());
            Log.d(TAG, "URL host: " + Uri.parse(url).getHost());
            Log.d(TAG, "URL user: " + Uri.parse(url).getUserInfo());
            Log.d(TAG, "URL last path segment: " + Uri.parse(url).getLastPathSegment());
            Log.d(TAG, "URL fragment: " + Uri.parse(url).getFragment());
            if (sScheme.equals("bxprofile")) {
                String sUsername = url.substring("bxprofile:".length());
                Intent i = new Intent(m_actWebPage, ProfileActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxcontact")) {
                String sUsername = Uri.parse(url).getUserInfo();
                String sUserTitle = Uri.parse(url).getHost();
                if (null == sUsername || 0 == sUsername.length()) {
                    sUsername = url.substring("bxcontact:".length());
                }
                Intent i = new Intent(m_actWebPage, MailComposeActivity.class);
                i.putExtra("recipient", sUsername);
                i.putExtra("recipient_title", null == sUserTitle
                        || 0 == sUserTitle.length() ? sUsername : sUserTitle);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxgoto")) {
                String sSection = url.substring("bxgoto:".length());
                if (sSection.equals("home")) {
                    m_oActivityHelper.gotoHome();
                } else if (sSection.equals("friends")) {
                    Intent i = new Intent(m_actWebPage, FriendsHomeActivity.class);
                    startActivityForResult(i, 0);
                } else if (sSection.equals("messages")) {
                    Intent i = new Intent(m_actWebPage, MailHomeActivity.class);
                    startActivityForResult(i, 0);
                } else if (sSection.equals("search")) {
                    Intent i = new Intent(m_actWebPage, SearchHomeActivity.class);
                    startActivityForResult(i, 0);
                }
                return true;
            } else if (sScheme.equals("ihdispensary")) { // ihdispensary://ID_Dispensario@LatUser/LngUser

                String iId = Uri.parse(url).getUserInfo();
                String sLat = Uri.parse(url).getHost();
                String sLong = Uri.parse(url).getLastPathSegment();

                Intent i = new Intent(m_actWebPage, ActivityMapsDispensaries.class);
                i.putExtra("id", iId);
                i.putExtra("lat", sLat);
                i.putExtra("lng", sLong);
                startActivityForResult(i, 0);
                return true;

            }else if (sScheme.equals("bxphoto")) { // bxphoto://USERNAME@ABUM_ID/IMAGE_ID
                String sUsername = Uri.parse(url).getUserInfo();
                String sAlbumId = Uri.parse(url).getHost();
                String sPhotoId = Uri.parse(url).getLastPathSegment();
                Intent i = new Intent(m_actWebPage, ImagesGallery.class);
                i.putExtra("username", sUsername);
                i.putExtra("album_id", sAlbumId);
                i.putExtra("photo_id", sPhotoId);
                i.putExtra("makeavatar","no");
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxvideo")) { // bxvideo://USERNAME@ABUM_ID/VIDEO_ID
                String sUsername = Uri.parse(url).getUserInfo();
                String sAlbumId = Uri.parse(url).getHost();
                String sMediaId = Uri.parse(url).getLastPathSegment();
                Intent i = new Intent(m_actWebPage, VideosFilesActivity.class);
                i.putExtra("username", sUsername);
                i.putExtra("album_id", sAlbumId);
                i.putExtra("media_id", sMediaId);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxaudio")) { // bxaudio://USERNAME@ABUM_ID/SOUND_ID
                String sUsername = Uri.parse(url).getUserInfo();
                String sAlbumId = Uri.parse(url).getHost();
                String sMediaId = Uri.parse(url).getLastPathSegment();
                Intent i = new Intent(m_actWebPage, SoundsFilesActivity.class);
                i.putExtra("username", sUsername);
                i.putExtra("album_id", sAlbumId);
                i.putExtra("media_id", sMediaId);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxphotoupload")) { // bxphotoupload:ALBUM_NAME
                String sAlbumName = Uri.decode(url.substring("bxphotoupload:".length()));
                Intent i = new Intent(m_actWebPage, AddImageActivity.class);
                i.putExtra("album_name", sAlbumName);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxvideoupload")) { // bxphotoupload:ALBUM_NAME
                String sAlbumName = Uri.decode(url.substring("bxvideoupload:".length()));
                Intent i = new Intent(m_actWebPage, AddVideoActivity.class);
                i.putExtra("album_name", sAlbumName);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxlocation")) { // bxlocation:USERNAME
                String sUsername = Uri.decode(url.substring("bxlocation:".length()));
                Intent i = new Intent(m_actWebPage, LocationActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxmylocation")) { // bxlocation:USERNAME
                String sUsername = Uri.decode(url.substring("bxmylocation:".length()));
                Intent i = new Intent(m_actWebPage, LocationActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxmystatus")) { // bxlocation:USERNAME
                String sStatus = Uri.decode(url.substring("bxmystatus:".length()));
                Intent i = new Intent(m_actWebPage, StatusMessageActivity.class);
                i.putExtra("status_message", sStatus);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxprofilefriends")) { // bxprofilefriends:USERNAME
                String sUsername = Uri.decode(url.substring("bxprofilefriends:".length()));
                Intent i = new Intent(m_actWebPage, FriendsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxphotoalbums")) { // bxphotoalbums:USERNAME
                String sUsername = Uri.decode(url.substring("bxphotoalbums:".length()));
                Intent i = new Intent(m_actWebPage, ImagesAlbumsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxvideoalbums")) { // bxvideoalbums:USERNAME
                String sUsername = Uri.decode(url.substring("bxvideoalbums:".length()));
                Intent i = new Intent(m_actWebPage, VideosAlbumsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxaudioalbums")) { // bxaudioalbums:USERNAME
                String sUsername = Uri.decode(url.substring("bxaudioalbums:".length()));
                Intent i = new Intent(m_actWebPage, SoundsAlbumsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;
            } else if (sScheme.equals("bxpagetitle")) { // bxaudioalbums:USERNAME
                String sTitle = Uri.decode(url.substring("bxpagetitle:".length()));
                setTitleCaption(sTitle);
                return true;
            } else {
                if (null != Uri.parse(url).getFragment() && Uri.parse(url).getFragment().equals("blank")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("#blank", "")));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
            Toast.makeText(m_actWebPage, description + " (" + failingUrl + ")",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void LaunchActivityVideos() {
        Connector o = Main.getConnector();
        Intent iv = new Intent(VideosActivity.this, VideosAlbumsActivity.class);
        iv.putExtra("username", o.getUsername());
        startActivityForResult(iv, 0);
        VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
    }

    protected void LaunchAddActivityVideos() {
        String sAlbumName = "VideoWall";
        Connector o = Main.getConnector();
        Intent iv = new Intent(VideosActivity.this, AddVideoActivity.class);
        iv.putExtra("album_name", sAlbumName);
        startActivityForResult(iv, 0);
        VideosActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
    }
    @Override
    public void onBackPressed() {
        // timer.cancel(); //Cancelamos Timer cuando salgamos de la acividad principal
        // super.onBackPressed();

    }
}
