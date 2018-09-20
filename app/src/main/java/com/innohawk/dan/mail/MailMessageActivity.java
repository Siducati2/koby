package com.innohawk.dan.mail;

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

import com.innohawk.dan.Connector;
import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.profile.ProfileActivity;

import java.util.Map;

public class MailMessageActivity extends ListActivityBase {

    private static final String TAG = "MailMessageActivity";
    Boolean m_isInbox;
    Integer m_iMsgId;
    String m_sRecipient = "";
    String m_sRecipientTitle = "";
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_reload;
    protected Button btn_action_replay;
    // activity ads
    private static final int ACTIVITY_MAIL_COMPOSE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_mail_message);
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

        //Replay
        btn_action_replay = (Button) findViewById(R.id.buttonreplay);
        btn_action_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourMethodReplay();
            }
        });

        Intent i = getIntent();
        m_isInbox = i.getBooleanExtra("inbox", true);
        if(m_isInbox)
            btn_action_replay.setVisibility(View.VISIBLE);

        m_iMsgId = i.getIntExtra("msg_id", 0);

        reloadRemoteData();
    }


    private void yourMethodReplay() {
        Intent i = new Intent(this, MailComposeActivity.class);
        i.putExtra("recipient", m_sRecipient);
        i.putExtra("recipient_title", m_sRecipientTitle);
        startActivityForResult(i, ACTIVITY_MAIL_COMPOSE);
        this.overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mail_reply:
                yourMethodReplay();
                return true;
                //break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void reloadRemoteData() {
        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                String.valueOf(m_iMsgId)
        };

        o.execAsyncMethod("dolphin." + (m_isInbox ? "getMessageInbox" : "getMessageSent"), aParams, new Connector.Callback() {

            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getMessagesInbox result: " + result.toString());
                Map<String, Object> mapMessage = (Map<String, Object>) result;
                m_sRecipient = (String) mapMessage.get("Nick");
                m_sRecipientTitle = (String) mapMessage.get(Main.getConnector().getProtocolVer() > 2 ? "UserTitleInterlocutor" : "Nick");
                MailMessageAdapter adapter = new MailMessageAdapter(m_actThis, mapMessage, m_isInbox);
                setListAdapter(adapter);
            }
        }, this);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (0 == position) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("username", m_sRecipient);
            i.putExtra("user_title", m_sRecipientTitle);
            startActivityForResult(i, 0);
            this.overridePendingTransition(0, 0);
        }
    }
}
