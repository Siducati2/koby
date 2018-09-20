package com.innohawk.dan.search;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.R;
import com.innohawk.dan.profile.ProfileActivity;

public class SearchResultsBaseActivity extends ListActivityBase {

    Menu m_oMenu;
    Integer m_iPerPage = 10;
    SearchResultsAdapter adapterSearchResults;
    Object m_aProfiles[];

    protected TextView textTitle;
    protected Button btn_action_back;

    protected Button btn_action_next;
    protected Button btn_action_reload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_search_results);
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
        btn_action_next = (Button) findViewById(R.id.buttonnext);
        btn_action_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });
        btn_action_reload = (Button) findViewById(R.id.buttonRefresh);
        btn_action_reload.setVisibility(View.VISIBLE);
        btn_action_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });

    }

    public void onNext() {
        // override
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String sUsername = adapterSearchResults.getUsername(position);
        if (null == sUsername)
            return;

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("username", sUsername);
        startActivityForResult(i, 0);
        this.overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        m_oMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_next:
                onNext();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void checkNextButton(boolean bEnable) {
        if (bEnable || null == m_oMenu)
            return;
        MenuItem item = m_oMenu.getItem(0);
        item.setVisible(bEnable);

        Log.d("SEARCHNEXT", "resultado es" + bEnable);

        if(bEnable)
            btn_action_next.setVisibility(View.VISIBLE);
        else
            btn_action_next.setVisibility(View.INVISIBLE);


    }
}
