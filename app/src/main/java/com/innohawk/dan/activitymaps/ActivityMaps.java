package com.innohawk.dan.activitymaps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innohawk.dan.Connector;
import com.innohawk.dan.FragmentActivityBase;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by innohawk on 20/10/17.
 */



public class ActivityMaps extends FragmentActivityBase{

    private static final String TAG = "OO ActivityMaps";
    private static final boolean VERBOSE = false;
    // Google Map
    private GoogleMap googleMap;
    private HashMap<CustomMarker, Marker> markersHashMap; //Puntos dispensarios
    private Iterator<Map.Entry<CustomMarker, Marker>> iter;
    private HashMap<CustomMarker, Marker> markersHashMapUsers; //Puntos Usuarios
    private Iterator<Map.Entry<CustomMarker, Marker>> iterUser;
    ArrayList<CustomMarker> arraySaveListUsers; //Array que guarda el customMarket
    private CameraUpdate camera;
    private CustomMarker customMarkerOne;
    private CustomMarker customMarkerUser;//Marcas PIN donde se guardan los usuarios!!!!
    Button btn_map, btn_detail, btn_reload;
    String lat, lng, name, address, id, latitude, longitude;
    //Visualizar las estrellas
    RatingBar rb;
    Float number_to_rating;
    String isGuest;
    ProgressDialog progressDialog;
    Integer counter = 1;
    ProgressBar CustomprogressBar;
    String[] separateddata;
    View layoutActivityMaps, layoutHomePermission, layoutMapsButtons; //Esto muestra el mensaje de no data y permisos ventana si androd>23
    RelativeLayout rl_dialog; //sale el mensaje d eaceptar permission!
    RelativeLayout rl_dialoguser;
    RelativeLayout rl_headerLayout;
    private String Error = null;
    RelativeLayout rl_information_maps_dispensaries; //Special info maps dispensaries
    View layoutViewInforDispensariesMaps; //Special info maps dispensaries
    RelativeLayout rl_information_maps_users; //Special info maps dispensaries
    View layoutViewInforUsersMaps; //Special info maps dispensaries

    protected Object[] m_aMenu; //Array con las coordenadas iniciales de los dispensarios
    protected Object[] m_aUserCoor; //Array con las coordenadas iniciales de los usuarios
    ArrayList<GetSetMaps> pinsmarker;

    //FIREBASE
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //Permission
    GPSTracker gps;
    static double latitude_permission;
    static double longitude_permission;
    private static final int MY_LOCATION = 0;
    int iClicks;
    String map;

    //Badge
    protected String bubbleLetters;
    protected String bubbleFriends;
    protected String bubbleChats;
    protected String bubbleFeeds;
    protected String bubbleDeals;

    //User Info
    protected Map<String, Object> m_map;
    protected String m_sUserTitle;
    protected String m_sUsername;
    protected String m_sThumb;
    protected String m_sInfo;
    protected String m_sStatus;

    String SpinnerTypes_, SpinnerCat_, SpinnerList_;
    String filter_search_String, filter_ratio_String, filter_age_String, filter_Gender_String, filter_Status_String, filter_Connects_String;
    double radius;

    //Custom Header
    private SQLiteActivity db; //logout
    int flag_menu;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Button btn_menu, btn_menu_close, goMyMessage, goAddMessage;
    EditText edit_search;
    String search = " ";
    Button goStore;
    RelativeLayout CloseDropMenu, CloseDropMenuBg;
    //Normal Header
    protected TextView textTitle;
    Button reload;
    Button Back;
    Typeface tt;

    //Filter
    int filter_active;

    //Converter
    static double d;
    static double miles;

    ImageView bubblefilter;

    //My custom pin
    Marker mPositionMarker;
    String getSexMember;



