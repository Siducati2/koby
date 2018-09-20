package com.innohawk.dan.friends;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.innohawk.dan.ThumbView;

import java.util.Map;

public class FriendsAdapter extends FriendsBaseAdapter {

    private Boolean m_isOwner;
    Typeface tt;
    public FriendsAdapter(Context context, Object[] aProfiles, Boolean isOwner) {
        super(context, aProfiles);
        m_isOwner = isOwner;
        initViews();

    }


	/*
    public View getView(int arg0, View oldView, ViewGroup arg2) {
		View view = super.getView(arg0, oldView, arg2);
		if (view != null)
			return view;	
		
		int iMin = m_listViews.size();
		int iMax = arg0;
		for (int j=iMin ; j < iMax+1 ; ++j)			
			m_listViews.add(arg0, getViewReal(j));
		return m_listViews.get(arg0);
	}
	*/

    public View getViewReal(int arg0) {
        Map<String, Object> map = m_listProfiles.get(arg0);
        ThumbView o;
        if (m_isOwner)
            o = new ThumbViewActionDelete(m_context, map, (String) map.get("Nick"),tt);
        else
            o = new ThumbView(m_context, map, (String) map.get("Nick"), tt);
        return o;
    }
}
