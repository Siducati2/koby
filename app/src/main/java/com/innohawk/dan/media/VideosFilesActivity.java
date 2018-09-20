package com.innohawk.dan.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.innohawk.dan.R;

public class VideosFilesActivity extends MediaFilesActivity {
    Typeface tt;
    public VideosFilesActivity() {
        super();
        m_sMethodXMLRPC = "dolphin.getVideoInAlbum";
        m_sMethodRemove = "dolphin.removeVideo";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_video_files);
        textTitle.setTypeface(tt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.media_add:
                actionAddNew();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void actionAddNew() {
        Intent i = new Intent(this, AddVideoActivity.class);
        i.putExtra("album_name", m_sAlbumName);
        startActivityForResult(i, 0);

    }

    protected MediaFilesAdapter getAdapterInstance(Context context, Object[] aFiles, String sUsername, Typeface tt) {
        return (MediaFilesAdapter) new VideosFilesAdapter(context, aFiles, sUsername,tt);
    }

    protected boolean isAddAllowed() {
        if (!super.isAddAllowed())
            return false;
        return m_oConnector.getProtocolVer() >= 5 ? true : false;
    }
}
