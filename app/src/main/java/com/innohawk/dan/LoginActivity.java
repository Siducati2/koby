package com.innohawk.dan;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.innohawk.dan.sqlite.SQLiteActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActivityBase {
    private static final String TAG = "OO LoginActivity";

    protected String m_sSiteUrl;
    protected LoginButton m_buttonLoginFB;
    protected View m_viewLoginButtonWrapperFB;
    protected Button m_buttonSubmit;
    protected EditText m_editUsername;
    protected EditText m_editPassword;
    protected int m_iIndex;
    private SQLiteActivity db;
    protected CallbackManager m_fbCallbackManager;

    private LoginActionCallback m_oCallback = new LoginActionCallback(this) {
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

                Bundle b = new Bundle();
                b.putInt("index", m_iIndex);
                b.putString("site", correctSiteUrl(m_sSiteUrl));
                b.putInt("member_id", iIdProfile);
                b.putString("username", sProfileUsername != null ? sProfileUsername : m_editUsername.getText().toString());
                b.putString("password", sProfilePwdHash != null ? sProfilePwdHash : m_editPassword.getText().toString());
                b.putBoolean("remember_password", true);
                b.putInt("protocol", iProtocolVer);
                Intent i = new Intent();
                i.putExtras(b);

                setResult(RESULT_LOGIN, i);

                finish();

            } else {

                alertError(R.string.msg_login_incorrect);
            }
        }
    };

    // result codes
    public static final int RESULT_DELETE = RESULT_FIRST_USER + 1;
    public static final int RESULT_LOGIN = RESULT_FIRST_USER + 2;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false, false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        m_fbCallbackManager = CallbackManager.Factory.create();

        //Desactivamos Fx de FB_Connect
        //LoginManager fbLoginManager = LoginManager.getInstance();
        //fbLoginManager.logOut();

        //Cambiar la tipografia
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        // Iniciamos la BBDD
        db = new SQLiteActivity(getApplicationContext());

        setContentView(R.layout.activity_login);

        m_editUsername = (EditText) findViewById(R.id.username);
        m_editUsername.setTypeface(tf);
        m_editPassword = (EditText) findViewById(R.id.password);
        m_editPassword.setTypeface(tf);
        m_buttonSubmit = (Button) findViewById(R.id.submit);
        m_buttonSubmit.setTypeface(tf);
        m_viewLoginButtonWrapperFB = (View) findViewById(R.id.fb_login_button_wrapper);
        m_buttonLoginFB = (LoginButton) findViewById(R.id.fb_login_button);



        Intent i = getIntent();
        m_sSiteUrl = i.getStringExtra("site");
        m_iIndex = i.getIntExtra("index", 0);
        m_editUsername.setText(i.getStringExtra("username"));
        m_editPassword.setText(i.getStringExtra("password"));
        m_buttonSubmit.setText(R.string.title_login);

        String sCaption = m_sSiteUrl.replace("http://", "").replace("/xmlrpc", "");
        if (sCaption.endsWith("/"))
            sCaption = sCaption.substring(0, sCaption.length() - 1);

        //setTitleCaption(sCaption);
        setTitleCaption(R.string.title);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {

                Object[] aParams = {
                        m_editUsername.getText().toString(),
                        m_editPassword.getText().toString()
                };

                actionLogin("dolphin.login4", aParams);
            }
        };
        m_buttonSubmit.setOnClickListener(listener);

        hideKeyboard();

        /* Code Add to CreationBCN::PcIntegrad */
        // Miramos a ver si existen usuarios Registrados en la tabla BBDD
        //Si existe es que hemos pasado directamente de ahí.. y lo que haremos será llamar la fx actionlogin con los datos
        HashMap<String, String> user = db.getExistUsers();
        String username = user.get("username");
        String password = user.get("pwr");
        Log.d(TAG, "username: " + username + " & pass: " + password);
        if(username != null && password != null) {
            Object[] aParams = {
                    username,
                    password
            };
            actionLogin("dolphin.login4", aParams);
        }
        /* End Code */

        if (m_editPassword.getText().toString().length() > 0) { // try autologin if password saved
            Object[] aParams = {
                    m_editUsername.getText().toString(),
                    m_editPassword.getText().toString()
            };

            actionLogin("dolphin.login4", aParams);
        } else {
           // checkCompatibilityWithFb();
        }
    }


    protected void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        m_fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (m_iIndex >= 0 && Main.LOCK_TO_SITE == null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.login, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_delete_site:
                Bundle b = new Bundle();
                b.putInt("index", m_iIndex);
                Intent i = new Intent();
                i.putExtras(b);
                setResult(RESULT_DELETE, i);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void actionLogin(String sMethod, Object[] aParams) {

        String sCorrectUrl = correctSiteUrl(m_sSiteUrl);

        try {
            URI.create(sCorrectUrl);
        } catch (IllegalArgumentException e) {
            alertError(R.string.msg_url_incorrect);
            return;
        }

        Connector o = new Connector(sCorrectUrl, m_editUsername.getText().toString(), m_editPassword.getText().toString(), 0);

        m_oCallback.setMethod(sMethod);
        m_oCallback.setParams(aParams);
        m_oCallback.setConnector(o);
        o.execAsyncMethod(sMethod, aParams, m_oCallback, m_actThis);
    }

    protected void checkCompatibilityWithFb() {

        String sMethod = "dolphin.service";
        String sCorrectUrl = correctSiteUrl(m_sSiteUrl);

        Connector o = new Connector(sCorrectUrl, "", "", 0);

        Object[] aEmptyArray = {};
        Object[] aParams = {"facebook_connect", "supported", aEmptyArray, "Module"};

        Connector.Callback oCallback = new Connector.Callback() {

            public boolean callFailed(Exception e) {
                return false;
            }

            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.service result: " + result + " / class: " + result.getClass());

                try {
                    if (!(result instanceof String) || 1 != Integer.parseInt((String) result))
                        return;
                } catch (NumberFormatException e) {
                    Log.d(TAG, "dolphin.service execption: " + e);
                    return;
                }

                m_viewLoginButtonWrapperFB.setVisibility(View.VISIBLE);

                m_buttonLoginFB.registerCallback(m_fbCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        Log.d(TAG, "newMeRequest: " + object);

                                        Object[] aVars = {object, loginResult.getAccessToken().getToken()};
                                        Object[] aParams = {
                                                "facebook_connect",
                                                "login",
                                                aVars,
                                                "Module"
                                        };

                                        actionLogin("dolphin.service", aParams);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,first_name,last_name,gender,link");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "FacebookCallback Cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        m_oActivityHelper.alertError(exception.toString());
                    }
                });

            }
        };

        Log.i(TAG, sMethod + ": " + aParams[2]);

        o.execAsyncMethod(sMethod, aParams, oCallback, m_actThis);
    }

    class LoginActionCallback extends Connector.Callback {
        protected LoginActivity context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;

        public LoginActionCallback(LoginActivity c) {
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
}

