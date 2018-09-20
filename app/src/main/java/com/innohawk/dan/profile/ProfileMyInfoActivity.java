package com.innohawk.dan.profile;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.innohawk.dan.Connector;
import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;

import java.util.Map;

public class ProfileMyInfoActivity extends ListActivityBase {
    private static final String TAG = "ProfileMyInfoActivity";

    protected LoaderImageView m_viewImageLoaderThumb;
    protected TextView m_viewTextUsername;
    protected TextView m_viewTextInfo;
    protected TextView m_viewTextStatus;

    String m_sUsername;
    String m_sUserTitle;
    String m_sThumb;
    String m_sInfo;
    String m_sStatus;
    protected TextView textTitle;
    protected Button btn_action_back;
    Typeface tt;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_info);

        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_profile_info);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        m_viewImageLoaderThumb = (LoaderImageView) findViewById(R.id.home_profile_thumb);
        m_viewTextUsername = (TextView) findViewById(R.id.home_profile_username);
        m_viewTextInfo = (TextView) findViewById(R.id.home_profile_info);
        m_viewTextStatus = (TextView) findViewById(R.id.home_profile_status);

        Intent i = getIntent();
        m_sUsername = i.getStringExtra("username");
        m_sUserTitle = i.getStringExtra("user_title");
        m_sThumb = i.getStringExtra("thumb");
        m_sInfo = i.getStringExtra("info");
        m_sStatus = i.getStringExtra("status");

        m_viewImageLoaderThumb.setImageDrawable(m_sThumb);
        m_viewTextUsername.setText(m_sUserTitle);
        m_viewTextInfo.setText(m_sInfo);
        m_viewTextStatus.setText(m_sStatus);
        m_viewTextUsername.setTypeface(tt);
        m_viewTextStatus.setTypeface(tt);
        m_viewTextStatus.setTypeface(tt);


        reloadRemoteData();
    }

    protected void reloadRemoteData() {
        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                m_sUsername,
                Main.getLang()
        };

        o.execAsyncMethod("dolphin.getUserInfoExtra", aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getUserInfoExtra result: " + result.toString());

                Object aInfoBlocks[] = (Object[]) result;

                for (int i = 0; i < aInfoBlocks.length; ++i) {
                    Map<String, Object> mapBlock = (Map<String, Object>) aInfoBlocks[i];
                    Log.d(TAG, "block / " + mapBlock.get("Title").toString());
                    Object aInfo[] = (Object[]) mapBlock.get("Info");
                    for (int j = 0; j < aInfo.length; ++j) {
                        Map<String, String> mapField = (Map<String, String>) aInfo[j];
                        Log.d(TAG, "\t" + mapField.get("Caption") + ": " + mapField.get("Value1"));
                    }
                }

                ProfileInfoAdapter adapter = new ProfileInfoAdapter(m_actThis, aInfoBlocks, m_sUsername,tt);
                setListAdapter(adapter);
            }
        }, this);
    }

}

