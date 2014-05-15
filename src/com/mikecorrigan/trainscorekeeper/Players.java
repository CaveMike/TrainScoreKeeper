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

import java.util.HashSet;
import java.util.Set;

import mikecorrigan.trainscorekeeper.R;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class Players implements Game.Listener {
    private final static String TAG = Players.class.getSimpleName();
    private final static boolean VERBOSE = false;

    public interface Listener {
        public void onSelectionChanged(int playerId);
    }

    final private String BUNDLE_SELECTED_ID = "selectedId";

    private final Set<Listener> listeners;
    private final SparseArray<Player> players;
    private int selectedId = -1;

    @SuppressLint("NewApi")
    public Players(final Activity context, ViewGroup viewGroup, final JSONArray jsonPlayers) {
        Log.vc(VERBOSE, TAG, "ctor: context=" + context + ", viewGroup=" + viewGroup +
                ", jsonPlayers=" + jsonPlayers);

        viewGroup.removeAllViews();

        Resources resources = context.getResources();

        String[] playerNames = resources.getStringArray(R.array.playerNames);

        TypedArray drawablesArray = resources.obtainTypedArray(R.array.playerDrawables);

        TypedArray colorIdsArray = resources.obtainTypedArray(R.array.playerTextColors);
        int[] colorIds = new int[colorIdsArray.length()];
        for (int i = 0; i < colorIdsArray.length(); i++) {
            colorIds[i] = colorIdsArray.getResourceId(i, -1);
        }

        listeners = new HashSet<Listener>();
        players = new SparseArray<Player>();

        for (int i = 0; i < playerNames.length; i++) {
            PlayerButton toggleButton = new PlayerButton(context);
            toggleButton.setPlayerId(i);
            LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
            toggleButton.setLayoutParams(layoutParams);
            Drawable drawable = drawablesArray.getDrawable(i);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                toggleButton.setBackgroundDrawable(drawable);
            } else {
                toggleButton.setBackground(drawable);
            }
            toggleButton.setPadding(10, 10, 10, 10);
            toggleButton.setTextColor(resources.getColor(colorIds[i]));
            toggleButton.setOnClickListener(onSelect);

            viewGroup.addView(toggleButton);

            final String name = playerNames[i];

            boolean enabled = true;

            // If the players configuration is available, determine which players are enabled.
            if (jsonPlayers != null) {
                enabled = false;
                for (int j = 0; j < jsonPlayers.length(); j++) {
                    String jsonName = jsonPlayers.optString(j, "");
                    if (jsonName.equalsIgnoreCase(name)) {
                        enabled = true;
                        break;
                    }
                }
            }

            Player player = new Player(toggleButton, i, name, enabled);
            players.put(i, player);
        }

        setSelection(-1);

        drawablesArray.recycle();
        colorIdsArray.recycle();
    }

    public void addListener(Listener listener) {
        Log.vc(VERBOSE, TAG, "addListener: listener=" + listener);
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        Log.vc(VERBOSE, TAG, "removeListener: listener=" + listener);
        listeners.remove(listener);
    }

    private void notifyListeners() {
        Log.vc(VERBOSE, TAG, "notifyListeners");

        for (Listener listener : listeners) {
            listener.onSelectionChanged(selectedId);
        }
    }

    public int getNum() {
        return players.size();
    }

    public boolean hasSelection() {
        return selectedId != -1;
    }

    public int getSelection() {
        return selectedId;
    }

    private void setSelection(int selectedId) {
        this.selectedId = selectedId;
        update();
        notifyListeners();
    }

    public void clearSelection() {
        Log.vc(VERBOSE, TAG, "clearSelection");

        if (selectedId != -1) {
            players.get(selectedId).setChecked(false);
        }

        setSelection(-1);
    }

    public String getSelectionName() {
        if (selectedId == -1) {
            return "";
        }

        return players.get(selectedId).getName();
    }

    public void saveUi(Bundle bundle) {
        bundle.putInt(BUNDLE_SELECTED_ID, selectedId);
        Log.vc(VERBOSE, TAG, "saveUi: bundle=" + Utils.bundleToString(bundle));
    }

    public void restoreUi(Bundle bundle) {
        Log.vc(VERBOSE, TAG, "restoreUi: bundle=" + Utils.bundleToString(bundle));
        setSelection(bundle.getInt(BUNDLE_SELECTED_ID));
    }

    private void update() {
        Log.vc(VERBOSE, TAG, "update");

        for (int id = 0; id < players.size(); id++) {
            Player player = players.get(id);
            player.update(selectedId);
        }
    }

    private final View.OnClickListener onSelect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.vc(VERBOSE, TAG, "onSelect: view=" + view);

            PlayerButton playerButton = (PlayerButton) view;
            int id = playerButton.getPlayerId();

            if (selectedId == -1) {
                setSelection(id);
                players.get(id).setChecked(true);
            } else {
                setSelection(-1);
                players.get(id).setChecked(false);
            }
        }
    };

    @Override
    public void onEnabled(int playerId, boolean enabled) {
        Log.vc(VERBOSE, TAG, "onEnabled: playerId=" + playerId + ", enabled=" + enabled);

        Player player = players.get(playerId);
        if (player != null) {
            player.setEnabled(enabled);
            player.update(selectedId);
        }
    }

    @Override
    public void onScoreChanged(int playerId, int score) {
        Log.vc(VERBOSE, TAG, "onScoreChanged: playerId=" + playerId + ", score=" + score);

        Player player = players.get(playerId);
        if (player != null) {
            player.setScore(score);
        }
    }

    @Override
    public String toString() {
        return "Players [listeners=" + listeners + ", players=" + players + ", selectedId="
                + selectedId + "]";
    }
}
