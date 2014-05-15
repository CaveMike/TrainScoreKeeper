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

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mikecorrigan.trainscorekeeper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Game {
    private final static String TAG = Game.class.getSimpleName();
    private final static boolean VERBOSE = false;

    private final static String SAVED_FILE = "saved0.txt";
    public final static String DEFAULT_GAME_SPEC = "rules/test";

    public interface Listener {
        public void onEnabled(int playerId, boolean enabled);

        public void onScoreChanged(int playerId, int score);
    }

    private final Context context;
    private String spec;
    private final int numPlayers;
    private final Set<Listener> listeners;
    private List<ScoreEvent> scoreEvents;
    private int lastScoreEvent;

    public Game(final Context context, final String spec, int numPlayers) {
        Log.vc(VERBOSE, TAG, "ctor");

        this.context = context;
        this.spec = spec;
        this.numPlayers = numPlayers;
        listeners = new HashSet<Listener>();
        scoreEvents = new LinkedList<ScoreEvent>();
        lastScoreEvent = 0;
    }

    public void addListener(Listener listener) {
        Log.vc(VERBOSE, TAG, "addListener: listener=" + listener);
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        Log.vc(VERBOSE, TAG, "removeListener: listener=" + listener);
        listeners.remove(listener);
    }

    private void notifyEnabled(int playerId, boolean enabled) {
        Log.vc(VERBOSE, TAG, "notifyListeners: playerId=" + playerId + ", enabled=" + enabled);

        for (Listener listener : listeners) {
            listener.onEnabled(playerId, enabled);
        }
    }

    private void notifyListeners(int playerId) {
        Log.vc(VERBOSE, TAG, "notifyListeners: playerId=" + playerId);

        for (Listener listener : listeners) {
            int score = getScore(playerId);
            listener.onScoreChanged(playerId, score);
        }
    }

    private void notifyListeners() {
        Log.vc(VERBOSE, TAG, "notifyListeners");

        for (int i = 0; i < numPlayers; i++) {
            notifyListeners(i);
        }
    }

    private int getScore(int playerId) {
        int score = 0;

        final List<ScoreEvent> subList = scoreEvents.subList(0, lastScoreEvent);
        for (final ScoreEvent scoreEvent : subList) {
            if (scoreEvent.isPlayerId(playerId)) {
                score += scoreEvent.getValue();
            }
        }

        Log.vc(VERBOSE, TAG, "getScore: playerId=" + playerId + ", score=" + score);
        return score;
    }

    public void resetGame() {
        Log.vc(VERBOSE, TAG, "resetGame");

        scoreEvents.clear();
        lastScoreEvent = 0;

        notifyListeners();
    }

    public void newGame(final Context context) {
        Log.vc(VERBOSE, TAG, "newGame");

        deleteSavedGame(context);
        resetGame();
    }

    public boolean isNewGame() {
        return scoreEvents.size() == 0;
    }

    public boolean undoEnabled() {
        return lastScoreEvent > 0;
    }

    public void undo() {
        Log.vc(VERBOSE, TAG, "undo");

        if (undoEnabled()) {
            lastScoreEvent -= 1;
            notifyListeners();
        }
    }

    public boolean redoEnabled() {
        return lastScoreEvent < scoreEvents.size();
    }

    public void redo() {
        Log.vc(VERBOSE, TAG, "redo");

        if (redoEnabled()) {
            lastScoreEvent += 1;
            notifyListeners();
        }
    }

    private void clearUndo() {
        Log.vc(VERBOSE, TAG, "clearUndo");

        if (lastScoreEvent != scoreEvents.size()) {
            scoreEvents = scoreEvents.subList(0, lastScoreEvent);
        }
    }

    public void addEvent(ScoreEvent scoreEvent) {
        Log.vc(VERBOSE, TAG, "addEvent: scoreEvent=" + scoreEvent);

        // Remove any undo events.
        clearUndo();

        scoreEvents.add(scoreEvent);
        lastScoreEvent = scoreEvents.size();

        notifyListeners(scoreEvent.getPlayerId());
    }

    public void getSummary(String[] header, int[][] scores) {
        Log.vc(VERBOSE, TAG, "getSummary: header.length=" + header.length
                + ", scores.length=" + scores.length);

        if (scores.length != numPlayers) {
            Log.e(TAG, "getSummary: invalid scores length=" + header.length
                    + ", expected=" + numPlayers);
            return;
        }

        if (scores[0].length != 4) {
            Log.e(TAG, "getSummary: invalid scores length=" + header.length);
            return;
        }

        if (header.length != 4) {
            Log.e(TAG, "getSummary: invalid header length=" + header.length);
            return;
        }

        ;
        header[0] = context.getString(R.string.player);
        header[1] = context.getString(R.string.trains);
        header[2] = context.getString(R.string.contracts);
        header[3] = context.getString(R.string.bonuses);

        final List<ScoreEvent> subList = scoreEvents.subList(0, lastScoreEvent);
        for (final ScoreEvent scoreEvent : subList) {
            int r = scoreEvent.getPlayerId();
            if (r < 0 || r > scores.length) {
                continue;
            }

            int c = typeToColumn(scoreEvent.getType());
            if (c < 0 || c > scores[r].length) {
                continue;
            }

            scores[r][0] += scoreEvent.getValue();
            scores[r][c] += scoreEvent.getValue();
        }
    }

    private int typeToColumn(int type) {
        switch (type) {
            case 0:
                return 1;
            case 1:
            case 2:
                return 2;
            case 3:
            case 4:
                return 3;
            default:
                return -1;
        }
    }

    public String getHistory() {
        Log.vc(VERBOSE, TAG, "getHistory");

        StringBuffer buffer = new StringBuffer();

        // Create an empty array of scores.
        final int[] scores = new int[numPlayers];

        for (final ScoreEvent scoreEvent : scoreEvents.subList(0, lastScoreEvent)) {
            scores[scoreEvent.getPlayerId()] += scoreEvent.getValue();

            final String logEntry = scoreEvent.formatHistory(context,
                    scores[scoreEvent.getPlayerId()]);
            buffer.append(logEntry);
        }

        return buffer.toString();
    }

    public boolean read(final Context context) {
        Log.vc(VERBOSE, TAG, "read: context=" + context);

        String path = context.getFilesDir() + File.separator + SAVED_FILE;
        File file = new File(path);

        JSONObject jsonRoot = JsonUtils.readFromFile(file);
        if (jsonRoot == null) {
            Log.e(TAG, "read: failed to read root");
            return false;
        }

        scoreEvents.clear();
        lastScoreEvent = 0;

        try {
            spec = jsonRoot.getString(JsonSpec.GAME_SPEC);
            JSONArray jsonScoreEvents = jsonRoot.getJSONArray(JsonSpec.SCORES_KEY);

            for (int i = 0; i < jsonScoreEvents.length(); i++) {
                JSONObject jsonScoreEvent = jsonScoreEvents.getJSONObject(i);
                ScoreEvent scoreEvent = new ScoreEvent(jsonScoreEvent);
                scoreEvents.add(scoreEvent);

                notifyEnabled(scoreEvent.getPlayerId(), true);
            }
        } catch (JSONException e) {
            Log.th(TAG, e, "read: parse failed");
            return false;
        }

        lastScoreEvent = scoreEvents.size();

        notifyListeners();

        return true;
    };

    public boolean write(final Context context) {
        Log.vc(VERBOSE, TAG, "write: context=" + context);

        // Ignore any undo events.
        List<ScoreEvent> temp = scoreEvents = scoreEvents.subList(0, lastScoreEvent);

        // Convert the list of events to a JSON array, then convert to a string.
        String string = null;
        try {
            JSONArray jsonScoreEvents = new JSONArray();
            for (ScoreEvent scoreEvent : temp) {
                JSONObject jsonScoreEvent = scoreEvent.toJson();
                if (jsonScoreEvent == null) {
                    Log.w(TAG, "write: skipping score event");
                    continue;
                }

                jsonScoreEvents.put(jsonScoreEvents.length(), jsonScoreEvent);
            }

            JSONObject jsonRoot = new JSONObject();
            jsonRoot.put(JsonSpec.SCORES_KEY, jsonScoreEvents);
            jsonRoot.put(JsonSpec.GAME_SPEC, spec);

            string = jsonRoot.toString();
        } catch (JSONException e) {
            Log.th(TAG, e, "write: failed");
            return false;
        }

        // Write the string into a file.
        if (string != null) {
            String path = context.getFilesDir() + File.separator + SAVED_FILE;
            File file = new File(path);
            FileUtils.writeStringToFile(file, string);
        }

        return true;
    }

    private boolean deleteSavedGame(final Context context) {
        Log.vc(VERBOSE, TAG, "deleteSavedGame: context=" + context);

        String path = context.getFilesDir() + File.separator + SAVED_FILE;
        File file = new File(path);
        return file.delete();
    }

    @Override
    public String toString() {
        return "Game [listeners=" + listeners + ", scoreEvents=" + scoreEvents
                + ", lastScoreEvent=" + lastScoreEvent + "]";
    }
}
