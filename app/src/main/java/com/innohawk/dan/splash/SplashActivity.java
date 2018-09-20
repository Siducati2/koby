package com.innohawk.dan.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;

import java.io.File;

public class SplashActivity extends ActivityBase {

    // Asyntask
    private static final String TAG = "OO SplasPage";
    public final static long SPLASH_DISPLAY_LENGTH = 4000;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, false, false, false);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(getRunnableStartApp(), SPLASH_DISPLAY_LENGTH);
    }

    private Runnable getRunnableStartApp() {
        Runnable runnable = new Runnable() {
            public void run() {

                File isExist = getFilesDir();
                File aCheckExist = new File(isExist, "aFileControlNavBar.txt");
                if (aCheckExist.length() == 0) { //No existe
                    Intent intent = null;
                    intent = new Intent(SplashActivity.this, IntroductionPage.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, Main.class);
                    startActivity(intent);
                    finish();
                }
                //Deshabilitamos tod_o para que vaya a Main Directamente...
                /*
                Intent intent = new Intent(SplashActivity.this, Main.class);
                startActivity(intent);
                finish();
                */
            }
        };
        return runnable;
    }
}
