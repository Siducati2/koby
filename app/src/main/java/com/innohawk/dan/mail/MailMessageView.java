package com.innohawk.dan.mail;

import android.content.Context;
import android.graphics.Typeface;

import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.ThumbView;

import java.util.Map;

public class MailMessageView extends ThumbView {

    protected Boolean m_isInbox;
    Typeface tt;
    public MailMessageView(Context context, Map<String, Object> map, Boolean isInbox, Typeface tt) {
        super(context, map, "", tt);
        m_isInbox = isInbox;
        setControlsData2();
    }

    protected void setControlsData(Typeface tt) {

    }

    protected void setControlsData2() {
        super.setControlsData(tt);
    }

    protected String getTextTitle() {
        return (String) m_map.get("Subject");
    }

    protected String getText1() {
        return String.format(m_context.getString(m_isInbox ? R.string.mail_from_x : R.string.mail_to_x), getInterlocutorTitle());
    }

    protected String getText2() {
        return (String) m_map.get("Date") + (0 == ((String) m_map.get("New")).compareTo("1") ? "   " + m_context.getString(R.string.mail_new) : "");
    }

    protected String getThumbFieldName() {
        return "Thumb";
    }

    protected String getInterlocutorTitle() {
        if (Main.getConnector().getProtocolVer() > 2)
            return (String) m_map.get("UserTitleInterlocutor");
        else
            return (String) m_map.get("Nick");
    }
}
