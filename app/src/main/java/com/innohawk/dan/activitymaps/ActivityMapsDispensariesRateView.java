package com.innohawk.dan.activitymaps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.appconfig.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by innohawk on 18/11/17.
 */

public class ActivityMapsDispensariesRateView extends ActivityBase {
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

    EditText edt_comment;
    String TextComment, TextAuthor, UrlThumb, RateGet;
    protected LoaderImageView m_viewImageLoader;
    RatingBar rb;
    Float number_to_rating;
    String ratingGeneral;
    Typeface tt;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.activity_maps_dispensaries_viewrate);
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

    }

    private void getintent() {
        // TODO Auto-generated method stub
        Intent ih = getIntent();
        id_Dispensaries = ih.getStringExtra("id");
        TextComment = ih.getStringExtra("desc");
        TextAuthor = ih.getStringExtra("author");
        UrlThumb = ih.getStringExtra("thumb");
        RateGet = ih.getStringExtra("ratting");
        MapsInitializer.initialize(getApplicationContext());
        findViewById();
    }

    private void findViewById(){
        TextView Rate = (TextView)findViewById(R.id.txt_rate);
        Rate.setText(R.string.dispensary_rate_author);
        Rate.setTypeface(tt);
        TextView txt_name = (TextView)findViewById(R.id.txt_author);
        txt_name.setText(TextAuthor);
        txt_name.setTypeface(tt);
        Comment = (EditText)findViewById(R.id.txt_postcmt);
        Comment.setText(TextComment);
        Comment.setTypeface(tt);
        //IMG BG
        String image = UrlThumb.replace(" ", "%20");
        ImageView programImage = (ImageView) findViewById(R.id.img_iconpin);
        //programImage.setImageResource(R.drawable.no_image);
        m_viewImageLoader = (LoaderImageView) findViewById(R.id.media_images_image_view);
        m_viewImageLoader.setOnlySpinnerDrawable((String) image);
        if(image.isEmpty()) {
            image = String.valueOf(R.drawable.ic_menu_make_avatar);
            programImage.setImageResource(R.drawable.ic_menu_make_avatar);
        }else{
            final int radius = 6;
            final int margin = 0;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin);
            Picasso.with(ActivityMapsDispensariesRateView.this)
                    .load(image)
                    .transform(transformation)
                    .into(programImage);
        }
        rb = (RatingBar) findViewById(R.id.yourrate);
        ratingGeneral = String.valueOf(RateGet);
        number_to_rating = Float.parseFloat(ratingGeneral);
        rb.setRating(number_to_rating);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
