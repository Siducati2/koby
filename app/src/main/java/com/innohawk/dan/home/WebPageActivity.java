package com.innohawk.dan.home;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activitymaps.ActivityMapsDispensaries;
import com.innohawk.dan.friends.FriendsActivity;
import com.innohawk.dan.friends.FriendsHomeActivity;
import com.innohawk.dan.location.LocationActivity;
import com.innohawk.dan.mail.MailComposeActivity;
import com.innohawk.dan.mail.MailHomeActivity;
import com.innohawk.dan.media.AddImageActivity;
import com.innohawk.dan.media.ImagesAlbumsActivity;
import com.innohawk.dan.media.ImagesGallery;
import com.innohawk.dan.media.SoundsAlbumsActivity;
import com.innohawk.dan.media.SoundsFilesActivity;
import com.innohawk.dan.media.VideosAlbumsActivity;
import com.innohawk.dan.media.VideosFilesActivity;
import com.innohawk.dan.profile.ProfileActivity;
import com.innohawk.dan.search.SearchHomeActivity;

public class WebPageActivity extends ActivityBase {
    private static final String TAG = "OO WebPageActivity";

    protected WebView m_viewWeb;
    protected ProgressBar m_progressBar;
    protected TextView textTitle;
    protected Button btn_action_back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String sTitle = i.getStringExtra("title");
        String sUrl = i.getStringExtra("url");

