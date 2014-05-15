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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FragmentSummary extends Fragment {
    private final static String TAG = FragmentSummary.class.getSimpleName();
    private final static boolean VERBOSE = true;

    private final static String ARG_INDEX = "index";
    private final static String ARG_TAB_SPEC = "tabSpec";

    // Model
    private Game game;

    // Controls
    private Players players;

    // View
    private TableLayout tableLayout;

    public static FragmentSummary newInstance(int index, JSONObject jsonSpec) {
        Log.vc(VERBOSE, TAG, "newInstance: jsonTab=" + jsonSpec);

        FragmentSummary fragment = new FragmentSummary();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_TAB_SPEC, jsonSpec.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onCreateView: inflater=" + inflater + ", container=" + container +
                ", savedInstanceState=" + Utils.bundleToString(savedInstanceState));

        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

        final MainActivity activity = (MainActivity) getActivity();
        final Context context = activity;
        final Resources resources = context.getResources();

        // Get the model and attach a listener.
        game = activity.getGame();
        if (game != null) {
            game.addListener(mGameListener);
        }

        players = activity.getPlayers();

        // Get resources.
        String[] playerNames = resources.getStringArray(R.array.playerNames);

        TypedArray drawablesArray = resources.obtainTypedArray(R.array.playerDrawables);

        TypedArray playerTextColorsArray = resources.obtainTypedArray(R.array.playerTextColors);
        int[] playerTextColorsIds = new int[playerTextColorsArray.length()];
        for (int i = 0; i < playerTextColorsArray.length(); i++) {
            playerTextColorsIds[i] = playerTextColorsArray.getResourceId(i, -1);
        }

        // Get root view.
        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);

        // Create table.
        tableLayout = new TableLayout(context);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tableLayout.setLayoutParams(tableLayoutParams);
        scrollView.addView(tableLayout);

        // Add header.
        {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tableLayout.addView(row);

            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(resources.getString(R.string.player));
            tv.setTypeface(null, Typeface.BOLD);
            row.addView(tv);

            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(resources.getString(R.string.trains));
            tv.setTypeface(null, Typeface.BOLD);
            row.addView(tv);

            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(resources.getString(R.string.contracts));
            tv.setTypeface(null, Typeface.BOLD);
            row.addView(tv);

            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            tv.setText(resources.getString(R.string.bonuses));
            tv.setTypeface(null, Typeface.BOLD);
            row.addView(tv);

        }

        // Add rows.
        for (int i = 0; i < players.getNum(); i++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tableLayout.addView(row);

            ToggleButton toggleButton = new ToggleButton(context);
            toggleButton.setGravity(Gravity.CENTER);
            toggleButton.setPadding(10, 10, 10, 10);
            toggleButton.setText(playerNames[i]);
            toggleButton.setClickable(false);
            Drawable drawable = drawablesArray.getDrawable(i);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                toggleButton.setBackgroundDrawable(drawable);
            } else {
                toggleButton.setBackground(drawable);
            }
            toggleButton.setTextColor(resources.getColor(playerTextColorsIds[i]));
            row.addView(toggleButton);

            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            row.addView(tv);

            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            row.addView(tv);

            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10, 10, 10, 10);
            row.addView(tv);

        }

        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "onCreateView: missing arguments");
            return rootView;
        }

        drawablesArray.recycle();
        playerTextColorsArray.recycle();

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

        if (tableLayout == null) {
            Log.w(TAG, "updateUi: no table, this=" + this);
            return;
        }

        // Subtract one to skip the header in the allocation.
        final int numRows = tableLayout.getChildCount() - 1;
        final TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
        final int numCols = headerRow.getChildCount();

        String[] header = new String[numCols];
        int[][] scores = new int[numRows][numCols];

        game.getSummary(header, scores);

        for (int c = 0; c < numCols; c++) {
            TextView textView = (TextView) headerRow.getChildAt(c);
            textView.setText(header[c]);
        }

        for (int r = 0; r < numRows; r++) {
            // Add one to the row index to skip the header.
            final TableRow row = (TableRow) tableLayout.getChildAt(r + 1);

            for (int c = 0; c < numCols; c++) {
                TextView textView = (TextView) row.getChildAt(c);
                textView.setText(Integer.toString(scores[r][c]));
            }
        }
    }
}
