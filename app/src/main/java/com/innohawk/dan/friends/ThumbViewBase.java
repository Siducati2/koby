package com.innohawk.dan.friends;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;

import com.innohawk.dan.ThumbView;

import java.util.Map;


public class ThumbViewBase extends ThumbView {
    Typeface tt;
    protected Button m_btnViewProfile = null;

    public ThumbViewBase(Context context, Map<String, Object> map, String username,Typeface tt) {
        super(context, map, username, tt);
    }

    @Override
    protected void addControls() {

    }

}
