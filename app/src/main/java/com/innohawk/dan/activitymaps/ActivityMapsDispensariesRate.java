package com.innohawk.dan.activitymaps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;

/**
 * Created by innohawk on 18/11/17.
 */

public class ActivityMapsDispensariesRate extends ActivityBase {
    private static final String TAG = "Rate Dispesaries";
    String isGuest;

    protected TextView textTitle;
    protected CheckBox Wall,Forum,Article;
    protected boolean wall_,forum_,article_;
    EditText Comment;
    String Comment_String;
    Button Send;
    Button goStore,btn_back;
    String id_Dispensaries;//Id Dispensario
    RatingBar rate_vote; //El que votamos
    double number_to_rating = 0.0;
    String ratingGeneral;
    EditText edt_comment;
    Typeface tt;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.activity_maps_dispensaries_rate);
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.dispensary_rate_title);
        textTitle.setTypeface(tt);

        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);

        btn_back = (Button)findViewById(R.id.buttonBack);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getintent();

        Wall = (CheckBox) findViewById(R.id.radioButton);
        Forum = (CheckBox) findViewById(R.id.radioButton2);
        Article = (CheckBox) findViewById(R.id.radioButton3);
        Wall.setChecked(false);
        Forum.setChecked(false);
        Article.setChecked(false);
        Wall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wall_ = isChecked;
            }
        });
        Forum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                forum_ = isChecked;
            }
        });
        Article.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                article_ = isChecked;
            }
        });

        //Give Rate
        rate_vote = (RatingBar)findViewById(R.id.yourrate);
        addListenerOnRatingBar();

        TextView Rate = (TextView)findViewById(R.id.txt_rate);
        Rate.setText(R.string.dispensary_rate_your);
        Rate.setTypeface(tt);

        Comment = (EditText)findViewById(R.id.txt_postcmt);
        Comment.setHint(R.string.dispensary_rate_hintcmt);
        Comment.setTypeface(tt);
        Send = (Button) findViewById(R.id.action_post);
        Send.setText(R.string.dispensary_rate_send);
        Send.setTypeface(tt);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Comment_String = Comment.getText().toString().replace(" ", "%20");
                    Comment_String = Comment.getText().toString();
                    if (Comment_String.equals(null)) {
                        Comment_String = "";
                    }
                    if(Comment_String.length()<=5)
                        Toast.makeText(ActivityMapsDispensariesRate.this,getResources().getString(R.string.dispensary_Exception), Toast.LENGTH_LONG).show();
                    else{
                        /*if((wall_==false)&&(forum_==false)&&(article_==false))
                            Toast.makeText(ActivityMapsDispensariesRate.this,getResources().getString(R.string.TabCommentExceptionSelect), Toast.LENGTH_LONG).show();
                        else {
                            if (isGuest.equals("yes"))
                                Toast.makeText(ActivityMapsDispensariesRate.this,getResources().getString(R.string.TabCommentGuest), Toast.LENGTH_LONG).show();
                            else
                                Server();
                        }*/
                        if (isGuest.equals("yes"))
                            Toast.makeText(ActivityMapsDispensariesRate.this,getResources().getString(R.string.dispensary_CommentGuest), Toast.LENGTH_LONG).show();
                        else
                            Server();
                    }
                }catch (NullPointerException e) {
                    // TODO: handle exception
                }

            }
        });

    }
    public void addListenerOnRatingBar() {
        rate_vote = (RatingBar) findViewById(R.id.yourrate);
        rate_vote.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //Toast.makeText(getApplicationContext(), String.valueOf(rating), Toast.LENGTH_SHORT).show();
                number_to_rating = Float.valueOf(String.valueOf(rating));
            }
        });
    }
    private void Server() {
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        sMethod = "ih.dispensarypostcmt";
        aParams = new Object[]{
                o.getUsername(),
                o.getPassword(),
                Main.getLang(),
                isGuest,
                String.valueOf(wall_),
                String.valueOf(forum_),
                String.valueOf(article_),
                Comment_String,
                String.valueOf(number_to_rating),
                id_Dispensaries,
                "0"
        };
        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Toast.makeText(ActivityMapsDispensariesRate.this,getResources().getString(R.string.dispensary_CommentPostOk), Toast.LENGTH_LONG).show();
                Comment.setText("");
                Wall.setChecked(false);
                Forum.setChecked(false);
                Article.setChecked(false);
            }
        }, this);
    }
    private void getintent() {
        // TODO Auto-generated method stub
        Intent ih = getIntent();
        id_Dispensaries = ih.getStringExtra("id");
        MapsInitializer.initialize(getApplicationContext());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
