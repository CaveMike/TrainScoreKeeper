//   Copyright 2012-2014 Michael T. Corrigan
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static boolean VERBOSE = true;

    private final static String KEY_GAME_SPEC = "spec";

    private final static int START_ACTIVITY_PREFERENCES = 1;

    // Model
    private Game game;

    // Control
    private Players players;

    // Views
    private ViewPager viewPager;
    private LinearLayout playersLayout;

    public Game getGame() {
        return game;
    }

    public Players getPlayers() {
        return players;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onCreate: savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);

        // Set up the content and get references to the views.
        setContentView(R.layout.activity_tab);
        playersLayout = (LinearLayout) findViewById(R.id.linear_layout_players);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // Set up preferences.
        PreferenceManager.setDefaultValues(this /* context */, R.xml.preferences, true);
        PreferenceManager.getDefaultSharedPreferences(this /* context */)
                .registerOnSharedPreferenceChangeListener(mPrefsListener);

        // Get the game spec.
        final String spec = getSpecFromIntent(getIntent(), Game.DEFAULT_GAME_SPEC);

        create(spec);

        restoreUi(savedInstanceState);
    }

    private void create(final String assetName) {
        Log.vc(VERBOSE, TAG, "create: assetName=" + assetName);

        JSONObject jsonRoot = JsonUtils.readFromAssets(this /* context */, assetName);
        if (jsonRoot == null) {
            Log.e(TAG,  "create: failed to read spec");
            return;
        }

        try {
            JSONArray jsonTabs = jsonRoot.getJSONArray(JsonSpec.TABS_KEY);

            createViewPager(jsonTabs);
            createActionBar(jsonTabs);
        } catch (JSONException e) {
            Log.th(TAG, e, "create: failed to parse JSON");
        }

        createPlayers(jsonRoot);
        createGame(assetName, jsonRoot);
    }

    private void createPlayers(final JSONObject jsonRoot) {
        Log.vc(VERBOSE, TAG, "createControls: jsonRoot=" + jsonRoot);

        JSONArray jsonPlayers = null;
        try {
            jsonPlayers = jsonRoot.getJSONArray(JsonSpec.PLAYERS_KEY);
        } catch (JSONException e) {
            Log.th(TAG, e, "createPlayers: failed to parse JSON");
        }

        players = new Players(this /* context */, playersLayout, jsonPlayers);
    }

    private void createGame(final String assetName, final JSONObject jsonRoot) {
        Log.vc(VERBOSE, TAG, "createModel: assetName=" + assetName + ", jsonRoot=" + jsonRoot);

        game = new Game(this /* context */, assetName, players.getNum());
        game.addListener(players);

        // Reset the state and UI.
        game.resetGame();
    }

    private void createViewPager(final JSONArray jsonTabs) {
        Log.vc(VERBOSE, TAG, "createViewPager: jsonTabs=" + jsonTabs);

        // Initialize the PageAdapter.
        XPagerAdapter pagerAdapter = new XPagerAdapter(this /* context */,
                getSupportFragmentManager(), jsonTabs);

        // Initialize the ViewPager.
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });
    }

    private void createActionBar(final JSONArray jsonTabs) {
        Log.vc(VERBOSE, TAG, "createActionBar: jsonTabs=" + jsonTabs);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.removeAllTabs();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.TabListener tabListener = new XTabListener(viewPager);

        // Add the button tabs to the ActionBar.
        int i;
        for (i = 0; i < jsonTabs.length(); i++) {
            JSONObject jsonTab = jsonTabs.optJSONObject(i);
            if (jsonTab == null) {
                continue;
            }

            final String name = jsonTab.optString(JsonSpec.TAB_NAME, JsonSpec.DEFAULT_TAB_NAME);

            actionBar.addTab(actionBar.newTab().setText(name)
                    .setTabListener(tabListener));
        }

        // Add the special tabs to the ActionBar.
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.summary))
                .setTabListener(tabListener));
        i++;
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.history))
                .setTabListener(tabListener));
        i++;
    }

    @Override
    public void onResume() {
        Log.vc(VERBOSE, TAG, "onResume");
        super.onResume();

        players.clearSelection();

        game.read(this /* context */);
    }

    @Override
    public void onPause() {
        Log.vc(VERBOSE, TAG, "onPause");
        super.onPause();

        game.write(this /* context */);
    }

    @Override
    protected void onDestroy() {
        Log.vc(VERBOSE, TAG, "onDestroy");
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this /* context */)
                .unregisterOnSharedPreferenceChangeListener(mPrefsListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.vc(VERBOSE, TAG, "onCreateOptionsMenu: menu=" + menu);

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.vc(VERBOSE, TAG, "onPrepareOptionsMenu: menu=" + menu);

        super.onPrepareOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.new_game);
        item.setEnabled(true);

        item = menu.findItem(R.id.undo);
        item.setEnabled(undoEnabled());

        item = menu.findItem(R.id.redo);
        item.setEnabled(redoEnabled());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.vc(VERBOSE, TAG, "onOptionsItemSelected: item=" + item);

        switch (item.getItemId()) {
            case R.id.new_game:
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this);
                builder.setMessage(R.string.new_game_prompt)
                        .setPositiveButton(R.string.yes, onNewGameConfirmDialog)
                        .setNegativeButton(R.string.no, onNewGameConfirmDialog)
                        .show();
                return true;
            case R.id.undo:
                undo();
                return true;
            case R.id.redo:
                redo();
                return true;
            case R.id.preferences:
                preferences();
                return true;
            case R.id.help:
                help();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean undoEnabled() {
        return game.undoEnabled();
    }

    private void undo() {
        Log.vc(VERBOSE, TAG, "undo");

        if (game.undoEnabled()) {
            game.undo();
            players.clearSelection();
        }
    }

    private boolean redoEnabled() {
        return game.redoEnabled();
    }

    private void redo() {
        Log.vc(VERBOSE, TAG, "redo");

        if (game.redoEnabled()) {
            game.redo();
            players.clearSelection();
        }
    }

    private void preferences() {
        Log.vc(VERBOSE, TAG, "preferences");

        final Intent intent = new Intent(this /* context */, Preferences.class);
        startActivityForResult(intent, START_ACTIVITY_PREFERENCES);
    }

    private void help() {
        Log.vc(VERBOSE, TAG, "help");

        About.show(this /* activity */);
    }

    private final DialogInterface.OnClickListener onNewGameConfirmDialog = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.vc(VERBOSE, TAG, "onNewGameConfirmDialog");

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startNewGameActivity();
                    break;
            }
        }
    };

    private void startNewGameActivity() {
        Log.vc(VERBOSE, TAG, "startNewGameActivity");

        Intent intent = new Intent(MainActivity.this /* context */, ActivityNewGame.class);
        startActivity(intent);

        game.newGame(this /* context */);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.vc(VERBOSE, TAG, "onActivityResult: requestCode=" + requestCode +
                ", resultCode=" + resultCode + ", intent=" + intent);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private String getSpecFromIntent(final Intent intent, String defaultSpec) {
        Log.vc(VERBOSE, TAG,  "getSpecFromIntent: intent=" + intent + ", defaultSpec=" + defaultSpec);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this /* context */);

        // Attempt to read the game spec from shared preferences.
        // If the preference does not exist, use the default spec passed in as a parameter.
        defaultSpec = sp.getString(Preferences.PREFS_KEY_GAME_SPEC, defaultSpec);

        if (intent == null) {
            Log.d(TAG, "getSpecFromIntent: no intent");
            return defaultSpec;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "getSpecFromIntent: no extras");
            return defaultSpec;
        }

        String spec = extras.getString(KEY_GAME_SPEC);
        Log.d(TAG,  "getSpecFromIntent: spec=" + spec);

        if (TextUtils.isEmpty(spec)) {
            Log.d(TAG, "getSpecFromIntent: no spec");
            return defaultSpec;
        }


        // Save the game spec to shared preferences.
        // This is only necessary if the activity was launched with an explicit game spec.
        sp.edit().putString(Preferences.PREFS_KEY_GAME_SPEC, spec).commit();

        return spec;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onRestoreInstanceState: savedInstanceState="
                + Utils.bundleToString(savedInstanceState));

        saveUi(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void saveUi(Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "saveUi: savedInstanceState="
                + Utils.bundleToString(savedInstanceState));

        if (savedInstanceState == null) {
            return;
        }

        players.saveUi(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onRestoreInstanceState: savedInstanceState="
                + Utils.bundleToString(savedInstanceState));

        super.onRestoreInstanceState(savedInstanceState);
        restoreUi(savedInstanceState);
    }

    private void restoreUi(Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "restoreUi: savedInstanceState="
                + Utils.bundleToString(savedInstanceState));

        if (savedInstanceState == null) {
            return;
        }

        players.restoreUi(savedInstanceState);
    }

    private OnSharedPreferenceChangeListener mPrefsListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.vc(VERBOSE, TAG, "onSharedPreferenceChanged: sharedPrefs=" + sharedPreferences
                    + ", key=" + key);
        }
    };

    public OnClickListener getScoreClickListener() {
        return mScoreClick;
    }

    private OnClickListener mScoreClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.vc(VERBOSE, TAG, "onScoreClick: view=" + view);

            if (!players.hasSelection()) {
                Log.w(TAG, "Ignoring button press since a player is not selected");
                return;
            }

            // Create the event.
            final ScoreButton scoreButton = (ScoreButton) view;
            final ScoreEvent scoreEvent =
                    new ScoreEvent(players.getSelection(), players.getSelectionName(),
                            scoreButton.getButtonSpec());

            // Add the event to the model.
            game.addEvent(scoreEvent);

            // Clear the checked button.
            players.clearSelection();
        }
    };

    public void enablePlayers(boolean enabled) {
        if (playersLayout == null) {
            return;
        }

        int visibility = enabled ? View.VISIBLE : View.GONE;
        playersLayout.setVisibility(visibility);
    }
}
