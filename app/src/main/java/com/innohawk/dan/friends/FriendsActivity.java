package com.innohawk.dan.friends;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.innohawk.dan.Connector;
import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.profile.ProfileActivity;

public class FriendsActivity extends ListActivityBase {
    private static final String TAG = "FriendsActivity";
    FriendsAdapter m_adpFriends;
    String m_sUsername;
    Object m_aFriends[];
    Connector m_oConnector;

    protected TextView textTitle;
    protected Button btn_action_back;
    /**
     * Called when the activity is first created.
     * El cambio de TAB es por Chat
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);

        ;
        setContentView(R.layout.list);

        //setTitleCaption(R.string.title_friends);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_friends);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent i = getIntent();
        m_sUsername = i.getStringExtra("username");

        m_oConnector = Main.getConnector();
        reloadRemoteData();
    }

    protected void reloadRemoteData() {
        Object[] aParams = {
                m_oConnector.getUsername(),
                m_oConnector.getPassword(),
                m_sUsername,
                Main.getLang()
        };

        Log.d(TAG, "starting dolphin.getFriends");

        m_oConnector.execAsyncMethod("dolphin.getFriends", aParams, new Connector.Callback() {

            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getFriends result: " + result.toString());

                m_aFriends = (Object[]) result;

                Log.d(TAG, "dolphin.getFriends num: " + m_aFriends.length);

                m_adpFriends = new FriendsAdapter(m_actThis, m_aFriends, m_sUsername.equalsIgnoreCase(m_oConnector.getUsername()));
                setListAdapter(m_adpFriends);

            }
        }, this);

    }

    public void onRemoveFriend(String s) {

        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                s
        };

        MyFriendActionCallback listener = new MyFriendActionCallback() {

            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.removeFriend result: " + result.toString());
                if (result.toString().equals("ok")) {
                    ((FriendsActivity) m_actThis).reloadRemoteData();
                }
            }
        };

        o.execAsyncMethod("dolphin.removeFriend", aParams, listener, this);

    }

    static class MyFriendActionCallback extends Connector.Callback {
        public void setRemovedUsername(String s) {
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String sUsername = m_adpFriends.getUsername(position);
        if (null == sUsername)
            return;

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("username", sUsername);
        startActivityForResult(i, 0);
        this.overridePendingTransition(0, 0);
    }
}
