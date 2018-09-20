package com.innohawk.dan.activitymaps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activityDropMenu.ActivityDropMenu;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.appconfig.RoundedCornersTransformation;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by innohawk on 24/10/17.
 */
public class ActivityMapsDispensaries extends ActivityBase {
    private static final String TAG = "OO ActivityMapsDispensa";
    String isGuest;
    protected Object[] m_aMenu, m_aPhotos; //Array con la lista
    static double latitude_permission;
    static double longitude_permission;
    String id_Dispensaries;
    String imageurl,telephone_num;
    ImageView imageView;
    RatingBar rb;
    Float number_to_rating;
    String ratingGeneral;

    //Tab
    TabHost host;

    RatingBar rb_cmt;
    Float number_to_rating_cmt;
    String ratingGeneral_cmt;
    //Lista Tab3
    protected Object[] m_aReview; //Array con la lista
    public static ArrayList<GetSetMaps> arrayList;
    ListView list_detail;
    protected LoaderImageView m_viewImageLoader;

    Button btn_call,btn_map,btn_web,btn_back,btn_gorate;



    private WebView webView;

    //permisos
    View layoutHomePermission;
    RelativeLayout rl_dialog;
    private static final int MY_CALL = 0;

    //Custom Header
    private SQLiteActivity db; //logout
    int flag_menu;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Button btn_menu,btn_menu_close,goMyMessage,goAddMessage;
    EditText edit_search;
    String search = " ";
    Button goStore;
    RelativeLayout CloseDropMenu,CloseDropMenuBg;
    //Normal Header
    protected TextView textTitle;
    Button reload;
    Button Back;
    Typeface tt;
    //User Info
    protected Map<String, Object> m_map; //Para mapear datos dle usuario!!!!!
    protected String m_sUserTitle;
    protected String m_sUsername;
    protected String m_sThumb;
    protected String m_sInfo;
    protected String m_sStatus;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.activity_maps_dispensaries);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        btn_back = (Button)findViewById(R.id.buttonBack);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_gorate = (Button)findViewById(R.id.btn_gorate);

        //Config header
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        initconfigheader();
        textTitle = (TextView) findViewById(R.id.titleEvent);
        textTitle.setText("");
        textTitle.setTypeface(tt);
        reload = (Button) findViewById(R.id.buttonRefresh);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });

        //Fx para saber si es guest!
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);
        if(isGuest.equals("no"))
            getUserInfo();
        //Inicializamos
        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Info");
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.dispensary_rate_tab1));
        host.addTab(spec);


        //Tab 2
        spec = host.newTabSpec("Menu");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.dispensary_rate_tab2));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Review");
        spec.setContent(R.id.tab3);
        spec.setIndicator(getResources().getString(R.string.dispensary_rate_tab3));
        host.addTab(spec);

        for(int i=0;i<host.getTabWidget().getChildCount();i++) {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setTypeface(tt);
        }

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
                    Animation anim = AnimationUtils.loadAnimation(ActivityMapsDispensaries.this, R.anim.lefttoright);
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
                Animation anim = AnimationUtils.loadAnimation(ActivityMapsDispensaries.this, R.anim.righttoleft);
                mDrawerLayout.setAnimation(anim);
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }

        });
        goStore = (Button) findViewById(R.id.btn_gostore);
        goStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                    i.putExtra("action", "store");
                    startActivityForResult(i, 0);
                    ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                        Intent i = new Intent(ActivityMapsDispensaries.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    } else {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                        Intent i = new Intent(ActivityMapsDispensaries.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
        Intent ih = getIntent();
        latitude_permission = Double.parseDouble(ih.getStringExtra("lat"));
        longitude_permission = Double.parseDouble(ih.getStringExtra("lng"));
        id_Dispensaries = ih.getStringExtra("id");
        MapsInitializer.initialize(getApplicationContext());
        drawer();
        //Load Data
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
                Uri uri = Uri.parse("http://budbuddee.com");
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
                    Intent i = new Intent(ActivityMapsDispensaries.this, ProfileMyInfoActivity.class);
                    i.putExtra("username", m_sUsername);
                    i.putExtra("user_title", m_sUserTitle);
                    i.putExtra("thumb", m_sThumb);
                    i.putExtra("info", m_sInfo);
                    i.putExtra("status", m_sStatus);
                    startActivityForResult(i, 0);
                    ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                }else{
                    Toast.makeText(ActivityMapsDispensaries.this,getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "timeline");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "buddees");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "BudW");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "BudS");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "BudE");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "BudC");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudG = (LinearLayout) findViewById(R.id.layoutmenu_games);
        ll_BudG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(ActivityMapsDispensaries.this,getResources().getString(R.string.toolbar_menu_games_coming), Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(ActivityMapsDispensaries.this, ActivityDropMenu.class);
                i.putExtra("action", "Settings");
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsDispensaries.this, HelpActivity.class);
                startActivityForResult(i, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent intent = new Intent(ActivityMapsDispensaries.this, Main.class);
                intent.putExtra("member_id", "");
                intent.putExtra("username", "");
                intent.putExtra("password", "");
                intent.putExtra("index", 0);
                startActivityForResult(intent, 1);
                finish();
            }
        });
    }
    protected void getUserInfo() {
        Object[] aParams;
        String sMethod;
        Connector o = Main.getConnector();
        Object[] aParamsLocal = {
                o.getUsername(),
                o.getPassword(),
                Main.getLang()
        };
        aParams = aParamsLocal;
        sMethod = "ih.getHomepage";

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getHomepageInfo(" + m_iProtocolVer + ") result: " + result.toString());
                m_map = (Map<String, Object>) result;
                Connector o = Main.getConnector();
                o.setSearchWithPhotos(true);
                m_sUsername = o.getUsername();
                m_sThumb = (String) m_map.get("thumb");
                m_sStatus = (String) m_map.get("status");
                m_sInfo = (String) m_map.get("user_info");
                m_sUserTitle = (String) m_map.get("user_title");
                if (null != m_map.get("search_with_photos"))
                    o.setSearchWithPhotos(((String) m_map.get("search_with_photos")).equals("1") ? true : false);
            }

        }, this);
    }
    // Image View Flipper - Header Image
    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return m_aPhotos.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.image_pager, container, false);
            imageView = (ImageView) itemView.findViewById(R.id.image_page_fliper);
            //Animator image
            Animation anim = AnimationUtils.loadAnimation(ActivityMapsDispensaries.this, R.anim.lefttoright); Animation anim1 = AnimationUtils.loadAnimation(ActivityMapsDispensaries.this, R.anim.right_slide_out);
            imageView.setAnimation(anim);
            AnimationSet s = new AnimationSet(true);// false mean dont share // interpolators
            s.addAnimation(anim);
            s.addAnimation(anim1);
            imageView.startAnimation(s);
            imageView.setImageResource(R.drawable.no_image);
            Map<String, String> map = (Map<String, String>) m_aPhotos[position];
            String sThumb = map.get("thumb");
            imageurl = sThumb;
            Log.d(TAG, "LOS PARAMETROS2 SON: " + imageurl);
            //imgLoader.DisplayImage(imageurl.replace(" ", "%20"), imageView);
            Picasso.with(ActivityMapsDispensaries.this).load(imageurl).placeholder(R.drawable.no_image).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }


    protected void reloadRemoteData() {
        String id = id_Dispensaries;
        Connector o = Main.getConnector();
        Object[] aVars = {o.getUsername(), o.getPassword(), Main.getLang(), id, isGuest};

        Object[] aParams = {
                "getItemDis",
                "ihCO",
                aVars
        };

        o.execAsyncMethod("ih.dispensariesdetails", aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;

                initMenu((Object[]) map.get("GetDisDetail"),(Object[]) map.get("GetDisPhotoCarousel"));
            }
        }, this);

    }

    protected void initMenu(Object[] aMenu, Object[] aPhotos) {
        m_aMenu = aMenu;
        m_aPhotos = aPhotos;

        @SuppressWarnings("unchecked") final Map<String, String> map = (Map<String, String>) m_aMenu[0];
        textTitle.setText(map.get("title"));
        final String iId_element = map.get("id");
        String sName = map.get("title");
        String sCountry = map.get("country");
        String sCity = map.get("city");
        String sTypeDis = map.get("type_cat");
        String dDescription = map.get("desc");
        final String fLat = map.get("lat");
        final String fLong = map.get("lng");
        //double latitude_ = Double.parseDouble(fLat);
        //double longitude_ = Double.parseDouble(fLong);
        String sFeat = map.get("feat");
        String iRate = map.get("rate");
        String iRateCount = map.get("rate_count");
        String sThumb = map.get("thumb");
        final String sWeb = map.get("web");
        telephone_num = map.get("phone");




        ImageView programImage = (ImageView) findViewById(R.id.img_logo);



        Picasso.with(ActivityMapsDispensaries.this)
                .load(sThumb)
                .placeholder(R.drawable.no_image) //optional
                //.resize(300, imgHeight)         //optional
                //.centerCrop()                        //optional
                .into(programImage);


        rb = (RatingBar) findViewById(R.id.rate);
        ratingGeneral = iRate;
        number_to_rating = Float.parseFloat(ratingGeneral);
        rb.setRating(number_to_rating);

        TextView txt_Title = (TextView) findViewById(R.id.textTitle);
        txt_Title.setText(sName);

        //Type
        TextView txt_Type = (TextView) findViewById(R.id.textType);
        txt_Type.setTextColor(getResources().getColor(R.color.red));
        if (sTypeDis.length() == 0)
            txt_Type.setText("-");
        else {
            if ((sTypeDis.equals("all"))||(sTypeDis.equals("Both"))||(sTypeDis.equals("both"))|| (sTypeDis.equals("All")))
                txt_Type.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
            else if ((sTypeDis.equals("medicinal"))||(sTypeDis.equals("Medicinal")))
                txt_Type.setText("" + getResources().getString(R.string.Maps_dialog_TypeMed));
            else if ((sTypeDis.equals("recreational"))||(sTypeDis.equals("Recreational")))
                txt_Type.setText("" + getResources().getString(R.string.Maps_dialog_TypeRec));
            else
                txt_Type.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
        }

        TextView txt_num_Carrousel = (TextView) findViewById(R.id.txt_image_scroll);
        txt_num_Carrousel.setText("");

        TextView txt_rateCount = (TextView) findViewById(R.id.textratecount);
        txt_rateCount.setText("["+iRateCount+"]");

        TextView txt_description = (TextView) findViewById(R.id.txt_desc);
        //txt_description.setText(dDescription);
        txt_description.setText(Html.fromHtml(" " +dDescription));
        //txt_description.setMovementMethod(LinkMovementMethod.getInstance());
        //txt_description.setTypeface(tt);




        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(ActivityMapsDispensaries.this);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        //Go Rate
        btn_gorate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(ActivityMapsDispensaries.this, ActivityMapsDispensariesRate.class);
                iv.putExtra("id", iId_element);
                startActivityForResult(iv, 0);
                ActivityMapsDispensaries.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });

        //WebView
        Connector o = Main.getConnector();
        String password = o.getPassword();
        String username = o.getUsername();
        int id_member = o.getMemberId();

        //public static String URL_STORE                   = "modules%2Fpcint%mobilestore%2Fclasses%PcintMobStoreRequest.php%3Faction%3Ddispensary";
        String sUrl = AppConfig.URL_METHODS + "r.php?user="+username+"&pwd="+password+"&url="+AppConfig.URL_STORE+"%26mid%3D"+id_member+"%26profile%3D"+id_member+"%26user%3D"+username+"%26pwd%3D"+password+"%26dispensary%3D"+id_Dispensaries;

        //Log.d(TAG, "DISPENSARIO : " + sUrl);

        webView = (WebView) findViewById(R.id.webViewDispensaries);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        //Brower niceties -- pinch / zoom, follow links in place
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(false);
        // Below required for geolocation
        webView.getSettings().setGeolocationEnabled(true);
        //HTML5 API flags
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        //Load WebView
        webView.loadUrl(sUrl, Main.getHeadersForLoggedInUser());
        webView.setWebViewClient(new WebPageViewClient(this));
        webView.setWebChromeClient(new WebPageChromeClient(this));


        btn_call = (Button) findViewById(R.id.button_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                verifyPermissionCall();
            }
        });

        btn_map = (Button) findViewById(R.id.button_direction);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "http://maps.google.com/maps?" + "saddr=" + latitude_permission + "," + longitude_permission + "&daddr=" + fLat + "," + fLong));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        btn_web = (Button) findViewById(R.id.button_web);
        btn_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Uri uri = Uri.parse(sWeb); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //Tab3
        loadReviews(iId_element);
    }



    protected void loadReviews(String iId_element) {
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        final String sGetReview;

        sMethod = "ih.dispensarygetcmt";
        sGetReview = "ihGetRateCmt";
        aParams = new Object[]{
                o.getUsername(),
                o.getPassword(),
                Main.getLang(),
                isGuest,
                iId_element,
                "0"
            };

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initReview((Object[]) map.get(sGetReview));
            }
        }, this);

    }
    protected void initReview(Object[] aMenu) {
        m_aReview = aMenu;
        arrayList = new ArrayList<GetSetMaps>();
        arrayList.clear();
        list_detail = (ListView) findViewById(R.id.list_detail);
        //Lo hacemos igual de grande que el num de listado
        //ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) list_detail.getLayoutParams();
        //lp.height = m_aReview.length * 100 + 300;
        //list_detail.setLayoutParams(lp);

        for (int i = 0; i < getCountArrayReview(); ++i)
        {
            GetSetMaps temp = new GetSetMaps();
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) m_aReview[i];

            temp.setId(map.get("id"));
            temp.setDesc(map.get("cmt"));
            temp.setRattingGeneral(map.get("numrate"));
            temp.setThubnailimage(map.get("thumb"));
            temp.setName(map.get("author"));
            arrayList.add(temp);

            list_detail.setAdapter(null);
            LazyAdapter lazy = new LazyAdapter(ActivityMapsDispensaries.this, arrayList);
            lazy.notifyDataSetChanged();
            list_detail.setAdapter(lazy);
            list_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        Intent iv = new Intent(ActivityMapsDispensaries.this, ActivityMapsDispensariesRateView.class);
                        iv.putExtra("id", "" + arrayList.get(position).getId());
                        iv.putExtra("thumb", "" + arrayList.get(position).getThubnailimage());
                        iv.putExtra("author", "" + arrayList.get(position).getName());
                        iv.putExtra("ratting", "" + arrayList.get(position).getRattingGeneral());
                        iv.putExtra("desc", "" + arrayList.get(position).getDesc());
                        startActivityForResult(iv, 0);
                    }
            });
        }
    }
    public int getCountArrayReview() {
        return m_aReview.length;
    }
    // binding data in listview usind adapter class: Normal Load
    public class LazyAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<GetSetMaps> data;
        private LayoutInflater inflater = null;
        public LazyAdapter(Activity a, ArrayList<GetSetMaps> str) {
            activity = a;
            data = str;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public void addItems(ArrayList<GetSetMaps> fileList) {
            // TODO Auto-generated method stub
            if (data != null) {
                data.addAll(fileList);
            } else {
                data = fileList;
            }
        }
        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ih = convertView;
            if (convertView == null) {
                ih = inflater.inflate(R.layout.activity_maps_dispensaries_reviewcell, null);
            }

            int color = getResources().getColor(R.color.new_bg_background); // Gris Oscuro
            /*if (position%2==0) {
                color = 0xFFBDBDBD; // Gris claro
            }*/

            ih.setBackgroundColor(color);
            Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
            TextView txt_name = (TextView) ih.findViewById(R.id.txt_author);
            txt_name.setText(data.get(position).getName());
            txt_name.setTypeface(tt);

            TextView txt_desc = (TextView) ih.findViewById(R.id.txt_description);
            txt_desc.setText(data.get(position).getDesc());
            txt_desc.setTypeface(tt);

            //Rate
            rb_cmt = (RatingBar) ih.findViewById(R.id.rate_review);
            ratingGeneral_cmt = data.get(position).getRattingGeneral();
            number_to_rating_cmt = Float.parseFloat(ratingGeneral_cmt);
            rb_cmt.setRating(number_to_rating_cmt);

            //IMG BG
            String image = data.get(position).getThubnailimage().replace(" ", "%20");
            ImageView programImage = (ImageView) ih.findViewById(R.id.img_iconpin);
            //programImage.setImageResource(R.drawable.no_image);
            m_viewImageLoader = (LoaderImageView) ih.findViewById(R.id.media_images_image_view);
            m_viewImageLoader.setOnlySpinnerDrawable((String) image);
            if(image.isEmpty()) {
                image = String.valueOf(R.drawable.ic_menu_make_avatar);
                programImage.setImageResource(R.drawable.ic_menu_make_avatar);
            }else{
                final int radius = 6;
                final int margin = 0;
                final Transformation transformation = new RoundedCornersTransformation(radius, margin);
                Picasso.with(ActivityMapsDispensaries.this)
                        .load(image)
                        .transform(transformation)
                        .into(programImage);
            }

            return ih;
        }
    }


    //innoHawk:: Varios para webview, permisos de localización, etc...
    interface ThirdPartyOnClickListener extends View.OnClickListener {
        public void setUrl(String s);

        public void setAction(int iAction);

        public void setTitle(String s);
    }
    private class WebPageChromeClient extends WebChromeClient {

        public WebPageChromeClient(ActivityMapsDispensaries act) {
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
        private static final String TAG = "Dispensaries_webview";
        protected ActivityMapsDispensaries m_actWebPage;

        public WebPageViewClient(ActivityMapsDispensaries act) {
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


    //Permisos
    @SuppressLint("WrongConstant")
    private void verifyPermissionCall() {
        int writePermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            writePermission = checkSelfPermission(android.Manifest.permission.CALL_PHONE);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            makeCAll();
        }
    }
    private void requestPermission() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
            showSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_CALL);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCAll();
            } else {
                showSnackBar();
            }
        }
    }
    private void showSnackBar() {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            rl_dialog = (RelativeLayout) findViewById(R.id.rl_permissiondialog);
            layoutHomePermission = getLayoutInflater().inflate(R.layout.activity_permission, rl_dialog, false);
            rl_dialog.addView(layoutHomePermission);
            rl_dialog.startAnimation(AnimationUtils.loadAnimation(ActivityMapsDispensaries.this, R.anim.popup));
            Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
            Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialog.setVisibility(View.GONE);
                    makeCAll();
                }
            });
            btn_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    openSettings();
                    Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
                    Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
                    btn_yes.setText("OK");
                    btn_settings.setVisibility(View.GONE);
                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            rl_dialog.setVisibility(View.GONE);
                            makeCAll();
                        }
                    });
                }
            });
        }
    }
    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    @SuppressLint("MissingPermission")
    private void makeCAll() {
        String uri = "tel:" + telephone_num;
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse(uri));
        if (ActivityCompat.checkSelfPermission(ActivityMapsDispensaries.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        startActivity(i);
    }
    public void onBackPressed() {
        super.onBackPressed();
    }
}
