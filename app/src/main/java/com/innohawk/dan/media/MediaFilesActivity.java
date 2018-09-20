package com.innohawk.dan.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.innohawk.dan.Connector;
import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;

import java.util.Map;

public class MediaFilesActivity extends ListActivityBase {

    private static final String TAG = "MediaFilesActivity";

    protected String m_sMethodXMLRPC;
    protected MediaFilesAdapter filesAdapter;
    protected String m_sUsername;
    protected String m_sMediaId;
    protected String m_sAlbumId;
    protected String m_sAlbumName;
    protected boolean m_isAlbumDefault;
    protected Object m_aFiles[];
    protected Connector m_oConnector;
    protected String m_sMethodRemove;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_add;
    protected Button btn_action_reload;
    Typeface tt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");
        Intent i = getIntent();
        m_sUsername = i.getStringExtra("username");
        m_sAlbumId = i.getStringExtra("album_id");
        m_sAlbumName = i.getStringExtra("album_name");
        m_sMediaId = i.getStringExtra("media_id");
        m_isAlbumDefault = i.getBooleanExtra("album_default", true);
        if (null != m_sMediaId) {
            TextView m_editSite = (TextView) findViewById(android.R.id.empty);
            m_editSite.setVisibility(View.GONE);
        }

        m_oConnector = Main.getConnector();
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

        //Submit
        btn_action_add = (Button) findViewById(R.id.buttonAdd);
        if(isAddAllowed())
            btn_action_add.setVisibility(View.VISIBLE);
        btn_action_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAddNew();
            }
        });

        reloadRemoteData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAddAllowed()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.media, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    protected void actionAddNew() {
        //La cogemos de las dependientes
    }

    protected void reloadRemoteData() {

        Object[] aParams = {
                m_oConnector.getUsername(),
                m_oConnector.getPassword(),
                m_sUsername,
                m_sAlbumId
        };

        m_oConnector.execAsyncMethod(m_sMethodXMLRPC, aParams, new Connector.Callback() {

            public void callFinished(Object result) {
                Log.d(TAG, m_sMethodXMLRPC + " result: " + result.toString());

                m_aFiles = null;
                m_aFiles = (Object[]) result;

                Log.d(TAG, m_sMethodXMLRPC + " num: " + m_aFiles.length);

                filesAdapter = null;
                filesAdapter = getAdapterInstance(m_actThis, m_aFiles, m_sUsername,tt);

                if (null == m_sMediaId) {
                    setListAdapter(filesAdapter);
                } else {
                    if (0 == m_aFiles.length) {
                        finish();
                    } else {
                        int iPos = filesAdapter.getPositionByFileId(m_sMediaId);
                        if (iPos < 0)
                            iPos = 0;
                        onListItemClick(null, null, iPos, 0);
                    }
                }
            }
        }, this);
    }

    protected MediaFilesAdapter getAdapterInstance(Context context, Object[] aFiles, String sUsername, Typeface tt) {
        // override this func
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

        Log.d(TAG, "onActivityResult | requestCode:" + requestCode + " | resultCode:" + resultCode + " | i:" + i);

        if (null != m_sMediaId)
            finish();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Map<String, Object> map = filesAdapter.getItem(position);
        if (null == map)
            return;

        String sUrl = (String) map.get("file");
        if (!sUrl.startsWith("http://") && !sUrl.startsWith("https://")) {
            sUrl = "http://www.youtube.com/watch?v=" + sUrl;
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(sUrl)), 2);
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(sUrl), "video/*");
            startActivityForResult(i, 1);
        }

    }

    public void onRemoveFile(String sId) {
        if (null == m_sMethodRemove || m_sMethodRemove.isEmpty())
            return;

        Log.d(TAG, "onRemove: " + sId);

        Object[] aParams = {
                m_oConnector.getUsername(),
                m_oConnector.getPassword(),
                sId
        };

        m_oConnector.execAsyncMethod(m_sMethodRemove, aParams, new Connector.Callback() {

            public void callFinished(Object result) {
                Log.d(TAG, m_sMethodRemove + " result: " + result.toString());
                if (result.toString().equals("ok")) {
                    reloadRemoteData();
                    Connector o = Main.getConnector();
                    o.setAlbumsReloadRequired(true);
                }
            }
        }, this);

    }

    public void onViewFile(String sId) {

    }

    protected boolean isAddAllowed() {
        // if not owner, don't allow file adding
        return m_sUsername.equalsIgnoreCase(m_oConnector.getUsername());
    }
}
