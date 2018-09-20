package com.innohawk.dan;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.innohawk.dan.activitymaps.ActivityMaps;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.customSplash.SplashSplashView;
import com.innohawk.dan.helps.AboutActivity;
import com.innohawk.dan.helps.HelpActivity;
import com.innohawk.dan.home.HomeActivity;
import com.innohawk.dan.notification.NotificationUtils;
import com.innohawk.dan.register.SignupActivity;
import com.innohawk.dan.splash.IntroductionPage;
import com.innohawk.dan.sqlite.SQLiteActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.innohawk.dan.notification.GcmUtilitiesActivity.CheckPlayServices;


public class Main extends ActivityBase {
    private static final String TAG = "OO Main";

    public static final String LOCK_TO_SITE = AppConfig.LOCK_TO_SITE;

    private static final int ACTIVITY_LOGIN = 0;
    private static final int ACTIVITY_HOME = 1;
    private static final int ACTIVITY_ABOUT = 2;
    private static final int ACTIVITY_LOGINFB = 3;

    public static Main MainActivity = null;

    protected static Connector m_oConnector = null;

    protected SiteAdapter adapter;

    /* Code Add to innoHawk */
    protected EditText m_editUsername;
    protected EditText m_editPassword;
    protected TextView m_account;
    protected Button btn_action_loginIMG; //Es el icono mail, que tocando la flecha cambia!!!
    protected Button btn_action_login;
    protected Button btn_action_forgot;
    protected Button btn_action_about;
    protected Button btn_action_guest;
    protected String m_sSiteUrl;




    private LoginButton loginButton; //Facebook
    protected Button btn_action_loginFB;//Hacemos la pirula: Este es el boton customizado que al hacer click va al fb

    ImageView ArrowMail,ArrowFB,ArrowGuest; //Las flechas!
    private CallbackManager callbackManager;


    protected Button btn_action_signup;
    protected TextView or_login_with;
    //Obsoleto
    private String GCM_ID;
    private String IMEI;
    private GoogleCloudMessaging gcm;
    //FIREBASE
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String token,emai;

    private SQLiteActivity db;
    public static String PACKAGE_NAME;
    /* End Code */

    //Permission
    private static final int MY_PERMISSION_VAL = 0; //Tiene implicito el READ (que es el que necesitamos
    private View mLayout;
    View layoutHomePermission;
    RelativeLayout rl_dialog;
    Typeface tt;

    //Splash Custom
    private static final boolean DO_XML = false;

    private ViewGroup mMainView;
    private SplashSplashView mSplashView;
    private View mContentView;
    private Handler mHandler = new Handler();
    /* End Code */

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Code Add to CreationBCN::PcIntegrad */
        //This next line is for facebook connect
        FacebookSdk.sdkInitialize(getApplicationContext());
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        PACKAGE_NAME = getApplicationContext().getPackageName();
        calculateHashKey(PACKAGE_NAME);


        MainActivity = this;




       // getWindow().requestFeature(Window.FEATURE_ACTION_BAR); //Esto hace no iniciar el edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //getActionBar().hide();

        setContentView(R.layout.main);
        m_sSiteUrl = AppConfig.URL_MAIN;

