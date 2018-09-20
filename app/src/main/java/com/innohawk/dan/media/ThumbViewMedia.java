package com.innohawk.dan.media;

import android.content.Context;
import android.graphics.Typeface;

import com.innohawk.dan.ThumbView;

import java.util.Map;

public class ThumbViewMedia extends ThumbView {


    public ThumbViewMedia(Context context, Map<String, Object> map, String username, Typeface tt) {
        super(context, map, username, tt);
    }

    protected String getTextTitle() {
        return (String) m_map.get("title");
    }

    protected String getText1() {
        return (String) m_map.get("desc");
    }

    protected String getThumbFieldName() {
        return "thumb";
    }
}
