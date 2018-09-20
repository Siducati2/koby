package com.innohawk.dan.search;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import com.innohawk.dan.ThumbView;
import com.innohawk.dan.friends.FriendsBaseAdapter;

import java.util.Map;

public class SearchResultsAdapter extends FriendsBaseAdapter {

    protected Boolean m_isFullPage = true;
    Typeface tt;
    public SearchResultsAdapter(Context context, Object[] aProfiles, Boolean isFullPage) {
        super(context, aProfiles);
        m_isFullPage = isFullPage;
        initViews();
    }

    public int getCount() {
        return super.getCount();
    }

    public View getView(int i, View oldView, ViewGroup arg2) {
        View view = super.getView(i, oldView, arg2);
        if (view != null)
            return view;

        Map<String, Object> map = m_listProfiles.get(i);
        return new ThumbView(m_context, map, (String) map.get("Nick"), tt);
    }
}
