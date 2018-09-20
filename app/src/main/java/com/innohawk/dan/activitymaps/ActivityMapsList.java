package com.innohawk.dan.activitymaps;

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
import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activityDropMenu.ActivityDropMenu;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.appconfig.RoundedCornersTransformation;
import com.innohawk.dan.helps.HelpActivity;
import com.innohawk.dan.profile.ProfileActivity;
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
public class ActivityMapsList extends ActivityBase {
    private static final String TAG = "OO ActivityMapsListView";
    String isGuest;
    View layoutActivityMaps; //Esto muestra el mensaje de no data y permisos ventana si androd>23
    RelativeLayout rl_dialog; //sale el mensaje d eaceptar permission!
    private String Error = null;
    protected Object[] m_aMenu; //Array con la lista
    public static ArrayList<GetSetMaps> arrayList;
    ListView list_detail;
    //Permission
    GPSTracker gps;
    static double latitude_permission;
    static double longitude_permission;
    private static final int MY_LOCATION = 0; //Tiene implicito el READ (que es el que necesitamos

    ProgressDialog progressDialog;

    int MainPosition = 0; //Esto es para ir a la ficha detalles
    int pos; // Es la posicion del elemento al hacer "click". No es el mismo que la id!!!!!

    //User Info
    protected Map<String, Object> m_map; //Para mapear datos dle usuario!!!!!
    protected String m_sUserTitle;
    protected String m_sUsername;
    protected String m_sThumb;
    protected String m_sInfo;
    protected String m_sStatus;
    //Header
    RelativeLayout rl_headerLayout,rl_headerFilterLayout;
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
    //Flags
    int filter_active;
    //Lo que recibimos de "mapas": Si viene de mapa, logicamente, la ltitud y la longitud
    String map;
    Button btn_map,btn_filter;
    //Filter:: solo si esl filtro esta activo
    String SpinnerTypes_,SpinnerCat_,SpinnerList_;
    String filter_search_String,filter_ratio_String,filter_age_String,filter_Gender_String,filter_Status_String,filter_Connects_String;
    double radius; //Esto es para hacer la bolita azul mas grande o mas pequeá, por defecto esta a 10 millas
    protected LoaderImageView m_viewImageLoader;

