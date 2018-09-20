package com.innohawk.dan.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.Notify.NotifyActiviy;
import com.innohawk.dan.R;
import com.innohawk.dan.SiteAdapter;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.friends.FriendsHomeActivity;
import com.innohawk.dan.location.LocationActivity;
import com.innohawk.dan.mail.MailHomeActivity;
import com.innohawk.dan.media.ImagesAlbumsActivity;
import com.innohawk.dan.media.SoundsAlbumsActivity;
import com.innohawk.dan.media.VideosAlbumsActivity;
import com.innohawk.dan.menu.MenuListAdapter;
import com.innohawk.dan.profile.ProfileMyInfoActivity;
import com.innohawk.dan.search.SearchHomeActivity;
import com.innohawk.dan.sqlite.SQLiteActivity;
import com.innohawk.dan.status.StatusMessageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends ActivityBase {

    private static final String TAG = "OO HomeActivity";

    public static final int RESULT_LOGOUT = RESULT_FIRST_USER + 1;

    private static final int ACTIVITY_PROFILE_INFO = 0;
    private static final int ACTIVITY_STATUS_MESSAGE = 1;
    private static final int ACTIVITY_LOCATION = 2;
    private static final int ACTIVITY_MAIL_HOME = 3;
    private static final int ACTIVITY_FRIENDS_HOME = 4;
    private static final int ACTIVITY_IMAGES_ALBUMS = 5;
    private static final int ACTIVITY_VIDEOS_ALBUMS = 6;
    private static final int ACTIVITY_SOUNDS_ALBUMS = 7;
    private static final int ACTIVITY_SEARCH_HOME = 8;
    private static final int ACTIVITY_WEB_PAGE = 9;
    private static final int ACTIVITY_NOTI_HOME = 10;
    private static final int ACTIVITY_EVENTS_MAIN = 12;


    protected HomeActivity m_actHome;
    protected String m_sThumb;
    protected String m_sInfo;
    protected String m_sStatus;
    protected int m_iMemberId;
    protected String m_sUsername;
    protected String m_sUserTitle;
    protected String m_sPasswd;
    protected String m_sSite;
    protected int m_iSiteIndex;
    protected int m_iProtocolVer;
    protected Map<String, Object> m_map;
    private Object[] SaveTemp_map;
    protected TableLayout m_table;
    protected HomeBtn m_btnMessages;
    protected HomeBtn m_btnFriends;

    //innoHawk:: Start Code for Bubble Toolbar | Start Code db | Start Code Menu Slider | Start Code WebView
    protected String bubbleLetters;
    protected String bubbleFriends;
    protected String bubbleChats;
    protected String bubbleFeeds;
    protected String bubbleDeals;
    protected String bubbleNoti;
    protected TextView badge_notification;
    protected SiteAdapter adapter;
    private SQLiteActivity db;
    private WebView webView;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuListAdapter mMenuAdapter;
    private String[] title;
    private String[] imageMenu;
    private String[] subtitle;
    //innoHawk:: End Code for Bubble Toolbar | Start Code db | Start Code Menu Slider | Start Code WebView


    protected EditText edit_search;
    protected Button btn_action_noti;
    protected Button btn_action_reload;
    protected Button btn_action_logut;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //innoHawk:: Start Code db
        db = new SQLiteActivity(getApplicationContext());
        //End Code

        setContentView(R.layout.home);
       // setTitleCaption(R.string.toolbar_open);
        m_actHome = this;
        m_table = (TableLayout) findViewById(R.id.home_table);
        Intent i = getIntent();
        m_sSite = i.getStringExtra("site");
        m_iSiteIndex = i.getIntExtra("index", 0);
        m_iMemberId = i.getIntExtra("member_id", 0);
        m_sUsername = i.getStringExtra("username");
        m_sPasswd = i.getStringExtra("password");
        m_iProtocolVer = i.getIntExtra("protocol", 2);
        Log.d(TAG, "m_sSite: " + m_sSite);
        Log.d(TAG, "m_sUsername: " + m_sUsername);


        badge_notification = (TextView) findViewById(R.id.home_noti_bubble);
        badge_notification.setVisibility(View.INVISIBLE);

        //innoHawk::  Start Code Menu Slider | Start Code WebView
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        initDrawMenu();
        //End Code

        btn_action_logut = (Button) findViewById(R.id.buttonLogout);
        btn_action_logut.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                mDrawerLayout.closeDrawer(mDrawerList);
                Bundle b = new Bundle();
                b.putInt("index", 0);
                Intent i = new Intent();
                i.putExtras(b);
                db.deleteUser();
                LoginManager.getInstance().logOut();
                setResult(RESULT_LOGOUT, i);

                //Borramos preferencias
                SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
                prefs.edit().remove("site").commit();
                prefs.edit().remove("member_id").commit();
                prefs.edit().remove("username").commit();
                prefs.edit().remove("password").commit();
                prefs.edit().remove("protocol").commit();
                prefs.edit().remove("index").commit();
                prefs.edit().remove("isGuest").commit();

                //Al loro! que no borramos el de guest.. por si vuelve a entrar!!!! :)

                Intent intentHome = new Intent(HomeActivity.this, Main.class);
                intentHome.putExtra("member_id", "");
                intentHome.putExtra("username", "");
                intentHome.putExtra("password", "");
                intentHome.putExtra("index", 0);

                startActivityForResult(intentHome, 1);

                finish();
            }
        });

        //Reload
        btn_action_reload = (Button) findViewById(R.id.buttonRefresh);
        btn_action_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });

        Typeface tb = Typeface.createFromAsset(getAssets(), "fonts/Carlito-Bold.ttf");
        edit_search = (EditText) findViewById(R.id.Search_Edit);
        edit_search.setTypeface(tb);

        //Reload
        btn_action_noti = (Button) findViewById(R.id.buttonNoti);
        btn_action_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchActivityNoti();
            }
        });

        //int textLength = edit_search.getText().length();
        //edit_search.setSelection(textLength, textLength);
        edit_search.addTextChangedListener(new TextWatcher() {
            int len=0;
            @Override
            public void afterTextChanged(Editable s) {
                String str = edit_search.getText().toString();
                if(str.length() > 1) {
                    String last = edit_search.getText().toString();
                    last = last.substring(last.length() - 1);
                    filterreloadButtons(SaveTemp_map, last);
                    edit_search.setText(last);
                    edit_search.setSelection(last.length(), last.length());
                }else {
                    if (str.length() == 1 && len < str.length()) {//len check for backspace
                        String TempFilter = edit_search.getText().toString();
                        if (TempFilter.equals(" "))
                            TempFilter = "all";
                        filterreloadButtons(SaveTemp_map, TempFilter);
                    } else if (str.equals("")) {
                        String TempFilter = "all";
                        filterreloadButtons(SaveTemp_map, TempFilter);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = edit_search.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //edit_search.setSelection(0, 0);
                String str = edit_search.getText().toString();
            }


        });

        reloadRemoteData();
    }


    //innoHawk:: Start Code Menu Slider | Start Code WebView

    private void initDrawMenu() {
        title = new String[] {};
        subtitle = new String[] {};
        imageMenu = new String[] {};
        mMenuAdapter = new MenuListAdapter(this, title, subtitle, imageMenu);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        setupDrawer();
    }

    private void setupDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerShadow(R.drawable.ic_menu_home, GravityCompat.START);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.toolbar_open, R.string.toolbar_close) {
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    setTitleCaption(R.string.toolbar_close);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    setTitleCaption(R.string.title_home);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(false);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_home, m_actHome.getTheme());
            mDrawerToggle.setHomeAsUpIndicator(drawable);
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    }



    //End Code

    protected void filterreloadButtons(Object[] aMenu, String tempFilter) {

        Connector o = Main.getConnector();

        final Map<String, Integer> mapIcons = new HashMap<String, Integer>();
        mapIcons.put("home_status.png", R.drawable.ic_home_status);
        mapIcons.put("home_location.png", R.drawable.ic_home_location);
        mapIcons.put("home_messages.png", R.drawable.ic_home_messages);
        mapIcons.put("home_friends.png", R.drawable.ic_home_friends);
        mapIcons.put("home_info.png", R.drawable.ic_home_info);
        mapIcons.put("home_search.png", R.drawable.ic_home_search);
        mapIcons.put("home_images.png", R.drawable.ic_home_photos);
        mapIcons.put("home_sounds.png", R.drawable.ic_home_sounds);
        mapIcons.put("home_videos.png", R.drawable.ic_home_videos);

        Map<Integer, View.OnClickListener> mapActions = new HashMap<Integer, View.OnClickListener>();
        mapActions.put(1, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityStatus();
            }
        });
        mapActions.put(2, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityLocation();
            }
        });
        mapActions.put(3, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityMail();
            }
        });
        mapActions.put(4, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityFriends();
            }
        });
        mapActions.put(5, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityInfo();
            }
        });
        mapActions.put(6, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivitySearch();
            }
        });
        mapActions.put(7, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityPhotos();
            }
        });
        mapActions.put(8, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityVideos();
            }
        });
        mapActions.put(9, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivitySounds();
            }
        });
        mapActions.put(12, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityEvents();
            }
        });

        m_table.removeAllViews();
        TableRow r = null;
        int iCount = aMenu.length;


        int cont = 0;
        int lastPosition = -1;

        String sTempFilterFirstCharacter;
        String sTitleFirstCharacter;
        if (tempFilter.equals("all")) {
            cont = aMenu.length;
        } else {
            sTempFilterFirstCharacter = tempFilter.substring(0, 1);
            for (int i = 0; i < iCount; ++i) { //Solo para contabilizar si hacemos filtro que no sea en blaco
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) aMenu[i];
                String sTitle = map.get("title");
                sTitleFirstCharacter = sTitle.substring(0, 1);
                if (sTitleFirstCharacter.equalsIgnoreCase(sTempFilterFirstCharacter)) {
                    cont++;
                }
            }
        }





        int new_position =0 ;
        for (int i = 0; i < iCount; ++i) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) aMenu[i];
            HomeBtn oBtn;
            String sTitle = map.get("title");
            sTitleFirstCharacter = sTitle.substring(0, 1);
            if (tempFilter.equals("all")) {
                sTitleFirstCharacter = "all";
                sTempFilterFirstCharacter = "all";
            } else
                sTempFilterFirstCharacter = tempFilter.substring(0, 1);
            if (sTitleFirstCharacter.equalsIgnoreCase(sTempFilterFirstCharacter)) {
                int iAction = Integer.valueOf(map.get("action"));
                String sActionData = map.get("action_data");
                String sBubble = map.get("bubble");
                String sIcon = map.get("icon");

                int iIcon = 0;
                if (mapIcons.containsKey(sIcon))
                    iIcon = mapIcons.get(sIcon);

                Log.d(TAG, "INDEX: " + cont);
                Log.d(TAG, "  TITLE: " + sTitle);
                Log.d(TAG, "  ICON: " + sIcon);
                Log.d(TAG, "  ACTION: " + iAction);
                Log.d(TAG, "  ACTION DATA: " + sActionData);
                Log.d(TAG, "  BUBBLE: " + sBubble);


                if (100 == iAction || 101 == iAction) {
                    oBtn = new HomeBtn3rdParty(this, sTitle, sBubble, sIcon);
                    ThirdPartyOnClickListener listener = new ThirdPartyOnClickListener() {
                        protected String m_sUrl;
                        protected String m_sTitle;
                        protected int m_iAction;

                        public void setUrl(String s) {
                            m_sUrl = s;
                        }

                        public void setTitle(String s) {
                            m_sTitle = s;
                        }

                        public void setAction(int i) {
                            m_iAction = i;
                        }

                        public void onClick(View view) {
                            if (101 == m_iAction)
                                LaunchActivityBrowser(m_sTitle, m_sUrl);
                            else
                                LaunchActivityWebPage(m_sTitle, m_sUrl);
                        }
                    };
                    listener.setTitle(sTitle);
                    listener.setUrl(sActionData);
                    listener.setAction(iAction);
                    oBtn.getBtn().setOnClickListener(listener);
                } else {
                    oBtn = new HomeBtn(this, sTitle, sBubble, iIcon);
                    if (mapActions.containsKey(iAction))
                        oBtn.getBtn().setOnClickListener(mapActions.get(iAction));
                }

                //innoHawk:: Añadido extra para diferenciar tablets de smartphones y cambiar tamaño botones, etc...
                boolean isTablet = isTablet(m_actThis);

                if (isTablet) {
                    if (0 == (new_position % 5)) {
                        r = new TableRow(this);
                        m_table.addView(r);
                        if(cont<5) {
                            ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.LEFT;
                            params.setMargins(30, 20, 30, 0);
                            m_table.setLayoutParams(params);
                        }
                    }
                } else {
                    if (0 == (new_position % 3)) {
                        r = new TableRow(this);
                        m_table.addView(r);
                        if(cont<3) {
                            ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.LEFT;
                            params.setMargins(30, 20, 30, 0);
                            m_table.setLayoutParams(params);
                        }
                    }
                }
                //End Code



                if (r != null) {
                    Animation animation = AnimationUtils.loadAnimation(HomeActivity.this,(cont > lastPosition) ? R.anim.card_flip_in: R.anim.card_flip_out);
                    r.startAnimation(animation);
                    r.addView(oBtn);
                    lastPosition = new_position;
                }
                if (3 == iAction) {
                    m_btnMessages = oBtn;
                    o.setUnreadLettersNum(Integer.parseInt(sBubble));

                } else if (4 == iAction) {
                    m_btnFriends = oBtn;
                    o.setFriendRequestsNum(Integer.parseInt(sBubble));
                }
                new_position++;
            }
        }
    }

    protected void reloadButtons(Object[] aMenu) {

        Connector o = Main.getConnector();

        final Map<String, Integer> mapIcons = new HashMap<String, Integer>();
        mapIcons.put("home_status.png", R.drawable.ic_home_status);
        mapIcons.put("home_location.png", R.drawable.ic_home_location);
        mapIcons.put("home_messages.png", R.drawable.ic_home_messages);
        mapIcons.put("home_friends.png", R.drawable.ic_home_friends);
        mapIcons.put("home_info.png", R.drawable.ic_home_info);
        mapIcons.put("home_search.png", R.drawable.ic_home_search);
        mapIcons.put("home_images.png", R.drawable.ic_home_photos);
        mapIcons.put("home_sounds.png", R.drawable.ic_home_sounds);
        mapIcons.put("home_videos.png", R.drawable.ic_home_videos);

        Map<Integer, View.OnClickListener> mapActions = new HashMap<Integer, View.OnClickListener>();
        mapActions.put(1, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityStatus();
            }
        });
        mapActions.put(2, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityLocation();
            }
        });
        mapActions.put(3, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityMail();
            }
        });
        mapActions.put(4, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityFriends();
            }
        });
        mapActions.put(5, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityInfo();
            }
        });
        mapActions.put(6, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivitySearch();
            }
        });
        mapActions.put(7, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityPhotos();
            }
        });
        mapActions.put(8, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityVideos();
            }
        });
        mapActions.put(9, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivitySounds();
            }
        });
        mapActions.put(12, new OnClickListener() {
            public void onClick(View v) {
                LaunchActivityEvents();
            }
        });

        m_table.removeAllViews();
        TableRow r = null; //Estaba

        int iCount = aMenu.length;

        //innoHawk::  Start Code Menu Slider
        final ArrayList<String> stringArrayListMenu = new ArrayList<String>();
        ArrayList<String> stringIconMenu = new ArrayList<String>();
        ArrayList<String> stringBadgeMenu = new ArrayList<String>();
        final ArrayList<String> stringActionsMenu = new ArrayList<String>();
        final ArrayList<Integer> integerActionMenu = new ArrayList<Integer>();
        //End Code

        for (int i = 0; i < iCount; ++i) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) aMenu[i];
            HomeBtn oBtn;
            String sTitle = map.get("title");
            int iAction = Integer.valueOf(map.get("action"));
            String sActionData = map.get("action_data");
            String sBubble = map.get("bubble");
            String sIcon = map.get("icon");

            int iIcon = 0;
            if (mapIcons.containsKey(sIcon))
                iIcon = mapIcons.get(sIcon);

            Log.d(TAG, "INDEX: " + i);
            Log.d(TAG, "  TITLE: " + sTitle);
            Log.d(TAG, "  ICON: " + sIcon);
            Log.d(TAG, "  ACTION: " + iAction);
            Log.d(TAG, "  ACTION DATA: " + sActionData);
            Log.d(TAG, "  BUBBLE: " + sBubble);

            //innoHawk::  Start Code Menu Slider
            stringArrayListMenu.add(sTitle);
            stringIconMenu.add(sIcon);
            if((sBubble == null)||(sBubble.equals("0"))||(sBubble.equals("")))
                sBubble = "0";
            stringBadgeMenu.add(sBubble);
            stringActionsMenu.add(sActionData);
            integerActionMenu.add(iAction);
            //End Code

            if (100 == iAction || 101 == iAction) {
                oBtn = new HomeBtn3rdParty(this, sTitle, sBubble, sIcon);
                ThirdPartyOnClickListener listener = new ThirdPartyOnClickListener() {
                    protected String m_sUrl;
                    protected String m_sTitle;
                    protected int m_iAction;

                    public void setUrl(String s) {
                        m_sUrl = s;
                    }

                    public void setTitle(String s) {
                        m_sTitle = s;
                    }

                    public void setAction(int i) {
                        m_iAction = i;
                    }

                    public void onClick(View view) {
                        if (101 == m_iAction)
                            LaunchActivityBrowser(m_sTitle, m_sUrl);
                        else
                            LaunchActivityWebPage(m_sTitle, m_sUrl);
                    }
                };
                listener.setTitle(sTitle);
                listener.setUrl(sActionData);
                listener.setAction(iAction);
                oBtn.getBtn().setOnClickListener(listener);
            } else {
                oBtn = new HomeBtn(this, sTitle, sBubble, iIcon);
                if (mapActions.containsKey(iAction))
                    oBtn.getBtn().setOnClickListener(mapActions.get(iAction));
            }

            //innoHawk:: Añadido extra para diferenciar tablets de smartphones y cambiar tamaño botones, etc...
            boolean isTablet = isTablet(m_actThis);

            if (isTablet) {
                if (0 == (i % 5)) {
                    r = new TableRow(this);
                    m_table.addView(r);
                    if(iCount<5) {
                        ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.LEFT;
                        params.setMargins(30, 20, 30, 0);
                        m_table.setLayoutParams(params);
                    }
                }
            } else {
                if (0 == (i % 3)) {
                    r = new TableRow(this);
                    m_table.addView(r);
                    if(iCount<3) {
                        ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.LEFT;
                        params.setMargins(30, 20, 30, 0);
                        m_table.setLayoutParams(params);
                    }
                }
            }
            //End Code

            if (r != null)
                r.addView(oBtn);

            if (3 == iAction) {
                m_btnMessages = oBtn;
                o.setUnreadLettersNum(Integer.parseInt(sBubble));

            } else if (4 == iAction) {
                m_btnFriends = oBtn;
                o.setFriendRequestsNum(Integer.parseInt(sBubble));
            }
        }

        //innoHawk:: Start Code for Bubble Toolbar |  Start Code Menu Slider
        stringArrayListMenu.add("");
        stringIconMenu.add("-");
        stringBadgeMenu.add("0");
        stringActionsMenu.add("-");
        integerActionMenu.add(0);

        String ConvertcsvtoString = stringArrayListMenu.toString().replace("[","\"").replace("]","\"")
                .replace(", ","\",\"").replace(" ", "_");
        title = ConvertcsvtoString.replaceAll("^[,\\s]+", "").replace("\"", "").split("[,\\s]+");
        String ConvertcsvtoStringImg = stringIconMenu.toString().replace("[", "\"").replace("]", "\"")
                .replace(", ", "\", \"");
        imageMenu = ConvertcsvtoStringImg.replaceAll("^[,\\s]+", "").replace("\"", "").split("[,\\s]+");
        String ConvertcsvtoStringBadge = stringBadgeMenu.toString().replace("[", "\"").replace("]", "\"")
                .replace(", ", "\", \"");
        subtitle = ConvertcsvtoStringBadge.replaceAll("^[,\\s]+", "").replace("\"", "").split("[,\\s]+");

        mMenuAdapter = new MenuListAdapter(this, title, subtitle, imageMenu);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String getNameAction;
                getNameAction = stringActionsMenu.get(position);
                Integer checkAction;
                checkAction = integerActionMenu.get(position);
                String getTitleAction;
                getTitleAction = stringArrayListMenu.get(position);
                mDrawerLayout.closeDrawer(mDrawerList);
                if (100 == checkAction || 101 == checkAction) {
                    if (101 == checkAction)
                        LaunchActivityBrowser(getTitleAction, getNameAction);
                    else
                        LaunchActivityWebPage(getTitleAction, getNameAction);

                } else {
                    if (1 == checkAction)
                        LaunchActivityStatus();
                    else if (2 == checkAction)
                        LaunchActivityLocation();
                    else if (3 == checkAction)
                        LaunchActivityMail();
                    else if (4 == checkAction)
                        LaunchActivityFriends();
                    else if (5 == checkAction)
                        LaunchActivityInfo();
                    else if (6 == checkAction)
                        LaunchActivitySearch();
                    else if (7 == checkAction)
                        LaunchActivityPhotos();
                    else if (8 == checkAction)
                        LaunchActivityVideos();
                    else if (9 == checkAction)
                        LaunchActivitySounds();
                    else if (12 == checkAction)
                        LaunchActivityEvents();
                }
            }
        });
        setupDrawer();
        //aGetBudgets();
		//End Code
    }



    // Fx que convierte un Array Int List en un int[]!!! super importante aunque no lo utilizamos
    static int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            intArray[i] = integerList.get(i);
        }
        return intArray;
    }


    protected void reloadRemoteData() {
        Object[] aParams;
        String sMethod;
        Connector o = new Connector(m_sSite, m_sUsername, m_sPasswd, m_iMemberId);

        o.setPassword(32 == m_sPasswd.length() || 40 == m_sPasswd.length() ? m_sPasswd : o.md5(m_sPasswd));
        o.setProtocolVer(m_iProtocolVer);

        Main.setConnector(o);
        Connector.saveConnector(this, o);

        //Main.getLang()
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

                SaveTemp_map = (Object[]) m_map.get("menu"); //Guardamos la variable para los filtros
                reloadButtons((Object[]) m_map.get("menu"));

                /*[user setUnreadDealsNum:[[resp valueForKey:@"badge_deals"] intValue]];
    [user setUnreadChatsNum:[[resp valueForKey:@"badge_chat"]  intValue]];
    [user setUnreadFeedsNum:[[resp valueForKey:@"badge_feeds"]  intValue]];

     bubbleNotification = (String) mapBubbles.get("badge_noti");
               /* TextView badge_notification = (TextView) findViewById(R.id.home_noti_bubble);
                badge_notification.setVisibility(View.VISIBLE);
                badge_notification.setText(bubbleNotification);
                if (bubbleNotification.equals("0")) {
                    badge_notification.setVisibility(View.INVISIBLE);
                }*/



                //Bubbles
                /*
                Map<String, Object> mapBubbles = (Map<String, Object>) result;
                bubbleFeeds     = (String) mapBubbles.get("badge_feeds");
                bubbleDeals     = (String) mapBubbles.get("badge_deals");
                bubbleChats     = (String) mapBubbles.get("badge_chat");
                bubbleNoti      = (String) mapBubbles.get("badge_noti");


                badge_notification.setText(bubbleNoti);
                if (bubbleNoti.equals("0")) {
                    badge_notification.setVisibility(View.INVISIBLE);
                }else {
                    badge_notification.setVisibility(View.VISIBLE);
                }
                TextView badge_feed = (TextView) findViewById(R.id.badge_notification_2);
                badge_feed.setVisibility(View.VISIBLE);
                badge_feed.setText(bubbleFeeds);
                if (bubbleFeeds.equals("0")) {
                    badge_feed.setVisibility(View.INVISIBLE);
                }

                TextView badge_deal = (TextView) findViewById(R.id.badge_notification_3);
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
                ih.setUnreadNotiNum(Integer.parseInt(bubbleNoti));


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
                TextView badge_home = (TextView) findViewById(R.id.badge_notification_1);
                TextView badge_more = (TextView) findViewById(R.id.badge_notification_5);
                badge_home.setVisibility(View.INVISIBLE);
                badge_more.setVisibility(View.INVISIBLE);

                */
                //innoHawk:: Start Code for Bubble Toolbar |Start Code WebView
                //aGetBudgets();
                //End Code

            }

        }, this);

    }

    /*Comprobar si es tablet o no... */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    //innoHawk:: Start Code for Bubble Toolbar

    /*
    protected void aGetBudgets() {
        //Get GCM & IMEI
        HashMap<String, String> gcm = db.getGCMInitial();
        String token = gcm.get("token");
        String emai = gcm.get("emai");
        String device = "android";
        if(token == null){
            token= String.valueOf(0);
        }
        if(emai == null) {
            emai= String.valueOf(0);
        }

        Connector o = Main.getConnector();
        Connector.saveConnector(this, o);
        Object[] aParams2 = {
                o.getUsername(),
                o.getPassword(),
                token,
                emai,
                device
        };
        o.execAsyncMethod("pcint.initFunction", aParams2, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "ResultPcintParam are: " + result.toString());
                Map<String, Object> mapBubbles = (Map<String, Object>) result;
                bubbleLetters = (String) mapBubbles.get("Messages");
                bubbleFriends = (String) mapBubbles.get("Friends");

                TextView badge_letter = (TextView) findViewById(R.id.badge_notification_3);
                //badge_letter.setText(Integer.toString(bubbleLetters));
                badge_letter.setVisibility(View.VISIBLE);
                badge_letter.setText(bubbleLetters);
                if (bubbleLetters.equals("0")) {
                    badge_letter.setVisibility(View.INVISIBLE);
                }

                TextView badge_friend = (TextView) findViewById(R.id.badge_notification_4);
                badge_friend.setVisibility(View.VISIBLE);
                badge_friend.setText(bubbleFriends);

                if (bubbleFriends.equals("0")) {
                    badge_friend.setVisibility(View.INVISIBLE);
                }
                //Creamos fichero de Bubble for Friend
                try {
                    FileOutputStream bubbleFriend = openFileOutput("aFileBubbleFriend.txt", MODE_PRIVATE);
                    OutputStreamWriter aControlBubbleFriend = new OutputStreamWriter(bubbleFriend);
                    aControlBubbleFriend.write(bubbleFriends);
                    aControlBubbleFriend.flush();
                    aControlBubbleFriend.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
                //Creamos fichero de Bubble for Message
                try {
                    FileOutputStream bubbleMessage = openFileOutput("aFileBubbleMessage.txt", MODE_PRIVATE);
                    OutputStreamWriter aControlBubbleMessage = new OutputStreamWriter(bubbleMessage);
                    aControlBubbleMessage.write(bubbleLetters);
                    aControlBubbleMessage.flush();
                    aControlBubbleMessage.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }

                //Deshabilitamos las Actividades que no tienen badgets...
                TextView badge_home = (TextView) findViewById(R.id.badge_notification_1);
                TextView badge_profile = (TextView) findViewById(R.id.badge_notification_2);
                TextView badge_search = (TextView) findViewById(R.id.badge_notification_5);
                badge_home.setVisibility(View.INVISIBLE);
                badge_profile.setVisibility(View.INVISIBLE);
                badge_search.setVisibility(View.INVISIBLE);

            }
        }, this);
    }
*/
	//End Code

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_logout:
                mDrawerLayout.closeDrawer(mDrawerList);
                db.deleteUser();

                Bundle b = new Bundle();
                b.putInt("index", m_iSiteIndex);
                Intent i = new Intent();
                i.putExtras(b);
                setResult(RESULT_LOGOUT, i);

                finish();
                break;
            case R.id.menu_refresh:
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            default:
                //innoHawk:: Start Code Menu Slider
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                        mDrawerLayout.closeDrawer(mDrawerList);
                    } else {
                        mDrawerLayout.openDrawer(mDrawerList);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //innoHawk::  Start Code Menu Slider
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerLayout != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerLayout != null) {
            // Pass any configuration change to the drawer toggles
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    //End Code

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (null == i)
            return;

        switch (requestCode) {
            case ACTIVITY_STATUS_MESSAGE:
                switch (resultCode) {
                    case StatusMessageActivity.RESULT_OK: {
                        Bundle b = i.getExtras();
                        updateStatus(b.getString("status_message"));
                    }
                    break;
                }
                break;
        }
    }


    protected void LaunchActivityStatus() {
        Intent i = new Intent(m_actHome, StatusMessageActivity.class);
        i.putExtra("status_message", (String) m_map.get("status"));
        startActivityForResult(i, ACTIVITY_STATUS_MESSAGE);
        this.overridePendingTransition(0, 0);
    }

    //innoHawk:: He cambiado nombre actividad por MyInfo
    protected void LaunchActivityInfo() {
        Intent i = new Intent(this, ProfileMyInfoActivity.class);
        i.putExtra("username", m_sUsername);
        i.putExtra("user_title", m_sUserTitle);
        i.putExtra("thumb", m_sThumb);
        i.putExtra("info", m_sInfo);
        i.putExtra("status", m_sStatus);
        startActivityForResult(i, ACTIVITY_PROFILE_INFO);
        this.overridePendingTransition(0, 0);
    }


    protected void LaunchActivityLocation() {
        Connector o = Main.getConnector();
        Intent i = new Intent(this, LocationActivity.class);
        i.putExtra("username", o.getUsername());
        startActivityForResult(i, ACTIVITY_LOCATION);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityMail() {
        Intent i = new Intent(this, MailHomeActivity.class);
        startActivityForResult(i, ACTIVITY_MAIL_HOME);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityFriends() {
        Intent i = new Intent(this, FriendsHomeActivity.class);
        startActivityForResult(i, ACTIVITY_FRIENDS_HOME);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityNoti() {
        Intent i = new Intent(this, NotifyActiviy.class);
        startActivityForResult(i, ACTIVITY_NOTI_HOME);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityPhotos() {
        Connector o = Main.getConnector();
        Intent i = new Intent(this, ImagesAlbumsActivity.class);
        i.putExtra("username", o.getUsername());
        startActivityForResult(i, ACTIVITY_IMAGES_ALBUMS);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityVideos() {
        Connector o = Main.getConnector();
        Intent i = new Intent(this, VideosAlbumsActivity.class);
        i.putExtra("username", o.getUsername());
        startActivityForResult(i, ACTIVITY_VIDEOS_ALBUMS);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivitySounds() {
        Connector o = Main.getConnector();
        Intent i = new Intent(this, SoundsAlbumsActivity.class);
        i.putExtra("username", o.getUsername());
        startActivityForResult(i, ACTIVITY_SOUNDS_ALBUMS);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityEvents() {

    }

    protected void LaunchActivitySearch() {
        Intent i = new Intent(this, SearchHomeActivity.class);
        startActivityForResult(i, ACTIVITY_SEARCH_HOME);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityWebPage(String sTitle, String sUrl) {
        Intent i = new Intent(this, WebPageActivity.class);
        i.putExtra("title", sTitle);
        i.putExtra("url", sUrl);
        startActivityForResult(i, ACTIVITY_WEB_PAGE);
        this.overridePendingTransition(0, 0);
    }

    protected void LaunchActivityBrowser(String sTitle, String sUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sUrl));
        startActivity(browserIntent);
        this.overridePendingTransition(0, 0);
    }

    protected void updateStatus(String s) {
        if (null == s)
            return;
        if (null == m_map)
            return;
        m_map.put("status", s);
        m_sStatus = s;
    }

    //innoHawk:: Varios para webview, permisos de localización, etc...
    interface ThirdPartyOnClickListener extends View.OnClickListener {
        public void setUrl(String s);

        public void setAction(int iAction);

        public void setTitle(String s);
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


    @Override
    public void onBackPressed() {
        //NO SE PODRA BACK BUTTON
    }
}
