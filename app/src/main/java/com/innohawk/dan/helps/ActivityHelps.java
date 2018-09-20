package com.innohawk.dan.helps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;

import java.util.Locale;
import java.util.Map;

/**
 * Created by innohawk on 20/11/17.
 */

public class ActivityHelps extends ActivityBase {
    private static final String TAG = "AboutActivity";

    protected TextView m_textAbout;

    protected TextView textTitle;
    protected Button btn_action_back;
    Typeface tt;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);

        setContentView(R.layout.activity_helps_layouts);

        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setTypeface(tt);
        /* Get & title */
        Intent ih = getIntent();
        String type = ih.getStringExtra("type");
        MapsInitializer.initialize(getApplicationContext());
        final String sCorrectUrl = AppConfig.URL_METHODS;
        final String sMethod;
        final String sGet;
        String sLang = Locale.getDefault().getLanguage();
        final Object[] aParams;
        Connector o = new Connector(sCorrectUrl, "", "", 0);
        if(type.equals("about")) {
            textTitle.setText(R.string.title_about);
            sMethod = "pcint.getAboutUs";
            aParams = new Object[]{
                    "getAboutUs",
                    sLang
            };
            sGet = "About";
        }else if(type.equals("privacy")) {
            textTitle.setText(R.string.privacity_help);
            sMethod = "pcint.getPrivacity";
            aParams = new Object[]{
                    "getPrivacity",
                    sLang
            };
            sGet = "Privacity";
        }else if(type.equals("terms")) {
            textTitle.setText(R.string.terms_help);
            sMethod = "pcint.getTerms";
            aParams = new Object[]{
                    "getTerms",
                    sLang
            };
            sGet = "TermsOfUse";
        }else if(type.equals("faq")) {
            textTitle.setText(R.string.Faqs_help);
            sMethod = "pcint.getFAQ";
            aParams = new Object[]{
                    "getFAQ",
                    sLang
            };
            sGet = "FAQ";
        }else{
            //help
            textTitle.setText(R.string.Help_help);
            sMethod = "pcint.getHelp";
            aParams = new Object[]{
                    "getHelp",
                    sLang
            };
            sGet = "Helps";
        }


        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final ProgressDialog progressDialog = new ProgressDialog(ActivityHelps.this, R.style.Theme_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getBaseContext().getString(R.string.loading));
        progressDialog.show();


        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "ResultPcintParam are: " + result.toString());
                Map<String, Object> mapGET = (Map<String, Object>) result;
                String sLangGetServer = (String) mapGET.get(sGet);

                m_textAbout = (TextView) findViewById(R.id.about_text);
                m_textAbout.setText(Html.fromHtml(sLangGetServer));
                m_textAbout.setMovementMethod(LinkMovementMethod.getInstance());
                m_textAbout.setTypeface(tt);
                progressDialog.dismiss();

            }
        }, this);
        /* End Code */
    }

    protected String getAppVer() {
        String sVer = "1";
        try {
            sVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e.getMessage());
        }
        return sVer;
    }

}
