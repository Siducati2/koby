package com.innohawk.dan.friends;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.innohawk.dan.Connector;
import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;

public class FriendsHomeActivity extends ListActivityBase {

    private static final int ACTIVITY_FRINDS_LIST = 0;
    Typeface tt;
    protected TextView textTitle;
    protected Button btn_action_back;
    /**
     * Called when the activity is first created.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false);

        setContentView(R.layout.list);

        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_friends_home);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FriendsHomeAdapter adapter = new FriendsHomeAdapter(this,tt);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (position) {
            case 0: {
                Connector o = Main.getConnector();
                Intent i = new Intent(this, FriendsActivity.class);
                i.putExtra("username", o.getUsername());
                startActivityForResult(i, ACTIVITY_FRINDS_LIST);
                this.overridePendingTransition(0, 0);
            }
            break;
            case 1: {
                Intent i = new Intent(this, FriendRequestsActivity.class);
                startActivityForResult(i, ACTIVITY_FRINDS_LIST);
                this.overridePendingTransition(0, 0);
            }
            break;
        }
    }

}
