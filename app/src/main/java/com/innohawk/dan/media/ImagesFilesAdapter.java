package com.innohawk.dan.media;

import android.content.Context;
import android.graphics.Typeface;

import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;

public class ImagesFilesAdapter extends MediaFilesAdapter {

    public ImagesFilesAdapter(Context context, Object[] aFiles, String sUsername, Typeface tt) {
        super(context, aFiles, sUsername, tt);
    }

    protected boolean isDeleteAllowed() {
        Connector o = Main.getConnector();
        return m_sUsername.equalsIgnoreCase(o.getUsername());
    }
}
