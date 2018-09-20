package com.innohawk.dan.home;

import android.content.Context;

import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.R;

public class HomeBtn3rdParty extends HomeBtn {

    protected LoaderImageView m_viewIconLoader;

    public HomeBtn3rdParty(Context context, String sTitle, String sBubble, String sIconUrl) {
        super(context, R.layout.view_home_btn_3rdparty, sTitle, sBubble);

        m_viewIconLoader = (LoaderImageView) findViewById(R.id.home_btn_icon);
        m_viewIconLoader.setNoImageResource(R.drawable.ic_home_default);
        m_viewIconLoader.setImageDrawable(sIconUrl);
    }

}
