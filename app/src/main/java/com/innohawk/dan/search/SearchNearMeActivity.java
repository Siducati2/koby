package com.innohawk.dan.search;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.innohawk.dan.R;
import com.innohawk.dan.location.LocationHelper;

public class SearchNearMeActivity extends SearchBaseActivity {

    protected CheckBox m_cbOnlineOnly;
    protected CheckBox m_cbWithPhotosOnly;
    protected ProgressDialog pd;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_submit;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false);

        setContentView(R.layout.search_near_me);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_search_near_me);
        textTitle.setTypeface(tt);
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

        m_cbOnlineOnly = (CheckBox) findViewById(R.id.online_only);
        m_cbWithPhotosOnly = (CheckBox) findViewById(R.id.with_photos_only);

        checkSearchWithPhotos(m_cbWithPhotosOnly, findViewById(R.id.with_photos_only_title));
    }


    @Override
    protected void actionSearchSubmit() {

        LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                stopProgress();
                if (null != location) {
                    Intent i = new Intent(m_actThis, SearchResultsNearMeActivity.class);
                    i.putExtra("online_only", m_cbOnlineOnly.isChecked());
                    i.putExtra("with_photos_only", m_cbWithPhotosOnly.isChecked());
                    i.putExtra("start", 0);
                    i.putExtra("lat", location.getLatitude());
                    i.putExtra("lng", location.getLongitude());
                    startActivityForResult(i, ACTIVITY_SEARCH_RESULTS);
                    m_actThis.overridePendingTransition(0, 0);

                } else {
                    Toast.makeText(m_actThis, R.string.location_not_available, Toast.LENGTH_LONG).show();
                }
            }
        };
        LocationHelper myLocation = new LocationHelper();
        if (myLocation.getLocation(m_actThis, locationResult))
            startProgress();
        else
            myLocation.openLocationEnableDialog();
    }

    public void startProgress() {
        getActionBarHelper().setRefreshActionItemState(true);
    }

    public void stopProgress() {
        getActionBarHelper().setRefreshActionItemState(false);
    }

}
