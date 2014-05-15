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

import java.util.LinkedList;
import java.util.List;

import mikecorrigan.trainscorekeeper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentButton extends Fragment {
    private final static String TAG = FragmentButton.class.getSimpleName();
    private final static boolean VERBOSE = true;

    private final static String ARG_INDEX = "index";
    private final static String ARG_TAB_SPEC = "tabSpec";

    // Controls
    private Players players;

    // Views
    // <LinearLayout tabLayout>
    // ..<TextView tabName>
    // ..<LinearLayout section>
    // ....<TextView sectionName>
    // ....<GridView>
    // ......<ToggleButtons>
    private LinearLayout tabLayout;

    public static FragmentButton newInstance(int index, JSONObject jsonTab) {
        Log.vc(VERBOSE, TAG, "newInstance: jsonTab=" + jsonTab);

        FragmentButton fragment = new FragmentButton();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_TAB_SPEC, jsonTab.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onCreateView: inflater=" + inflater + ", container=" + container +
                ", savedInstanceState=" + Utils.bundleToString(savedInstanceState));

        View rootView = inflater.inflate(R.layout.fragment_button, container, false);

        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "onCreateView: missing arguments");
            return rootView;
        }

        // The activity must support a standard OnClickListener.
        final MainActivity mainActivity = (MainActivity) getActivity();
        final Context context = mainActivity;

        players = mainActivity.getPlayers();
        if (players != null) {
            players.addListener(mPlayersListener);
        }

        // final int index = args.getInt(ARG_INDEX);
        final String tabSpec = args.getString(ARG_TAB_SPEC);

        try {
            JSONObject jsonTab = new JSONObject(tabSpec);

            final String tabName = jsonTab.optString(JsonSpec.TAB_NAME, JsonSpec.DEFAULT_TAB_NAME);
            if (!TextUtils.isEmpty(tabName)) {
                TextView tv = (TextView) rootView.findViewById(R.id.text_view_name);
                tv.setText(tabName);
            }

            tabLayout = (LinearLayout) rootView;

            JSONArray jsonSections = jsonTab.getJSONArray(JsonSpec.SECTIONS_KEY);
            for (int i = 0; i < jsonSections.length(); i++) {
                JSONObject jsonSection = jsonSections.getJSONObject(i);

                LinearLayout sectionLayout = new LinearLayout(context);
                sectionLayout.setOrientation(LinearLayout.VERTICAL);
                tabLayout.addView(sectionLayout);

                // If a section is named, label it.
                final String sectionName = jsonSection.optString(JsonSpec.SECTION_NAME,
                        JsonSpec.DEFAULT_SECTION_NAME);
                if (!TextUtils.isEmpty(sectionName)) {
                    TextView textView = new TextView(context);
                    textView.setText(sectionName);
                    sectionLayout.addView(textView);
                }

                int numColumns = jsonSection.optInt(JsonSpec.SECTION_COLUMNS,
                        JsonSpec.DEFAULT_SECTION_COLUMNS);

                List<View> buttonViews = new LinkedList<View>();

                JSONArray buttons = jsonSection.getJSONArray(JsonSpec.BUTTONS_KEY);
                for (int k = 0; k < buttons.length(); k++) {
                    JSONObject jsonButton = buttons.getJSONObject(k);

                    ScoreButton buttonView = new ScoreButton(context);
                    buttonView.setButtonSpec(jsonButton);
                    buttonView.setOnClickListener(mainActivity.getScoreClickListener());

                    // Add the button to the section.
                    buttonViews.add(buttonView);
                }

                GridView gridView = new GridView(context);
                gridView.setNumColumns(numColumns);
                gridView.setAdapter(new ViewAdapter(context, buttonViews));
                sectionLayout.addView(gridView);
            }
        } catch (JSONException e) {
            Log.th(TAG, e, "onCreateView: failed to parse JSON");
        }

        updateUi();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.vc(VERBOSE, TAG, "onDestroyView");
        super.onDestroyView();

        if (players != null) {
            players.removeListener(mPlayersListener);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.vc(VERBOSE, TAG, "setUserVisibleHint: isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            MainActivity activity = (MainActivity) getActivity();
            activity.enablePlayers(true);

            updateUi();
        }
    }

    Players.Listener mPlayersListener = new Players.Listener() {
        @Override
        public void onSelectionChanged(int playerId) {
            Log.vc(VERBOSE, TAG, "onSelectionChanged: playerId=" + playerId);
            updateUi();
        }
    };

    private void updateUi() {
        Log.vc(VERBOSE, TAG, "updateUi");
        if (players != null) {
            final boolean enabled = players.hasSelection();
            ViewUtils.setEnabledRecursive(tabLayout, enabled);
        }
    }
}
