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

public class PrivacityActivity extends ActivityBase {
    private static final String TAG = "PrivacityActivity";

    protected TextView m_textPrivacity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);

        setContentView(R.layout.help_privacity);
        setTitleCaption(R.string.privacity_help);

        final String sCorrectUrl = AppConfig.URL_METHODS;
        final String sMethod = "pcint.getPrivacity";
        String sLang = Locale.getDefault().getLanguage();
        final Object[] aParams = {"getPrivacity", sLang};
        Connector o = new Connector(sCorrectUrl, "", "", 0);

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "ResultPcintParam are: " + result.toString());
                Map<String, Object> mapGET = (Map<String, Object>) result;
                String sLangPrivacity = (String) mapGET.get("Privacity");

                m_textPrivacity = (TextView) findViewById(R.id.privacity_text);

                m_textPrivacity.setText(Html.fromHtml(sLangPrivacity));
                m_textPrivacity.setMovementMethod(LinkMovementMethod.getInstance());

            }
        }, this);
    }
}