    int from = 0;
    int to = 100;
    int total_dispensarios = 0;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.activity_maps);
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest", null);

        //Recogemos en variables
        m_sUsername = prefs.getString("username", null);
        m_sUserTitle = m_sUsername;
        if (isGuest.equals("no"))
            getUserInfo();

        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");

        setHeaderLayaout();

        //Radio por defecto de la bolita
        radius = 10 * 1.8383 * 1000; //1.8 es pq al server le pasamos los km!!! al recibir del server, lo dividiremos para dibujar

        SharedPreferences filter_prefs = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
        if (filter_prefs.getString("isFilterActive", null) == null) {
            filter_active = 0;
            bubblefilter.setVisibility(View.INVISIBLE);
        } else {
            filter_active = 1; //esta activado
            bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar hab
        }

        if (filter_active == 1) {
            getintentFilter();
        }

        verifyPermissionLocation();
    }


    class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; counter <= params[0]; counter++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Tarea completa!. =)";
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.setMessage("YA!!!!!!!!");
        }
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Vamos!!!!");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(getString(R.string.loading)+  values[0]);
        }
    }

    private void initconfigheader() {
        // TODO Auto-generated method stub
        //De momento ocultamos la opacidad
        CloseDropMenu = (RelativeLayout) findViewById(R.id.rl_background_closeDropmenu);
        CloseDropMenuBg = (RelativeLayout) findViewById(R.id.rl_background_closeDropmenu_image);
        CloseDropMenu.setVisibility(View.INVISIBLE);
        CloseDropMenuBg.setVisibility(View.INVISIBLE);
        btn_menu_close = (Button) findViewById(R.id.btn_menu_close); //only when drap its open
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
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.lefttoright);
                    CloseDropMenuBg.setVisibility(View.VISIBLE);
                    mDrawerLayout.setAnimation(anim);
                    flag_menu = 1;
                    CloseDropMenu.setVisibility(View.VISIBLE);
                } else {
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
                Animation anim = AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.righttoleft);
                mDrawerLayout.setAnimation(anim);
                mDrawerLayout.setVisibility(View.INVISIBLE);
            }

        });
        goStore = (Button) findViewById(R.id.btn_gostore);
        goStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                    i.putExtra("action", "store");
                    startActivityForResult(i, 0);
                    ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                } catch (NullPointerException e) {
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
                        Intent i = new Intent(ActivityMaps.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    } else {
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                        Intent i = new Intent(ActivityMaps.this, SearchHeaderActivity.class);
                        i.putExtra("search", search);
                        startActivityForResult(i, 0);
                        ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                        edit_search.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
        drawer();
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
        TextView _budName = (TextView) findViewById(R.id.txt_name);
        _budName.setTypeface(tt);
        TextView _profile = (TextView) findViewById(R.id.ll_profile_text);
        _profile.setTypeface(tt);
        TextView _timeline = (TextView) findViewById(R.id.ll_timeline_text);
        _timeline.setTypeface(tt);
        TextView _buddees = (TextView) findViewById(R.id.ll_buddees_text);
        _buddees.setTypeface(tt);
        TextView _budw = (TextView) findViewById(R.id.ll_world_text);
        _budw.setTypeface(tt);
        TextView _buds = (TextView) findViewById(R.id.ll_strains_text);
        _buds.setTypeface(tt);
        TextView _bude = (TextView) findViewById(R.id.ll_events_text);
        _bude.setTypeface(tt);
        TextView _budc = (TextView) findViewById(R.id.ll_ads_text);
        _budc.setTypeface(tt);
        TextView _budg = (TextView) findViewById(R.id.ll_games_text);
        _budg.setTypeface(tt);
        TextView _config = (TextView) findViewById(R.id.ll_settings_text);
        _config.setTypeface(tt);
        TextView _about = (TextView) findViewById(R.id.ll_contact_text);
        _about.setTypeface(tt);
        TextView _exit = (TextView) findViewById(R.id.ll_logout_text);
        _exit.setTypeface(tt);
        //Bud Title: Go website
        LinearLayout ll_goweb = (LinearLayout) findViewById(R.id.layoutmenu_website);
        ll_goweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
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
                if (isGuest.equals("no")) {
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    if (flag_menu == 0) {
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                        mDrawerLayout.setVisibility(View.VISIBLE);
                        flag_menu = 1;
                    } else {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        mDrawerLayout.setVisibility(View.INVISIBLE);
                        CloseDropMenu.setVisibility(View.INVISIBLE);
                        CloseDropMenuBg.setVisibility(View.INVISIBLE);
                        flag_menu = 0;
                    }
                    Intent i = new Intent(ActivityMaps.this, ProfileMyInfoActivity.class);
                    i.putExtra("username", m_sUsername);
                    i.putExtra("user_title", m_sUserTitle);
                    i.putExtra("thumb", m_sThumb);
                    i.putExtra("info", m_sInfo);
                    i.putExtra("status", m_sStatus);
                    startActivityForResult(i, 0);
                    ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                } else {
                    Toast.makeText(ActivityMaps.this, getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
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
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "timeline");
                startActivityForResult(i, 0);
                //ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "buddees");
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
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
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "BudW");
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudS = (LinearLayout) findViewById(R.id.layoutmenu_strains);
        ll_BudS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "BudS");
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudE = (LinearLayout) findViewById(R.id.layoutmenu_events);
        ll_BudE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "BudE");
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudC = (LinearLayout) findViewById(R.id.layoutmenu_ads);
        ll_BudC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "BudC");
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_BudG = (LinearLayout) findViewById(R.id.layoutmenu_games);
        ll_BudG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(ActivityMaps.this, getResources().getString(R.string.toolbar_menu_games_coming), Toast.LENGTH_LONG).show();
            }
        });
        LinearLayout ll_Settings = (LinearLayout) findViewById(R.id.layoutmenu_settings);
        ll_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, ActivityDropMenu.class);
                i.putExtra("action", "Settings");
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        LinearLayout ll_Contact = (LinearLayout) findViewById(R.id.layoutmenu_contact);
        ll_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    mDrawerLayout.setVisibility(View.INVISIBLE);
                    CloseDropMenu.setVisibility(View.INVISIBLE);
                    CloseDropMenuBg.setVisibility(View.INVISIBLE);
                    flag_menu = 0;
                }
                Intent i = new Intent(ActivityMaps.this, HelpActivity.class);
                startActivityForResult(i, 0);
                ActivityMaps.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });

        LinearLayout ll_logout = (LinearLayout) findViewById(R.id.layoutmenu_logout);
        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView logout = (TextView) findViewById(R.id.ll_logout_text);
                mDrawerLayout.setVisibility(View.INVISIBLE);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (flag_menu == 0) {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    mDrawerLayout.setVisibility(View.VISIBLE);
                    flag_menu = 1;
                } else {
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
                Intent intent = new Intent(ActivityMaps.this, Main.class);
                intent.putExtra("member_id", "");
                intent.putExtra("username", "");
                intent.putExtra("password", "");
                intent.putExtra("index", 0);
                startActivityForResult(intent, 1);
                finish();
            }
        });
    }

    //Solo si el filstro esta activo
    private void getintentFilter() {
        // TODO Auto-generated method stub
        Intent ih = getIntent();
        // Get the extras (if there are any)
        if (getIntent() != null && getIntent().getExtras() != null) {
            String pack = ih.getStringExtra("APP_PACKAGE_EXTRAS");
            if (pack != null) {
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
                filter_active = 1;
                bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar habilitado
                //Toast.makeText(ActivityMaps.this,"El filtro está acticado... "+SpinnerTypes_ + " "+SpinnerList_, Toast.LENGTH_LONG).show();
            } else {
                radius = 10 * 1.8383 * 1000; //Por defecto a 10
                filter_active = 0;
                bubblefilter.setVisibility(View.INVISIBLE);
                //Toast.makeText(ActivityMaps.this,"FILTRO PERO NO FILTRO... ", Toast.LENGTH_LONG).show();
            }

        } else {
            radius = 10 * 1.8383 * 1000;
            filter_active = 0;
            bubblefilter.setVisibility(View.INVISIBLE);
            //Toast.makeText(ActivityMaps.this,"NO FILTRO... ", Toast.LENGTH_LONG).show();
        }
    }

    protected void getUserInfo() {
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        String token_fireSaved = prefs.getString("token_firebase", null);

        Object[] aParams;
        String sMethod;
        Connector o = Main.getConnector();
        Object[] aParamsLocal = {
                o.getUsername(),
                o.getPassword(),
                Main.getLang(),
                token_fireSaved
        };
        aParams = aParamsLocal;
        sMethod = "ih.getHomepageFirebase";


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
                //Get Sex
                getSexMember = (String) m_map.get("sex_user");
                if (null != m_map.get("search_with_photos"))
                    o.setSearchWithPhotos(((String) m_map.get("search_with_photos")).equals("1") ? true : false);
            }

        }, this);
    }

    protected void reloadRemoteData() { //Primer metodo... aqui tb aconseguimos el numero total de dispensarios!!!!!

        //Definimos la progress bar
        //progressDialog = new ProgressDialog(ActivityMaps.this,R.style.CustomDialog);
        progressDialog = new ProgressDialog(ActivityMaps.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();

        String lat_server = String.valueOf(latitude_permission);
        String lng_server = String.valueOf(longitude_permission);

        //new MyAsyncTask().execute(20);
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        final String sGetMap;

        if (filter_active == 1) {
            bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar habilitado
            sMethod = "ih.filtermaps";
            sGetMap = "ihMapsFilter";
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
        } else {
            bubblefilter.setVisibility(View.INVISIBLE);
            sMethod = "ih.initmapsByParts"; //initmaps
            sGetMap = "ihMaps";
            aParams = new Object[]{
                    lat_server,
                    lng_server,
                    o.getUsername(),
                    o.getPassword(),
                    Main.getLang(),
                    isGuest,
                    "android",
                    "map",
                    String.valueOf(from),
                    String.valueOf(to),
                    "0"
            };
        }

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                //Total de dispensarios:: Esto es nuevo!!!!
                total_dispensarios = Integer.parseInt((String) map.get("total_dispensaries")); //Con esto obtenemos dispensarios
                //Bubbles
                Map<String, Object> mapBubbles = (Map<String, Object>) result;
                bubbleFeeds = (String) mapBubbles.get("badge_feeds"); //Write Cmt?: 1
                bubbleDeals = (String) mapBubbles.get("badge_deals"); //Notificaciones: 5
                bubbleChats = (String) mapBubbles.get("badge_chat"); //Chats: 4
                //Cmt y News
                TextView badge_feed = (TextView) findViewById(R.id.badge_notification_1);
                badge_feed.setVisibility(View.VISIBLE);
                badge_feed.setText(bubbleFeeds);
                if (bubbleFeeds.equals("0")) {
                    badge_feed.setVisibility(View.INVISIBLE);
                }

                TextView badge_deal = (TextView) findViewById(R.id.badge_notification_5);
                badge_deal.setVisibility(View.VISIBLE);
                badge_deal.setText(bubbleDeals);
                if (bubbleDeals.equals("0")) {
                    badge_deal.setVisibility(View.INVISIBLE);
                }
                TextView badge_chat = (TextView) findViewById(R.id.badge_notification_4);
                badge_chat.setVisibility(View.VISIBLE);
                badge_chat.setText(bubbleChats);
                if (bubbleChats.equals("0")) {
                    badge_chat.setVisibility(View.INVISIBLE);
                }
                //Guardamos los valores en las fx
                Connector ih = Main.getConnector();
                ih.setUnreadFeedsNum(Integer.parseInt(bubbleFeeds));
                ih.setUnreadDealsNum(Integer.parseInt(bubbleDeals));
                ih.setUnreadChatsNum(Integer.parseInt(bubbleChats));
                //Creamos fichero de Bubble for Chat
                try {
                    FileOutputStream bubbleChat = openFileOutput("aFileBubbleChat.txt", MODE_PRIVATE);
                    OutputStreamWriter aControlBubbleChat = new OutputStreamWriter(bubbleChat);
                    aControlBubbleChat.write(bubbleChats);
                    aControlBubbleChat.flush();
                    aControlBubbleChat.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
                //Creamos fichero de Bubble for Feed
                try {
                    FileOutputStream bubbleFeed = openFileOutput("aFileBubbleFeed.txt", MODE_PRIVATE);
                    OutputStreamWriter aControlBubbleFeed = new OutputStreamWriter(bubbleFeed);
                    aControlBubbleFeed.write(bubbleFeeds);
                    aControlBubbleFeed.flush();
                    aControlBubbleFeed.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
                //Creamos fichero de Bubble for Deals
                try {
                    FileOutputStream bubbleDeal = openFileOutput("aFileBubbleDeals.txt", MODE_PRIVATE);
                    OutputStreamWriter aControlBubbleDeal = new OutputStreamWriter(bubbleDeal);
                    aControlBubbleDeal.write(bubbleDeals);
                    aControlBubbleDeal.flush();
                    aControlBubbleDeal.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
                //Deshabilitamos las Actividades que no tienen badgets...
                TextView badge_home = (TextView) findViewById(R.id.badge_notification_2);//Video?
                TextView badge_more = (TextView) findViewById(R.id.badge_notification_3);//Maps
                badge_home.setVisibility(View.INVISIBLE);
                badge_more.setVisibility(View.INVISIBLE);
                initMenu((Object[]) map.get(sGetMap));
            }
        }, this);

    }


    protected void initMenu(Object[] aMenu) {
        m_aMenu = aMenu;
        //setCustomMarkerOneMap(); //Metodo original:: OLD FX
        setCustomMarkerOneMapByFirstPart(); //Metodo de pruebas:: NEW FX
    }
    public int getCount() {
        return m_aMenu.length;
    }
    public Object getItem(int position) {
        // not implemented
        return null;
    }
    public long getItemId(int position) {
        return position;
    }
    private void initilizeMap() {

        //googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap_) {
                googleMap = googleMap_;
                // check if map is created successfully or not
                if (googleMap == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.GPS_location_textDecline), Toast.LENGTH_SHORT).show();
                }

                (findViewById(R.id.mapFragment)).getViewTreeObserver()
                        .addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                // gets called after layout has been done but
                                // before
                                // display
                                // so we can get the height then hide the view
                                if (android.os.Build.VERSION.SDK_INT >= 16) {
                                    (findViewById(R.id.mapFragment)).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                } else {
                                    (findViewById(R.id.mapFragment)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                }

                            }
                        });


                //Borramos los dibujos
                googleMap.clear();
                double new_radius = radius;//Dibujamos con el valor del radio, sean 10, o el valor que haya puesto el user
                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(latitude_permission, longitude_permission))
                        .radius(new_radius)
                        .strokeWidth(1)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.parseColor("#500084d3"));
                // Supported formats are: #RRGGBB #AARRGGBB
                //   #AA is the alpha, or amount of transparency
                googleMap.addCircle(circleOptions);
                initializeUiSettings();
                initializeMapLocationSettings();
                initializeMapTraffic();
                initializeMapType();
                initializeMapViewSettings();


            }
        });
        // check if map is created successfully or not
        /*if (googleMap == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.GPS_location_textDecline), Toast.LENGTH_SHORT).show();
        }

        (findViewById(R.id.mapFragment)).getViewTreeObserver()
                .addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // gets called after layout has been done but
                        // before
                        // display
                        // so we can get the height then hide the view
                        if (android.os.Build.VERSION.SDK_INT >= 16) {
                            (findViewById(R.id.mapFragment)).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            (findViewById(R.id.mapFragment)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                    }
                });


        //Borramos los dibujos
        googleMap.clear();
        double new_radius = radius;//Dibujamos con el valor del radio, sean 10, o el valor que haya puesto el user
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude_permission, longitude_permission))
                .radius(new_radius)
                .strokeWidth(1)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3"));
        // Supported formats are: #RRGGBB #AARRGGBB
        //   #AA is the alpha, or amount of transparency
        googleMap.addCircle(circleOptions);

*/


    }

    //new fx load more elements maps
    public void setCustomMarkerOneMapByFirstPart() {
        if (Error != null) {
            RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
            if (rl_back == null) {
                rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);
                layoutActivityMaps = getLayoutInflater().inflate(R.layout.dialog_notdata, rl_dialoguser, false);
                rl_dialoguser.addView(layoutActivityMaps);
                rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                Button btn_yes = (Button) layoutActivityMaps.findViewById(R.id.btn_yes);
                btn_yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        rl_dialoguser.setVisibility(View.GONE);
                    }
                });
            }
        } else {
            for (int i = 0; i < getCount(); i++) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) m_aMenu[i];
                String iId_element = map.get("id");
                String sName = map.get("name");
                String sAddress = map.get("address");
                String sCity = map.get("city");
                String fDistance = map.get("distance");
                String fLat = map.get("latitude");
                String fLong = map.get("longitude");
                String fRate = map.get("rate");
                String sType = map.get("type");
                String sThumb = map.get("thumb");
                String sSex = map.get("sex");
                String sHour = map.get("hour");
                String sTypeCat = map.get("type_cat");
                String sAge = map.get("age");
                String lat1 = fLat;
                String lng1 = fLong;
                Drawable circleDrawable = getResources().getDrawable(R.drawable.activity_maps_pindispensaries);
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                customMarkerOne = new CustomMarker("markerOne", Double.parseDouble(lat1), Double.parseDouble(lng1));
                final MarkerOptions markerOption = new MarkerOptions().position(new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude())).icon(markerIcon).title(sName + "::" + sAddress + "::" + sCity + "::" + fDistance + "::" + sType + "::" + fLat + "::" + fLong + "::" + iId_element + "::" + fRate + "::" + sThumb + "::" + sSex + "::" + "sUsername" + "::" + sHour + "::" + sTypeCat + "::" + sAge);
                Marker newMark = googleMap.addMarker(markerOption);
                addMarkerToHashMap(customMarkerOne, newMark);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker arg0) { // TODO
                        String title = arg0.getTitle();
                        if(title.equals("My Location")){
                            //Que muestre mi localizacion
                        }else {
                            separateddata = title.split("::");
                            String TYPE = separateddata[4].toLowerCase();
                            if (TYPE.equals("dispensaries")) {
                                rl_information_maps_dispensaries = (RelativeLayout) findViewById(R.id.rl_infodispensaries);
                                rl_information_maps_dispensaries.setVisibility(View.VISIBLE);
                                layoutViewInforDispensariesMaps = getLayoutInflater().inflate(R.layout.activity_maps_infodialog, rl_information_maps_dispensaries, false);
                                rl_information_maps_dispensaries.addView(layoutViewInforDispensariesMaps);
                                rl_information_maps_dispensaries.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                                // rl_information_maps_dispensaries.bringToFront();
                                //Logo
                                String sThumb_ = separateddata[9].toLowerCase();
                                ImageView programImage = (ImageView) layoutViewInforDispensariesMaps.findViewById(R.id.img_logo);
                                Picasso.with(ActivityMaps.this)
                                        .load(sThumb_)
                                        .placeholder(R.drawable.no_image) //optional
                                        .into(programImage);
                                //Rate
                                String iRate = separateddata[8].toLowerCase();
                                rb = (RatingBar) layoutViewInforDispensariesMaps.findViewById(R.id.rate);
                                number_to_rating = Float.parseFloat(iRate);
                                rb.setRating(number_to_rating);
                                //Title
                                String title__;
                                if (separateddata[0].toLowerCase().length() == 0)
                                    title__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                else
                                    title__ = separateddata[0];
                                TextView Title = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textTitle);
                                Title.setTypeface(tt);
                                Title.setText("" + title__);
                                //City
                                String city__;
                                if (separateddata[2].toLowerCase().length() == 0)
                                    city__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                else
                                    city__ = separateddata[2];
                                TextView City = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textCity);
                                City.setTypeface(tt);
                                City.setText("" + city__);
                                //Tipo (ya no es addresss)
                                String address__;
                                TextView Address = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textAd_orSe);
                                Address.setTypeface(tt);
                                Address.setTextColor(getResources().getColor(R.color.red));
                                if (separateddata[13].toLowerCase().length() == 0)
                                    Address.setText("-");
                                else {
                                    address__ = separateddata[13].toLowerCase();
                                    if ((address__.equals("all")) || (address__.equals("Both"))||(address__.equals("both"))|| (address__.equals("All")))
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                                    else if ((address__.equals("medicinal")) || (address__.equals("Medicinal")))
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeMed));
                                    else if ((address__.equals("recreational")) || (address__.equals("Recreational")))
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeRec));
                                    else
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                                }
                                //Horario
                                String hour__;
                                TextView hour = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textHour);
                                hour.setTypeface(tt);
                                if (separateddata[12].toLowerCase().length() == 0) {
                                    hour__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                    hour.setText(" " + hour__);
                                } else {
                                    hour__ = separateddata[12];
                                    if ((hour__.equals("Open")) || (hour__.equals("open")))
                                        hour.setText("" + getResources().getString(R.string.Maps_dialog_HourOpen));
                                    else if ((hour__.equals("Closed")) || (hour__.equals("closed")))
                                        hour.setText("" + getResources().getString(R.string.Maps_dialog_HourClose));
                                    else
                                        hour.setText("" + getResources().getString(R.string.Maps_dialog_HourNo));
                                }
                                //Botones
                                Button close = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.but_close);
                                close.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                    }
                                });
                                Button btn_yes = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.btn_yes);
                                btn_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                        Intent iv = new Intent(ActivityMaps.this, ActivityMapsDispensaries.class);
                                        iv.putExtra("lat", "" + latitude_permission);
                                        iv.putExtra("lng", "" + longitude_permission);
                                        iv.putExtra("id", "" + separateddata[7].toLowerCase());
                                        startActivity(iv);
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
            }

            camera = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_permission, longitude_permission), 12);
            googleMap.animateCamera(camera);
            //getUserCoordenates();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            //Cargams usuarios + More Dispensaries
            //Es importante hacerlo en esta fx, pq luego hacemos otra de MORE DISPENSARIES ya que sino entrariamos en bucle con el Timer y es una locura!
            arraySaveListUsers = new ArrayList<>(); //Metemos el array para luego borrar lo que tengamos que borrar
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // task to be done every 15000 milliseconds
                            iClicks = iClicks + 1;
                            //Toast.makeText(ActivityMaps.this,"timer", Toast.LENGTH_LONG).show();
                            getUserCoordenates();
                        }
                    });

                }
            }, 0, 15000);
            if(from <= total_dispensarios){
                from = from + to;
                reloadRemoteDataMoreByParts();
            }
        }
    }
    //new fx load more elements maps
    protected void reloadRemoteDataMoreByParts() {
        String lat_server = String.valueOf(latitude_permission);
        String lng_server = String.valueOf(longitude_permission);
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        final String sGetMap;

        if (filter_active == 1) {
            bubblefilter.setVisibility(View.INVISIBLE);//Aqui deberia estar habilitado
            sMethod = "ih.filtermaps";
            sGetMap = "ihMapsFilter";
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
        } else {
            bubblefilter.setVisibility(View.INVISIBLE);
            sMethod = "ih.initmapsByPartsMore"; //initmaps
            sGetMap = "ihMaps";
            aParams = new Object[]{
                    lat_server,
                    lng_server,
                    o.getUsername(),
                    o.getPassword(),
                    Main.getLang(),
                    isGuest,
                    "android",
                    "map",
                    String.valueOf(from),
                    String.valueOf(to),
                    "0"
            };
        }

        ConnectorMaps oo = new ConnectorMaps(AppConfig.URL_METHODS, o.getUsername(), o.getPassword(), o.getMemberId());
        oo.execAsyncMethodMyCoordenates(sMethod, aParams, new ConnectorMaps.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initMenuMoreByParts((Object[]) map.get(sGetMap));
            }
        }, this);
    }
    protected void initMenuMoreByParts(Object[] aMenu) {
        m_aMenu = aMenu;
        //setCustomMarkerOneMap(); //Metodo original
        setCustomMarkerOneMapMoreByParts(); //Metodo de pruebas!
    }
    public void setCustomMarkerOneMapMoreByParts() {
        for (int i = 0; i < getCount(); i++) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) m_aMenu[i];
            String iId_element = map.get("id");
            String sName = map.get("name");
            String sAddress = map.get("address");
            String sCity = map.get("city");
            String fDistance = map.get("distance");
            String fLat = map.get("latitude");
            String fLong = map.get("longitude");
            String fRate = map.get("rate");
            String sType = map.get("type");
            String sThumb = map.get("thumb");
            String sSex = map.get("sex");
            String sHour = map.get("hour");
            String sTypeCat = map.get("type_cat");
            String sAge = map.get("age");
            String lat1 = fLat;
            String lng1 = fLong;
            Drawable circleDrawable = getResources().getDrawable(R.drawable.activity_maps_pindispensaries);
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
            customMarkerOne = new CustomMarker("markerOne", Double.parseDouble(lat1), Double.parseDouble(lng1));
            final MarkerOptions markerOption = new MarkerOptions().position(new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude())).icon(markerIcon).title(sName + "::" + sAddress + "::" + sCity + "::" + fDistance + "::" + sType + "::" + fLat + "::" + fLong + "::" + iId_element + "::" + fRate + "::" + sThumb + "::" + sSex + "::" + "sUsername" + "::" + sHour + "::" + sTypeCat + "::" + sAge);
            Marker newMark = googleMap.addMarker(markerOption);
            addMarkerToHashMap(customMarkerOne, newMark);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) { // TODO
                    String title = arg0.getTitle();
                    if(title.equals("My Location")){
                        //Que muestre mi localizacion
                    }else {
                        separateddata = title.split("::");
                        String TYPE = separateddata[4].toLowerCase();
                        if (TYPE.equals("dispensaries")) {
                            rl_information_maps_dispensaries = (RelativeLayout) findViewById(R.id.rl_infodispensaries);
                            rl_information_maps_dispensaries.setVisibility(View.VISIBLE);
                            layoutViewInforDispensariesMaps = getLayoutInflater().inflate(R.layout.activity_maps_infodialog, rl_information_maps_dispensaries, false);
                            rl_information_maps_dispensaries.addView(layoutViewInforDispensariesMaps);
                            rl_information_maps_dispensaries.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                            String sThumb_ = separateddata[9].toLowerCase();
                            ImageView programImage = (ImageView) layoutViewInforDispensariesMaps.findViewById(R.id.img_logo);
                            Picasso.with(ActivityMaps.this)
                                    .load(sThumb_)
                                    .placeholder(R.drawable.no_image) //optional
                                    .into(programImage);
                            //Rate
                            String iRate = separateddata[8].toLowerCase();
                            rb = (RatingBar) layoutViewInforDispensariesMaps.findViewById(R.id.rate);
                            number_to_rating = Float.parseFloat(iRate);
                            rb.setRating(number_to_rating);
                            //Title
                            String title__;
                            if (separateddata[0].toLowerCase().length() == 0)
                                title__ = getResources().getString(R.string.Maps_dialog_Undefined);
                            else
                                title__ = separateddata[0];
                            TextView Title = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textTitle);
                            Title.setTypeface(tt);
                            Title.setText("" + title__);
                            //City
                            String city__;
                            if (separateddata[2].toLowerCase().length() == 0)
                                city__ = getResources().getString(R.string.Maps_dialog_Undefined);
                            else
                                city__ = separateddata[2];
                            TextView City = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textCity);
                            City.setTypeface(tt);
                            City.setText("" + city__);
                            //Tipo (ya no es addresss)
                            String address__;
                            TextView Address = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textAd_orSe);
                            Address.setTypeface(tt);
                            Address.setTextColor(getResources().getColor(R.color.red));
                            if (separateddata[13].toLowerCase().length() == 0)
                                Address.setText("-");
                            else {
                                address__ = separateddata[13].toLowerCase();
                                if ((address__.equals("all")) || (address__.equals("Both"))||(address__.equals("both"))|| (address__.equals("All")))
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                                else if ((address__.equals("medicinal")) || (address__.equals("Medicinal")))
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeMed));
                                else if ((address__.equals("recreational")) || (address__.equals("Recreational")))
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeRec));
                                else
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                            }
                            //Horario
                            String hour__;
                            TextView hour = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textHour);
                            hour.setTypeface(tt);
                            if (separateddata[12].toLowerCase().length() == 0) {
                                hour__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                hour.setText(" " + hour__);
                            } else {
                                hour__ = separateddata[12];
                                if ((hour__.equals("Open")) || (hour__.equals("open")))
                                    hour.setText("" + getResources().getString(R.string.Maps_dialog_HourOpen));
                                else if ((hour__.equals("Closed")) || (hour__.equals("closed")))
                                    hour.setText("" + getResources().getString(R.string.Maps_dialog_HourClose));
                                else
                                    hour.setText("" + getResources().getString(R.string.Maps_dialog_HourNo));
                            }
                            //Botones
                            Button close = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.but_close);
                            close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                    }
                            });
                            Button btn_yes = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.btn_yes);
                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                        Intent iv = new Intent(ActivityMaps.this, ActivityMapsDispensaries.class);
                                        iv.putExtra("lat", "" + latitude_permission);
                                        iv.putExtra("lng", "" + longitude_permission);
                                        iv.putExtra("id", "" + separateddata[7].toLowerCase());
                                        startActivity(iv);
                                    }
                            });
                        }
                    }
                    return true;
                }
            });
        }
        //Cargamso mas diespensarios
        if(from <= total_dispensarios){
            from = from + to;
            reloadRemoteDataMoreByParts();
        }
    }

    // set position in google map from latitude and longitude
    //OLD FX de DISPENSARIOS!
    public void setCustomMarkerOneMap() {
        if (Error != null) {
            RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
            if (rl_back == null) {
                rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);
                layoutActivityMaps = getLayoutInflater().inflate(R.layout.dialog_notdata, rl_dialoguser, false);
                rl_dialoguser.addView(layoutActivityMaps);
                rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                Button btn_yes = (Button) layoutActivityMaps.findViewById(R.id.btn_yes);
                btn_yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        rl_dialoguser.setVisibility(View.GONE);
                    }
                });
            }
        } else {
            //if (map.equals("yes")) {
            String iId_element;
            String sName;
            String sAddress;
            String sCity;
            String fDistance;
            String fLat;
            String fLong;
            String fRate;
            String sThumb;
            String sSex;
            String sHour;
            String sTypeCat;
            String sAge;

            for (int i = 0; i < getCount(); i++) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) m_aMenu[i];
                iId_element = map.get("id");
                sName = map.get("name");
                sAddress = map.get("address");
                sCity = map.get("city");
                fDistance = map.get("distance");
                fLat = map.get("latitude");
                fLong = map.get("longitude");
                fRate = map.get("rate");
                String sType = map.get("type");
                sThumb = map.get("thumb");
                sSex = map.get("sex");
                sHour = map.get("hour");
                sTypeCat = map.get("type_cat");
                sAge = map.get("age");

                String lat1 = fLat;
                String lng1 = fLong;
                Drawable circleDrawable = getResources().getDrawable(R.drawable.activity_maps_pindispensaries);
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                customMarkerOne = new CustomMarker("markerOne", Double.parseDouble(lat1), Double.parseDouble(lng1));
                final MarkerOptions markerOption = new MarkerOptions()
                        .position(new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude()))
                        .icon(markerIcon)
                        .title(
                                sName +
                                        "::" + sAddress +
                                        "::" + sCity +
                                        "::" + fDistance +
                                        "::" + sType +
                                        "::" + fLat +
                                        "::" + fLong +
                                        "::" + iId_element +
                                        "::" + fRate +
                                        "::" + sThumb +
                                        "::" + sSex +
                                        "::" + "sUsername" +
                                        "::" + sHour +
                                        "::" + sTypeCat +
                                        "::" + sAge
                        );

                Marker newMark = googleMap.addMarker(markerOption);
                addMarkerToHashMap(customMarkerOne, newMark);


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker arg0) { // TODO
                        String title = arg0.getTitle();
                        if(title.equals("My Location")){
                            //Que muestre mi localizacion
                        }else {
                            separateddata = title.split("::");
                            String TYPE = separateddata[4].toLowerCase();

                            if (TYPE.equals("dispensaries")) {

                                rl_information_maps_dispensaries = (RelativeLayout) findViewById(R.id.rl_infodispensaries);
                                rl_information_maps_dispensaries.setVisibility(View.VISIBLE);
                                layoutViewInforDispensariesMaps = getLayoutInflater().inflate(R.layout.activity_maps_infodialog, rl_information_maps_dispensaries, false);
                                rl_information_maps_dispensaries.addView(layoutViewInforDispensariesMaps);
                                rl_information_maps_dispensaries.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                                // rl_information_maps_dispensaries.bringToFront();
                                //Logo
                                String sThumb_ = separateddata[9].toLowerCase();
                                ImageView programImage = (ImageView) layoutViewInforDispensariesMaps.findViewById(R.id.img_logo);
                                Picasso.with(ActivityMaps.this)
                                        .load(sThumb_)
                                        .placeholder(R.drawable.no_image) //optional
                                        //.resize(300, imgHeight)         //optional
                                        //.centerCrop()                        //optional
                                        .into(programImage);
                                //Rate
                                String iRate = separateddata[8].toLowerCase();
                                rb = (RatingBar) layoutViewInforDispensariesMaps.findViewById(R.id.rate);
                                number_to_rating = Float.parseFloat(iRate);
                                rb.setRating(number_to_rating);
                                //Title
                                String title__;
                                if (separateddata[0].toLowerCase().length() == 0)
                                    title__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                else
                                    title__ = separateddata[0];
                                TextView Title = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textTitle);
                                Title.setTypeface(tt);
                                Title.setText("" + title__);
                                //City
                                String city__;
                                if (separateddata[2].toLowerCase().length() == 0)
                                    city__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                else
                                    city__ = separateddata[2];
                                TextView City = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textCity);
                                City.setTypeface(tt);
                                City.setText("" + city__);
                                //Tipo (ya no es addresss)
                                String address__;
                                TextView Address = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textAd_orSe);
                                Address.setTypeface(tt);
                                Address.setTextColor(getResources().getColor(R.color.red));
                                if (separateddata[13].toLowerCase().length() == 0)
                                    Address.setText("-");
                                else {
                                    address__ = separateddata[13];
                                    if ((address__.equals("all")) || (address__.equals("Both"))||(address__.equals("both"))|| (address__.equals("All")))
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                                    else if ((address__.equals("medicinal")) || (address__.equals("Medicinal")))
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeMed));
                                    else if ((address__.equals("recreational")) || (address__.equals("Recreational")))
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeRec));
                                    else
                                        Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                                }
                                //Horario
                                String hour__;
                                TextView hour = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textHour);
                                hour.setTypeface(tt);
                                if (separateddata[12].toLowerCase().length() == 0) {
                                    hour__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                    hour.setText(" " + hour__);
                                } else {
                                    hour__ = separateddata[12].toLowerCase();
                                    if ((hour__.equals("Open")) || (hour__.equals("open")))
                                        hour.setText("" + getResources().getString(R.string.Maps_dialog_HourOpen));
                                    else if ((hour__.equals("Closed")) || (hour__.equals("closed")))
                                        hour.setText("" + getResources().getString(R.string.Maps_dialog_HourClose));
                                    else
                                        hour.setText("" + getResources().getString(R.string.Maps_dialog_HourNo));
                                }

                                //Botones
                                Button close = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.but_close);
                                close.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                    }
                                });

                                Button btn_yes = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.btn_yes);
                                btn_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                        Intent iv = new Intent(ActivityMaps.this, ActivityMapsDispensaries.class);
                                        iv.putExtra("lat", "" + latitude_permission);
                                        iv.putExtra("lng", "" + longitude_permission);
                                        iv.putExtra("id", "" + separateddata[7].toLowerCase());
                                        startActivity(iv);
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
            }

            camera = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_permission, longitude_permission), 12);
            googleMap.animateCamera(camera);
            //getUserCoordenates();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


            //TIMER SUPER IMPORTANTE ESTA EN FragmentActivityBase... y cuando cambiamos de TAB, se cancela!!!
            arraySaveListUsers = new ArrayList<>(); //Metemos el array para luego borrar lo que tengamos que borrar
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // task to be done every 15000 milliseconds
                            iClicks = iClicks + 1;
                            //Toast.makeText(ActivityMaps.this,"timer", Toast.LENGTH_LONG).show();
                            getUserCoordenates();
                        }
                    });

                }
            }, 0, 15000);
        }

    }
    protected void getUserCoordenates() {
        Connector o = Main.getConnector();
        Object[] aParams_user;
        String sMethod_user;
        final String sGetMapUser;

        if (filter_active == 1) {
            sMethod_user = "ih.getuserposfilter";
            sGetMapUser = "GetUserCoordSyncFilter";
            aParams_user = new Object[]{
                    o.getUsername(),
                    o.getPassword(),
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
        } else {
            sMethod_user = "ih.getuserpos";
            sGetMapUser = "GetUserCoordSync";
            aParams_user = new Object[]{
                    o.getUsername(),
                    o.getPassword(),
                    isGuest,
                    "0"
            };
        }


        //Importante, metemos el Connetor mapa, ya que no hay mensajes de errroes y loading, es transparente
        ConnectorMaps oo = new ConnectorMaps(AppConfig.URL_METHODS, o.getUsername(), o.getPassword(), o.getMemberId());
        oo.execAsyncMethodMyCoordenates(sMethod_user, aParams_user, new ConnectorMaps.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initUserCoord((Object[]) map.get(sGetMapUser));
            }
        }, this);
    }
    protected void initUserCoord(Object[] aUser) {
        m_aUserCoor = aUser;
        //Checkeamos si primer valor es nulo. Si lo es, borramos y solo marcamos nuestro pin.
        if(getCountUserArray()>0) {
            Map<String, String> mapCheckNull = (Map<String, String>) m_aUserCoor[0];
            String iId_elementCheckNull = mapCheckNull.get("id");
            if (iId_elementCheckNull != null) {
                setCustomMarkerUserMoved();
            } else {
                //Borramos lo que tenemos en mapa....
                setMyCustomPin();
            }
        }else{
            //Borramos lo que tenemos en mapa....
            setMyCustomPin();
        }
    }
    public void setMyCustomPin(){
        //Borramos lo que tenemos en mapa....
        if (arraySaveListUsers.size() > 0) {
            for (int k = 0; k < arraySaveListUsers.size(); k++) {
                removeMarkerUsers(arraySaveListUsers.get(k));
            }
            arraySaveListUsers.clear();
        }
        //Pintamos nuestro Custom Pin
        if (mPositionMarker == null) {
            int NamePin = 0;
            if (isGuest.equals("no")) {
                if (getSexMember.equals("male"))
                    NamePin = R.drawable.pin_map_male_member;
                else
                    NamePin = R.drawable.pin_map_female_member;
            } else
                NamePin = R.drawable.new_pin_guest;

            mPositionMarker = googleMap.addMarker(new MarkerOptions().flat(false)
                    .icon(BitmapDescriptorFactory.fromResource(NamePin)).anchor(0.5f, 0.5f)
                    .title("My Location")
                    .position(new LatLng(latitude_permission, longitude_permission)));
        } else {
            Location location = gps.getLocation();
            animateMarker(mPositionMarker, location); // Helper method for smooth
        }
    }
    public int getCountUserArray() {
        return m_aUserCoor.length;
    }
    // set position in google map from latitude and longitude
    public void setCustomMarkerUserMoved() {
        String iId_element;
        String sName;
        String sAddress;
        String sCity;
        String fDistance;
        String fLat;
        String fLong;
        String sThumb;
        String fRate;
        String sSex;
        String sUsername;
        String sHour;
        String sTypeCat;
        String sAge;

        if (arraySaveListUsers.size() > 0) {
            for (int k = 0; k < arraySaveListUsers.size(); k++) {
                removeMarkerUsers(arraySaveListUsers.get(k));
            }
            arraySaveListUsers.clear();
        }

        //Pintamos nuestro Custom Pin
        if (mPositionMarker == null) {
            int  NamePin = 0;
            if (isGuest.equals("no")){
                if (getSexMember.equals("male"))
                    NamePin = R.drawable.pin_map_male_member;
                else
                    NamePin = R.drawable.pin_map_female_member;
            }else
                NamePin = R.drawable.new_pin_guest;
            mPositionMarker = googleMap.addMarker(new MarkerOptions().flat(false)
                    .icon(BitmapDescriptorFactory.fromResource(NamePin)).anchor(0.5f,0.5f)
                    .title("My Location")
                    .position(new LatLng(latitude_permission, longitude_permission)));

        }else{
            Location location = gps.getLocation();
            animateMarker(mPositionMarker, location); // Helper method for smooth
        }

        for (int i = 0; i < getCountUserArray(); i++) {  //ES IMPORTANTE
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) m_aUserCoor[i];

            iId_element = map.get("id");
            sName = map.get("name");
            sAddress = map.get("address");
            sCity = map.get("city");
            fDistance = map.get("distance");
            fLat = map.get("latitude");
            fLong = map.get("longitude");
            String sType = map.get("type");
            fRate = map.get("rate");
            sThumb = map.get("thumb");
            sSex = map.get("sex");
            sUsername = map.get("username");
            sHour = map.get("hour");
            sTypeCat = map.get("type_cat");
            sAge = map.get("age");
            String lat1 = fLat;
            String lng1 = fLong;
            Drawable circleDrawable;
            if (sType.equals("profiles")) {
                if (sSex.equals("male"))
                    circleDrawable = getResources().getDrawable(R.drawable.new_pin_male);
                else
                    circleDrawable = getResources().getDrawable(R.drawable.new_pin_female);
            } else {
                circleDrawable = getResources().getDrawable(R.drawable.new_pin_guest);
            }
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);


            CustomMarker customMarkerUserID; //Lo creamos para eliminar el pin... o saber si existe el pin! en posicion 00, pq sino, tiene la misma
            //id pero diferente posicion, pro lo que la marca no es la misma!!!!
            customMarkerUserID = new CustomMarker("markerUser_" + iId_element, 0.0, 0.0);
            customMarkerUser = new CustomMarker("markerUser_" + iId_element, Double.parseDouble(lat1), Double.parseDouble(lng1));

            //arraySaveListUsers.add(i, customMarkerUser); //Guardamos valores al Array de lista
            arraySaveListUsers.add(customMarkerUser);

            MarkerOptions markerOption = new MarkerOptions()
                    .position(new LatLng(customMarkerUser.getCustomMarkerLatitude(), customMarkerUser.getCustomMarkerLongitude()))
                    .icon(markerIcon)
                    .title(
                            sName +
                                    "::" + sAddress +
                                    "::" + sCity +
                                    "::" + fDistance +
                                    "::" + sType +
                                    "::" + fLat +
                                    "::" + fLong +
                                    "::" + iId_element +
                                    "::" + fRate +
                                    "::" + sThumb +
                                    "::" + sSex +
                                    "::" + sUsername +
                                    "::" + sHour +
                                    "::" + sTypeCat +
                                    "::" + sAge
                    );

            String sVisibility = map.get("visibility");

            if (sVisibility.equals("yes")) {
                Marker newMark = googleMap.addMarker(markerOption);
                addMarkerToHashMapUsers(customMarkerUser, newMark);
            }

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) { // TODO
                    String title = arg0.getTitle();
                    if(title.equals("My Location")){
                        //Que muestre mi localizacion
                    }else {
                        separateddata = title.split("::");
                        String TYPE = separateddata[4].toLowerCase();

                        if (TYPE.equals("dispensaries")) {
                            rl_information_maps_dispensaries = (RelativeLayout) findViewById(R.id.rl_infodispensaries);
                            rl_information_maps_dispensaries.setVisibility(View.VISIBLE);
                            layoutViewInforDispensariesMaps = getLayoutInflater().inflate(R.layout.activity_maps_infodialog, rl_information_maps_dispensaries, false);
                            rl_information_maps_dispensaries.addView(layoutViewInforDispensariesMaps);
                            rl_information_maps_dispensaries.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                            // rl_information_maps_dispensaries.bringToFront();
                            //Logo
                            String sThumb_ = separateddata[9].toLowerCase();
                            ImageView programImage = (ImageView) layoutViewInforDispensariesMaps.findViewById(R.id.img_logo);
                            Picasso.with(ActivityMaps.this)
                                    .load(sThumb_)
                                    .placeholder(R.drawable.no_image) //optional
                                    //.resize(300, imgHeight)         //optional
                                    //.centerCrop()                        //optional
                                    .into(programImage);
                            //Rate
                            String iRate = separateddata[8].toLowerCase();
                            rb = (RatingBar) layoutViewInforDispensariesMaps.findViewById(R.id.rate);
                            number_to_rating = Float.parseFloat(iRate);
                            rb.setRating(number_to_rating);

                            //Title
                            String title__;
                            if (separateddata[0].toLowerCase().length() == 0)
                                title__ = getResources().getString(R.string.Maps_dialog_Undefined);
                            else
                                title__ = separateddata[0];
                            TextView Title = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textTitle);
                            Title.setTypeface(tt);
                            Title.setText("" + title__);
                            //City
                            String city__;
                            if (separateddata[2].toLowerCase().length() == 0)
                                city__ = getResources().getString(R.string.Maps_dialog_Undefined);
                            else
                                city__ = separateddata[2];
                            TextView City = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textCity);
                            City.setTypeface(tt);
                            City.setText("" + city__);
                            //Tipo (ya no es addresss)
                            String address__;
                            TextView Address = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textAd_orSe);
                            Address.setTypeface(tt);
                            Address.setTextColor(getResources().getColor(R.color.red));
                            if (separateddata[13].toLowerCase().length() == 0)
                                Address.setText("-");
                            else {
                                address__ = separateddata[13];
                                if ((address__.equals("all")) || (address__.equals("Both"))||(address__.equals("both"))|| (address__.equals("All")))
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                                else if ((address__.equals("medicinal")) || (address__.equals("Medicinal")))
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeMed));
                                else if ((address__.equals("recreational")) || (address__.equals("Recreational")))
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeRec));
                                else
                                    Address.setText("" + getResources().getString(R.string.Maps_dialog_TypeAll));
                            }
                            //Horario
                            String hour__;
                            TextView hour = (TextView) layoutViewInforDispensariesMaps.findViewById(R.id.textHour);
                            hour.setTypeface(tt);
                            if (separateddata[12].toLowerCase().length() == 0) {
                                hour__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                hour.setText(" " + hour__);
                            } else {
                                hour__ = separateddata[12].toLowerCase();
                                if ((hour__.equals("Open")) || (hour__.equals("open")))
                                    hour.setText("" + getResources().getString(R.string.Maps_dialog_HourOpen));
                                else if ((hour__.equals("Closed")) || (hour__.equals("closed")))
                                    hour.setText("" + getResources().getString(R.string.Maps_dialog_HourClose));
                                else
                                    hour.setText("" + getResources().getString(R.string.Maps_dialog_HourNo));
                            }

                            Button btn_yes = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.btn_yes);
                            btn_yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                    Intent iv = new Intent(ActivityMaps.this, ActivityMapsDispensaries.class);
                                    iv.putExtra("lat", "" + latitude_permission);
                                    iv.putExtra("lng", "" + longitude_permission);
                                    iv.putExtra("id", "" + separateddata[7].toLowerCase());
                                    startActivity(iv);
                                }
                            });
                            Button close = (Button) layoutViewInforDispensariesMaps.findViewById(R.id.but_close);
                            close.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    rl_information_maps_dispensaries.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            if (TYPE.equals("profiles")) { //Son members
                                rl_information_maps_users = (RelativeLayout) findViewById(R.id.rl_infousers);
                                rl_information_maps_users.setVisibility(View.VISIBLE);
                                layoutViewInforUsersMaps = getLayoutInflater().inflate(R.layout.activity_maps_membersdialog, rl_information_maps_users, false);
                                rl_information_maps_users.addView(layoutViewInforUsersMaps);
                                rl_information_maps_users.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
                                //Logo
                                String sThumb_ = separateddata[9].toLowerCase();
                                ImageView programImage = (ImageView) layoutViewInforUsersMaps.findViewById(R.id.img_logo);
                                if (sThumb_.isEmpty())
                                    sThumb_ = String.valueOf(R.drawable.no_image);
                                else {
                                    final int radius = 40;
                                    final int margin = 0;
                                    final Transformation transformation = new RoundedCornersTransformation(radius, margin);
                                    Picasso.with(ActivityMaps.this)
                                            .load(sThumb_)
                                            .transform(transformation)
                                            .placeholder(R.drawable.no_image)
                                            .into(programImage);
                                }

                                //Title
                                String title__;
                                if (separateddata[0].toLowerCase().length() == 0)
                                    title__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                else
                                    title__ = separateddata[0];
                                TextView Title = (TextView) layoutViewInforUsersMaps.findViewById(R.id.textTitle);
                                Title.setTypeface(tt);
                                Title.setText("" + title__);
                                //City
                                String city__;
                                if (separateddata[2].toLowerCase().length() == 0)
                                    city__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                else
                                    city__ = separateddata[2];
                                TextView City = (TextView) layoutViewInforUsersMaps.findViewById(R.id.textCity);
                                City.setTypeface(tt);
                                City.setText("" + city__);
                                //Es ahora Sex and Ages
                                String address__, age__;
                                TextView Address = (TextView) layoutViewInforUsersMaps.findViewById(R.id.textAd_orSe);
                                Address.setTypeface(tt);
                                if ((separateddata[10].toLowerCase().length() == 0) || (separateddata[14].toLowerCase().length() == 0)) {
                                    address__ = getResources().getString(R.string.Maps_dialog_Undefined);
                                    Address.setText("" + address__);
                                } else {
                                    address__ = separateddata[10];
                                    age__ = separateddata[14];
                                    Address.setText("" + age__ + ", " + address__);
                                }

                                //Botones
                                Button close = (Button) layoutViewInforUsersMaps.findViewById(R.id.but_close);
                                close.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        rl_information_maps_users.setVisibility(View.INVISIBLE);
                                    }
                                });

                                Button btn_yes = (Button) layoutViewInforUsersMaps.findViewById(R.id.btn_yes);
                                btn_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        if (isGuest.equals("no")) {
                                            rl_information_maps_users.setVisibility(View.INVISIBLE);
                                            Intent i = new Intent(ActivityMaps.this, ProfileActivity.class);
                                            //i.putExtra("username", separateddata[11].toLowerCase());
                                            i.putExtra("username", separateddata[11]);
                                            startActivityForResult(i, 0);
                                            ActivityMaps.this.overridePendingTransition(0, 0);
                                        } else {
                                            Toast.makeText(ActivityMaps.this, getResources().getString(R.string.userGuest_exception_noprofile), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {//Son GUEST:: Deshabilitamos pop por decision cliente y tb nuestro pin...
                            }
                        } //ESTE IF CIERRA EL DE DISPENSARIOS...
                    }
                    return true;
                }
            });
        }
    }

    //set header
    protected void setHeaderLayaout() {
        //Ponemos en marcha los botones....rl_infodialog
        RelativeLayout temp_rl_header = (RelativeLayout) findViewById(R.id.rl_fixedbuttons);
        if (temp_rl_header == null) {
            rl_headerLayout = (RelativeLayout) findViewById(R.id.rl_headerLayout);
            layoutMapsButtons = getLayoutInflater().inflate(R.layout.activity_maps_buttons, rl_headerLayout, false);
            rl_headerLayout.addView(layoutMapsButtons);
            rl_headerLayout.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
            Button btn_filter = (Button) layoutMapsButtons.findViewById(R.id.btn_filter);
            btn_filter.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //rl_headerLayout.setVisibility(View.GONE);
                    //Toast.makeText(ActivityMaps.this,"filtro", Toast.LENGTH_LONG).show();
                    Intent ih = new Intent(ActivityMaps.this, ActivityMapsFilter.class);
                    ih.putExtra("map", "" + "yes");
                    ih.putExtra("filteractive", "" + filter_active);
                    ih.putExtra("lat", "" + latitude_permission);
                    ih.putExtra("lng", "" + longitude_permission);
                    //timer.cancel(); //no lo aconsejamos x finish activity en listview
                    //onPause();
                    startActivity(ih);
                    overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                }
            });
            Button btn_listview = (Button) layoutMapsButtons.findViewById(R.id.btn_listview);
            btn_listview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(ActivityMaps.this,"Listview", Toast.LENGTH_LONG).show();
                    Intent ih = new Intent(ActivityMaps.this, ActivityMapsList.class);
                    ih.putExtra("filteractive", "" + filter_active);
                    ih.putExtra("lat", "" + latitude_permission);
                    ih.putExtra("lng", "" + longitude_permission);
                    //Nota: Si el filtro esta activo, el listado debe salir del filtro... asi que pasaremos los datos!
                    if (filter_active == 1) {
                        ih.putExtra("KeySearch", "" + filter_search_String);
                        ih.putExtra("Ratio", "" + filter_ratio_String);
                        ih.putExtra("Age", "" + filter_age_String);
                        ih.putExtra("Gender", "" + filter_Gender_String);
                        ih.putExtra("Status", "" + filter_Status_String);
                        ih.putExtra("SpinnerTypes", "" + SpinnerTypes_);
                        ih.putExtra("SpinnerCat", "" + SpinnerCat_);
                        ih.putExtra("SpinnerList", "" + SpinnerList_);
                        ih.putExtra("Connect", "" + filter_Connects_String);
                        ih.putExtra("APP_PACKAGE_EXTRAS", "com.filter");
                        ih.putExtra("lat", "" + latitude_permission);
                        ih.putExtra("lng", "" + longitude_permission);
                        ih.putExtra("filteractive", "1");
                    }
                    //timer.cancel(); //no lo aconsejamos x finish activity en listview
                    startActivity(ih);
                    overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);

                }
            });
            bubblefilter = (ImageView) findViewById(R.id.bubble_filter);
            bubblefilter.setVisibility(View.INVISIBLE);
            initconfigheader();
            reload = (Button) findViewById(R.id.buttonRefresh);
            reload.setVisibility(View.INVISIBLE);
            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if(filter_active == 1)
                    //  getintentFilter();
                    //else
                    SharedPreferences prefs = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
                    prefs.edit().remove("isFilterActive").commit();
                    radius = 2 * 1.8383 * 1000; //1.8 es pq al server le pasamos los km!!! al recibir del server, lo dividiremos para dibujar
                    filter_active = 0;
                    bubblefilter.setVisibility(View.INVISIBLE);
                    verifyPermissionLocation();
                }
            });
        }

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()/2, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()/2);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void zoomToMarkers(View v) {
        zoomAnimateLevelToFitMarkers(20);
    }

    public void initializeUiSettings() {
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    public void initializeMapLocationSettings() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(ActivityMaps.this, R.string.google_location_text, Toast.LENGTH_LONG).show();
            return;
        }
        googleMap.setMyLocationEnabled(false);//Custom Pin Deshabilitamos para no mostrar circulo azul
    }

    public void initializeMapTraffic() {
        googleMap.setTrafficEnabled(false);
    }

    public void initializeMapType() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void initializeMapViewSettings() {
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(false);
    }

    // this is method to help us set up a Marker that stores the Markers we want to plot on the map
    public void setUpMarkersHashMap() {
        if (markersHashMap == null) {
            markersHashMap = new HashMap<CustomMarker, Marker>();
        }
    }

    // this is method to help us add a Marker into the hashmap that stores the Markers
    public void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        markersHashMap.put(customMarker, marker);
    }

    // this is method to help us find a Marker that is stored into the hashmap
    public Marker findMarker(CustomMarker customMarker) {
        iter = markersHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry mEntry = iter.next();
            CustomMarker key = (CustomMarker) mEntry.getKey();
            if (customMarker.getCustomMarkerId().equals(key.getCustomMarkerId())) {
                Marker value = (Marker) mEntry.getValue();
                return value;
            }
        }
        return null;
    }

    // Functiones especiales para los usuarios
    // this is method to help us remove a Marker
    public void setUpMarkersHashMapUsers() {
        if (markersHashMapUsers == null) {
            markersHashMapUsers = new HashMap<CustomMarker, Marker>();
        }
    }

    // this is method to help us add a Marker into the hashmap that stores the Markers
    public void addMarkerToHashMapUsers(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMapUsers();
        markersHashMapUsers.put(customMarker, marker);
    }

    public void moveMarkerUser(CustomMarker customMarker, LatLng latlng) {
        if (findMarkerUsers(customMarker) != null) {
            findMarkerUsers(customMarker).setPosition(latlng);
            customMarker.setCustomMarkerLatitude(latlng.latitude);
            customMarker.setCustomMarkerLongitude(latlng.longitude);
        }
    }

    public void removeMarkerUsers(CustomMarker customMarker) {
        if (markersHashMapUsers != null) {
            if (findMarkerUsers(customMarker) != null) {
                findMarkerUsers(customMarker).remove();
                markersHashMapUsers.remove(customMarker);
            }
        }
    }

    public Marker findMarkerUsers(CustomMarker customMarker) {
        if (markersHashMapUsers != null) {
            iterUser = markersHashMapUsers.entrySet().iterator();
            while (iterUser.hasNext()) {
                Map.Entry mEntry = iterUser.next();
                CustomMarker key = (CustomMarker) mEntry.getKey();
                if (customMarker.getCustomMarkerId().equals(key.getCustomMarkerId())) {
                    Marker value = (Marker) mEntry.getValue();
                    return value;
                }
            }
        }
        return null;
    }

    // this is method to help us fit the Markers into specific bounds for camera position
    public void zoomAnimateLevelToFitMarkers(int padding) {
        iter = markersHashMap.entrySet().iterator();
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        LatLng ll = null;
        while (iter.hasNext()) {
            Map.Entry mEntry = iter.next();
            CustomMarker key = (CustomMarker) mEntry.getKey();
            ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());
            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        // Change the padding as per needed
        //camera = CameraUpdateFactory.newLatLngBounds(bounds, 200, 400, 17);
        //googleMap.animateCamera(camera);

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

    //LOCATION MAPS
    //Paso 1. Verificar permiso
    @SuppressLint("WrongConstant")
    private void verifyPermissionLocation() {
        //WRITE_EXTERNAL_STORAGE tiene implícito READ_EXTERNAL_STORAGE porque pertenecen al mismo grupo de permisos
        int locationPermission = 0;
        int locationPermission2 = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            locationPermission2 = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if ((locationPermission != PackageManager.PERMISSION_GRANTED) || (locationPermission2 != PackageManager.PERMISSION_GRANTED)) {
            requestPermission();
        } else {
            getGPSLocation();
        }
    }

    private void requestPermission() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) || (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            showSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGPSLocation();
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
            rl_dialog.startAnimation(AnimationUtils.loadAnimation(ActivityMaps.this, R.anim.popup));
            Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
            Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialog.setVisibility(View.GONE);
                    getGPSLocation();
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
                            getGPSLocation();
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

    private void getGPSLocation() { //SIEMPRE tenemos en cuenta si es GUEST
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) || (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            latitude_permission = 0.0f;
            longitude_permission = 0.0f;
        } else {
            gps = new GPSTracker(ActivityMaps.this, isGuest);
            if (gps.canGetLocation()) {
                latitude_permission = gps.getLatitude();
                longitude_permission = gps.getLongitude();
            } else {
                // can't get location GPS or Network is not enabled Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
                //Damos los valores manualmente
                latitude_permission = 0.0f;
                longitude_permission = 0.0f;
            }
        }


        // Removes all markers, overlays, and polylines from the map.

        //googleMap.clear();
        //markersHashMap.clear();
        // markersHashMapUsers.clear();




        reloadRemoteData();
        // Loading map
        initilizeMap();
        //initializeUiSettings();
        //initializeMapLocationSettings();
        //initializeMapTraffic();
        //initializeMapType();
        //initializeMapViewSettings();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (VERBOSE) Log.v(TAG, "123_ ++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VERBOSE) Log.v(TAG, "123_ + ON RESUME +");
        /*
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(AppConfig.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(AppConfig.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());*/
    }

    @Override
    public void onPause() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        if (VERBOSE) Log.v(TAG, "123_ - ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (VERBOSE) Log.v(TAG, "123_ -- ON STOP --");
        // googleMap.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (VERBOSE) Log.v(TAG, "123_ - ON DESTROY -");
        SharedPreferences prefs = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
        prefs.edit().remove("isFilterActive").commit();
    }

    // convert latitude and longitude to km
    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        d = tmp / factor;
        return d;
    }

    // convert km to miles
    public static double convertmiles(String value, String places) {
		/*
		 * double earthRadius = 6371000; // meters // double dLat =
		 * Math.toRadians(40.7127 - Double.parseDouble(value)); // double dLng =
		 * Math.toRadians(74.0059 - Double.parseDouble(places)); double dLat =
		 * Math.toRadians(latitude - Double.parseDouble(value)); double dLng =
		 * Math.toRadians(longitude - Double.parseDouble(places)); double a =
		 * Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		 * Math.cos(Math.toRadians(21.2305574))
		 * Math.cos(Math.toRadians(latitude)) * Math.sin(dLng / 2) Math.sin(dLng
		 * / 2); double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		 * float dist = (float) (earthRadius * c); Log.d("Distance", "" + dist);
		 */
        Location selected_location = new Location("locationA");
        try {
            selected_location.setLatitude(Double.parseDouble(value));
            selected_location.setLongitude(Double.parseDouble(places));
            Location near_locations = new Location("locationA");
            near_locations.setLatitude(latitude_permission);
            near_locations.setLongitude(longitude_permission);
            double dist = selected_location.distanceTo(near_locations);
            double km = dist / 1000.0;
            Log.d("Distance in KM", "" + km);
            double rounded = (double) Math.round(km * 100) / 100;
            Log.d("rounded", "" + rounded);
            round(rounded, 0);
            miles = d / 1.6;
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }
        return miles;
    }

    @Override
    public void onBackPressed() {
        // timer.cancel(); //Cancelamos Timer cuando salgamos de la acividad principal
        // super.onBackPressed();

    }

    public void animateMarker(final Marker marker, final Location location) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
               // marker.setRotation(90);

                // set rotate pin
                //float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
                Matrix matrix = new Matrix();
                //matrix.postRotate(angleDeg);


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

        if (location.hasBearing()) {
            marker.setRotation(location.getBearing());
        }
    }

}
