package com.innohawk.dan.MenuTab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activityDropMenu.ActivityDropMenu;
import com.innohawk.dan.activitymaps.GetSetMaps;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.appconfig.LoadImgNew;
import com.innohawk.dan.appconfig.RoundedCornersTransformation;
import com.innohawk.dan.friends.FriendsHomeActivity;
import com.innohawk.dan.helps.HelpActivity;
import com.innohawk.dan.mail.MailComposeActivity;
import com.innohawk.dan.mail.MailHomeActivity;
import com.innohawk.dan.profile.ProfileMyInfoActivity;
import com.innohawk.dan.search.SearchHeaderActivity;
import com.innohawk.dan.sqlite.SQLiteActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by innohawk on 24/10/17.
 */
public class MenuTabActivity extends ActivityBase {
    private static final String TAG = "OO Notifications";
    String isGuest;
    private String Error = null;
    protected Object[] m_aMenu; //Array con la lista
    public static ArrayList<GetSetMaps> arrayList;
    ListView list_detail;
    ProgressDialog progressDialog;
    int MainPosition = 0; //Esto es para ir a la ficha detalles
    int pos; // Es la posicion del elemento al hacer "click". No es el mismo que la id!!!!!
    //User Info
    protected String m_sUserTitle;
    protected String m_sUsername;
    protected String m_sThumb;
    protected String m_sInfo;
    protected String m_sStatus;
    //Loading position
    protected LoadImgNew m_viewImageLoader;
    RelativeLayout rl_dialoguser,rl_notdata;
    View layoutActivityDialog,layoutActivityDialogNotData;
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
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.tabmenu);
        //Fx para saber si es guest!
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);
        //Config header
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        initconfigheader();
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.notifications_title);
        textTitle.setTypeface(tt);
        reload = (Button) findViewById(R.id.buttonRefresh);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });
        goMyMessage = (Button) findViewById(R.id.buttonMy);
        goAddMessage = (Button) findViewById(R.id.buttonAdd);
        goMyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isGuest.equals("yes"))
                        Toast.makeText(MenuTabActivity.this,getResources().getString(R.string.TabNotiException), Toast.LENGTH_LONG).show();
                    else
                        LaunchActivityMessges();
                }catch (NullPointerException e) {
                    // TODO: handle exception
                }

            }
        });
        goAddMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isGuest.equals("yes"))
                        Toast.makeText(MenuTabActivity.this,getResources().getString(R.string.TabNotiException), Toast.LENGTH_LONG).show();
                    else
                        LaunchAddActivityMessages();
                }catch (NullPointerException e) {
                    // TODO: handle exception
                }
            }
        });
        getintent(); //En el intent, recibimos la latitud, longitud de la posicion del usuario!
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
                    Animation anim = AnimationUtils.loadAnimation(MenuTabActivity.this, R.anim.lefttoright);
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
                Animation anim = AnimationUtils.loadAnimation(MenuTabActivity.this, R.anim.righttoleft);
                mDrawerLayout.setAnimation(anim);
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }

        });
        goStore = (Button) findViewById(R.id.btn_gostore);
        goStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                    i.putExtra("action", "store");
                    startActivityForResult(i, 0);
                    MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                        Intent i = new Intent(MenuTabActivity.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    } else {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                        Intent i = new Intent(MenuTabActivity.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
        if(isGuest.equals("yes"))
            Toast.makeText(MenuTabActivity.this, getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
        else
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
                    Intent i = new Intent(MenuTabActivity.this, ProfileMyInfoActivity.class);
                    i.putExtra("username", m_sUsername);
                    i.putExtra("user_title", m_sUserTitle);
                    i.putExtra("thumb", m_sThumb);
                    i.putExtra("info", m_sInfo);
                    i.putExtra("status", m_sStatus);
                    startActivityForResult(i, 0);
                    MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                }else{
                    Toast.makeText(MenuTabActivity.this,getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "timeline");
                startActivityForResult(i, 0);
                //MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                String Control = "com.innohawk.dan.VideosTab.VideosActivity";
                try {
                    FileOutputStream control = openFileOutput("aFileControlNavBar.txt", MODE_PRIVATE);
                    OutputStreamWriter aControl = new OutputStreamWriter(control);
                    aControl.write(Control);
                    aControl.flush();
                    aControl.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "buddees");
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudW");
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudS");
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudE");
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "BudC");
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudG = (LinearLayout) findViewById(R.id.layoutmenu_games);
        ll_BudG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(MenuTabActivity.this,getResources().getString(R.string.toolbar_menu_games_coming), Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(MenuTabActivity.this, ActivityDropMenu.class);
                i.putExtra("action", "Settings");
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(MenuTabActivity.this, HelpActivity.class);
                startActivityForResult(i, 0);
                MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent intent = new Intent(MenuTabActivity.this, Main.class);
                intent.putExtra("member_id", "");
                intent.putExtra("username", "");
                intent.putExtra("password", "");
                intent.putExtra("index", 0);
                startActivityForResult(intent, 1);
                finish();
            }
        });
    }
    //Server
    protected void reloadRemoteData() {
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        final String sGetMap;


        sMethod = "ih.getNotifications"; //El nuevo formato seria : getNotificationsNew
        sGetMap = "GetNotify";
        aParams = new Object[]{
                    o.getUsername(),
                    o.getPassword(),
                    Main.getLang(),
                    isGuest,
                    "0"
        };
        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initMenu((Object[]) map.get(sGetMap));
            }
        }, this);

    }
    protected void initMenu(Object[] aMenu) {
        m_aMenu = aMenu;
        if(getCountArray() == 0) {
            //RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
            //if (rl_back == null) {
                rl_notdata = (RelativeLayout) findViewById(R.id.rl_notdata_);
                layoutActivityDialogNotData = getLayoutInflater().inflate(R.layout.dialog_notdata, rl_notdata, false);
                rl_notdata.addView(layoutActivityDialogNotData);
                rl_notdata.startAnimation(AnimationUtils.loadAnimation(MenuTabActivity.this, R.anim.popup));
                Button btn_yes = (Button) layoutActivityDialogNotData.findViewById(R.id.btn_yes);
                btn_yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        rl_notdata.setVisibility(View.GONE);
                    }
                });
            //}
        }
        arrayList = new ArrayList<GetSetMaps>();
        arrayList.clear();
        for (int i = 0; i < getCountArray(); ++i)
        {
            GetSetMaps temp = new GetSetMaps();
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) m_aMenu[i];
            Log.d(TAG, "MAPEO: " + m_aMenu[i]);
            temp.setId(map.get("id"));
            temp.setTime(map.get("date"));
            temp.setRead(Integer.parseInt(String.valueOf(map.get("read"))));//Read
            temp.setFlagType(map.get("type")); //Type
            temp.setDesc(map.get("msg"));//Message::  msg es el old //new_text es el nuevo
            temp.setThubnailimage(map.get("img"));//
            temp.setName(map.get("user_created"));//User
            //'id_item'=> new xmlrpcval($row['id_item']),
            //'id_user_created'=> new xmlrpcval($row['user_created']),
            arrayList.add(temp);
            list_detail = (ListView) findViewById(R.id.list_detail);
            list_detail.setAdapter(null);
            LazyAdapter lazy = new LazyAdapter(MenuTabActivity.this, arrayList);
            lazy.notifyDataSetChanged();
            list_detail.setAdapter(lazy);
            list_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int posit, long id) {
                        // TODO Auto-generated method stub
                        MainPosition = 1;
                        pos = posit;
                        ContinueIntent();
                    }


            });
            list_detail.setLongClickable(true);
            list_detail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);
                    layoutActivityDialog = getLayoutInflater().inflate(R.layout.innohawk_dialog_remove, rl_dialoguser, false);
                    rl_dialoguser.addView(layoutActivityDialog);
                    rl_dialoguser.setVisibility(View.VISIBLE);
                    rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(MenuTabActivity.this, R.anim.popup));
                    Button btn_yes = (Button) layoutActivityDialog.findViewById(R.id.btn_yes);
                    btn_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                MainPosition = 1;
                                pos = position;
                                Connector o = Main.getConnector();
                                Object[] aParams;
                                String sMethod;
                                sMethod = "ih.trashNotifications";
                                aParams = new Object[]{
                                        o.getUsername(),
                                        o.getPassword(),
                                        Main.getLang(),
                                        isGuest,
                                        arrayList.get(pos).getId(),
                                        "0"
                                };
                                o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
                                    @SuppressWarnings("unchecked")
                                    public void callFinished(Object result) {
                                        reloadRemoteData();
                                        //Toast.makeText(MenuTabActivity.this, "result: " + result.toString(), Toast.LENGTH_LONG).show();
                                        //and update read
                                    }
                                }, MenuTabActivity.this);
                                rl_dialoguser.setVisibility(View.INVISIBLE);
                            }
                    });
                    Button btn_cancel = (Button) layoutActivityDialog.findViewById(R.id.btn_cancel);
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                rl_dialoguser.setVisibility(View.INVISIBLE);
                            }
                    });
                    return true;
                }
            });
        }
    }
    public int getCountArray() {
        return m_aMenu.length;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View ih = convertView;
            if (convertView == null) {
                //ih = inflater.inflate(R.layout.tabmenucellnew, null);
                ih = inflater.inflate(R.layout.tabmenucell, null);
            }

            //setName
            TextView txt_desc = (TextView) ih.findViewById(R.id.txt_description);
            if (arrayList.get(pos).getFlagType().equals("chat")) {
                txt_desc.setText(data.get(position).getName() + ":   " + data.get(position).getDesc());
            }else{
                txt_desc.setText(" " + data.get(position).getDesc());
            }
            //txt_desc.setText(Html.fromHtml(" " +data.get(position).getDesc()));
            //txt_desc.setMovementMethod(LinkMovementMethod.getInstance());
            txt_desc.setTypeface(tt);

            TextView txt_date = (TextView) ih.findViewById(R.id.txt_data);
            txt_date.setText(data.get(position).getTime());


            //Animation anim = AnimationUtils.loadAnimation(MenuTabActivity.this, R.anim.lefttoright); Animation anim1 = AnimationUtils.loadAnimation(MenuTabActivity.this, R.anim.right_slide_out);
            //trash.setAnimation(anim);

            //Type:: Si es nuevo o no
            TextView type = (TextView) ih.findViewById(R.id.newnoti); //si está premium o no
            int Flag_Type = data.get(position).getRead();
            if(Flag_Type == 0){
                type.setVisibility(View.VISIBLE);
                //type.setText(getResources().getString(R.string.notifications_new)); //Cogemos mail new
            }else{
                type.setVisibility(View.INVISIBLE);
            }

            //IMG BG
            String image = data.get(position).getThubnailimage().replace(" ", "%20");
            ImageView programImage = (ImageView) ih.findViewById(R.id.img_iconpin);
            //programImage.setImageResource(R.drawable.no_image);
            m_viewImageLoader = (LoadImgNew) ih.findViewById(R.id.media_images_image_view);
            m_viewImageLoader.setOnlySpinnerDrawable((String) image);
            if(image.isEmpty()) {
                image = String.valueOf(R.drawable.ic_menu_make_avatar);
                programImage.setImageResource(Integer.parseInt(image));
                final int radius = 30;
                final int margin = 2;
                final Transformation transformation = new RoundedCornersTransformation(radius, margin);
                Picasso.with(MenuTabActivity.this)
                        .load(image)
                        .transform(transformation)
                        .into(programImage);
            }else{
                final int radius = 30;
                final int margin = 2;
                final Transformation transformation = new RoundedCornersTransformation(radius, margin);
                Picasso.with(MenuTabActivity.this)
                        .load(image)
                        .transform(transformation)
                        .into(programImage);
            }


            return ih;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.gmap, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;

    }

    private void ContinueIntent() {
        progressDialog = new ProgressDialog(MenuTabActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();
        //update read
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        sMethod = "ih.readNotifications";
        aParams = new Object[]{
                o.getUsername(),
                o.getPassword(),
                Main.getLang(),
                isGuest,
                arrayList.get(pos).getId(),
                "0"
        };
        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                reloadRemoteData();
                //Toast.makeText(MenuTabActivity.this, "result: " + result.toString(), Toast.LENGTH_LONG).show();
            }
        }, MenuTabActivity.this);

        if (arrayList.get(pos).getFlagType().equals("friend")) {
            /*Intent iv = new Intent(MenuTabActivity.this, FriendsHomeActivity.class);
            //iv.putExtra("lat", "" + latitude_permission);
            startActivityForResult(iv, 0);
            MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/
            Intent iv = new Intent(MenuTabActivity.this, FriendsHomeActivity.class);
            iv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(new Intent(iv));
            this.overridePendingTransition(0, 0);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }else if (arrayList.get(pos).getFlagType().equals("mail")) {
            Intent iv = new Intent(MenuTabActivity.this, MailHomeActivity.class);
            iv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivityForResult(iv, 1);
            this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }else if (arrayList.get(pos).getFlagType().equals("chat")) {
           /* Intent iv = new Intent(MenuTabActivity.this, WebPageActivity.class);
            String password = o.getPassword();
            String username = o.getUsername();
            int id_member = o.getMemberId();
            String sMethod_;
            sMethod_ = "";
            sMethod_ = "modules%2Fpcint%2Fmobilechats%2Fclasses%2FPcintMobChatRequest%3Faction%3Dmenu";
            String sUrl = AppConfig.URL_METHODS + "r.php?user="+username+"&pwd="+password+"&url="+sMethod_+"%26mid%3D"+id_member+"%26isguest%3D"+isGuest+"%26user%3D"+username+"%26pwd%3D"+password;
            iv.putExtra("url", "{xmlrpc_url}r.php?url=modules%2Fpcint%2Fmobilechats%2Fclasses%2FPcintMobChatRequest.php%3Faction%3Dmenu%26mid%3D{member_id}&user={member_username}&pwd={member_password}");
            iv.putExtra("title", getResources().getString(R.string.TabChat));
            startActivityForResult(iv, 2);
            MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);*/
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }else if (arrayList.get(pos).getFlagType().equals("events")) {
            /*Intent iv = new Intent(MenuTabActivity.this, WebPageActivity.class);
            String password = o.getPassword();
            String username = o.getUsername();
            int id_member = o.getMemberId();
            String sMethod_;
            sMethod_ = "";
            sMethod_ = "modules%2Fpcint%2Fmobileevents%2Fclasses%2FPcintMobEventsVi.php%3Faction%3Dmenu";
            String sUrl = AppConfig.URL_METHODS + "r.php?user="+username+"&pwd="+password+"&url="+sMethod_+"%26mid%3D"+id_member+"%26isguest%3D"+isGuest+"%26user%3D"+username+"%26pwd%3D"+password;
            iv.putExtra("url", sUrl);
            iv.putExtra("title", getResources().getString(R.string.toolbar_menu_budevents));
            startActivityForResult(iv, 3);
            MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);*/
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }else{ //System
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    protected void LaunchActivityMessges() {
        Connector o = Main.getConnector();
        Intent iv = new Intent(MenuTabActivity.this, MailHomeActivity.class);
        startActivityForResult(iv, 0);
        MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
    }

    protected void LaunchAddActivityMessages() {
        Connector o = Main.getConnector();
        Intent iv = new Intent(MenuTabActivity.this, MailComposeActivity.class);
        startActivityForResult(iv, 0);
        MenuTabActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }
}
