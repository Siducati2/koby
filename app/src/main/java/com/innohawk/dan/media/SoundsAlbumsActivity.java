package com.innohawk.dan.media;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.innohawk.dan.R;

public class SoundsAlbumsActivity extends MediaAlbumsActivity {

    public SoundsAlbumsActivity() {
        super();
        m_sMethodXMLRPC = "dolphin.getAudioAlbums";
        m_classFilesActivity = SoundsFilesActivity.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_sound_albums);
        textTitle.setTypeface(tt);
    }

    protected MediaAlbumsAdapter getAdapterInstance(Context context, Object[] aAlbums, Typeface tt) {
        return (MediaAlbumsAdapter) new SoundsAlbumsAdapter(context, aAlbums,tt);
    }

}
