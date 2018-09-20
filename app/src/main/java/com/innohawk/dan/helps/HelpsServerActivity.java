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

public class HelpsServerActivity extends ActivityBase {
    private static final String TAG = "HelpsActivity";

    protected TextView m_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);

        setContentView(R.layout.activity_helps_server);
        setTitleCaption(R.string.Help_help);

        final String sCorrectUrl = AppConfig.URL_METHODS;
        final String sMethod = "pcint.getHelp";
        String sLang = Locale.getDefault().getLanguage();
        final Object[] aParams = {"getHelp", sLang};
        Connector o = new Connector(sCorrectUrl, "", "", 0);

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "ResultPcintParam are: " + result.toString());
                Map<String, Object> mapGET = (Map<String, Object>) result;
                String sLangGetServer = (String) mapGET.get("Helps");

                m_text = (TextView) findViewById(R.id.help_server_text);
                m_text.setText(Html.fromHtml(sLangGetServer));
                m_text.setMovementMethod(LinkMovementMethod.getInstance());

            }
        }, this);
    }
}
