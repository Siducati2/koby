/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.innohawk.dan.actionbar;

import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * An abstract class that handles some common action bar-related functionality in the app. This
 * class provides functionality useful for both phones and tablets, and does not require any Android
 * 3.0-specific features, although it uses them if available.
 * <p/>
 * Two implementations of this class are {@link ActionBarHelperBase} for a pre-Honeycomb version of
 * the action bar, and {@link ActionBarHelperHoneycomb}, which uses the built-in ActionBar features
 * in Android 3.0 and later.
 */
public abstract class ActionBarHelper {
    protected Activity mActivity;
    protected ProgressDialog m_dialogProgress;

    /**
     * Factory method for creating {@link ActionBarHelper} objects for a
     * given activity. Depending on which device the app is running, either a basic helper or
     * Honeycomb-specific helper will be returned.
     */
    public static ActionBarHelper createInstance(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new ActionBarHelperICS(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new ActionBarHelperHoneycomb(activity);
        } else {
            return new ActionBarHelperBase(activity);
        }
    }

    protected ActionBarHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * Action bar helper code to be run in {@link Activity#onCreate(android.os.Bundle)}.
     */
    public void onCreate(Bundle savedInstanceState) {
        Connector o = Main.getConnector();
        if (null != o && o.isLoading() && o.isSameContext(mActivity))
            setRefreshActionItemStateAlternate(true);
    }

    /**
     * Action bar helper code to be run in {@link Activity#onPostCreate(android.os.Bundle)}.
     */
    public void onPostCreate(Bundle savedInstanceState) {
    }

    protected void onDestroy() {
        if (m_dialogProgress != null)
            m_dialogProgress.dismiss();
    }

    /**
     * Action bar helper code to be run in {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
     * <p/>
     * NOTE: Setting the visibility of menu items in <em>menu</em> is not currently supported.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Action bar helper code to be run in {@link Activity#onTitleChanged(CharSequence, int)}.
     */
    protected void onTitleChanged(CharSequence title, int color) {
    }

    /**
     * Sets the indeterminate loading state of the item with ID {@link R.id.menu_refresh}.
     * (where the item ID was menu_refresh).
     */
    public abstract void setRefreshActionItemState(boolean refreshing);

    /**
     * Returns a {@link MenuInflater} for use when inflating menus. The implementation of this
     * method in {@link ActionBarHelperBase} returns a wrapped menu inflater that can read
     * action bar metadata from a menu resource pre-Honeycomb.
     */
    public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
        return superMenuInflater;
    }

    protected void setRefreshActionItemStateAlternate(boolean refreshing) {
        if (refreshing && null == m_dialogProgress) {
            if (!mActivity.isFinishing()) {
                String sLoading = mActivity.getResources().getString(R.string.loading);
                m_dialogProgress = ProgressDialog.show(mActivity, "", sLoading, true, false);
            }
        } else if (null != m_dialogProgress) {
            try {
                m_dialogProgress.dismiss();
            } catch (Exception e) {
                // nothing, dialog is already hidden
            } finally {
                m_dialogProgress = null;
            }
        }
    }

}
