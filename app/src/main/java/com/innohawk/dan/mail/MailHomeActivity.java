
package com.innohawk.dan.mail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.R;

public class MailHomeActivity extends ListActivityBase {

    private static final int ACTIVITY_MAIL_MESSAGES_INBOX = 0;
    private static final int ACTIVITY_MAIL_MESSAGES_OUTBOX = 1;
    private static final int ACTIVITY_MAIL_COMPOSE = 2;
    protected TextView textTitle;
    protected Button btn_action_back;
    Typeface tt;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false);

        setContentView(R.layout.list);

        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_mail_home);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MailHomeAdapter adapter = new MailHomeAdapter(this,tt);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (position) {
            case 0: {
                Intent i = new Intent(this, MailMessagesActivity.class);
                i.putExtra("inbox", true);
                startActivityForResult(i, ACTIVITY_MAIL_MESSAGES_INBOX);
                this.overridePendingTransition(0, 0);
            }
            break;
            case 1: {
                Intent i = new Intent(this, MailMessagesActivity.class);
                i.putExtra("inbox", false);
                startActivityForResult(i, ACTIVITY_MAIL_MESSAGES_OUTBOX);
                this.overridePendingTransition(0, 0);
            }
            break;
            case 2: {
                Intent i = new Intent(this, MailComposeActivity.class);
                startActivityForResult(i, ACTIVITY_MAIL_COMPOSE);
                this.overridePendingTransition(0, 0);
            }
            break;
        }
    }
}
