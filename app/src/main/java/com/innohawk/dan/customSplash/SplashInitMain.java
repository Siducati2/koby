package com.innohawk.dan.customSplash;

/**
 * Created by innohawk on 2/12/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookSdk;
import com.innohawk.dan.BuildConfig;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.activitymaps.ActivityMaps;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.sqlite.SQLiteActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class SplashInitMain extends Activity {

    private static final String TAG = "MainActivity";
    private static final boolean DO_XML = false;

    private ViewGroup mMainView;
    private SplashSplashView mSplashView;
    private View mContentView;
    private Handler mHandler = new Handler();

    protected static Connector m_oConnector = null;
    SQLiteActivity db_;
    private static final int ACTIVITY_HOME = 1;
    protected String m_sSiteUrl,Username,Pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        db_ = new SQLiteActivity(getApplicationContext());
        HashMap<String, String> user = db_.getExistUsers();
        Username = user.get("username");
        Pwd = user.get("pwr");
        m_sSiteUrl = AppConfig.URL_MAIN;
        mMainView = new SplashMainView(getApplicationContext());
        mSplashView = ((SplashMainView) mMainView).getSplashView();
        setContentView(mMainView);
        // pretend like we are loading data
        startLoadingData();
    }

    private void startLoadingData(){
        // finish "loading data" in a random time between 1 and 3 seconds
        Random random = new Random();
        mHandler.postDelayed(new Runnable(){
            @Override
            public void run(){
                onLoadingDataEnded();
            }
        }, 900);
    }

    private void onLoadingDataEnded(){
        // start the splash animation
        mSplashView.splashAndDisappear(new SplashSplashView.ISplashListener(){
            @Override
            public void onStart(){
                // log the animation start event
                if(BuildConfig.DEBUG){
                    //Log.d(TAG, "splash started");
                }
            }
            @Override
            public void onUpdate(float completionFraction){
                // log animation update events
                if(BuildConfig.DEBUG){
                   // Log.d(TAG, "splash at " + String.format("%.2f", (completionFraction * 100)) + "%");
                }
            }
            @Override
            public void onEnd(){
                // log the animation end event
                if(BuildConfig.DEBUG){
                   // Log.d(TAG, "splash ended");
                }
                // free the view so that it turns into garbage
                mSplashView = null;
                ((SplashMainView) mMainView).unsetSplashView();
                //Log.d(TAG, "username: " + Username + " & pass: " + Pwd);
                if(Username != null && Pwd != null) {
                        //TAB BAR:: Nos aseguramos que se inicie en HomeActivity NavBar - Además sirve de control para que no salga mas la INTRO al inicio...
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
                    //Recogemos en variables
                        String iIdProfile;
                        String iProtocolVer;
                        String sProfileUsername = null;
                        String sProfilePwdHash = null;
                        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
                        iIdProfile = prefs.getString("member_id",null);
                        iProtocolVer = prefs.getString("protocol",null);
                        sProfileUsername = prefs.getString("username",null);
                        sProfilePwdHash = prefs.getString("password",null);

                        int ID_int_Profile = Integer.parseInt(iIdProfile);
                        int ID_int_Protocol = Integer.parseInt(iProtocolVer);

                        if(Username.equals(sProfileUsername) && Pwd.equals(sProfilePwdHash) ) {
                            Intent intentHome = new Intent(SplashInitMain.this, ActivityMaps.class);
                            intentHome.putExtra("site", correctSiteUrl(m_sSiteUrl));
                            intentHome.putExtra("member_id", ID_int_Profile);
                            intentHome.putExtra("username", sProfileUsername);
                            intentHome.putExtra("password", sProfilePwdHash);
                            intentHome.putExtra("protocol", ID_int_Protocol);
                            intentHome.putExtra("index", 0);

                            //Super importante para grabar el connector
                            Connector o = new Connector(correctSiteUrl(m_sSiteUrl),
                                    sProfileUsername,
                                    sProfilePwdHash,
                                    ID_int_Profile);
                            String Pass = sProfilePwdHash;
                            o.setPassword(32 == Pass.length() || 40 == Pass.length() ? Pass : o.md5(Pass));
                            o.setProtocolVer(ID_int_Protocol);
                            Main.setConnector(o);
                            Connector.saveConnector(SplashInitMain.this, o);
                            //Super importante

                            startActivityForResult(intentHome, ACTIVITY_HOME);
                            finish();
                        }
                }else {
                        Intent intent = new Intent(SplashInitMain.this, Main.class);
                        startActivity(intent);
                        finish();
                }

            }
        });
    }
    public static String getLang() {
        String s = Locale.getDefault().getLanguage();
        Log.i(TAG, "Lang: " + s);
        return s;
    }
    public String correctSiteUrl(String sUrl) {
        if (!sUrl.startsWith("http://") && !sUrl.startsWith("https://"))
            sUrl = "http://" + sUrl;
        if (!sUrl.endsWith("/"))
            sUrl += "/";
        if (!sUrl.endsWith("xmlrpc/"))
            sUrl += "xmlrpc/";
        return sUrl;
    }
}
