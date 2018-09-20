package com.innohawk.dan.media;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.innohawk.dan.R;

public class SoundsFilesActivity extends MediaFilesActivity {
    Typeface tt;
    public SoundsFilesActivity() {
        super();
        m_sMethodXMLRPC = "dolphin.getAudioInAlbum";
        m_sMethodRemove = "dolphin.removeAudio5";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_sound_files);
        textTitle.setTypeface(tt);
    }

    protected MediaFilesAdapter getAdapterInstance(Context context, Object[] aFiles, String sUsername, Typeface tt) {
        return (MediaFilesAdapter) new SoundsFilesAdapter(context, aFiles, sUsername,tt);
    }

    protected boolean isAddAllowed() {
        return false;
    }
}
