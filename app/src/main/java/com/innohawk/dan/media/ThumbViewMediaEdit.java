package com.innohawk.dan.media;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.innohawk.dan.R;

import java.util.Map;

public class ThumbViewMediaEdit extends ThumbViewMedia {

    protected Button m_btnDelete;
    protected Button m_btnView;
    Typeface tt;
    public ThumbViewMediaEdit(Context context, Map<String, Object> map, String username, Typeface tt) {
        super(context, map, username,tt);
    }

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

            ConfirmationOnClickListener listener = new ConfirmationOnClickListener(this.getContext(), (String) m_map.get("id")) {
                public void onConfirm() {
                    MediaFilesActivity activity = (MediaFilesActivity) m_context;
                    activity.onRemoveFile(m_sData);
                }
            };

            //m_btnDelete = (Button) LayoutInflater.from(this.getContext()).inflate(R.layout.view_action_button, null, false);
            m_btnDelete = (Button) LayoutInflater.from(this.getContext()).inflate(R.layout.innohawk_view_action_button_reject, null, false);
            m_btnDelete.setOnClickListener(listener);
            m_btnDelete.setText(getContext().getString(R.string.media_files_delete));
            m_btnDelete.setTypeface(tt);
            m_btnDelete.setFocusable(false);
            m_btnDelete.setFocusableInTouchMode(false);
        }
        return m_btnDelete;
    }

}
