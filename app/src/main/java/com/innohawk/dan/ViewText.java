package com.innohawk.dan;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewText extends LinearLayout {

    protected TextView m_viewText;
    protected LinearLayout m_layout;
    Typeface tt;
    public ViewText(Context context, String sText, Typeface tt) {
        super(context);

        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");

        LayoutInflater.from(context).inflate(R.layout.view_text, this, true);

        m_layout = (LinearLayout) findViewById(R.id.layout);
        m_viewText = (TextView) findViewById(R.id.text);
        m_viewText.setTypeface(tt);
        m_viewText.setText(sText);
        m_viewText.setTextSize(14);

    }


}
