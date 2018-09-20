package com.innohawk.dan.friends;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import java.util.Map;

public class FriendRequestsAdapter extends FriendsBaseAdapter {
    Typeface tt;
    public FriendRequestsAdapter(Context context, Object[] aProfiles) {
        super(context, aProfiles);
        initViews();
    }

    public View getViewReal(int arg0) {
        Map<String, Object> map = m_listProfiles.get(arg0);

        return new ThumbViewActionApproveReject(m_context, map, (String) map.get("Nick"),  tt);
    }

}
