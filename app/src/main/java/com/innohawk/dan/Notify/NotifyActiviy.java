package com.innohawk.dan.Notify;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.R;

/**
 * Created by innohawk on 10/10/17.
 */

public class NotifyActiviy extends ActivityBase {
    private static final String TAG = "NewsActivity";


    protected TextView textTitle;
    protected Button btn_action_back;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activty_notify);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.NotifyHome);
        textTitle.setTypeface(tt);
        //Back

        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

}