        setContentView(R.layout.web_page);

        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(sTitle);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        m_viewWeb = (WebView) findViewById(R.id.web_view);
        m_viewWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        m_viewWeb.setBackgroundColor(0);
        m_viewWeb.getSettings().setJavaScriptEnabled(true);
        m_viewWeb.loadUrl(sUrl, Main.getHeadersForLoggedInUser());
        m_viewWeb.setWebViewClient(new WebPageViewClient(this));
        m_viewWeb.setWebChromeClient(new WebPageChromeClient(this));
    }

    @Override
    public void setContentView(int iLayoutResID) {
        super.setContentView(iLayoutResID);
    }

    protected void reloadRemoteData() {
        m_viewWeb.clearCache(true);
        m_viewWeb.reload();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the BACK key and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && m_viewWeb.canGoBack()) {
            m_viewWeb.goBack();
            return true;
        }
        // If it wasn't the BACK key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class WebPageChromeClient extends WebChromeClient {

        public WebPageChromeClient(WebPageActivity act) {
            super();
        }

        public void onProgressChanged(WebView view, int progress) {
            getActionBarHelper().setRefreshActionItemState(progress >= 100 ? false : true);
        }
    }

    private class WebPageViewClient extends WebViewClient {
        protected WebPageActivity m_actWebPage;

        public WebPageViewClient(WebPageActivity act) {
            super();
            m_actWebPage = act;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String sScheme = Uri.parse(url).getScheme();

            Log.d(TAG, "URL scheme: " + Uri.parse(url).getScheme());
            Log.d(TAG, "URL host: " + Uri.parse(url).getHost());
            Log.d(TAG, "URL user: " + Uri.parse(url).getUserInfo());
            Log.d(TAG, "URL last path segment: " + Uri.parse(url).getLastPathSegment());
            Log.d(TAG, "URL fragment: " + Uri.parse(url).getFragment());

            if (sScheme.equals("bxprofile")) {

                String sUsername = url.substring("bxprofile:".length());
                Intent i = new Intent(m_actWebPage, ProfileActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxcontact")) {

                String sUsername = Uri.parse(url).getUserInfo();
                String sUserTitle = Uri.parse(url).getHost();

                if (null == sUsername || 0 == sUsername.length()) {
                    sUsername = url.substring("bxcontact:".length());
                }

                Intent i = new Intent(m_actWebPage, MailComposeActivity.class);
                i.putExtra("recipient", sUsername);
                i.putExtra("recipient_title", null == sUserTitle || 0 == sUserTitle.length() ? sUsername : sUserTitle);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxgoto")) {

                String sSection = url.substring("bxgoto:".length());
                if (sSection.equals("home")) {
                    m_oActivityHelper.gotoHome();
                } else if (sSection.equals("friends")) {
                    Intent i = new Intent(m_actWebPage, FriendsHomeActivity.class);
                    startActivityForResult(i, 0);
                } else if (sSection.equals("messages")) {
                    Intent i = new Intent(m_actWebPage, MailHomeActivity.class);
                    startActivityForResult(i, 0);
                } else if (sSection.equals("search")) {
                    Intent i = new Intent(m_actWebPage, SearchHomeActivity.class);
                    startActivityForResult(i, 0);
                }
                return true;

            } else if (sScheme.equals("bxphoto")) { // bxphoto://USERNAME@ABUM_ID/IMAGE_ID

                String sUsername = Uri.parse(url).getUserInfo();
                String sAlbumId = Uri.parse(url).getHost();
                String sPhotoId = Uri.parse(url).getLastPathSegment();

                Intent i = new Intent(m_actWebPage, ImagesGallery.class);
                i.putExtra("username", sUsername);
                i.putExtra("album_id", sAlbumId);
                i.putExtra("photo_id", sPhotoId);
                i.putExtra("makeavatar","no");
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxvideo")) { // bxvideo://USERNAME@ABUM_ID/VIDEO_ID

                String sUsername = Uri.parse(url).getUserInfo();
                String sAlbumId = Uri.parse(url).getHost();
                String sMediaId = Uri.parse(url).getLastPathSegment();

                Intent i = new Intent(m_actWebPage, VideosFilesActivity.class);
                i.putExtra("username", sUsername);
                i.putExtra("album_id", sAlbumId);
                i.putExtra("media_id", sMediaId);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("ihdispensary")) { // ihdispensary://ID_Dispensario@LatUser/LngUser

                String iId = Uri.parse(url).getUserInfo();
                String sLat = Uri.parse(url).getHost();
                String sLong = Uri.parse(url).getLastPathSegment();

                Intent i = new Intent(m_actWebPage, ActivityMapsDispensaries.class);
                i.putExtra("id", iId);
                i.putExtra("lat", sLat);
                i.putExtra("lng", sLong);
                startActivityForResult(i, 0);
                return true;

            }else if (sScheme.equals("bxaudio")) { // bxaudio://USERNAME@ABUM_ID/SOUND_ID

                String sUsername = Uri.parse(url).getUserInfo();
                String sAlbumId = Uri.parse(url).getHost();
                String sMediaId = Uri.parse(url).getLastPathSegment();

                Intent i = new Intent(m_actWebPage, SoundsFilesActivity.class);
                i.putExtra("username", sUsername);
                i.putExtra("album_id", sAlbumId);
                i.putExtra("media_id", sMediaId);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxphotoupload")) { // bxphotoupload:ALBUM_NAME

                String sAlbumName = Uri.decode(url.substring("bxphotoupload:".length()));
                Intent i = new Intent(m_actWebPage, AddImageActivity.class);
                i.putExtra("album_name", sAlbumName);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxlocation")) { //  bxlocation:USERNAME

                String sUsername = Uri.decode(url.substring("bxlocation:".length()));
                Intent i = new Intent(m_actWebPage, LocationActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxprofilefriends")) { //  bxprofilefriends:USERNAME

                String sUsername = Uri.decode(url.substring("bxprofilefriends:".length()));
                Intent i = new Intent(m_actWebPage, FriendsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxphotoalbums")) { // bxphotoalbums:USERNAME

                String sUsername = Uri.decode(url.substring("bxphotoalbums:".length()));
                Intent i = new Intent(m_actWebPage, ImagesAlbumsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxvideoalbums")) { // bxvideoalbums:USERNAME

                String sUsername = Uri.decode(url.substring("bxvideoalbums:".length()));
                Intent i = new Intent(m_actWebPage, VideosAlbumsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxaudioalbums")) { // bxaudioalbums:USERNAME

                String sUsername = Uri.decode(url.substring("bxaudioalbums:".length()));
                Intent i = new Intent(m_actWebPage, SoundsAlbumsActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxpagetitle")) { // bxaudioalbums:USERNAME

                String sTitle = Uri.decode(url.substring("bxpagetitle:".length()));
                setTitleCaption(sTitle);
                return true;

            } else {

                if (null != Uri.parse(url).getFragment() && Uri.parse(url).getFragment().equals("blank")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("#blank", "")));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(m_actWebPage, description + " (" + failingUrl + ")", Toast.LENGTH_SHORT).show();
        }

    }

}
