package com.innohawk.dan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innohawk.dan.ChatTab.ChatActivity;
import com.innohawk.dan.MenuTab.MenuTabActivity;
import com.innohawk.dan.NewsTab.NewsActivity;
import com.innohawk.dan.VideosTab.VideosActivity;
import com.innohawk.dan.actionbar.ActionBarActivity;
import com.innohawk.dan.activitymaps.ActivityMaps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

abstract public class ActivityBase extends ActionBarActivity {

    protected ActivityBaseHelper m_oActivityHelper;

    protected View m_viewMain;

    protected Boolean m_isToolbarEnabled;
    protected Boolean m_isReloadEnabled;
    protected Bundle m_savedInstanceState;

    /* Code Add to innohawk::PcIntegrad */
    protected RadioGroup m_layoutToolbarContainer;
    protected RelativeLayout m_layoutToolbar;
    protected ActivityBase m_actThis;
    protected ListActivityBase m_actThisList;
    private static final String TAG = "OO ActivityBase";
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
    protected String bubbleLetters;
    protected String bubbleFriends;
    protected String bubbleChats;
    protected String bubbleFeeds;
    protected String bubbleDeals;
    protected String controlActivity;
    static final int SIZE_BLOCK_PRESS_TOOLBAR = 100;
    static final int SIZE_BLOCK_BUBBLE_FRIENDS = 5;
    static final int SIZE_BLOCK_BUBBLE_MESSAGE = 5;
    static final int SIZE_BLOCK_BUBBLE_FEEDS = 5;
    static final int SIZE_BLOCK_BUBBLE_DEALS = 5;
    static final int SIZE_BLOCK_BUBBLE_CHATS = 5;
    /* End Code */

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.onCreate(savedInstanceState, true, true);
    }

    protected void onCreate(Bundle savedInstanceState, boolean isToolbarEnabled) {
        this.onCreate(savedInstanceState, isToolbarEnabled, true);
    }

    protected void onCreate(Bundle savedInstanceState, boolean isToolbarEnabled, boolean isReloadEnabled) {
        this.onCreate(savedInstanceState, isToolbarEnabled, isReloadEnabled, true);
    }

    protected void onCreate(Bundle savedInstanceState, boolean isToolbarEnabled, boolean isReloadEnabled, boolean isTryToRestoreConnector) {
        super.onCreate(savedInstanceState);
        m_actThis = this;
        m_isToolbarEnabled = isToolbarEnabled;
        m_isReloadEnabled = isReloadEnabled;
        m_savedInstanceState = savedInstanceState;
        m_oActivityHelper = new ActivityBaseHelper(this, isTryToRestoreConnector, m_isToolbarEnabled);
    }

    @Override
    public void setContentView(int iLayoutResID) {
        m_viewMain = getLayoutInflater().inflate(iLayoutResID, null);
        super.setContentView(m_viewMain);

        /* Code Add to innohawk::PcIntegrad */

        Intent i = getIntent();
        m_sSite = i.getStringExtra("site");
        m_iSiteIndex = i.getIntExtra("index", 0);
        m_iMemberId = i.getIntExtra("member_id", 0);
        m_sUsername = i.getStringExtra("username");
        m_sPasswd = i.getStringExtra("password");
        m_iProtocolVer = i.getIntExtra("protocol", 2);
        String sClass = this.getClass().getSimpleName();
        String sPackge = this.getClass().getName();

        Log.d(TAG, "El nombre de la Activity es::" + sPackge + " ");

        if ((sClass.equals("Main")) || (sClass.equals("AboutActivity")) || (sClass.equals("EventsActivityVideoFull")) || (sClass.equals("SignupActivity"))  || (sClass.equals("LoginActivity")) || (sClass.equals("FaqActivity")) || (sClass.equals("HelpsServerActivity")) || (sClass.equals("PrivacityActivity"))  || (sClass.equals("TermsActivity"))) {
            //No mostramos NavBar
        } else {

            if (m_isToolbarEnabled) {
                m_layoutToolbar = (RelativeLayout) getLayoutInflater().inflate(R.layout.toolbar, null);
                RelativeLayout.LayoutParams paramsToolbar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                this.addContentView(m_layoutToolbar, paramsToolbar);
                m_layoutToolbarContainer = (RadioGroup) findViewById(R.id.toolbarradiogroup);
                final float scale = getResources().getDisplayMetrics().density;
                //m_layoutToolbarContainer.setBackgroundColor(Color.TRANSPARENT);
                //m_viewMain.setPadding(0, 0, 0, (int) (60 * scale + 0.5f));
                m_viewMain.setPadding(0, 0, 0, 0);

                //Deshabilitamos las Actividades que no tienen badgets...
                TextView badge_feed = (TextView) findViewById(R.id.badge_notification_1);
                TextView badge_home = (TextView) findViewById(R.id.badge_notification_2);//Video
                TextView badge_deal = (TextView) findViewById(R.id.badge_notification_5);//Noti
                TextView badge_chat = (TextView) findViewById(R.id.badge_notification_4); //era por friend
                TextView badge_more = (TextView) findViewById(R.id.badge_notification_3); //Maps
                badge_home.setVisibility(View.INVISIBLE);
                badge_feed.setVisibility(View.INVISIBLE);
                badge_chat.setVisibility(View.INVISIBLE);
                badge_deal.setVisibility(View.INVISIBLE);
                badge_more.setVisibility(View.INVISIBLE);


                //Leemos El Fichero Bubble de Feed y Mostramos si puede
                File aCheckExistFileBubbleFeed = getFilesDir();
                File aCheckExistfeed = new File(aCheckExistFileBubbleFeed, "aFileBubbleFeed.txt");
                if (aCheckExistfeed.length() != 0) {
                    try {
                        FileInputStream BubbleFeed = openFileInput("aFileBubbleFeed.txt");
                        InputStreamReader aBubbleFeed = new InputStreamReader(BubbleFeed);
                        char[] inputBufferfeed = new char[SIZE_BLOCK_BUBBLE_FEEDS];
                        int aCharFeedRead;
                        String aControlBubbleFeed = "";
                        while ((aCharFeedRead = aBubbleFeed.read(inputBufferfeed)) > 0) {
                            String aReadStringFeed = String.copyValueOf(inputBufferfeed, 0, aCharFeedRead);
                            inputBufferfeed = new char[SIZE_BLOCK_BUBBLE_FEEDS];
                            aControlBubbleFeed = aReadStringFeed;
                        }
                        bubbleFeeds = aControlBubbleFeed;
                        aBubbleFeed.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bubbleFeeds != null) {
                        if (!bubbleFeeds.equals("0") && bubbleFeeds.length() > 0) {
                            badge_feed.setVisibility(View.VISIBLE);
                            badge_feed.setText(bubbleFeeds);
                        } else {
                            badge_feed.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        badge_feed.setVisibility(View.INVISIBLE);
                    }
                }

                //Leemos El Fichero Bubble de Deal y Mostramos si puede
                File aCheckExistFileBubbleDeals = getFilesDir();
                File aCheckExistdeals = new File(aCheckExistFileBubbleDeals, "aFileBubbleDeals.txt");
                if (aCheckExistdeals.length() != 0) {
                    try {
                        FileInputStream BubbleDeal = openFileInput("aFileBubbleDeals.txt");
                        InputStreamReader aBubbleDeal = new InputStreamReader(BubbleDeal);
                        char[] inputBufferdeal = new char[SIZE_BLOCK_BUBBLE_DEALS];
                        int aCharDealRead;
                        String aControlBubbleDeal = "";
                        while ((aCharDealRead = aBubbleDeal.read(inputBufferdeal)) > 0) {
                            String aReadStringDeal = String.copyValueOf(inputBufferdeal, 0, aCharDealRead);
                            inputBufferdeal = new char[SIZE_BLOCK_BUBBLE_DEALS];
                            aControlBubbleDeal = aReadStringDeal;
                        }
                        bubbleDeals = aControlBubbleDeal;
                        aBubbleDeal.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bubbleDeals != null) {
                        if (!bubbleDeals.equals("0") && bubbleDeals.length() > 0) {
                            badge_deal.setVisibility(View.VISIBLE);
                            badge_deal.setText(bubbleDeals);
                        } else {
                            badge_deal.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        badge_deal.setVisibility(View.INVISIBLE);
                    }
                }

                //Leemos El Fichero Bubble de Chats y Mostramos si puede
                File aCheckExistFileBubbleChat = getFilesDir();
                File aCheckExistchat = new File(aCheckExistFileBubbleChat, "aFileBubbleChat.txt");
                if (aCheckExistchat.length() != 0) {
                    try {
                        FileInputStream BubbleChat = openFileInput("aFileBubbleChat.txt");
                        InputStreamReader aBubbleChat = new InputStreamReader(BubbleChat);
                        char[] inputBufferChat = new char[SIZE_BLOCK_BUBBLE_CHATS];
                        int aCharChatRead;
                        String aControlBubbleChat = "";
                        while ((aCharChatRead = aBubbleChat.read(inputBufferChat)) > 0) {
                            String aReadStringChat = String.copyValueOf(inputBufferChat, 0, aCharChatRead);
                            inputBufferChat = new char[SIZE_BLOCK_BUBBLE_CHATS];
                            aControlBubbleChat = aReadStringChat;
                        }
                        bubbleChats = aControlBubbleChat;
                        aBubbleChat.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bubbleChats != null) {
                        if (!bubbleChats.equals("0") && bubbleChats.length() > 0) {
                            badge_chat.setVisibility(View.VISIBLE);
                            badge_chat.setText(bubbleChats);
                        } else {
                            badge_chat.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        badge_chat.setVisibility(View.INVISIBLE);
                    }
                }





                File aFileExist = getFilesDir();
                File aActivityHome = new File(aFileExist, "aFileControlNavBar.txt");
                if (aActivityHome.length() != 0) {
                    try {
                        FileInputStream ControlActivity = openFileInput("aFileControlNavBar.txt");
                        InputStreamReader aControlActivity = new InputStreamReader(ControlActivity);
                        char[] inputBufferControlActivity = new char[SIZE_BLOCK_PRESS_TOOLBAR];
                        int aCharControlActivityRead;
                        String aControlBubbleControlActivity = "";
                        while ((aCharControlActivityRead = aControlActivity.read(inputBufferControlActivity)) > 0) {
                            String aReadStringControlActivity = String.copyValueOf(inputBufferControlActivity, 0, aCharControlActivityRead);
                            inputBufferControlActivity = new char[SIZE_BLOCK_PRESS_TOOLBAR];
                            aControlBubbleControlActivity = aReadStringControlActivity;
                        }
                        controlActivity = aControlBubbleControlActivity;
                        aControlActivity.close();

                        Log.d(TAG, "ActivityBase:: El valor de Control  es::" + controlActivity + " ");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else { //Analizamos la activa...
                    String Control = "com.innohawk.dan.activitymaps.ActivityMaps";
                    controlActivity = "com.innohawk.dan.activitymaps.ActivityMaps";
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

                // Declaramos las variables de NavBar y Cargamos la último Actividad
                RadioButton radioButton;

                if (controlActivity.equals("com.innohawk.dan.VideosTab.VideosActivity")) {
                    radioButton = (RadioButton) findViewById(R.id.btnHome);
                    radioButton.setChecked(true);
                } else {
                    radioButton = (RadioButton) findViewById(R.id.btnHome);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerHome);
                }

                if (controlActivity.equals("com.innohawk.dan.ChatTab.ChatActivity")) { //El cambio es por friend
                    radioButton = (RadioButton) findViewById(R.id.btnFriend);
                    radioButton.setChecked(true);
                } else {
                    radioButton = (RadioButton) findViewById(R.id.btnFriend);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerChat);
                }

                if (controlActivity.equals("com.innohawk.dan.NewsTab.NewsActivity")) {
                    radioButton = (RadioButton) findViewById(R.id.btnProfile);
                    radioButton.setChecked(true);
                } else {
                    radioButton = (RadioButton) findViewById(R.id.btnProfile);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerProfile);
                }

                if (controlActivity.equals("com.innohawk.dan.MenuTab.MenuTabActivity")) {
                    radioButton = (RadioButton) findViewById(R.id.btnSearch);
                    radioButton.setChecked(true);
                } else {
                    radioButton = (RadioButton) findViewById(R.id.btnSearch);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerSearch);
                }

                if (controlActivity.equals("com.innohawk.dan.activitymaps.ActivityMaps")) {
                    radioButton = (RadioButton) findViewById(R.id.btnMail);
                    radioButton.setChecked(true);
                } else {
                    radioButton = (RadioButton) findViewById(R.id.btnMail);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerMail);
                }

                if ((sClass.equals("VideosActivity"))){
                    radioButton = (RadioButton) findViewById(R.id.btnHome);
                    radioButton.setChecked(true);
                    //Desactivamos todoe el resto
                    radioButton = (RadioButton) findViewById(R.id.btnSearch);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerSearch);
                    radioButton = (RadioButton) findViewById(R.id.btnMail);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerMail);
                    radioButton = (RadioButton) findViewById(R.id.btnProfile);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerProfile);
                    radioButton = (RadioButton) findViewById(R.id.btnFriend);
                    radioButton.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListenerChat);
                }
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListenerHome = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                m_actThis.gotoHome();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListenerMail = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                m_actThis.gotoMail();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListenerProfile = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                m_actThis.gotoProfile();
            }
        }
    };


    private CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListenerChat = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                m_actThis.gotoFriend();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListenerSearch = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                m_actThis.gotoSearch();
            }
        }
    };


    protected void gotoHome() {
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

        Intent intentHome = new Intent(m_actThis, VideosActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Connector o = Main.getConnector();
        intentHome.putExtra("site", o.getSiteUrl());
        intentHome.putExtra("username", o.getUsername());
        intentHome.putExtra("password", o.getPasswordClear());
        intentHome.putExtra("protocol", o.getProtocolVer());
        intentHome.putExtra("member_id", o.getMemberId());
        this.startActivity(new Intent(intentHome));
        this.overridePendingTransition(0, 0);
    }

    protected void gotoMail() {
        String Control = "com.innohawk.dan.activitymaps.ActivityMaps";
        try {
            FileOutputStream control = openFileOutput("aFileControlNavBar.txt", MODE_PRIVATE);
            OutputStreamWriter aControl = new OutputStreamWriter(control);
            aControl.write(Control);
            aControl.flush();
            aControl.close();
        } catch (IOException error) {
            error.printStackTrace();
        }

        Intent intentHome = new Intent(m_actThis, ActivityMaps.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Connector o = Main.getConnector();
        intentHome.putExtra("site", o.getSiteUrl());
        intentHome.putExtra("username", o.getUsername());
        intentHome.putExtra("password", o.getPasswordClear());
        intentHome.putExtra("protocol", o.getProtocolVer());
        intentHome.putExtra("member_id", o.getMemberId());
        this.startActivity(new Intent(intentHome));
        this.overridePendingTransition(0, 0);
    }

    protected void gotoSearch() {

            String Control = "com.innohawk.dan.MenuTab.MenuTabActivity";
            try {
                FileOutputStream control = openFileOutput("aFileControlNavBar.txt", MODE_PRIVATE);
                OutputStreamWriter aControl = new OutputStreamWriter(control);
                aControl.write(Control);
                aControl.flush();
                aControl.close();
            } catch (IOException error) {
                error.printStackTrace();
            }

            Intent intentHome = new Intent(m_actThis, MenuTabActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Connector o = Main.getConnector();
            intentHome.putExtra("site", o.getSiteUrl());
            intentHome.putExtra("username", o.getUsername());
            intentHome.putExtra("password", o.getPasswordClear());
            intentHome.putExtra("member_id", o.getMemberId());
            intentHome.putExtra("protocol", o.getProtocolVer());
            this.startActivity(new Intent(intentHome));
            this.overridePendingTransition(0, 0);

    }

    protected void gotoFriend() {

            String Control = "com.innohawk.dan.ChatTab.ChatActivity";
            try {
                FileOutputStream control = openFileOutput("aFileControlNavBar.txt", MODE_PRIVATE);
                OutputStreamWriter aControl = new OutputStreamWriter(control);
                aControl.write(Control);
                aControl.flush();
                aControl.close();
            } catch (IOException error) {
                error.printStackTrace();
            }

            Intent intentHome = new Intent(m_actThis, ChatActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Connector o = Main.getConnector();
            intentHome.putExtra("site", o.getSiteUrl());
            intentHome.putExtra("username", o.getUsername());
            intentHome.putExtra("password", o.getPasswordClear());
            intentHome.putExtra("member_id", o.getMemberId());
            intentHome.putExtra("protocol", o.getProtocolVer());
            this.startActivity(new Intent(intentHome));
            this.overridePendingTransition(0, 0);

    }

    protected void gotoProfile() {

            String Control = "com.innohawk.dan.NewsTab.NewsActivity";
            try {
                FileOutputStream control = openFileOutput("aFileControlNavBar.txt", MODE_PRIVATE);
                OutputStreamWriter aControl = new OutputStreamWriter(control);
                aControl.write(Control);
                aControl.flush();
                aControl.close();
            } catch (IOException error) {
                error.printStackTrace();
            }

            Intent intentHome = new Intent(m_actThis, NewsActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Connector o = Main.getConnector();
            intentHome.putExtra("site", o.getSiteUrl());
            intentHome.putExtra("username", o.getUsername());
            intentHome.putExtra("password", o.getPasswordClear());
            intentHome.putExtra("protocol", o.getProtocolVer());
            intentHome.putExtra("member_id", o.getMemberId());
            this.startActivity(new Intent(intentHome));
            this.overridePendingTransition(0, 0);

    }

    /* End Code */

    public void setTitleCaption(String s) {
        setTitle(s);
    }

    protected void setTitleCaption(int iStringId) {
        setTitle(getString(iStringId));
    }

    protected void reloadRemoteData() {

    }

    protected void customAction() {

    }

    protected void alertError(Integer iLangString) {
        m_oActivityHelper.alertError(getString(iLangString));
    }

    public String correctSiteUrl(String sUrl) {
        return m_oActivityHelper.correctSiteUrl(sUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return m_oActivityHelper.onCreateOptionsMenu(menu, m_isReloadEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_oActivityHelper.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                reloadRemoteData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

}