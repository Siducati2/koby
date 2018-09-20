package com.innohawk.dan.customSplash;

/**
 * Created by innohawk on 2/12/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

import com.innohawk.dan.R;

public class SplashMainView extends FrameLayout {

    public SplashMainView(Context context){
        super(context);
        initialize();
    }

    private SplashSplashView mSplashView;

    private void initialize(){
        Context context = getContext();

        // initialize the view with all default values
        // you don't need to set these default values, they are already set, except for setIconResource
        // this is only for demonstration purposes
        mSplashView = new SplashSplashView(context);
        mSplashView.setDuration(1000); // the animation will last 0.5 seconds
        mSplashView.setBackgroundColor(Color.TRANSPARENT); // transparent hole will look white before the animation
        mSplashView.setIconColor(getResources().getColor(R.color.new_bg_background)); // this is the Twitter blue color
        mSplashView.setIconResource(R.drawable.logo_about_512); // a Twitter icon with transparent hole in it
        mSplashView.setRemoveFromParentOnEnd(true); // remove the SplashView from MainView once animation is completed

        // add the view
        addView(mSplashView);
    }

    public void unsetSplashView(){
        mSplashView = null;
    }

    public SplashSplashView getSplashView(){
        return mSplashView;
    }
}
