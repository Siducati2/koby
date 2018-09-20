package com.innohawk.dan.menu;

/**
 * Created by innohawk on 21/5/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;

import java.util.HashMap;
import java.util.Map;

public class MenuListAdapter extends BaseAdapter {

    // Declare variables
    Context context;
    String[] mTitle;
    String[] mSubTitle;
    String[] iMageMenu;
    LayoutInflater inflater;
    private static final String TAG = "OO ListMenuArray";

    public MenuListAdapter(Context context, String[] title, String[] subtitle, String[] icon) {
        this.context = context;
        this.mTitle = title;
        this.mSubTitle = subtitle;
        this.iMageMenu = icon;
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitle[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView txtTitle;
        TextView txtSubTitle;
        String txtConvertIcon;
        LoaderImageView m_viewIconLoader;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        // Locate the TextViews
        txtTitle = (TextView)itemView.findViewById(R.id.title);
        txtSubTitle = (TextView)itemView.findViewById(R.id.subtitle);
        // Set the data
        String TitleAction = (mTitle[position]);
        TitleAction.replace("_", ":");
        txtTitle.setText(TitleAction);
        //Badge
        String NumberBadge = (mSubTitle[position]);
        txtSubTitle.setText(NumberBadge);
        txtSubTitle.setTextColor(Color.parseColor(AppConfig.COLOR_MENU_TEXT_BADGE));
        if((NumberBadge == null)||(NumberBadge.equals("0"))||(NumberBadge.equals("")))
            txtSubTitle.setVisibility(View.GONE);
        // Locate the ImageView
        m_viewIconLoader = (LoaderImageView)itemView.findViewById(R.id.icon);

        //Iconos
        txtConvertIcon = (iMageMenu[position]);
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
        int ImageX = 0;
        if (mapIcons.containsKey(txtConvertIcon)) {
            m_viewIconLoader.setNoImageResource(ImageX);
            m_viewIconLoader.setImageMenuSlideIntoApp(txtConvertIcon);
        }else{
            m_viewIconLoader.setNoImageResource(R.drawable.ic_home_default);
            m_viewIconLoader.setImageMenuSlideURL(txtConvertIcon);
        }
        return itemView;
    }

    public static int[] StringArrToIntArr(String[] s) {
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }
}
