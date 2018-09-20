package com.innohawk.dan.mail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.UserPickerActivity;

public class MailComposeActivity extends ActivityBase {
    private static final int ACTIVITY_USER_PICKER = 0;
    private static final String TAG = "MailComposeActivity";
    protected Button m_buttonSelectUser;
    protected EditText m_editRecipient;
    protected EditText m_editSubject;
    protected EditText m_editText;
    protected RadioButton m_radioOptionsMe;
    protected RadioButton m_radioOptionsRecipient;
    protected RadioButton m_radioOptionsBoth;
    protected String m_sRecipient;
    protected String m_sRecipientTitle;
    MailComposeActivity m_actMailCompose;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_send;
    Typeface tt;
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b, true, false);
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        setContentView(R.layout.mail_compose);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_mail_compose);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Send
        btn_action_send = (Button) findViewById(R.id.buttonsend);
        btn_action_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSendMail();
            }
        });


        m_editRecipient = (EditText) findViewById(R.id.mail_compose_recipient);
        m_editSubject = (EditText) findViewById(R.id.mail_compose_subject);
        m_editText = (EditText) findViewById(R.id.mail_compose_text);
        m_buttonSelectUser = (Button) findViewById(R.id.mail_compose_select_user);
        m_radioOptionsMe = (RadioButton) findViewById(R.id.mail_compose_options_me);
        m_radioOptionsRecipient = (RadioButton) findViewById(R.id.mail_compose_options_recipient);
        m_radioOptionsBoth = (RadioButton) findViewById(R.id.mail_compose_options_both);
        m_editRecipient.setTypeface(tt);
        m_editSubject.setTypeface(tt);
        m_editText.setTypeface(tt);
        m_radioOptionsMe.setTypeface(tt);
        m_radioOptionsRecipient.setTypeface(tt);
        m_radioOptionsBoth.setTypeface(tt);

        TextView recipient = (TextView)findViewById(R.id.text_recipient);
        TextView subject = (TextView)findViewById(R.id.text_subject);
        TextView text = (TextView)findViewById(R.id.text_text);
        recipient.setTypeface(tt);
        subject.setTypeface(tt);
        text.setTypeface(tt);

        m_actMailCompose = this;

        Intent i = getIntent();
        m_sRecipient = i.getStringExtra("recipient");
        m_sRecipientTitle = i.getStringExtra("recipient_title");
        if (null != m_sRecipientTitle)
            m_editRecipient.setText(m_sRecipientTitle);

        Object data = getLastNonConfigurationInstance();
        if (data != null)
            m_sRecipient = (String) data;

        m_buttonSelectUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(m_actMailCompose, UserPickerActivity.class);
                startActivityForResult(i, ACTIVITY_USER_PICKER);
                m_actMailCompose.overridePendingTransition(0, 0);
            }
        });

    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return m_sRecipient;
    }

    protected void actionSendMail() {
        Connector o = Main.getConnector();

        if (0 == m_editRecipient.getText().length() ||
                0 == m_editSubject.getText().length() ||
                0 == m_editText.getText().length()) {
            AlertDialog dialog = new AlertDialog.Builder(m_actMailCompose).create();
            dialog.setTitle(getString(R.string.mail_error));
            dialog.setMessage(getString(R.string.mail_form_error));
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.close), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }


        String sOptions = "";
        if (m_radioOptionsMe.isChecked())
            sOptions = "me";
        else if (m_radioOptionsRecipient.isChecked())
            sOptions = "recipient";
        else if (m_radioOptionsBoth.isChecked())
            sOptions = "both";

        Log.d(TAG, "recipient: " + m_sRecipient);

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                m_sRecipient,
                m_editSubject.getText().toString(),
                m_editText.getText().toString(),
                sOptions
        };

        o.execAsyncMethod("dolphin.sendMessage", aParams, new Connector.Callback() {
            public void callFinished(Object result) {

                Log.d(TAG, "dolphin.sendMessage result: " + result.toString());
                Integer iResult = Integer.valueOf(result.toString());
                String sErrorMsg = "";
                String sTitle = getString(R.string.mail_error);
                switch (iResult) {
                    case 1:
                        sErrorMsg = getString(R.string.mail_msg_send_failed);
                        break;
                    case 3:
                        sErrorMsg = getString(R.string.mail_wait_before_sendin_another_msg);
                        break;
                    case 5:
                        sErrorMsg = getString(R.string.mail_you_are_blocked);
                        break;
                    case 10:
                        sErrorMsg = getString(R.string.mail_recipient_is_inactive);
                        break;
                    case 1000:
                        sErrorMsg = getString(R.string.mail_unknown_recipient);
                        break;
                    case 1001:
                        sErrorMsg = getString(R.string.mail_membership_dont_allow);
                        break;

                    default:
                        sTitle = getString(R.string.mail_success);
                        sErrorMsg = getString(R.string.mail_msg_successfully_sent);
                }

                AlertDialog dialog = new AlertDialog.Builder(m_actMailCompose).create();
                dialog.setTitle(sTitle);
                dialog.setMessage(sErrorMsg);
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.show();

            }
        }, m_actMailCompose);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (null == i)
            return;

        Bundle b = i.getExtras();
        m_sRecipient = b.getString("name");
        m_sRecipientTitle = b.getString("user_title");
        m_editRecipient.setText(m_sRecipientTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mail_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mail_compose_send:
                actionSendMail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
