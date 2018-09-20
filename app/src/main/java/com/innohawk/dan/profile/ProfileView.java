package com.innohawk.dan.profile;

import android.content.Context;
import android.graphics.Typeface;

import com.innohawk.dan.ThumbView;

import java.util.Map;

public class ProfileView extends ThumbView {

    public ProfileView(Context context, Map<String, Object> map, String username, Typeface tt) {
        super(context, map, username,tt);
    }

    @Override
    protected String getText2() {
        return "";
    }

    public void updateTextWitNewStatusMessage(String sStatusMesage) {
        if (0 != sStatusMesage.compareTo((String) m_map.get("status")))
            m_map.put("status", sStatusMesage);
        m_viewText2.setText(getText2());
    }

    protected String getThumbFieldName() {
        return "thumb";
    }
}
