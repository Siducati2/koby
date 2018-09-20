package com.innohawk.dan.friends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.ViewText;

public class FriendsHomeAdapter extends BaseAdapter {

    private Context m_context;
    Typeface tt;
    public FriendsHomeAdapter(Context context, Typeface tt) {
        this.m_context = context;
    }

    public int getCount() {
        return 2;
    }

    public Object getItem(int arg0) {
        // not implemented
        return null;
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    @SuppressLint("StringFormatMatches")
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        String s = "";
        Connector o = Main.getConnector();

        switch (arg0) {
            case 0:
                s = m_context.getString(R.string.friends_menu);
                break;
            case 1:
                if (o.getFriendRequestsNum() > 0) {
                    s = String.format(m_context.getString(R.string.friends_requests_menu_num), o.getFriendRequestsNum());
                } else {
                    s = m_context.getString(R.string.friends_requests_menu);
                }
                break;
        }

        return new ViewText(this.m_context, s, tt);
    }

}
