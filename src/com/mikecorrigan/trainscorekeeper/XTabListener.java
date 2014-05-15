//   Copyright 2014 Michael T. Corrigan
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mikecorrigan.trainscorekeeper;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;

public class XTabListener implements ActionBar.TabListener {
    private final static String TAG = XTabListener.class.getSimpleName();
    private final static boolean VERBOSE = true;

    private final ViewPager viewPager;

    public XTabListener(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.vc(VERBOSE, TAG, "onTabSelected: tab=" + tab + ", ft=" + ft);

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        Log.vc(VERBOSE, TAG, "onTabUnselected: tab=" + tab + ", ft=" + ft);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        Log.vc(VERBOSE, TAG, "onTabReselected: tab=" + tab + ", ft=" + ft);
    }
};
