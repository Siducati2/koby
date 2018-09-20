package com.innohawk.dan.mail;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.innohawk.dan.R;

import java.util.Map;

public class MailMessageAdapter extends BaseAdapter {

    protected Context m_context;
    protected Map<String, Object> m_mapMessage;
    protected Boolean m_isInbox;
    protected MailMessageView m_viewThumb;
    protected LinearLayout m_viewMessage;
    Typeface tt;
    public MailMessageAdapter(Context context, Map<String, Object> mapMessage, Boolean isInbox) {
        this.m_context = context;
        this.m_mapMessage = mapMessage;
        this.m_isInbox = isInbox;
    }

    public int getCount() {
        return 2;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case 0:
                if (null == m_viewThumb) {
                    m_viewThumb = new MailMessageView(m_context, m_mapMessage, m_isInbox,tt);
                }
                return m_viewThumb;
            case 1: {
                if (null == m_viewMessage) {
                    m_viewMessage = (LinearLayout) LayoutInflater.from(m_context).inflate(R.layout.view_text, null, false);
                    m_viewMessage.removeAllViews();
                    WebView webView = new WebView(m_context);
                    webView.loadData("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><body>" + m_mapMessage.get("Text") + "</body></html>", "text/html", "utf-8");
                    m_viewMessage.addView(webView);
                }
                return m_viewMessage;
            }
        }
        return null;
    }

}