    ImageView bubblefilter;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.activity_maps_listview);
        //Inicializamos
        //Fx para saber si es guest!
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);
        if(isGuest.equals("no"))
            getUserInfo();
        //Config header
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        bubblefilter = (ImageView)findViewById(R.id.bubble_filter);
        bubblefilter.setVisibility(View.INVISIBLE);
        reload = (Button) findViewById(R.id.buttonRefresh);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
                prefs.edit().remove("isFilterActive").commit();
                radius = 10 * 1.8383 * 1000; //1.8 es pq al server le pasamos los km!!! al recibir del server, lo dividiremos para dibujar
                filter_active = 0;
                bubblefilter.setVisibility(View.INVISIBLE);
                reloadRemoteData();
            }
        });
        btn_map = (Button) findViewById(R.id.btn_gomap);
        btn_filter = (Button) findViewById(R.id.btn_filter);
        initconfigheader();
        getintent(); //En el intent, recibimos la latitud, longitud de la posicion del usuario!

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
                m_sThumb = (String) m_map.get("thumb");
                m_sStatus = (String) m_map.get("status");
                m_sInfo = (String) m_map.get("user_info");
                m_sUserTitle = (String) m_map.get("user_title");
                if (null != m_map.get("search_with_photos"))
                    o.setSearchWithPhotos(((String) m_map.get("search_with_photos")).equals("1") ? true : false);
            }

        }, this);
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
                    Animation anim = AnimationUtils.loadAnimation(ActivityMapsList.this, R.anim.lefttoright);
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
                Animation anim = AnimationUtils.loadAnimation(ActivityMapsList.this, R.anim.righttoleft);
                mDrawerLayout.setAnimation(anim);
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }

        });
        goStore = (Button) findViewById(R.id.btn_gostore);
        goStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                    i.putExtra("action", "store");
                    startActivityForResult(i, 0);
                    ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                        Intent i = new Intent(ActivityMapsList.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    } else {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                        Intent i = new Intent(ActivityMapsList.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
        map = "no";
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //ActivityMapsList.this.finish();
                //overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                Intent ih = new Intent(ActivityMapsList.this, ActivityMaps.class);
                ih.putExtra("filteractive", "" + filter_active);
                ih.putExtra("lat", "" + latitude_permission);
                ih.putExtra("lng", "" + longitude_permission);
                //Nota: Si el filtro esta activo, el listado debe salir del filtro... asi que pasaremos los datos!
                if(filter_active == 1) {
                    ih.putExtra("KeySearch", ""+filter_search_String);
                    ih.putExtra("Ratio", ""+filter_ratio_String);
                    ih.putExtra("Age", ""+filter_age_String);
                    ih.putExtra("Gender", ""+filter_Gender_String);
                    ih.putExtra("Status", ""+filter_Status_String);
                    ih.putExtra("SpinnerTypes", ""+SpinnerTypes_);
                    ih.putExtra("SpinnerCat", ""+SpinnerCat_);
                    ih.putExtra("SpinnerList", ""+SpinnerList_);
                    ih.putExtra("Connect", ""+filter_Connects_String);
                    ih.putExtra("APP_PACKAGE_EXTRAS", "com.filter");
                    ih.putExtra("lat", ""+latitude_permission);
                    ih.putExtra("lng", ""+longitude_permission);
                    ih.putExtra("filteractive", "1");
                }
                //timer.cancel(); //no lo aconsejamos x finish activity en listview
                startActivity(ih);
                overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent ih = new Intent(ActivityMapsList.this, ActivityMapsFilter.class);
                ih.putExtra("map", "" + "no");
                ih.putExtra("filteractive", "" + filter_active);
                ih.putExtra("lat", "" + latitude_permission);
                ih.putExtra("lng", "" + longitude_permission);
                //timer.cancel(); //no lo aconsejamos x finish activity en listview
                startActivity(ih);
                overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
    }
    private void getintent() {
        // TODO Auto-generated method stub

        SharedPreferences filter_prefs = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
        if(filter_prefs.getString("isFilterActive",null) == null) {
            Intent ih = getIntent();
            latitude_permission = Double.parseDouble(ih.getStringExtra("lat"));
            longitude_permission = Double.parseDouble(ih.getStringExtra("lng"));
            filter_active = (int) Double.parseDouble(ih.getStringExtra("filteractive"));
            MapsInitializer.initialize(getApplicationContext());
            filter_active = 0;
            bubblefilter.setVisibility(View.INVISIBLE);
            //Toast.makeText(ActivityMapsList.this,"Activity--List; No filter", Toast.LENGTH_LONG).show();
        }else {
            //Toast.makeText(ActivityMapsList.this,"Activity--List; FILTER ON", Toast.LENGTH_LONG).show();
            bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar hab
            filter_active = 1; //esta activado
            Intent ih = getIntent();
            String pack = ih.getStringExtra("APP_PACKAGE_EXTRAS");
            if(pack != null) {
                filter_search_String = ih.getStringExtra("KeySearch");
                filter_ratio_String = ih.getStringExtra("Ratio");
                filter_age_String = ih.getStringExtra("Age");
                filter_Gender_String = ih.getStringExtra("Gender");
                filter_Status_String = ih.getStringExtra("Status");
                SpinnerTypes_ = ih.getStringExtra("SpinnerTypes");
                SpinnerCat_ = ih.getStringExtra("SpinnerCat");
                SpinnerList_ = ih.getStringExtra("SpinnerList");
                filter_Connects_String = ih.getStringExtra("Connect");
                MapsInitializer.initialize(getApplicationContext());
                if (filter_ratio_String == null)
                    filter_ratio_String = "10";
                double new_radio = Double.parseDouble(filter_ratio_String);
                radius = new_radio * 1.8383 * 1000;
                latitude_permission = Double.parseDouble(ih.getStringExtra("lat"));
                longitude_permission = Double.parseDouble(ih.getStringExtra("lng"));
                filter_active = (int) Double.parseDouble(ih.getStringExtra("filteractive"));
                filter_active = 1;
                bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar hab
                //Toast.makeText(ActivityMapsList.this,"El filtro está acticado... :"+SpinnerTypes_ + "::"+filter_Gender_String , Toast.LENGTH_LONG).show();
            }else{
                radius = 10 * 1.8383 * 1000;
                filter_active = 0;
                bubblefilter.setVisibility(View.INVISIBLE);
                //Toast.makeText(ActivityMapsList.this,"FILTRO PERO SIN DATOS, ASI QUE COMO SI NADA... ", Toast.LENGTH_LONG).show();
            }
        }

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
                    Intent i = new Intent(ActivityMapsList.this, ProfileMyInfoActivity.class);
                    i.putExtra("username", m_sUsername);
                    i.putExtra("user_title", m_sUserTitle);
                    i.putExtra("thumb", m_sThumb);
                    i.putExtra("info", m_sInfo);
                    i.putExtra("status", m_sStatus);
                    startActivityForResult(i, 0);
                    ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                }else{
                    Toast.makeText(ActivityMapsList.this,getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "timeline");
                startActivityForResult(i, 0);
                //ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "buddees");
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "BudW");
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "BudS");
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "BudE");
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "BudC");
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudG = (LinearLayout) findViewById(R.id.layoutmenu_games);
        ll_BudG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(ActivityMapsList.this,getResources().getString(R.string.toolbar_menu_games_coming), Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(ActivityMapsList.this, ActivityDropMenu.class);
                i.putExtra("action", "Settings");
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent i = new Intent(ActivityMapsList.this, HelpActivity.class);
                startActivityForResult(i, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                Intent intent = new Intent(ActivityMapsList.this, Main.class);
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

        progressDialog = new ProgressDialog(ActivityMapsList.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();

        String lat_server = String.valueOf(latitude_permission);
        String lng_server = String.valueOf(longitude_permission);


        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        final String sGetMap;

        if(filter_active == 1) {
            bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar hab
            sMethod = "ih.filtermaps";
            sGetMap = "ihMapsListFilter";
            aParams = new Object[]{
                    lat_server,
                    lng_server,
                    o.getUsername(),
                    o.getPassword(),
                    Main.getLang(),
                    isGuest,
                    SpinnerTypes_,
                    SpinnerCat_,
                    SpinnerList_,
                    filter_Connects_String,
                    filter_age_String,
                    filter_Gender_String,
                    filter_Status_String,
                    filter_search_String,
                    filter_ratio_String,
                    "0"
            };
        }else{
            bubblefilter.setVisibility(View.INVISIBLE);
            sMethod = "ih.initmaps";
            sGetMap = "ihMapsList";
            aParams = new Object[]{
                    lat_server,
                    lng_server,
                    o.getUsername(),
                    o.getPassword(),
                    Main.getLang(),
                    isGuest,
                    "android",
                    "list",
                    "0"
            };
        }

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initMenu((Object[]) map.get(sGetMap));
            }
        }, this);

    }
    protected void initMenu(Object[] aMenu) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        m_aMenu = aMenu;
        arrayList = new ArrayList<GetSetMaps>();
        arrayList.clear();
        for (int i = 0; i < getCountArray(); ++i)
        {
            GetSetMaps temp = new GetSetMaps();
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) m_aMenu[i];
            if(map.get("visibility").equals("yes")) {
                temp.setId(map.get("id"));
                temp.setName(map.get("name"));
                temp.setAddress(map.get("address"));
                temp.setLatitude(map.get("latitude"));
                temp.setLongitude(map.get("longitude"));
                temp.setDistance(map.get("distance"));
                temp.setFlagType(map.get("type"));
                temp.setSex(map.get("sex"));
                temp.setTime(map.get("age"));
                temp.setThubnailimage(map.get("thumb"));
                temp.setUsername(map.get("username"));
                temp.setAdressList(map.get("city")); //Lista que lo pasamos a city.. habra que cambiarlo
                arrayList.add(temp);
                list_detail = (ListView) findViewById(R.id.list_detail);
                list_detail.setAdapter(null);
                LazyAdapter lazy = new LazyAdapter(ActivityMapsList.this, arrayList);
                lazy.notifyDataSetChanged();
                list_detail.setAdapter(lazy);
                list_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        MainPosition = 1;
                        pos = position;
                        ContinueIntent();
                    }
                });
            }


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
        public View getView(int position, View convertView, ViewGroup parent) {
            View ih = convertView;
            if (convertView == null) {
                //ih = inflater.inflate(R.layout.activity_maps_listviewcells, null);
                ih = inflater.inflate(R.layout.activity_maps_listviewcellsnew, null);
            }

            /*
            int color = 0xFF979494; // Gris Oscuro
            if (position%2==0) {
                color = 0xFFBDBDBD; // Gris claro
            }

            ih.setBackgroundColor(color);
            */

            //OLD STYLE
            /*
            TextView txt_km = (TextView) ih.findViewById(R.id.txt_distance);
            txt_km.setText(data.get(position).getDistance() + " milles");
            txt_km.setTypeface(tt);
            TextView txt_rname = (TextView) ih.findViewById(R.id.txt_name);
            txt_rname.setText(data.get(position).getName());
            txt_rname.setTypeface(tt);

            String Sex = data.get(position).getSex();
            //Typo pIN
            ImageView pin = (ImageView) ih.findViewById(R.id.img_iconpin); //premium
            String Flag_active = data.get(position).getFlagType();
            if(Flag_active.equals("dispensaries")){
                pin.setBackgroundResource(R.drawable.activity_maps_pindispensaries);
            }else if(Flag_active.equals("profiles")){
                if(Sex.equals("male"))
                    pin.setBackgroundResource(R.drawable.activity_maps_pinmale);
                else
                    pin.setBackgroundResource(R.drawable.activity_maps_pinfemale);
            }else{ //GUEST
                pin.setBackgroundResource(R.drawable.activity_maps_pinguest);
            }


            //Type
            ImageView type = (ImageView) ih.findViewById(R.id.img_icontype); //premium
            String Flag_Type = data.get(position).getFlagType();
            if(Flag_Type.equals("dispensaries")){
                type.setBackgroundResource(R.drawable.dispensary_card);
            }else if(Flag_Type.equals("profiles")){
                type.setBackgroundResource(R.drawable.dispensary_chat);
            }else{
                type.setBackgroundResource(R.color.color_trans);
            }

            */
            //NEW STYLE
            Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
            TextView title = (TextView) ih.findViewById(R.id.text_title);
            title.setTypeface(tt);
            title.setText(" " +data.get(position).getName());
            TextView age = (TextView) ih.findViewById(R.id.txt_age);
            age.setTypeface(tt);
            TextView txt_km = (TextView) ih.findViewById(R.id.txt_distance);
            txt_km.setTypeface(tt);
            txt_km.setText(" " +data.get(position).getDistance() + " " +getResources().getString(R.string.ud_distance));
            TextView sex = (TextView) ih.findViewById(R.id.txt_sex);
            sex.setTypeface(tt);
            String Flag_active = data.get(position).getFlagType();
            if (Flag_active.equals("dispensaries")) {
                age.setText("");
                age.setVisibility(View.INVISIBLE);
                sex.setText(" " +data.get(position).getAddress() + " - " +data.get(position).getAdressList());

            } else if (Flag_active.equals("profiles")) {
                age.setText(" " +data.get(position).getTime() +" "+getResources().getString(R.string.ud_age));
                sex.setText(" " +data.get(position).getSex());
            } else {
                age.setText(" ");
                sex.setText(" " +data.get(position).getSex());
            }
            //IMG BG
            String image = data.get(position).getThubnailimage().replace(" ", "%20");

            //Log.d(TAG, "IMAGEN THUMB ES:" + image);

            ImageView programImage = (ImageView) ih.findViewById(R.id.img_thumb);
            m_viewImageLoader = (LoaderImageView) ih.findViewById(R.id.media_images_image_view);
            m_viewImageLoader.setOnlySpinnerDrawable((String) image);
            if(image.isEmpty()) {
                image = String.valueOf(R.drawable.logo_about_256);
                programImage.setImageResource(Integer.parseInt(image));
            }else{
                final int radius = 6;
                final int margin = 0;
                final Transformation transformation = new RoundedCornersTransformation(radius, margin);
                Picasso.with(ActivityMapsList.this)
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
        progressDialog = new ProgressDialog(ActivityMapsList.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();
        if (MainPosition == 1) {
            if(arrayList.get(pos).getFlagType().equals("dispensaries")) {
                Intent iv = new Intent(ActivityMapsList.this, ActivityMapsDispensaries.class);
                iv.putExtra("lat", "" + latitude_permission);
                iv.putExtra("lng", "" + longitude_permission);
                iv.putExtra("id", "" + arrayList.get(pos).getId());
                startActivityForResult(iv, 0);
                ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }else{
                if(arrayList.get(pos).getFlagType().equals("profiles")) {
                    if(isGuest.equals("no")) {
                        Intent i = new Intent(ActivityMapsList.this, ProfileActivity.class);
                        i.putExtra("username", arrayList.get(pos).getUsername());
                        startActivityForResult(i, 0);
                        ActivityMapsList.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                    }else{
                        Toast.makeText(ActivityMapsList.this,getResources().getString(R.string.userGuest_exception_noprofile), Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(ActivityMapsList.this,getResources().getString(R.string.userGuest_exception_noprofile), Toast.LENGTH_LONG).show();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }
}
