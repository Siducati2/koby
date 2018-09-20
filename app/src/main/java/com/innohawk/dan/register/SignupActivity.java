package com.innohawk.dan.register;

/**
 * Created by innohawk on 19/3/16.
 */

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activitymaps.ActivityMaps;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.home.HomeActivity;
import com.innohawk.dan.sqlite.SQLiteActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

//import com.innohawk.seeprofile.SiteAdapter;

public class SignupActivity extends ActivityBase {
    private static final String TAG = "SignupActivity";

    /*protected EditText _emailText;
    protected EditText _passwordText;
    protected EditText _confirmpasswordText;
    protected EditText _firstText;
    protected EditText _lastText;
    protected Button btn_action_birth;
    protected Button btn_action_continue;*/

    protected TextView textTitle;
    protected EditText emailText;
    protected EditText passText;
    protected EditText cpassText;
    protected EditText firstText;
    protected EditText lastText;
    protected EditText birthText;
    protected Button btn_action_birth;
    protected RadioButton Gender;
    protected RadioButton GenderFemale;
    protected RadioButton GenderIntersex;
    protected Button btn_action_sign;
    protected Button btn_action_back;
    private String Error = null;

    /*@BindView(R.id.input_nickname) EditText _nicknameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_confirmpassword) EditText _confirmpasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.btn_terms) ImageButton _checkButton;
    @BindView(R.id.check_terms) CheckBox _checkUse;*/

