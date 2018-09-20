package com.innohawk.dan.helps;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;

import java.util.Locale;
import java.util.Map;

public class AboutActivity extends ActivityBase {
    private static final String TAG = "AboutActivity";

    protected TextView m_textAbout;

    protected TextView textTitle;
    protected Button btn_action_back;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, false, false, false);

        setContentView(R.layout.about);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_about);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final ProgressDialog progressDialog = new ProgressDialog(AboutActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getBaseContext().getString(R.string.loading));
        progressDialog.show();


        /* Code Add to CreationBCN::PcIntegrad */
        final String sCorrectUrl = AppConfig.URL_METHODS;
        final String sMethod = "pcint.getAboutUs";
        String sLang = Locale.getDefault().getLanguage();
        final Object[] aParams = {"getAboutUs", sLang};
        Connector o = new Connector(sCorrectUrl, "", "", 0);

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "ResultPcintParam are: " + result.toString());
                Map<String, Object> mapGET = (Map<String, Object>) result;
                String sLangGetServer = (String) mapGET.get("About");

                m_textAbout = (TextView) findViewById(R.id.about_text);
                m_textAbout.setText(Html.fromHtml(sLangGetServer));
                m_textAbout.setMovementMethod(LinkMovementMethod.getInstance());
                progressDialog.dismiss();

            }
        }, this);
        /* End Code */
    }

    protected String getAppVer() {
        String sVer = "1";
        try {
            sVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.w(TAG, e.getMessage());
        }
        return sVer;
    }

}
