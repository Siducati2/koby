package com.innohawk.dan;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewTextArea extends LinearLayout {

    protected TextView m_viewText;
    protected LinearLayout m_layout;
    Typeface tt;
    public ViewTextArea(Context context, String sText) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_text_simple, this, true);
        Typeface tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");

        m_layout = (LinearLayout) findViewById(R.id.layout);
        m_viewText = (TextView) findViewById(R.id.text_simple);
        m_viewText.setTypeface(tt);
        m_viewText.setText(sText);

        m_viewText.setSingleLine(false);

    }


}