    private static final int ACTIVITY_SIGNUP = 0;
    private static final int ACTIVITY_HOME = 1;
    private static final int ACTIVITY_HELP = 2;
    //protected SiteAdapter adapter;
    public static SignupActivity SignupActivity = null;
    protected EditText usernameCallback;
    protected EditText passCallback;
    private SQLiteActivity db;
    Typeface tb;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, false, false, false);

        FacebookSdk.sdkInitialize(getApplicationContext());
        // Iniciamos la BBDD
        db = new SQLiteActivity(getApplicationContext());

        setContentView(R.layout.activity_signup);
        SignupActivity = this;

        //Cambiar la tipografia
        tb = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");

        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.btn_register);
        textTitle.setTypeface(tb);

        emailText = (EditText) findViewById(R.id.editTextMail);
        emailText.setTypeface(tb);
        passText = (EditText) findViewById(R.id.editTextPassword);
        passText.setTransformationMethod(new PasswordTransformationMethod());
        passText.setTypeface(tb);
        cpassText = (EditText) findViewById(R.id.editTextCPassword);
        cpassText.setTransformationMethod(new PasswordTransformationMethod());
        cpassText.setTypeface(tb);
        firstText = (EditText) findViewById(R.id.editTextFirst);
        firstText.setTypeface(tb);
        lastText = (EditText) findViewById(R.id.editTextLast);
        lastText.setTypeface(tb);
        birthText = (EditText) findViewById(R.id.editTextBith);
        birthText.setTypeface(tb);
        Gender = (RadioButton) findViewById(R.id.male);
        GenderFemale = (RadioButton) findViewById(R.id.female);
        //GenderIntersex = (RadioButton) findViewById(R.id.gay);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Data
        btn_action_birth = (Button) findViewById(R.id.action_Birthday);
        btn_action_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        Gender.setChecked(true);
        Gender.setBackgroundColor(getResources().getColor(R.color.icono_all_page));
        GenderFemale.setChecked(false);
        GenderFemale.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
        //GenderIntersex.setChecked(false);
        //GenderIntersex.setBackgroundColor(getResources().getColor(R.color.new_bg_background));

        Gender.setOnCheckedChangeListener(GenderPress);
        GenderFemale.setOnCheckedChangeListener(GenderFemalePress);
        //GenderIntersex.setOnCheckedChangeListener(GenderGayPress);

        btn_action_sign = (Button) findViewById(R.id.btn_signup);
        btn_action_sign.setTypeface(tb);
        btn_action_sign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signup();
            }
        });
        /*_nicknameText = (EditText) findViewById(R.id.input_nickname);
        _nicknameText.setTypeface(tf);
        _emailText = (EditText) findViewById(R.id.input_email);
          _emailText.setTypeface(tf);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _passwordText.setTypeface(tf);
        _confirmpasswordText = (EditText) findViewById(R.id.input_confirmpassword);
        _confirmpasswordText.setTypeface(tf);
        _checkUse = (CheckBox) findViewById(R.id.check_terms);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setTypeface(tf);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        _checkButton = (ImageButton) findViewById(R.id.btn_terms);

        _checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent i = new Intent(SignupActivity, HelpActivity.class);
                startActivityForResult(i, ACTIVITY_HELP);
                //finish();
            }
        });*/
    }

    //Cambios estado SEGMENTControl
    private CompoundButton.OnCheckedChangeListener GenderPress = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Gender.setChecked(true);
                GenderFemale.setChecked(false);
                //GenderIntersex.setChecked(false);
                Gender.setBackgroundColor(getResources().getColor(R.color.icono_all_page));
                GenderFemale.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
                //GenderIntersex.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener GenderFemalePress = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Gender.setChecked(false);
                GenderFemale.setChecked(true);
                //GenderIntersex.setChecked(false);
                GenderFemale.setBackgroundColor(getResources().getColor(R.color.icono_all_page));
                Gender.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
                //GenderIntersex.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
            }
        }
    };

   /* private CompoundButton.OnCheckedChangeListener GenderGayPress = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Gender.setChecked(false);
                GenderFemale.setChecked(false);
                GenderIntersex.setChecked(true);
                GenderIntersex.setBackgroundColor(getResources().getColor(R.color.icono_all_page));
                Gender.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
                GenderFemale.setBackgroundColor(getResources().getColor(R.color.new_bg_background));
            }
        }
    };*/

    public void signup() {
        if ((!validate()) || (Error!=null)) {
            onSignupFailed();
            return;
        }

        //_signupButton.setEnabled(false);


        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("@string/creating_signup");
        progressDialog.setMessage(getBaseContext().getString(R.string.creating_signup));
        progressDialog.show();



        final String sCorrectUrl = AppConfig.URL_REGISTER;
        final String sMethod = "pcint.signup_mobile";
        String sLang = Locale.getDefault().getLanguage();

        String email = emailText.getText().toString();
        String password = passText.getText().toString();
        String name = firstText.getText().toString();
        String last = lastText.getText().toString();
        String birth = birthText.getText().toString();
        boolean gendermale = Gender.isChecked();
        boolean genderfemale = GenderFemale.isChecked();
       // boolean gendergay = GenderIntersex.isChecked();
        String gender;
        password = md5(password);

        if(genderfemale==true)
            gender = "female";
        else{
            if(gendermale==true)
                gender = "male";
            else
                gender = "male";
        }





        Object[] aVars = {email, password, name, last, birth, gender};
        final Object[] aParams = {
                "signup_mobile_creationbcn",
                "signUP",
                aVars,
                sLang
        };

        //Nos Logeamos

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        System.out.println(Arrays.toString(aParams));//Para ver lo que printamos
                        actionSignUp(sMethod, sCorrectUrl, aParams);
                        progressDialog.dismiss();
                    }
                }, 3000);

    }


    public String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] hash = digest.digest();
            String sRet = "";
            for (int i = 0; i < digest.getDigestLength(); ++i)
                sRet += String.format("%02x", hash[i]);
            return sRet;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void showDatePicker(){
        DialogFragment newFragmet = new DataPickerFragment();
        newFragmet.show(getFragmentManager(),"datePicker");
    }

    @SuppressLint("ValidFragment")
    public class DataPickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle saveInstanceState){

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR)-22;
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),this,year,month,day);
        }

        @SuppressLint("ResourceAsColor")
        public void onDateSet(DatePicker view, int year, int month, int day){
            final Calendar cal = Calendar.getInstance();
            int yearToday = cal.get(Calendar.YEAR);
            //String Error_Birthday = getString(R.string.valid_birthday_signup);
            if((yearToday-year >=22)){
                birthText.setText(+year+"-"+(month+1+"-"+day));
                birthText.setTextColor(R.color.black);
                Error = null;
            }else{
                birthText.setText(getString(R.string.valid_birthday_signup));
                birthText.setTextColor(Color.parseColor("#ff0000"));
                Error = "yes";
            }

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (null == i)
            return;
        Bundle b = i.getExtras();
        switch (requestCode) {
            case ACTIVITY_SIGNUP: {
                int index = b.getInt("index");
                String site = b.getString("site");
                int iMemberId = b.getInt("member_id");
                String username = b.getString("username");
                String password = b.getString("password");
                int iProtocolVer = b.getInt("protocol");
                Boolean isRememberPassword = b.getBoolean("remember_password");

                //Super importante para grabar el connector
                Connector o = new Connector(correctSiteUrl(site),
                        username,
                        password,
                        iMemberId);
                String Pass = password;
                o.setPassword(32 == Pass.length() || 40 == Pass.length() ? Pass : o.md5(Pass));
                o.setProtocolVer(iProtocolVer);
                Main.setConnector(o);
                Connector.saveConnector(SignupActivity.this, o);
                //Super importante

                Intent intentHome = new Intent(this, HomeActivity.class);
                intentHome.putExtra("site", site);
                intentHome.putExtra("member_id", iMemberId);
                intentHome.putExtra("username", username);
                intentHome.putExtra("password", password);
                intentHome.putExtra("protocol", iProtocolVer);
                intentHome.putExtra("index", index);

                startActivityForResult(intentHome, ACTIVITY_HOME);
            }
            break;
        }

    }


    protected void actionSignUp(String sMethod, String sCorrectUrl, Object[] aParams) {
        Connector o = new Connector(sCorrectUrl, "", "", 0);
        m_oSignUpCallback.setMethod(sMethod);
        m_oSignUpCallback.setParams(aParams);
        m_oSignUpCallback.setConnector(o);
        o.execAsyncMethod(sMethod, aParams, m_oSignUpCallback, this);
    }

    //Fx que complementa la llamada Sync al Servidor, es la "respuesta" que se obtiene al hacer la peticion...
    private actionSignUpCallback m_oSignUpCallback = new actionSignUpCallback(this) {

        public boolean callFailed(Exception e) { return true; }

        public void callFinished(Object result) {
            Log.d(TAG, "SignUP  Result: " + result + " / class: " + result.getClass());
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

            Log.d(TAG, "SignUP: ID:" + iIdProfile + " / Protocol: " + iProtocolVer);

            if (iIdProfile > 0) {

                //Se inserta Usuario en BBDD
                db.addUser(sProfileUsername, sProfilePwdHash);

                /*
                String sURLValid = AppConfig.URL_METHODS;
                Bundle b = new Bundle();
                b.putInt("index", 0);
                b.putString("site", sURLValid);
                b.putInt("member_id", iIdProfile);
                b.putString("username", sProfileUsername != null ? sProfileUsername : usernameCallback.getText().toString());
                b.putString("password", sProfilePwdHash != null ? sProfilePwdHash : passCallback.getText().toString());
                b.putBoolean("remember_password", true);
                b.putInt("protocol", iProtocolVer);

                Intent i = new Intent(SignupActivity, HomeActivity.class);
                i.putExtras(b);
                //_signupButton.setEnabled(true);
                setResult(RESULT_OK, null);
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.congratulations_signup), Toast.LENGTH_LONG).show();
                startActivityForResult(i, ACTIVITY_SIGNUP);
                finish();
                */
                //Guardamos en variables
                String sURLValid = AppConfig.URL_METHODS;
                SharedPreferences.Editor editor = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE).edit();
                editor.putString("site","" + correctSiteUrl(sURLValid));
                editor.putString("member_id","" + iIdProfile);
                editor.putString("username","" + sProfileUsername);
                editor.putString("password","" + sProfilePwdHash);
                editor.putString("protocol","" + iProtocolVer);
                editor.putString("isGuest","no"); //Aqui definimos que login con guest:: Con logout, se borra
                editor.putString("index","0");
                editor.commit();

                //Super importante para grabar el connector
                Connector o = new Connector("" + correctSiteUrl(sURLValid),
                        "" + sProfileUsername,
                        "" + sProfilePwdHash,
                        iIdProfile);
                String Pass = "" + sProfilePwdHash;
                o.setPassword(32 == Pass.length() || 40 == Pass.length() ? Pass : o.md5(Pass));
                o.setProtocolVer(iProtocolVer);
                Main.setConnector(o);
                Connector.saveConnector(SignupActivity.this, o);
                //Super importante

                Intent intentHome = new Intent(SignupActivity.this, ActivityMaps.class);
                intentHome.putExtra("site", correctSiteUrl(sURLValid));
                intentHome.putExtra("member_id", iIdProfile);
                intentHome.putExtra("username", sProfileUsername);
                intentHome.putExtra("password", sProfilePwdHash);
                intentHome.putExtra("protocol", iProtocolVer);
                intentHome.putExtra("isGuest", "no");
                intentHome.putExtra("index", 0);

                startActivityForResult(intentHome, ACTIVITY_HOME);

                finish();
            } else if(iIdProfile == -1){
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.usernameExist_signup), Toast.LENGTH_LONG).show();
                //_signupButton.setEnabled(true);
            }else if(iIdProfile == -2){
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.emailExist_signup), Toast.LENGTH_LONG).show();
                //_signupButton.setEnabled(true);
            }else{
                //Sacar TOAST
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.failed_signup), Toast.LENGTH_LONG).show();
                //_signupButton.setEnabled(true);
            }
        }
    };

    class actionSignUpCallback extends Connector.Callback {
        protected SignupActivity context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;

        public actionSignUpCallback(SignupActivity c) {
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

    //Función obsoleta
    public void onSignupSuccess() {
        //_signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.error_signup), Toast.LENGTH_LONG).show();
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

    @SuppressLint("ResourceAsColor")
    public boolean validate() {
        boolean valid = true;

        Gender = (RadioButton) findViewById(R.id.male);
        GenderFemale = (RadioButton) findViewById(R.id.female);
        final String email = emailText.getText().toString();
        final String password = passText.getText().toString();
        final String confirmpassword = cpassText.getText().toString();
        final String first = firstText.getText().toString();
        final String last = lastText.getText().toString();
        final String birth = birthText.getText().toString();
        boolean gendermale = Gender.isChecked();
        boolean genderfemale = GenderFemale.isChecked();

        String Error_Name = getString(R.string.valid_username_signup);
        String Error_LastName = getString(R.string.valid_username_signup);
        String Error_Email = getString(R.string.valid_email_signup);
        String Error_Pass = getString(R.string.valid_pass_signup);
        String Error_ConfirPass = getString(R.string.valid_confirmpass_signup);
        String Error_Day = getString(R.string.valid_birthday_signup);


        if(email.isEmpty())
        {
            emailText.setTextColor(Color.parseColor("#ff0000"));
            //Toast.makeText(getBaseContext(), Error_Email, Toast.LENGTH_LONG).show();
            emailText.setText(Error_Email);
            emailText.setTextColor(Color.parseColor("#ff0000"));
            valid = false;
        }else if(!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //Toast.makeText(getBaseContext(), Error_Email, Toast.LENGTH_LONG).show();
            emailText.setText(Error_Email);
            emailText.setTextColor(Color.parseColor("#ff0000"));
            valid = false;
        }else{
            //valid = true;
        }

        if(birth.isEmpty())
        {
            birthText.setTextColor(Color.parseColor("#ff0000"));
            //Toast.makeText(getBaseContext(), Error_Email, Toast.LENGTH_LONG).show();
            birthText.setText(Error_Day);
            birthText.setTextColor(Color.parseColor("#ff0000"));
            valid = false;
        }else{
            //valid = true;
        }

        if (first.isEmpty()) {
            //firstText.setError(Error_Name);
            firstText.setTextColor(Color.parseColor("#ff0000"));
            firstText.setText(Error_Name);
            firstText.setTextColor(Color.parseColor("#ff0000"));
            //Toast.makeText(getBaseContext(), Error_Name, Toast.LENGTH_LONG).show();
            valid = false;
        }else{
            //valid = true;
        }

        if (last.isEmpty()) {
            lastText.setTextColor(Color.parseColor("#ff0000"));
            //Toast.makeText(getBaseContext(), Error_Name, Toast.LENGTH_LONG).show();
            lastText.setText(Error_Name);
            lastText.setTextColor(Color.parseColor("#ff0000"));
            valid = false;
        }else{
            //valid = true;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passText.setTextColor(Color.parseColor("#ff0000"));
            //Toast.makeText(getBaseContext(), Error_Pass, Toast.LENGTH_LONG).show();
            passText.setText(Error_Pass);
            passText.setTransformationMethod(null);
            passText.setTextColor(Color.parseColor("#ff0000"));
            valid = false;
        }else {
            //valid = true;
        }


        if(password.equals(confirmpassword)){
            //valid = true;
        }else {
            cpassText.setTextColor(Color.parseColor("#ff0000"));
            cpassText.setText(Error_ConfirPass);
            cpassText.setTransformationMethod(null);
            cpassText.setTextColor(Color.parseColor("#ff0000"));
            valid = false;
        }


        if(valid==false) {
            btn_action_sign.setEnabled(false);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            btn_action_sign.setEnabled(true);
                            emailText.setText(email);
                            emailText.setTextColor(R.color.black);
                            passText.setText(password);
                            passText.setTextColor(R.color.black);
                            passText.setTransformationMethod(new PasswordTransformationMethod());
                            cpassText.setText(confirmpassword);
                            cpassText.setTransformationMethod(new PasswordTransformationMethod());
                            cpassText.setTextColor(R.color.black);
                            birthText.getText().toString();
                            birthText.setText(birth);
                            firstText.setText(first);
                            firstText.setTextColor(R.color.black);
                            lastText.setText(last);
                            lastText.setTextColor(R.color.black);

                        }
                    }, 6000);
        }

        return valid;
    }
}