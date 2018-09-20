package com.innohawk.dan.helps;

/**
 * Created by innohawk on 23/3/16.
 */
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;

import java.util.Locale;
import java.util.Map;

public class TermsActivity extends ActivityBase {
    private static final String TAG = "TermsActivity";

    protected TextView m_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);

        setContentView(R.layout.activity_terms);
        setTitleCaption(R.string.terms_help);

        final String sCorrectUrl = AppConfig.URL_METHODS;
        final String sMethod = "pcint.getTerms";
        String sLang = Locale.getDefault().getLanguage();
        final Object[] aParams = {"getTerms", sLang};
        Connector o = new Connector(sCorrectUrl, "", "", 0);

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "ResultPcintParam are: " + result.toString());
                Map<String, Object> mapGET = (Map<String, Object>) result;
                String sLangGetServer = (String) mapGET.get("TermsOfUse");

                m_text = (TextView) findViewById(R.id.termsofuse_text);
                m_text.setText(Html.fromHtml(sLangGetServer));
                m_text.setMovementMethod(LinkMovementMethod.getInstance());

            }
        }, this);
    }
}
