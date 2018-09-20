package com.innohawk.dan.helps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.R;
import com.innohawk.dan.SiteAdapter;
import com.innohawk.dan.appconfig.AppConfig;

public class HelpActivity extends ListActivityBase {

    private static final int ACTIVITY_ABOUT_US = 0;
    private static final int ACTIVITY_PRIVACITY = 1;
    private static final int ACTIVITY_TERMS = 2;
    private static final int ACTIVITY_FAQ = 3;
    private static final int ACTIVITY_HELP = 4;
    String email = AppConfig.EmailSend;
    protected SiteAdapter adapter;


    //Normal Header
    protected TextView textTitle;
    Button reload,Back;
    String isGuest;
    Typeface tt;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false);
        setContentView(R.layout.activity_help);

        //Fx para saber si es guest!
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");

        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.special_help);
        textTitle.setTypeface(tt);
        reload = (Button) findViewById(R.id.buttonRefresh);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });
        Back = (Button)findViewById(R.id.buttonBack);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        HelpHomeAdapter adapter = new HelpHomeAdapter(this,tt);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (position) {
            case 0: {
                Intent i = new Intent(this, ActivityHelps.class);
                i.putExtra("type", "about");
                startActivityForResult(i, ACTIVITY_ABOUT_US);
            }
            break;
            case 1: {
                Intent i = new Intent(this, ActivityHelps.class);
                i.putExtra("type", "privacy");
                startActivityForResult(i, ACTIVITY_PRIVACITY);
            }
            break;
            case 2: {
                Intent i = new Intent(this, ActivityHelps.class);
                i.putExtra("type", "terms");
                startActivityForResult(i, ACTIVITY_TERMS);
            }
            break;
            case 3: {
                Intent i = new Intent(this, ActivityHelps.class);
                i.putExtra("type", "faq");
                startActivityForResult(i, ACTIVITY_FAQ);
            }
            break;
            case 4: {
                Intent i = new Intent(this, ActivityHelps.class);
                i.putExtra("type", "help");
                startActivityForResult(i, ACTIVITY_HELP);
            }
            break;
            case 5: {
                openGmail();
                this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
            break;
        }
    }

    //SendEmail
    private void openGmail() {
        // TODO Auto-generated method stub
        try {
            Intent gmail = new Intent(Intent.ACTION_VIEW);
            gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
            gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
            gmail.setData(Uri.parse(email));
            gmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sendEmail_Subject));
            gmail.setType("text/plain");
            gmail.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(gmail);
        } catch (Exception e) {
            sendEmail();
        }
    }
    private void sendEmail() {
        // TODO Auto-generated method stub
        String recipient = email;
        String subject = getString(R.string.sendEmail_Subject);
        @SuppressWarnings("unused")
        String body = "";
        String[] recipients = { recipient };
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {
            startActivity(Intent.createChooser(email, getString(R.string.sendEmail_Client)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.sendEmail_NoClient), Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
