package com.innohawk.dan.status;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;

public class StatusMessageActivity extends ActivityBase {
    private static final String TAG = "StatusMessageActivity";
    public static final int RESULT_OK = RESULT_FIRST_USER + 1;
    private EditText m_editStatus;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_update;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false);

        setContentView(R.layout.status_message);


        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_status_message);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        m_editStatus = (EditText) findViewById(R.id.status_message);

        Intent i = getIntent();
        if (null != i.getStringExtra("status_message")) {
            String s = i.getStringExtra("status_message");
            m_editStatus.setText(s);
        }

        //Back
        btn_action_update = (Button) findViewById(R.id.buttonupdate);
        btn_action_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionUpdateMessage();
            }
        });
    }

    protected void actionUpdateMessage() {
        Connector o = Main.getConnector();

        Log.d(TAG, o.getPassword());

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                m_editStatus.getText().toString()
        };

        o.execAsyncMethod("dolphin.updateStatusMessage", aParams, new Connector.Callback() {
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.updateStatusMessage result: " + result.toString());
                Bundle b = new Bundle();
                b.putString("status_message", m_editStatus.getText().toString());
                Intent i = new Intent();
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();
            }
        }, m_actThis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.status_message_update:
                actionUpdateMessage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
