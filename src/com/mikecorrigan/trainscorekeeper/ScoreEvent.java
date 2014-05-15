//   Copyright 2012 Michael T. Corrigan
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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ScoreEvent {
    private final static String TAG = ScoreEvent.class.getSimpleName();
    private final static boolean VERBOSE = false;

    final private int playerId;
    final private String playerName;
    final private int type;
    final private int value;
    final private String history;

    // Create a score event from a button click.
    public ScoreEvent(int playerId, final String playerName, JSONObject jsonButton) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.type = jsonButton.optInt(JsonSpec.BUTTON_TYPE, JsonSpec.DEFAULT_BUTTON_TYPE);
        this.value = jsonButton.optInt(JsonSpec.BUTTON_VALUE, JsonSpec.DEFAULT_BUTTON_VALUE);
        this.history = jsonButton.optString(JsonSpec.BUTTON_HISTORY,
                JsonSpec.DEFAULT_BUTTON_HISTORY);

        Log.vc(VERBOSE, TAG, "ctor: " + this);
    }

    // Create a score event from a JSON object (from a saved game).
    public ScoreEvent(JSONObject jsonScoreEvent) {
        this.playerId = jsonScoreEvent.optInt(JsonSpec.SCORE_ID, JsonSpec.DEFAULT_SCORE_ID);
        this.playerName = jsonScoreEvent
                .optString(JsonSpec.SCORE_NAME, JsonSpec.DEFAULT_SCORE_NAME);
        this.type = jsonScoreEvent.optInt(JsonSpec.SCORE_TYPE, JsonSpec.DEFAULT_SCORE_TYPE);
        this.value = jsonScoreEvent.optInt(JsonSpec.SCORE_VALUE, JsonSpec.DEFAULT_SCORE_VALUE);
        this.history = jsonScoreEvent.optString(JsonSpec.SCORE_HISTORY,
                JsonSpec.DEFAULT_SCORE_HISTORY);

        Log.vc(VERBOSE, TAG, "ctor: " + this);
    }

    public boolean isPlayerId(int playerId) {
        return playerId == this.playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public String formatHistory(Context context, int score) {
        String action = String.format(history, null, value);
        Log.e(TAG, "history=" + history + ", action=" + action + ", value=" + value);
        String format = String.format(context.getString(R.string.history_entry), playerName,
                action, score);
        return format;
    }

    public JSONObject toJson() {
        Log.vc(VERBOSE, TAG, "toJson");

        JSONObject jsonScoreEvent = new JSONObject();

        try {
            jsonScoreEvent.put(JsonSpec.SCORE_ID, playerId);
            jsonScoreEvent.put(JsonSpec.SCORE_NAME, playerName);
            jsonScoreEvent.put(JsonSpec.SCORE_TYPE, type);
            jsonScoreEvent.put(JsonSpec.SCORE_VALUE, value);
            jsonScoreEvent.put(JsonSpec.SCORE_HISTORY, history);
        } catch (JSONException e) {
            Log.e(TAG, "toJson: failed: e=" + e);
            return null;
        }

        return jsonScoreEvent;
    }

    @Override
    public String toString() {
        return "ScoreEvent [playerId=" + playerId +", playerName=" + playerName + ", type="
                + type + ", value=" + value + ", history=" + history + "]";
    }
}
