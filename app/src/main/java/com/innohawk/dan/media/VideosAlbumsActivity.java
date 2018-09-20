package com.innohawk.dan.media;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.innohawk.dan.R;

public class VideosAlbumsActivity extends MediaAlbumsActivity {

    public VideosAlbumsActivity() {
        super();
        m_sMethodXMLRPC = "dolphin.getVideoAlbums";
        m_classFilesActivity = VideosFilesActivity.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_video_albums);
        textTitle.setTypeface(tt);

    }

    protected MediaAlbumsAdapter getAdapterInstance(Context context, Object[] aAlbums, Typeface tt) {
        return (MediaAlbumsAdapter) new VideosAlbumsAdapter(context, aAlbums,tt);
    }

}
