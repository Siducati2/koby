package com.innohawk.dan.helps;

/**
 * Created by innohawk on 23/3/16.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.ViewText;

public class HelpHomeAdapter extends BaseAdapter {

    private Context m_context;
    Typeface tt;
    public HelpHomeAdapter(Context context, Typeface tt) {
        this.m_context = context;
    }

    public int getCount() {
        return 6;
    }

    public Object getItem(int arg0) {
        // not implemented
        return null;
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {

        String s = "";
        Connector o = Main.getConnector();

        switch (arg0) {
            case 0:
                s = m_context.getString(R.string.about_help);
                break;
            case 1:
                s = m_context.getString(R.string.privacity_help);
                break;
            case 2:
                s = m_context.getString(R.string.terms_help);
                break;
            case 3:
                s = m_context.getString(R.string.Faqs_help);
                break;
            case 4:
                s = m_context.getString(R.string.Help_help);
                break;
            case 5:
                s = m_context.getString(R.string.Help_contact);
                break;
        }

        return new ViewText(m_context, s,tt);
    }

}