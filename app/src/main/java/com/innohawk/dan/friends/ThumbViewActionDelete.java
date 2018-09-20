package com.innohawk.dan.friends;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.innohawk.dan.R;

import java.util.Map;

public class ThumbViewActionDelete extends ThumbViewBase {

    public Button m_btnDelete = null;
    Typeface tt;
    public ThumbViewActionDelete(Context context, Map<String, Object> map, String username, Typeface tt) {
        super(context, map, username,tt);
    }

    @Override
    protected void addControls() {
        super.addControls();

        LinearLayout l = new LinearLayout(m_context);
        l.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        l.setOrientation(HORIZONTAL);

        l.addView(getButtonDeleteControl());

        m_viewInfoWrapper.addView(l);
    }

    protected Button getButtonDeleteControl() {
        if (null == m_btnDelete) {

            ConfirmationOnClickListener listener = new ConfirmationOnClickListener(this.getContext(), this.m_sUsername) {
                public void onConfirm() {
                    FriendsActivity activity = (FriendsActivity) m_context;
                    activity.onRemoveFriend(m_sData);
                }
            };

            m_btnDelete = (Button) LayoutInflater.from(this.getContext()).inflate(R.layout.innohawk_view_action_button_reject, null, false);
            m_btnDelete.setOnClickListener(listener);
            m_btnDelete.setText(getContext().getString(R.string.friends_delete));
            m_btnDelete.setFocusable(false);
            m_btnDelete.setFocusableInTouchMode(false);
        }
        return m_btnDelete;
    }

}
