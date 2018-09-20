package com.innohawk.dan.videos_custom;

        import android.annotation.SuppressLint;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.webkit.GeolocationPermissions;
        import android.webkit.WebChromeClient;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.Toast;

        import com.innohawk.dan.ActivityBase;
        import com.innohawk.dan.Main;
        import com.innohawk.dan.R;
        import com.innohawk.dan.appconfig.AppConfig;
        import com.innohawk.dan.friends.FriendsActivity;
        import com.innohawk.dan.friends.FriendsHomeActivity;
        import com.innohawk.dan.location.LocationActivity;
        import com.innohawk.dan.mail.MailComposeActivity;
        import com.innohawk.dan.mail.MailHomeActivity;
        import com.innohawk.dan.media.AddImageActivity;
        import com.innohawk.dan.media.AddVideoActivity;
        import com.innohawk.dan.media.ImagesAlbumsActivity;
        import com.innohawk.dan.media.ImagesGallery;
        import com.innohawk.dan.media.SoundsAlbumsActivity;
        import com.innohawk.dan.media.SoundsFilesActivity;
        import com.innohawk.dan.media.VideosAlbumsActivity;
        import com.innohawk.dan.media.VideosFilesActivity;
        import com.innohawk.dan.profile.ProfileActivity;
        import com.innohawk.dan.search.SearchHomeActivity;

public class VideosActivity extends ActivityBase {
    private WebView webView;
    protected int m_iMemberId_;
    protected String m_sUser_;
    protected String m_sPass_;
    private static final String TAG = "OO NOTIActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_page);
        setTitleCaption(R.string.special_video);

        Intent i        = getIntent();
        m_iMemberId_    = i.getIntExtra("member_id", 0);
        m_sUser_        = i.getStringExtra("username");
        m_sPass_        = i.getStringExtra("password");
        iGetWebView();
    }

    protected void iGetWebView(){
        String sUrl = AppConfig.URL_VIDEOS +"?mid="+m_iMemberId_;
        webView = (WebView) findViewById(R.id.web_view);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        //Brower niceties -- pinch / zoom, follow links in place
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        // Below required for geolocation
        webView.getSettings().setGeolocationEnabled(true);
        //HTML5 API flags
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationDatabasePath( getFilesDir().getPath() );
        //Load WebView
        webView.loadUrl(sUrl, Main.getHeadersForLoggedInUser());
        webView.setWebViewClient(new WebPageViewClient(this));
        webView.setWebChromeClient(new WebPageChromeClient(this));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
    }

    private class WebPageChromeClient extends WebChromeClient {

        public WebPageChromeClient(VideosActivity act) {
            super();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    private class WebPageViewClient extends WebViewClient {
        private static final String TAG = "OO NotificationsActivity";
        protected VideosActivity m_actWebPage;

        public WebPageViewClient(VideosActivity act) {
            super();
            m_actWebPage = act;
        }

        @SuppressLint("LongLogTag")
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

            } else if (sScheme.equals("bxaudio")) { // bxaudio://USERNAME@ABUM_ID/SOUND_ID

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

            } else if (sScheme.equals("bxvideoupload")) { // bxphotoupload:ALBUM_NAME

                String sAlbumName = Uri.decode(url.substring("bxvideoupload:".length()));
                Intent i = new Intent(m_actWebPage, AddVideoActivity.class);
                i.putExtra("album_name", sAlbumName);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxlocation")) { // bxlocation:USERNAME

                String sUsername = Uri.decode(url.substring("bxlocation:".length()));
                Intent i = new Intent(m_actWebPage, LocationActivity.class);
                i.putExtra("username", sUsername);
                startActivityForResult(i, 0);
                return true;

            } else if (sScheme.equals("bxprofilefriends")) { // bxprofilefriends:USERNAME

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

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(m_actWebPage, description + " (" + failingUrl + ")", Toast.LENGTH_SHORT).show();
        }
    }


}
