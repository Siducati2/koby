package com.innohawk.dan.friends;

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
import com.innohawk.dan.friends.FriendsActivity.MyFriendActionCallback;
import com.innohawk.dan.profile.ProfileActivity;

public class FriendRequestsActivity extends ListActivityBase {

    private static final String TAG = "FriendRequestsActivity";
    FriendRequestsAdapter m_adpFriendsRequests;
    Object m_aFriendRequests[];

    protected TextView textTitle;
    protected Button btn_action_back;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_friend_requests);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reloadRemoteData();
    }

    protected void reloadRemoteData() {
        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                Main.getLang()
        };

        Log.d(TAG, "starting dolphin.getFriendRequests");

        o.execAsyncMethod("dolphin.getFriendRequests", aParams, new Connector.Callback() {

            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getFriendRequests result: " + result.toString());

                m_aFriendRequests = (Object[]) result;

                Log.d(TAG, "dolphin.getFriendRequests num: " + m_aFriendRequests.length);

                m_adpFriendsRequests = new FriendRequestsAdapter(m_actThis, m_aFriendRequests);
                setListAdapter(m_adpFriendsRequests);
            }
        }, this);
    }

    public void onAcceptFriend(String s) {

        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                s
        };

        MyFriendActionCallback listener = new MyFriendActionCallback() {
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.acceptFriendRequest result: " + result.toString());
                if (result.toString().equals("ok")) {
                    ((FriendRequestsActivity) m_actThis).reloadRemoteData();
                }
            }
        };

        o.execAsyncMethod("dolphin.acceptFriendRequest", aParams, listener, this);
    }

    public void onRejectFriend(String s) {

        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                s
        };

        MyFriendActionCallback listener = new MyFriendActionCallback() {
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.declineFriendRequest result: " + result.toString());
                if (result.toString().equals("ok")) {
                    ((FriendRequestsActivity) m_actThis).reloadRemoteData();
                }
            }
        };

        o.execAsyncMethod("dolphin.declineFriendRequest", aParams, listener, this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.d(TAG, "onListItemClick - " + m_adpFriendsRequests.toString());

        String sUsername = m_adpFriendsRequests.getUsername(position);
        if (null == sUsername)
            return;

        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("username", sUsername);
        startActivityForResult(i, 0);
        this.overridePendingTransition(0, 0);
    }

}
