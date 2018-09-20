package com.innohawk.dan.appconfig;

/**
 * Created by xploresound on 27/2/18.
 */

/**
 * Created by innohawk on 23/11/17.
 */
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.innohawk.dan.Main;
import com.innohawk.dan.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class LoadImgNew extends LinearLayout {

    private static final int COMPLETE = 0;
    private static final int FAILED = 1;
    private static final int COMPLETEONLYSPINNER=2;
    private static final int COMPLETEONLYSPINNER_FAILED=3;

    protected Context m_context;
    protected Drawable m_drawable;
    protected ProgressBar m_spinner;
    protected ImageView m_viewImage;
    protected LoadImgNew m_layoutImageLoader;
    protected int m_iNoImageResource;

    private static final String TAG = "OO LoaderImageView";
    /**
     * This is used when creating the view in XML
     * To have an image load in XML use the tag 'image="http://developer.android.com/images/dialog_buttons.png"'
     * Replacing the url with your desired image
     * Once you have instantiated the XML view you can call
     * setImageDrawable(url) to change the image
     *
     * @param context
     * @param attrSet
     */
    public LoadImgNew(final Context context, final AttributeSet attrSet) {
        super(context, attrSet);
        final String url = attrSet.getAttributeValue(null, "image");
        instantiate(context, url);
    }

    /**
     * This is used when creating the view programatically
     * Once you have instantiated the view you can call
     * setImageDrawable(url) to change the image
     *
     * @param context  the Activity context
     * @param imageUrl the Image URL you wish to load
     */
    public LoadImgNew(final Context context, final String imageUrl) {
        super(context);
        instantiate(context, imageUrl);
    }

    /**
     * First time loading of the LoaderImageView
     * Sets up the LayoutParams of the view, you can change these to
     * get the required effects you want
     */
    private void instantiate(final Context context, final String imageUrl) {
        m_context = context;
        m_layoutImageLoader = this;
        m_iNoImageResource = R.drawable.no_image;

        m_viewImage = new ImageView(m_context);
        m_viewImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        m_spinner = new ProgressBar(m_context);
        m_spinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        m_spinner.setIndeterminate(true);

        addView(m_spinner);
        addView(m_viewImage);

        if (imageUrl != null) {
            setImageDrawable(imageUrl);
        }
    }

    /**
     * Set's the view's drawable, this uses the internet to retrieve the image
     * don't forget to add the correct permissions to your manifest
     *
     * @param imageUrl the url of the image you wish to load
     */
    public Drawable setImageDrawable(final String imageUrl) {
        m_spinner.setVisibility(View.VISIBLE);
        m_viewImage.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                try {
                    m_drawable = getDrawableFromUrl(imageUrl);
                    imageLoadedHandler.sendEmptyMessage(COMPLETE);
                } catch (MalformedURLException e) {
                    imageLoadedHandler.sendEmptyMessage(FAILED);
                } catch (IOException e) {
                    imageLoadedHandler.sendEmptyMessage(FAILED);
                }
            }

            ;
        }.start();

        return null;
    }

    //innoHawk: Only Spinner
    public Drawable setOnlySpinnerDrawable(final String imageUrl) {
        m_spinner.setVisibility(View.VISIBLE);
        m_viewImage.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                try {
                    m_drawable = getDrawableFromUrl(imageUrl);
                    imageLoadedHandler.sendEmptyMessage(COMPLETEONLYSPINNER);
                } catch (MalformedURLException e) {
                    imageLoadedHandler.sendEmptyMessage(COMPLETEONLYSPINNER_FAILED);
                } catch (IOException e) {
                    imageLoadedHandler.sendEmptyMessage(COMPLETEONLYSPINNER_FAILED);
                }
            }

            ;
        }.start();

        return null;
    }

    //InnoHawk FX: Valida cuando se quiere cargar una imagen que esté dnetro e la App
    public void setImageMenuSlideIntoApp(final String img) {
        String txtConvertIcon = img;
        Map<String, Integer> mapIcons = new HashMap<String, Integer>();
        mapIcons.put("home_status.png", R.drawable.ic_home_status);
        mapIcons.put("home_location.png", R.drawable.ic_home_location);
        mapIcons.put("home_messages.png", R.drawable.ic_home_messages);
        mapIcons.put("home_friends.png", R.drawable.ic_home_friends);
        mapIcons.put("home_info.png", R.drawable.ic_home_info);
        mapIcons.put("home_search.png", R.drawable.ic_home_search);
        mapIcons.put("home_images.png", R.drawable.ic_home_photos);
        mapIcons.put("home_sounds.png", R.drawable.ic_home_sounds);
        mapIcons.put("home_videos.png", R.drawable.ic_home_videos);
        m_spinner.setVisibility(View.VISIBLE);
        m_viewImage.setVisibility(View.GONE);
        int ImageX = 0;
        if (mapIcons.containsKey(txtConvertIcon)) {
            m_viewImage.setVisibility(View.VISIBLE);
            m_spinner.setVisibility(View.GONE);
            ImageX = mapIcons.get(txtConvertIcon);
            int color = Color.parseColor(AppConfig.COLOR_MENU_ICON_SLIDE); //The color u want
            m_viewImage.setImageResource(ImageX);
            m_viewImage.setColorFilter(color);
        }
    }
    //InnoHawk Add Fx
    public Drawable setImageMenuSlideURL(final String imageUrl) {
        m_spinner.setVisibility(View.VISIBLE);
        m_viewImage.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                try {
                    m_drawable = getDrawableFromUrl(imageUrl);
                    imageLoadedHandler2.sendEmptyMessage(COMPLETE);
                } catch (MalformedURLException e) {
                    imageLoadedHandler2.sendEmptyMessage(FAILED);
                } catch (IOException e) {
                    imageLoadedHandler2.sendEmptyMessage(FAILED);
                }
            }

            ;
        }.start();

        return null;
    }
    //InnoGawk Add FX Only MENU img URL
    private final Handler imageLoadedHandler2 = new Handler(new Callback() {
        public boolean handleMessage(Message msg) {
            int color = Color.parseColor(AppConfig.COLOR_MENU_ICON_SLIDE); //The color u want
            switch (msg.what) {
                case COMPLETE:
                    m_viewImage.setImageDrawable(m_drawable);
                    m_viewImage.setVisibility(View.VISIBLE);
                    m_viewImage.setColorFilter(color);
                    m_spinner.setVisibility(View.GONE);
                    break;
                case FAILED:
                default:
                    // 'failed' image here
                    m_layoutImageLoader.setGravity(Gravity.CENTER);
                    m_spinner.setVisibility(View.GONE);
                    m_viewImage.setImageResource(m_iNoImageResource);
                    m_viewImage.setVisibility(View.VISIBLE);
                    m_viewImage.setColorFilter(color);
                    break;
            }
            return true;
        }
    });
    /**
     * Set image resource to be shown if image can not be loaded
     *
     * @param iResourceId resource id
     */
    public void setNoImageResource(int iResourceId) {
        m_iNoImageResource = iResourceId;
    }

    /**
     * Callback that is received once the image has been downloaded
     */
    private final Handler imageLoadedHandler = new Handler(new Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE:
                    m_viewImage.setImageDrawable(m_drawable);
                    m_viewImage.setVisibility(View.VISIBLE);
                    m_spinner.setVisibility(View.GONE);
                    break;
                case COMPLETEONLYSPINNER:
                    m_viewImage.setVisibility(View.GONE);
                    //m_viewImage.setColorFilter(color);
                    m_spinner.setVisibility(View.GONE);
                    break;
                case COMPLETEONLYSPINNER_FAILED:
                    m_viewImage.setVisibility(View.GONE);
                    //m_viewImage.setColorFilter(color);
                    m_spinner.setVisibility(View.GONE);
                    break;
                case FAILED:
                default:
                    // 'failed' image here
                    m_layoutImageLoader.setGravity(Gravity.CENTER);
                    m_spinner.setVisibility(View.GONE);
                    m_viewImage.setImageResource(m_iNoImageResource);
                    m_viewImage.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        }
    });

    /**
     * Pass in an image url to get a drawable object
     *
     * @return a drawable object
     * @throws IOException
     * @throws MalformedURLException
     */
    private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
        URLConnection urlCon = (new java.net.URL(url)).openConnection();
        String sCookie = Main.getCookieForLoggedInUser();
        if (sCookie != null)
            urlCon.addRequestProperty("Cookie", sCookie);
        return Drawable.createFromStream(urlCon.getInputStream(), "name");
    }



}