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

import mikecorrigan.trainscorekeeper.R;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentHistory extends Fragment {
    private final static String TAG = FragmentHistory.class.getSimpleName();
    private final static boolean VERBOSE = true;

    private final static String ARG_INDEX = "index";
    private final static String ARG_TAB_SPEC = "tabSpec";

    // Model
    private Game game;

    // View
    private TextView textView;
    private ScrollView scrollView;

    public static FragmentHistory newInstance(int index, JSONObject jsonSpec) {
        Log.vc(VERBOSE, TAG, "newInstance: jsonTab=" + jsonSpec);

        FragmentHistory fragment = new FragmentHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_TAB_SPEC, jsonSpec.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onCreateView: inflater=" + inflater + ", container=" + container +
                ", savedInstanceState=" + Utils.bundleToString(savedInstanceState));

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        textView = (TextView) rootView.findViewById(R.id.text_view_history);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);

        // Get the model and attach a listener.
        MainActivity activity = (MainActivity) getActivity();
        game = activity.getGame();
        if (game != null) {
            game.addListener(mGameListener);
        }

        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "onCreateView: missing arguments");
            return rootView;
        }

        // final int index = args.getInt(ARG_INDEX);
        // final String tabSpec = args.getString(ARG_TAB_SPEC);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.vc(VERBOSE, TAG, "onResume");
        super.onResume();

        updateUi();
    }

    @Override
    public void onDestroyView() {
        Log.vc(VERBOSE, TAG, "onDestroyView");
        super.onDestroyView();

        if (game != null) {
            game.removeListener(mGameListener);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.vc(VERBOSE, TAG, "setUserVisibleHint: isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            MainActivity activity = (MainActivity) getActivity();
            activity.enablePlayers(false);
        }
    }

    private final Game.Listener mGameListener = new Game.Listener() {
        @Override
        public void onEnabled(int playerId, boolean enabled) {
            Log.vc(VERBOSE, TAG, "onEnabled: playerId=" + playerId + ", enabled=" + enabled);
        }

        @Override
        public void onScoreChanged(int playerId, int score) {
            Log.vc(VERBOSE, TAG, "onScoreChanged: playerId=" + playerId + ", score=" + score);
            updateUi();
        }
    };

    private void updateUi() {
        Log.vc(VERBOSE, TAG, "updateUi");

        if (game == null) {
            Log.w(TAG, "updateUi: no game, this=" + this);
            return;
        }

        if (textView == null) {
            Log.w(TAG, "updateUi: no view, this=" + this);
            return;
        }

        final String text = game.getHistory();
        Log.vc(VERBOSE, TAG, "history=" + text);
        textView.setText(text);

        if (scrollView != null) {
            scrollView.fullScroll(View.FOCUS_DOWN);
        }
    }
}
