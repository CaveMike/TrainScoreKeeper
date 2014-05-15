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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mikecorrigan.trainscorekeeper.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class XPagerAdapter extends FragmentPagerAdapter {
    private final static String TAG = XPagerAdapter.class.getSimpleName();
    private final static boolean VERBOSE = true;

    private class PageSpec {
        public final Class<?> clazz;
        public final String title;
        public final JSONObject jsonSpec;
        public Fragment fragment;

        public PageSpec(final Class<?> clazz, final String title, final JSONObject spec) {
            this.clazz = clazz;
            this.title = title;
            this.jsonSpec = spec;
        }
    }

    private final PageSpec[] pageSpecs;

    public XPagerAdapter(final Context context, final FragmentManager fm, final JSONArray jsonTabs) {
        super(fm);
        Log.vc(VERBOSE, TAG, "ctor: fm=" + fm + ", jsonTabs=" + jsonTabs);

        // Add two for Summary and History tabs.
        pageSpecs = new PageSpec[jsonTabs.length() + 2];

        int i = 0;

        // Add button fragments.
        for (; i < jsonTabs.length(); i++) {
            JSONObject jsonTab = jsonTabs.optJSONObject(i);
            if (jsonTab == null) {
                continue;
            }
            final String name = jsonTab.optString(JsonSpec.TAB_NAME, JsonSpec.DEFAULT_TAB_NAME);
            pageSpecs[i] = new PageSpec(FragmentButton.class, name, jsonTab);
        }

        // Add summary fragment.
        pageSpecs[i] = new PageSpec(FragmentSummary.class,
                context.getResources().getString(R.string.summary), new JSONObject());
        i++;

        // Add history fragment.
        pageSpecs[i] = new PageSpec(FragmentHistory.class,
                context.getResources().getString(R.string.history), new JSONObject());
        i++;
    }

    @Override
    public Fragment getItem(int index) {
        Log.vc(VERBOSE, TAG, "getItem: index=" + index);

        if (index > pageSpecs.length) {
            Log.e(TAG, "getItem: invalid index=" + index);
            return null;
        }

        PageSpec pageSpec = pageSpecs[index];
        if (pageSpec == null) {
            Log.e(TAG, "getItem: page spec for index=" + index);
            return null;
        }

        try {
            Method method = pageSpec.clazz.getMethod("newInstance", Integer.TYPE, JSONObject.class);
            pageSpec.fragment = (Fragment) method.invoke(null /* this */, index, pageSpec.jsonSpec);
            return pageSpec.fragment;
        } catch (IllegalAccessException e) {
            Log.th(TAG, e, "getItem");
        } catch (InvocationTargetException e) {
            Log.th(TAG, e, "getItem");
        } catch (NoSuchMethodException e) {
            Log.th(TAG, e, "getItem");
        }

        Log.e(TAG, "getItem: failed, index=" + index);
        return null;
    }

    @Override
    public long getItemId(int index) {
        Log.vc(VERBOSE, TAG, "getItemId: index=" + index);

        if (index > pageSpecs.length) {
            Log.e(TAG, "getItemId: invalid index=" + index);
            return -1;
        }

        PageSpec pageSpec = pageSpecs[index];
        if (pageSpec == null) {
            Log.e(TAG, "getItemId: invalid page spec for index=" + index);
            return -1;
        }

        return pageSpec.hashCode();
    }

    @Override
    public int getItemPosition(Object object)
    {
        Log.vc(VERBOSE, TAG, "getItemPosition: object=" + object);

        for (int i = 0; i < pageSpecs.length; i++) {
            final PageSpec pageSpec = pageSpecs[i];
            if (pageSpec == null) {
                continue;
            }

            if (pageSpec.fragment == object) {
                Log.d(TAG, "getItemPosition: found, i=" + i);
                return POSITION_UNCHANGED;
            }
        }

        Log.d(TAG, "getItemPosition: not found");
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return pageSpecs.length;
    }

    @Override
    public CharSequence getPageTitle(int index) {
        Log.vc(VERBOSE, TAG, "getPageTitle: index=" + index);

        if (index > pageSpecs.length) {
            Log.e(TAG, "getPageTitle: invalid index=" + index);
            return "";
        }

        PageSpec pageSpec = pageSpecs[index];
        if (pageSpec == null) {
            Log.e(TAG, "getPageTitle: invalid page spec for index=" + index);
            return "";
        }

        return pageSpec.title;
    }
}
