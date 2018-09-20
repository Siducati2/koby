package com.innohawk.dan.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.innohawk.dan.R;

public class ImagesAlbumsActivity extends MediaAlbumsActivity {
    Typeface tt;
    public ImagesAlbumsActivity() {
        super();
        m_sMethodXMLRPC = "dolphin.getImageAlbums";

        m_classFilesActivity = ImagesFilesActivity.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_image_albums);
        textTitle.setTypeface(tt);
    }

    protected MediaAlbumsAdapter getAdapterInstance(Context context, Object[] aAlbums, Typeface tt) {
        return (MediaAlbumsAdapter) new ImagesAlbumsAdapter(context, aAlbums, this.tt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (m_oConnector.getAlbumsReloadRequired()) {
            reloadRemoteData();
            m_oConnector.setAlbumsReloadRequired(false);
        }
    }
}
