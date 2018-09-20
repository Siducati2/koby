package com.innohawk.dan.mail;

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

import java.util.Map;

public class MailMessagesActivity extends ListActivityBase {
    private static final int ACTIVITY_MAIL_MESSAGE = 0;
    private static final String TAG = "MailMessagesActivity";

    protected Boolean m_isInbox;
    protected Object m_aMessages[];
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_reload;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_mail_messages);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Reload
        btn_action_reload = (Button) findViewById(R.id.buttonRefresh);
        btn_action_reload.setVisibility(View.VISIBLE);
        btn_action_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });

        Intent i = getIntent();
        m_isInbox = i.getBooleanExtra("inbox", true);

        reloadRemoteData();
    }


    protected void reloadRemoteData() {
        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword()
        };

        o.execAsyncMethod("dolphin." + (m_isInbox ? "getMessagesInbox" : "getMessagesSent"), aParams, new Connector.Callback() {

            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getMessagesInbox result: " + result.toString());

                m_aMessages = (Object[]) result;

                Log.d(TAG, "dolphin.getMessagesInbox num: " + m_aMessages.length);

                MailMessagesAdapter adapter = new MailMessagesAdapter(m_actThis, m_aMessages, m_isInbox);
                setListAdapter(adapter);
            }
        }, this);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Map<String, String> mapMessage = (Map<String, String>) m_aMessages[position];

        Intent i = new Intent(this, MailMessageActivity.class);
        i.putExtra("inbox", m_isInbox);
        i.putExtra("msg_id", new Integer(mapMessage.get("ID")));
        startActivityForResult(i, ACTIVITY_MAIL_MESSAGE);
        this.overridePendingTransition(0, 0);
    }
}
