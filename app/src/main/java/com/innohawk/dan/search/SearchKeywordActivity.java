package com.innohawk.dan.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.innohawk.dan.R;

public class SearchKeywordActivity extends SearchBaseActivity {

    protected EditText m_editKeyword;
    protected CheckBox m_cbOnlineOnly;
    protected CheckBox m_cbWithPhotosOnly;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_submit;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false);

        setContentView(R.layout.search_keyword);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_search_keyword);
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

        m_editKeyword = (EditText) findViewById(R.id.keyword);
        m_cbOnlineOnly = (CheckBox) findViewById(R.id.online_only);
        m_cbWithPhotosOnly = (CheckBox) findViewById(R.id.with_photos_only);

        checkSearchWithPhotos(m_cbWithPhotosOnly, findViewById(R.id.with_photos_only_title));
    }




    @Override
    protected void actionSearchSubmit() {
        Intent i = new Intent(m_actThis, SearchResultsKeywordActivity.class);
        i.putExtra("keyword", m_editKeyword.getText().toString());
        i.putExtra("online_only", m_cbOnlineOnly.isChecked());
        i.putExtra("with_photos_only", m_cbWithPhotosOnly.isChecked());
        i.putExtra("start", 0);
        startActivityForResult(i, ACTIVITY_SEARCH_RESULTS);
        m_actThis.overridePendingTransition(0, 0);
    }

}
