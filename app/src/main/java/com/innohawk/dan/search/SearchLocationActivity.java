package com.innohawk.dan.search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.innohawk.dan.CountryPickerActivity;
import com.innohawk.dan.R;

public class SearchLocationActivity extends SearchBaseActivity {

    private static final int ACTIVITY_COUNTRY_PICKER = 1;

    private Button m_buttonSelectCountry;
    private EditText m_editCountry;
    private String m_sCountryCode;
    private EditText m_editCity;
    private CheckBox m_cbOnlineOnly;
    private CheckBox m_cbWithPhotosOnly;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_submit;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false);

        setContentView(R.layout.search_location);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_search_location);
        textTitle.setTypeface(tt);

        m_editCountry = (EditText) findViewById(R.id.country);
        m_buttonSelectCountry = (Button) findViewById(R.id.search_select_country);
        m_editCity = (EditText) findViewById(R.id.city);
        m_cbOnlineOnly = (CheckBox) findViewById(R.id.online_only);
        m_cbWithPhotosOnly = (CheckBox) findViewById(R.id.with_photos_only);

        checkSearchWithPhotos(m_cbWithPhotosOnly, findViewById(R.id.with_photos_only_title));

        Object data = getLastNonConfigurationInstance();
        if (data != null)
            m_sCountryCode = (String) data;

        m_buttonSelectCountry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(m_actThis, CountryPickerActivity.class);
                startActivityForResult(i, ACTIVITY_COUNTRY_PICKER);
                m_actThis.overridePendingTransition(0, 0);
            }
        });


        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Submit
        btn_action_submit = (Button) findViewById(R.id.buttonsubmit);
        btn_action_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSearchSubmit();
            }
        });
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return m_sCountryCode;
    }


    @Override
    protected void actionSearchSubmit() {
        if (null == m_sCountryCode || 0 == m_sCountryCode.length()) {
            AlertDialog dialog = new AlertDialog.Builder(m_actThis).create();
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage(getString(R.string.search_country_empty));
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.close), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }

        Intent i = new Intent(m_actThis, SearchResultsLocationActivity.class);
        i.putExtra("country", m_sCountryCode);
        i.putExtra("city", m_editCity.getText().toString());
        i.putExtra("online_only", m_cbOnlineOnly.isChecked());
        i.putExtra("with_photos_only", m_cbWithPhotosOnly.isChecked());
        i.putExtra("start", 0);
        startActivityForResult(i, ACTIVITY_SEARCH_RESULTS);
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (null == i)
            return;

        Bundle b = i.getExtras();
        m_editCountry.setText(b.getString("name"));
        m_sCountryCode = b.getString("code");
    }
}