        //Las flechas
        //Por si las moscas, si no tirase el login hayq ue añadir listBase Activity y lo de adapter
        ArrowMail = (ImageView) findViewById(R.id.icon_mail_arrow);
        ArrowMail.setVisibility(View.VISIBLE);
        ArrowFB = (ImageView) findViewById(R.id.icon_fb_arrow);
        ArrowFB.setVisibility(View.INVISIBLE);
        ArrowGuest = (ImageView) findViewById(R.id.icon_guest_arrow);
        ArrowGuest.setVisibility(View.INVISIBLE);
        //Cambiar la tipografia
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Carlito-BoldItalic.ttf");
        Typeface tb = Typeface.createFromAsset(getAssets(), "fonts/Carlito-Bold.ttf");

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setTypeface(tt);
        m_account = (TextView) findViewById(R.id.textViewAccount);
        m_account.setTypeface(tt);
        m_editUsername = (EditText) findViewById(R.id.editTextUsername);
        m_editUsername.setTypeface(tt);
        m_editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrowMail.setVisibility(View.VISIBLE);
                ArrowFB.setVisibility(View.INVISIBLE);
                ArrowGuest.setVisibility(View.INVISIBLE);
            }

        });

        m_editPassword = (EditText) findViewById(R.id.editTextPassword);
        m_editPassword.setTypeface(tt);
        m_editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrowMail.setVisibility(View.VISIBLE);
                ArrowFB.setVisibility(View.INVISIBLE);
                ArrowGuest.setVisibility(View.INVISIBLE);
            }

        });





        //About
        btn_action_about = (Button) findViewById(R.id.btn_terms);
        btn_action_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                AboutActivity();
            }
        });

        // This next lines & fx are for login and signup
        btn_action_login = (Button) findViewById(R.id.action_LogIn);
        btn_action_login.setTypeface(tt);
        btn_action_login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ArrowMail.setVisibility(View.VISIBLE);
                ArrowFB.setVisibility(View.INVISIBLE);
                ArrowGuest.setVisibility(View.INVISIBLE);
                Object[] aParams = {
                        m_editUsername.getText().toString(),
                        m_editPassword.getText().toString()
                };

                actionLogin("dolphin.login4", aParams,m_sSiteUrl);
            }
        });


        //Forgot
        btn_action_forgot = (Button) findViewById(R.id.action_Forgot);
        btn_action_forgot.setTypeface(tt);
        btn_action_forgot.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String sLang = Locale.getDefault().getLanguage();
                String username = m_editUsername.getText().toString();
                Object[] aParams = {
                        username,
                        sLang
                };
                if(username.equals("")||username.equals(" ")||username.equals(null)||username.matches("")||username.isEmpty()) {
                    Toast.makeText(Main.this, getResources().getString(R.string.Main_errorForgot), Toast.LENGTH_LONG).show();
                }else{
                    actionForgot("innohawk.recoveryPass", aParams,m_sSiteUrl);
                }
            }
        });

        // Iniciamos la BBDD
        db = new SQLiteActivity(getApplicationContext());
        Log.d(TAG, "WAITT: "+PACKAGE_NAME);
        if( CheckPlayServices(this) == true ){
            //Para no estar consultando tot el rato, lo que haremos será ver la bbdd. Si existen los valores
            //se omite pedir el token a GCM
            Log.d(TAG, "FIREBASE ENTRAMOS IF: "+PACKAGE_NAME);
            HashMap<String, String> gcm = db.getGCMInitial();
            token = gcm.get("token");
            emai = gcm.get("emai");

            Log.d(TAG, "FIREBASE ENTRAMOS token: "+token);
            //Guardamos memoria temporal
            SharedPreferences.Editor editor = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE).edit();
            editor.putString("token_firebase","" + token);
            editor.commit();

            //if(token == null && emai == null) {
            //  Log.d(TAG, "ENTRAMOS IF NULL: "+token);
            initPNFunction(this);
            //}
        }



        btn_action_signup = (Button) findViewById(R.id.action_SignUp);
        btn_action_signup.setTypeface(tt);
        btn_action_signup.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SignUpActivity();
            }
        });

        //or_login_with = (TextView) findViewById(R.id.info);
        //or_login_with.setTypeface(tf);


        // Important!!!:: for user_birthday you can put your app Facebook in permission reviews of Facebook
        // The permission normals are: Email, Profile Public and User friends...
        // Implement code of Facebook Connect
        //Desactivamos

        loginButton = (LoginButton)findViewById(R.id.loginFB_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends","user_location","user_hometown"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                //Datos para apuntar server
                                String sCorrectUrl = AppConfig.URL_METHODS;
                                String sMethod = "pcint.fb_connect";
                                String sLang = Locale.getDefault().getLanguage();

                                //Get Datos de Fb
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String first_name = object.getString("first_name");
                                    String last_name = object.getString("last_name");
                                    String gender = object.getString("gender");
                                    String link = object.getString("link");
                                    String locale = object.getString("locale");

                                    Object[] aVars = {id, email, first_name, last_name, gender, name, link, locale, loginResult.getAccessToken().getToken()};
                                    Object[] aParams = {
                                            "fb_connect_creationbcn",
                                            "loginFB",
                                            aVars,
                                            sLang
                                    };

                                    //Nos Logeamos
                                    actionLogInSocials(sMethod, sCorrectUrl, aParams);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,first_name,last_name,gender,link,locale");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Main.this, getResources().getString(R.string.Main_fb_cancel), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(Main.this, getResources().getString(R.string.Main_fb_error), Toast.LENGTH_LONG).show();
            }
        });

        btn_action_guest = (Button) findViewById(R.id.btn_guest);
        btn_action_guest.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ArrowMail.setVisibility(View.INVISIBLE);
                ArrowFB.setVisibility(View.INVISIBLE);
                ArrowGuest.setVisibility(View.VISIBLE);
                SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
                String Guest_User = prefs.getString("guest_name",null); //Si alguna vez ha entrado como GUEST, esta variable SIEMPRE estará guardada
                //Luego tendremos en cuenta otras variables (si isGuest = yes or = no) para saber si hemos login con guest o no
                if(Guest_User==null){
                    Object[] aParams = {
                            "guest",
                            "guest",
                            "",
                            Main.getLang(),
                            ""

                    };
                    Connector o = new Connector(correctSiteUrl(m_sSiteUrl), "", "", 0);
                    guest_oCallback.setMethod("ih.initguest");
                    guest_oCallback.setParams(aParams);
                    guest_oCallback.setConnector(o);
                    o.execAsyncMethod("ih.initguest", aParams, guest_oCallback, Main.this);
                }else{
                    Object[] aParams = {
                            Guest_User,
                            "guest",
                            "yes",
                            Main.getLang(),
                            ""

                    };
                    Connector o = new Connector(correctSiteUrl(m_sSiteUrl), "", "", 0);
                    guest_oCallback.setMethod("ih.initguest");
                    guest_oCallback.setParams(aParams);
                    guest_oCallback.setConnector(o);
                    o.execAsyncMethod("ih.initguest", aParams, guest_oCallback, Main.this);
                }
            }
        });

        btn_action_loginIMG = (Button) findViewById(R.id.icon_mail);
        btn_action_loginIMG.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ArrowMail.setVisibility(View.VISIBLE);
                ArrowFB.setVisibility(View.INVISIBLE);
                ArrowGuest.setVisibility(View.INVISIBLE);
            }
        });

        btn_action_loginFB = (Button) findViewById(R.id.loginFB_buttonDraw);
        btn_action_loginFB.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ArrowMail.setVisibility(View.INVISIBLE);
                ArrowFB.setVisibility(View.VISIBLE);
                ArrowGuest.setVisibility(View.INVISIBLE);
                if (v.getId() == R.id.loginFB_buttonDraw){
                    loginButton.performClick();
                }

            }
        });
         /* End Code */


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
        /* Permission Request */
        verifyPermissionRequest();
        /* Code Add to CreationBCN::PcIntegrad */
        // Miramos a ver si existen usuarios Registrados en la tabla BBDD para pasar a Login Direct
        HashMap<String, String> user = db.getExistUsers();
        String username = user.get("username");
        String password = user.get("pwr");
        Log.d(TAG, "username: " + username + " & pass: " + password);
        if(username != null && password != null) {
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

            if(username.equals(sProfileUsername) && password.equals(sProfilePwdHash) ) {
                Intent intentHome = new Intent(Main.this, ActivityMaps.class);
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
                Connector.saveConnector(Main.this, o);
                //Super importante

                startActivityForResult(intentHome, ACTIVITY_HOME);
                finish();
            }
        }
        /* End Code */

        //adapter = new SiteAdapter(this, null);
        //setListAdapter(adapter);
    }





    //Forgot Action
    protected void actionForgot(String sMethod, Object[] aParams, String sURL) {

        String sCorrectUrl = correctSiteUrl(sURL);

        try {
            URI.create(sCorrectUrl);
        } catch (IllegalArgumentException e) {
            Toast.makeText(Main.this, getResources().getString(R.string.msg_url_incorrect), Toast.LENGTH_LONG).show();
            return;
        }

        Connector o = new Connector(sCorrectUrl, "", "", 0);



        f_oCallback.setMethod(sMethod);
        f_oCallback.setParams(aParams);
        f_oCallback.setConnector(o);
        o.execAsyncMethod(sMethod, aParams, f_oCallback, Main.this);
    }
    class ForgotActionCallback extends Connector.Callback {
        protected Main context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;

        public ForgotActionCallback(Main c) {
            context = c;
        }

        public void setMethod(String s) {
            sMethod = s;
        }

        public void setParams(Object[] a) {
            aParams = a;
        }

        public Object[] getParams() {
            return aParams;
        }

        public void setConnector(Connector o) {
            oConnector = o;
        }
    }
    private ForgotActionCallback f_oCallback = new ForgotActionCallback(Main.this) {
        public boolean callFailed(Exception e) {
            Toast.makeText(Main.this, getResources().getString(R.string.Main_errorForgotConnect), Toast.LENGTH_LONG).show();
            return true;
        }

        public void callFinished(Object result) {
            Toast.makeText(Main.this, getResources().getString(R.string.Main_ForgotConnect), Toast.LENGTH_LONG).show();
        }
    };

    //Login Action
    protected void actionLogin(String sMethod, Object[] aParams, String sURL) {

        String sCorrectUrl = correctSiteUrl(sURL);

        try {
            URI.create(sCorrectUrl);
        } catch (IllegalArgumentException e) {
            Toast.makeText(Main.this, getResources().getString(R.string.msg_url_incorrect), Toast.LENGTH_LONG).show();
            return;
        }

        Connector o = new Connector(sCorrectUrl, m_editUsername.getText().toString(), m_editPassword.getText().toString(), 0);

        m_oCallback.setMethod(sMethod);
        m_oCallback.setParams(aParams);
        m_oCallback.setConnector(o);

        o.execAsyncMethod(sMethod, aParams, m_oCallback, Main.this);
    }
    class LoginActionCallback extends Connector.Callback {
        protected Main context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;
        public LoginActionCallback(Main c) {
            context = c;
        }
        public void setMethod(String s) {
            sMethod = s;
        }
        public void setParams(Object[] a) {
            aParams = a;
        }
        public Object[] getParams() {
            return aParams;
        }
        public void setConnector(Connector o) {
            oConnector = o;
        }
    }
    private LoginActionCallback m_oCallback = new LoginActionCallback(Main.this) {
        public boolean callFailed(Exception e) {
            if (e.getMessage().endsWith("[code 1]") && "dolphin.login4" == sMethod) { // method dolphin.login4 not found
                Log.d(TAG, "new protocol dolphin.login4 function not found, using old protocol dolphin.login function");
                Object[] aParams = m_oCallback.getParams();
                aParams[1] = oConnector.md5((String) aParams[1]);
                setMethod("dolphin.login2");
                oConnector.execAsyncMethod("dolphin.login2", aParams, this, context);
                return false;
            } else if (e.getMessage().endsWith("[code 1]") && "dolphin.login2" == sMethod) { // method dolphin.login2 not found
                Log.d(TAG, "new protocol dolphin.login2 function not found, using old protocol dolphin.login function");
                setMethod("dolphin.login");
                oConnector.execAsyncMethod("dolphin.login", aParams, this, context);
                return false;
            } else {
                return true;
            }
        }

        public void callFinished(Object result) {
            Log.d(TAG, "dolphin.login result: " + result.toString() + " / class: " + result.getClass());

            int iIdProfile;
            int iProtocolVer;
            String sProfileUsername = null;
            String sProfilePwdHash = null;


            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result;

                if (map.get("member_id") instanceof String)
                    iIdProfile = Integer.parseInt((String) map.get("member_id"));
                else
                    iIdProfile = (Integer) map.get("member_id");

                if (map.get("protocol_ver") instanceof String)
                    iProtocolVer = Integer.parseInt((String) map.get("protocol_ver"));
                else
                    iProtocolVer = (Integer) map.get("protocol_ver");

                if (iProtocolVer >= 4) {
                    sProfileUsername = (String) map.get("member_username");
                    sProfilePwdHash = (String) map.get("member_pwd_hash");
                }

            } else {
                iIdProfile = Integer.valueOf(result.toString());
                iProtocolVer = 1;
            }

            Log.d(TAG, "MEMBER ID: " + iIdProfile);
            Log.d(TAG, "PROTOCOL VERSION: " + iProtocolVer);


            if (iIdProfile > 0) {

                //Se inserta Usuario en BBDD
                db.addUser(sProfileUsername, sProfilePwdHash);

                /*Bundle b = new Bundle();
                b.putInt("index", 0);
                b.putString("site", correctSiteUrl(m_sSiteUrl));
                b.putInt("member_id", iIdProfile);
                b.putString("username", sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                b.putString("password", sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                b.putBoolean("remember_password", true);
                b.putInt("protocol", iProtocolVer);
                Intent i = new Intent();
                i.putExtras(b);

                setResult(RESULT_LOGIN, i);*/

                //Guardamos en variables
                SharedPreferences.Editor editor = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE).edit();
                editor.putString("site","" + correctSiteUrl(m_sSiteUrl));
                editor.putString("member_id","" + iIdProfile);
                editor.putString("username","" + sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                editor.putString("password","" + sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                editor.putString("protocol","" + iProtocolVer);
                editor.putString("isGuest","no"); //Aqui definimos que login con guest:: Con logout, se borra
                editor.putString("index","0");
                editor.commit();

                //Super importante para grabar el connector
                Connector o = new Connector("" + correctSiteUrl(m_sSiteUrl),
                        "" + sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString(),
                        "" + sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString(),
                        iIdProfile);
                String Pass = "" + sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString();
                o.setPassword(32 == Pass.length() || 40 == Pass.length() ? Pass : o.md5(Pass));
                o.setProtocolVer(iProtocolVer);
                Main.setConnector(o);
                Connector.saveConnector(Main.this, o);
                //Super importante

                Intent intentHome = new Intent(Main.this, ActivityMaps.class);
                intentHome.putExtra("site", correctSiteUrl(m_sSiteUrl));
                intentHome.putExtra("member_id", iIdProfile);
                intentHome.putExtra("username", sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                intentHome.putExtra("password", sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                intentHome.putExtra("protocol", iProtocolVer);
                intentHome.putExtra("isGuest", "no");
                intentHome.putExtra("index", 0);

                startActivityForResult(intentHome, ACTIVITY_HOME);

                finish();

            } else {
                Toast.makeText(Main.this, getResources().getString(R.string.msg_login_incorrect), Toast.LENGTH_LONG).show();
            }
        }
    };

    class LoginActionCallbackGUEST extends Connector.Callback {
        protected Main context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;
        public LoginActionCallbackGUEST(Main c) {
            context = c;
        }
        public void setMethod(String s) {
            sMethod = s;
        }
        public void setParams(Object[] a) {
            aParams = a;
        }
        public Object[] getParams() {
            return aParams;
        }
        public void setConnector(Connector o) {
            oConnector = o;
        }
    }
    private LoginActionCallbackGUEST guest_oCallback = new LoginActionCallbackGUEST(Main.this) {
        public boolean callFailed(Exception e) {
            return true;
        }

        public void callFinished(Object result) {
            int iIdProfile;
            int iProtocolVer;
            String sProfileUsername = null;
            String sProfilePwdHash = null;
             @SuppressWarnings("unchecked")
             Map<String, Object> map = (Map<String, Object>) result;

            if (map.get("member_id") instanceof String)
                iIdProfile = Integer.parseInt((String) map.get("member_id"));
            else
                iIdProfile = (Integer) map.get("member_id");

            if (map.get("protocol_ver") instanceof String)
                iProtocolVer = Integer.parseInt((String) map.get("protocol_ver"));
            else
                iProtocolVer = (Integer) map.get("protocol_ver");

            sProfileUsername = (String) map.get("member_username");
            sProfilePwdHash = (String) map.get("member_pwd_hash");

            if (iIdProfile > 0) {
                //Se inserta Usuario en BBDD
                db.addUser(sProfileUsername, sProfilePwdHash);
                //Guardamos en variables
                SharedPreferences.Editor editor = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE).edit();
                editor.putString("site","" + correctSiteUrl(m_sSiteUrl));
                editor.putString("member_id","" + iIdProfile);
                editor.putString("guest_name","" + sProfileUsername); //Definimos el name del guest:: Nunca se borra
                editor.putString("isGuest","yes"); //Aqui definimos que login con guest:: Con logout, se borra
                editor.putString("username","" + sProfileUsername);
                editor.putString("password","" + sProfilePwdHash);
                editor.putString("protocol","" + iProtocolVer);
                editor.putString("index","0");
                editor.commit();

                //Super importante para grabar el connector
                Connector o = new Connector("" + correctSiteUrl(m_sSiteUrl),
                        "" + sProfileUsername,
                        "" + sProfilePwdHash,
                        iIdProfile);
                String Pass = "" + sProfilePwdHash;
                o.setPassword(32 == Pass.length() || 40 == Pass.length() ? Pass : o.md5(Pass));
                o.setProtocolVer(iProtocolVer);
                Main.setConnector(o);
                Connector.saveConnector(Main.this, o);
                //Super importante
                Intent intentHome = new Intent(Main.this, ActivityMaps.class);
                intentHome.putExtra("site", correctSiteUrl(m_sSiteUrl));
                intentHome.putExtra("member_id", iIdProfile);
                intentHome.putExtra("username", sProfileUsername);
                intentHome.putExtra("password", sProfilePwdHash);
                intentHome.putExtra("protocol", iProtocolVer);
                intentHome.putExtra("guest_name", sProfileUsername);
                intentHome.putExtra("isGuest", "yes");
                intentHome.putExtra("index", 0);
                startActivityForResult(intentHome, ACTIVITY_HOME);
                finish();
            }
        }
    };

    //Get KEYHASH FB_Connect
    private void calculateHashKey(String PackageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(PackageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("SHA-1:", "SHA1: "+md);
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //TOKEN
    public void initPNFunction(final Context context){
        //GCM:: Obsoleto
        /*new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... arg0) {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                try {
                    GCM_ID = GcmUtilitiesActivity.ObtenerRegistrationTokenEnGcm(getApplicationContext());
                    IMEI = DameIMEI(context);
                    Log.d(TAG, "El resultado de GCM es: "+GCM_ID);
                    Log.d(TAG, "El resultado del IMEI es: "+IMEI);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //Enviamos EMEI y GCM_ID al Servidor y lo registramos en la tabla Pcint_CGM_Main
                try {
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()); //Get Data today
                    db.addToken(GCM_ID, IMEI, currentDateTimeString);
                    String sSendInfoServer = GcmUtilitiesActivity.RegistrarseEnAplicacionServidor(MainActivity, GCM_ID);
                    Log.d(TAG, "El resultado de guardar en el servidor ha sido: "+sSendInfoServer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();*/
        //Firebase
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        displayFirebaseRegId();
    }
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(AppConfig.MY_PREFS_SYSYEM, 0);
        String regId = pref.getString("token", null);
        Log.e(TAG, "FIREBASE reg id: " + regId);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(AppConfig.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(AppConfig.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    /* End Code */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        callbackManager.onActivityResult(requestCode, resultCode, i);

        if (null == i)
            return;

        Bundle b = i.getExtras();

        switch (requestCode) {

            case ACTIVITY_HOME:
                switch (resultCode) {
                    case HomeActivity.RESULT_LOGOUT: {
                        //Delete Users to BBDD
                        db.deleteUser();
                        Main.setConnector(null);
                        int index = b.getInt("index");
                        //adapter.updatePassword(index, "");
                        //adapter.writeToFile(this);
                        //setListAdapter(adapter);
                        Log.d(TAG, "Logged out:" + index);
                        //for Facebook:: Si estuviese la session abierta de FB
                        LoginManager.getInstance().logOut();
                    }
                    break;
                }
                break;

            case ACTIVITY_LOGINFB: {
                int index = b.getInt("index");
                String site = b.getString("site");
                int iMemberId = b.getInt("member_id");
                String username = b.getString("username");
                String password = b.getString("password");
                int iProtocolVer = b.getInt("protocol");
                Boolean isRememberPassword = b.getBoolean("remember_password");

                //adapter.update(index, site, username, isRememberPassword ? password : "");
                //adapter.writeToFile(this);
                //setListAdapter(adapter);


                Intent intentHome = new Intent(this, ActivityMaps.class);
                intentHome.putExtra("site", site);
                intentHome.putExtra("member_id", iMemberId);
                intentHome.putExtra("username", username);
                intentHome.putExtra("password", password);
                intentHome.putExtra("protocol", iProtocolVer);
                intentHome.putExtra("index", index);

                startActivityForResult(intentHome, ACTIVITY_HOME);
            }
            break;

            case ACTIVITY_LOGIN:
                switch (resultCode) {
                    case LoginActivity.RESULT_LOGIN: {
                        int index = b.getInt("index");
                        String site = b.getString("site");
                        int iMemberId = b.getInt("member_id");
                        String username = b.getString("username");
                        String password = b.getString("password");
                        int iProtocolVer = b.getInt("protocol");
                        Boolean isRememberPassword = b.getBoolean("remember_password");

                        //adapter.update(index, site, username, isRememberPassword ? password : "");
                        //adapter.writeToFile(this);
                        //setListAdapter(adapter);

                        Intent intentHome = new Intent(this, ActivityMaps.class);
                        intentHome.putExtra("site", site);
                        intentHome.putExtra("member_id", iMemberId);
                        intentHome.putExtra("username", username);
                        intentHome.putExtra("password", password);
                        intentHome.putExtra("protocol", iProtocolVer);
                        intentHome.putExtra("index", index);

                        startActivityForResult(intentHome, ACTIVITY_HOME);

                    }
                    break;
                    case LoginActivity.RESULT_DELETE: {
                        int index = b.getInt("index");
                        //adapter.delete(index);
                        //adapter.writeToFile(this);
                        //setListAdapter(adapter);
                    }
                    break;
                }
                break;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /* Code Add to CreationBCN::PcIntegrad */
            //Lo hemos borrado del menu:: main.xml

            /* End Code */
            case R.id.main_about:
                Intent i = new Intent(this, HelpActivity.class);
                startActivityForResult(i, ACTIVITY_ABOUT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Connector getConnector() {
        return m_oConnector;
    }
    public static void setConnector(Connector connector) {
        m_oConnector = connector;
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

    /* Code Add to CreationBCN::PcIntegrad, implemented to LoginActivity for Busdost */

    //Fx que vuelve a ver la Intro...
    protected void actionGoIntroduction() {
        Intent act = new Intent(this, IntroductionPage.class);
        startActivity(act);
        finish();
    }
    //Fx para el login Con las Redes Sociales
    protected void actionLogInSocials(String sMethod, String sCorrectUrl, Object[] aParams) {
        Connector o = new Connector(sCorrectUrl, "", "", 0);
        m_oSocialCallback.setMethod(sMethod);
        m_oSocialCallback.setParams(aParams);
        m_oSocialCallback.setConnector(o);
        o.execAsyncMethod(sMethod, aParams, m_oSocialCallback, Main.this);
    }
    //Fx para ir a RegisterLayout
    public void SignUpActivity() {
        Intent act = new Intent(this, SignupActivity.class);
        startActivity(act);
    }
    //Fx para ir a RegisterLayout
    public void AboutActivity() {
        Intent act = new Intent(this, AboutActivity.class);
        startActivity(act);
    }

    //Fx que complementa la llamada Sync al Servidor, es la "respuesta" que se obtiene al hacer la peticion...
    private actionLogInSocialsCallback m_oSocialCallback = new actionLogInSocialsCallback(this) {

        public boolean callFailed(Exception e) { return true; }

        public void callFinished(Object result) {
            Log.d(TAG, "Socials Login Result: " + result + " / class: " + result.getClass());
            int iIdProfile;
            int iProtocolVer;
            String sProfileUsername = null;
            String sProfilePwdHash = null;

            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result;

                if (map.get("member_id") instanceof String)
                    iIdProfile = Integer.parseInt((String) map.get("member_id"));
                else
                    iIdProfile = (Integer) map.get("member_id");

                if (map.get("protocol_ver") instanceof String)
                    iProtocolVer = Integer.parseInt((String) map.get("protocol_ver"));
                else
                    iProtocolVer = (Integer) map.get("protocol_ver");

                if (iProtocolVer >= 4) {
                    sProfileUsername = (String) map.get("member_username");
                    sProfilePwdHash = (String) map.get("member_pwd_hash");
                }
            }else{
                iIdProfile = Integer.valueOf(result.toString());
                iProtocolVer = 1;
            }

            Log.d(TAG, "Socials Login Result: ID:" + iIdProfile + " / Protocol: " + iProtocolVer);

            if (iIdProfile > 0) {

                //Se inserta Usuario en BBDD
                db.addUser(sProfileUsername, sProfilePwdHash);


                /*String sURLValid = AppConfig.URL_METHODS;
                Bundle b = new Bundle();
                b.putInt("index", 0);
                b.putString("site", sURLValid);
                b.putInt("member_id", iIdProfile);
                b.putString("username", sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                b.putString("password", sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                b.putBoolean("remember_password", true);
                b.putInt("protocol", iProtocolVer);*/

                //Intent i = new Intent(MainActivity, HomeActivity.class);
                //i.putExtras(b);
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.Main_fb_toast_validate), Toast.LENGTH_LONG).show();

                //startActivityForResult(i, ACTIVITY_LOGINFB);
                //finish();
                //Guardamos en variables
                SharedPreferences.Editor editor = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE).edit();
                editor.putString("site","" + correctSiteUrl(m_sSiteUrl));
                editor.putString("member_id","" + iIdProfile);
                editor.putString("username","" + sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                editor.putString("password","" + sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                editor.putString("protocol","" + iProtocolVer);
                editor.putString("isGuest","no"); //Aqui definimos que login con guest:: Con logout, se borra
                editor.putString("index","0");
                editor.commit();

                //Super importante para grabar el connector
                Connector o = new Connector("" + correctSiteUrl(m_sSiteUrl),
                        "" + sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString(),
                        "" + sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString(),
                        iIdProfile);
                String Pass = "" + sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString();
                o.setPassword(32 == Pass.length() || 40 == Pass.length() ? Pass : o.md5(Pass));
                o.setProtocolVer(iProtocolVer);
                Main.setConnector(o);
                Connector.saveConnector(Main.this, o);
                //Super importante

                Intent intentHome = new Intent(Main.this, ActivityMaps.class);
                intentHome.putExtra("site", correctSiteUrl(m_sSiteUrl));
                intentHome.putExtra("member_id", iIdProfile);
                intentHome.putExtra("username", sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                intentHome.putExtra("password", sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                intentHome.putExtra("protocol", iProtocolVer);
                intentHome.putExtra("isGuest", "no");
                intentHome.putExtra("index", 0);

                startActivityForResult(intentHome, ACTIVITY_LOGINFB);

                finish();
            } else {
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.Main_fb_error_login), Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
            }
        }
    };
    class actionLogInSocialsCallback extends Connector.Callback {
        protected Main context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;

        public actionLogInSocialsCallback(Main c) {
            context = c;
        }
        public void setMethod(String s) {
            sMethod = s;
        }
        public void setParams(Object[] a) {
            aParams = a;
        }
        public Object[] getParams() {
            return aParams;
        }
        public void setConnector(Connector o) {
            oConnector = o;
        }
    }
    /* End Code */

    public static String getCookieForLoggedInUser() {
        Connector oConnector = Main.getConnector();
        if (Main.getConnector() != null && Main.getConnector().getProtocolVer() >= 4)
            return "memberID=" + oConnector.getMemberId() + "; memberPassword=" + oConnector.getPassword() + "; lang=" + Main.getLang();
        else
            return null;
    }
    public static Map<String, String> getHeadersForLoggedInUser() {
        Map<String, String> mapHeaders = new HashMap<String, String>();
        if (Main.getConnector() != null && Main.getConnector().getProtocolVer() >= 4)
            mapHeaders.put("Cookie", Main.getCookieForLoggedInUser());
        return mapHeaders;
    }

    /* Start code innoHawk permission Request */

    //Paso 1. Verificar permiso
    @SuppressLint("WrongConstant")
    private void verifyPermissionRequest() {
        //WRITE_EXTERNAL_STORAGE tiene implícito READ_EXTERNAL_STORAGE porque pertenecen al mismo grupo de permisos
        int locationPermission = 0;
        int locationPermission2 = 0;
        int camara = 0;
        int write = 0;
        int micro = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            locationPermission2 = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            camara = checkSelfPermission(android.Manifest.permission.CAMERA);
            write = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            micro = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO);
        }
        if ((locationPermission != PackageManager.PERMISSION_GRANTED)||(locationPermission2 != PackageManager.PERMISSION_GRANTED)||(camara != PackageManager.PERMISSION_GRANTED)||(write != PackageManager.PERMISSION_GRANTED)||(micro != PackageManager.PERMISSION_GRANTED)) {
            requestPermission();
        }
    }

    //Paso 2: Solicitar permiso
    private void requestPermission() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if( (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            showSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_VAL);
            }
        }
    }

    //Paso 3: Procesar respuesta de usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_PERMISSION_VAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showSnackBar();
            }
        }
    }


    /**
     * Método para mostrar el snackbar de la aplicación.
     * Snackbar es un componente de la librería de diseño 'com.android.support:design:23.1.0'
     * y puede ser personalizado para realizar una acción, como por ejemplo abrir la actividad de
     * configuración de nuestra aplicación.
     */
    private void showSnackBar() {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
            layoutHomePermission = getLayoutInflater().inflate(R.layout.activity_permission, rl_dialog, false);
            rl_dialog.addView(layoutHomePermission);
            Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
            Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
            btn_yes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialog.setVisibility(View.GONE);
                }
            });
            btn_settings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    openSettings();
                    Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
                    Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
                    btn_yes.setText("OK");
                    btn_settings.setVisibility(View.GONE);
                    btn_yes.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            rl_dialog.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    /**
     * Abre el intento de detalles de configuración de nuestra aplicación
     */
    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}