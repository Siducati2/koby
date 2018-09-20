package com.innohawk.dan.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.nineoldandroids.view.ViewHelper;


public class IntroductionPage extends AppCompatActivity {


    static final int PAGES = 4;
    ViewPager introContent;
    PagerAdapter contentAdapter;
    LinearLayout bubble_Indicator;
    Button button_Skip;
    Button button_Done;
    ImageButton button_Next;
    boolean isOpaque = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.introduction_page);

        button_Skip = Button.class.cast(findViewById(R.id.btn_intro_skip));
        button_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });


        button_Next = ImageButton.class.cast(findViewById(R.id.btn_intro_next));
        button_Next.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                introContent.setCurrentItem(introContent.getCurrentItem() + 1, true);
            }

        });


        button_Done = Button.class.cast(findViewById(R.id.btn_intro_done));
        button_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });


        introContent = (ViewPager) findViewById(R.id.introContent);
        contentAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        introContent.setAdapter(contentAdapter);
        introContent.setPageTransformer(true, new CrossfadePageTransformer());

        introContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        introContent.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        introContent.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == PAGES - 2) {
                    button_Skip.setVisibility(View.GONE);
                    button_Next.setVisibility(View.GONE);
                    button_Done.setVisibility(View.VISIBLE);
                } else if (position < PAGES - 2) {
                    button_Skip.setVisibility(View.VISIBLE);
                    button_Next.setVisibility(View.VISIBLE);
                    button_Done.setVisibility(View.GONE);
                } else if (position == PAGES - 1) {
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        MakeBubbles();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (introContent != null) {
            introContent.clearOnPageChangeListeners();
        }
    }


    private void MakeBubbles() {
        bubble_Indicator = LinearLayout.class.cast(findViewById(R.id.bubble_indicator));
        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);
        for (int i = 0; i < PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_circle_bubble);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            bubble_Indicator.addView(circle);
        }
        setIndicator(0);
    }


    private void setIndicator(int index) {
        if (index < PAGES) {
            for (int i = 0; i < PAGES - 1; i++) {
                ImageView circle = (ImageView) bubble_Indicator.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.pageViewBubble));
                } else {
                    circle.setColorFilter(getResources().getColor(R.color.pagePassBubble));
                }
            }
        }
    }


    private void endTutorial() {
        Intent intent = null;
        intent = new Intent(IntroductionPage.this, Main.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }


    @Override
    public void onBackPressed() {
        if (introContent.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            introContent.setCurrentItem(introContent.getCurrentItem() - 1);
        }
    }


    private class ScreenSlideAdapter extends FragmentStatePagerAdapter {

        public ScreenSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentActivityBase welcomeScreenFragment = null;
            switch (position) {
                case 0:
                    welcomeScreenFragment = FragmentActivityBase.newInstance(R.layout.introduction_content1);
                    break;
                case 1:
                    welcomeScreenFragment = FragmentActivityBase.newInstance(R.layout.introduction_content2);
                    break;
                case 2:
                    welcomeScreenFragment = FragmentActivityBase.newInstance(R.layout.introduction_content3);
                    break;
                case 3:
                    welcomeScreenFragment = FragmentActivityBase.newInstance(R.layout.introduction_content4);
                    break;
            }
            return welcomeScreenFragment;
        }

        @Override
        public int getCount() {
            return PAGES;
        }
    }


    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head = page.findViewById(R.id.titleCont);
            View text_content = page.findViewById(R.id.textCont);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {
            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));
                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }
            }
        }
    }
}
