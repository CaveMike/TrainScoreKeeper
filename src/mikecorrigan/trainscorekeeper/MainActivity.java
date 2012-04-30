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
package mikecorrigan.trainscorekeeper;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mikecorrigan.trainscorekeeper.ScoreSpec.TYPE;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements Rules, OnSharedPreferenceChangeListener {
	final private String TAG = this.getClass().getSimpleName();

	// State
	private List<ScoreEvent> scoreEvents;
	private int lastScoreEvent;

	final static int[] colors = { R.id.buttonRed, R.id.buttonGreen,
		R.id.buttonBlue, R.id.buttonYellow, R.id.buttonBlack, R.id.buttonPurple, R.id.buttonWhite };
	final static int[] colorStringIds = { R.string.red, R.string.green,
		R.string.blue, R.string.yellow, R.string.black, R.string.purple, R.string.white };
	private Map<Integer, String> colorStrings;
	private Map<Integer, Boolean> colorEnabled;

	// UI
	private TableLayout tableLayoutColors;

	private TextView textViewTrains;
	private TextView textViewContractsCompleted;
	private TextView textViewBonuses;
	private TextView textViewContractsMissed;

	private GridView gridViewTrains;
	private GridView gridViewContractsCompleted;
	private GridView gridViewBonuses;
	private GridView gridViewContractsMissed;

	private int selectedColor;
	private Map<Integer, ToggleButton> colorButtons;
	private CheckBox lastTurnButton;
	private TextView historyView;
	private ScrollView scrollView;

	// Database
	final private String DATABASE = "database.db";
	final private String TABLE_EVENTS = "events";
	final private String FIELD_TYPE = "type";
	final private String FIELD_VALUE = "value";
	final private String FIELD_DESCRIPTION = "description";
	final private String FIELD_COLOR = "color";
	final private String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS
			+ " ( id INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_TYPE
			+ " INTEGER, " + FIELD_VALUE + " INTEGER, " + FIELD_DESCRIPTION
			+ " TEXT, " + FIELD_COLOR + " INTEGER);";
	final private String DROP_TABLE_EVENTS = "DROP TABLE " + TABLE_EVENTS;
	final private int START_ACTIVITY_PREFERENCES = 1;

	// Bundle
	final private String BUNDLE_SELECTED_COLOR = "selectedColor";
	final private String BUNDLE_LAST_TURN_COMPLETE = "lastTurnComplete";

	private boolean isLastTurnComplete() {
		return lastTurnButton.isChecked();
	}

	private boolean newGameEnabled() {
		return scoreEvents.size() > 0 || isLastTurnComplete();
	}

	private void newGame() {
		scoreEvents.clear();
		lastScoreEvent = 0;
		selectedColor = -1;
		lastTurnButton.setChecked(false);
		updateUi();
		updateScores();
	}

	private boolean undoEnabled() {
		return lastScoreEvent > 0;
	}

	private void undo() {
		if (undoEnabled()) {
			lastScoreEvent -= 1;
			updateUi();
			updateScores();
		}
	}

	private boolean redoEnabled() {
		return lastScoreEvent < scoreEvents.size();
	}

	private void redo() {
		if (redoEnabled()) {
			lastScoreEvent += 1;
			updateUi();
			updateScores();
		}
	}

	private void preferences() {
		Intent intent = new Intent(this, Preferences.class);
		startActivityForResult(intent, START_ACTIVITY_PREFERENCES);
	}

	private void help() {
		About.show(this);
	}

	private void updateUi() {
		final int visibleA = isLastTurnComplete() ? View.GONE : View.VISIBLE;
		final int visibleB = isLastTurnComplete() ? View.VISIBLE : View.GONE;
		final int visibleC = selectedColor == -1 ? View.VISIBLE
				: View.INVISIBLE;

		final boolean enabledA = selectedColor != -1;

		for (int color : colors) {
			ToggleButton colorButton = colorButtons.get(color);
			View parent = (View) colorButton.getParent();

			final int visibleD = colorEnabled.get(color) ? View.VISIBLE : View.GONE;

			if (parent instanceof TableRow) {
				parent.setVisibility(visibleD);
			} else {
				colorButton.setVisibility(visibleD);
			}

			if (visibleD == View.GONE ) {
				continue;
			}

			if (color != selectedColor) {
				colorButton.setVisibility(visibleC);
			}
			colorButton.setText(Integer.toString(getScore(color)));
		}

		enableGridView(gridViewTrains, enabledA);
		gridViewTrains.setVisibility(visibleA);

		enableGridView(gridViewContractsCompleted, enabledA);
		gridViewContractsCompleted.setVisibility(visibleA);

		enableGridView(gridViewBonuses, enabledA);
		gridViewBonuses.setVisibility(visibleB);

		enableGridView(gridViewContractsMissed, enabledA);
		gridViewContractsMissed.setVisibility(visibleB);

		if (textViewTrains != null) {
			textViewTrains.setVisibility(visibleA);
		}

		if (textViewContractsCompleted != null) {
			textViewContractsCompleted.setVisibility(visibleA);
		}

		if (textViewBonuses != null) {
			textViewBonuses.setVisibility(visibleB);
		}

		if (textViewContractsMissed != null) {
			textViewContractsMissed.setVisibility(visibleB);
		}

		if (historyView != null) {
			updateHistoryView();
		}
	}

	private void updateHistoryView() {
		Map<Integer, Integer> scores = new HashMap<Integer, Integer>();
		for (int color : colors) {
			scores.put(color, 0);
		}

		historyView.setText(null);
		for (ScoreEvent scoreEvent : scoreEvents.subList(0, lastScoreEvent)) {
			int score = scores.get(scoreEvent.getColor());
			score += scoreEvent.getValue();
			scores.put(scoreEvent.getColor(), score);

			String logEntry;
			logEntry = String.format(getString(R.string.history_entry),
					colorStrings.get(scoreEvent.getColor()),
					scoreEvent.getLongDescription(getApplicationContext()),
					score);
			historyView.append(logEntry);
		}

		if (scrollView != null) {
			scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		}
	}

	private void enableViewRecursive(ViewGroup view, boolean enabled) {
		for (int i = 0; i < view.getChildCount(); i++) {
			View child = view.getChildAt(i);
			child.setEnabled(enabled);

			if (child instanceof ViewGroup) {
				enableViewRecursive((ViewGroup) child, enabled);
			}
		}
	}

	private void enableGridView(GridView gridView, boolean enabled) {
		ViewAdapter adapter = (ViewAdapter) gridView.getAdapter();
		adapter.setEnabled(enabled);
	}

	private View.OnClickListener onSelectColor = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			if (selectedColor == -1) {
				selectedColor = view.getId();
			} else {
				selectedColor = -1;
			}
			updateUi();
		}
	};

	private View.OnClickListener onScoreEvent = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ((selectedColor == -1)) {
				throw new InvalidParameterException("Invalid color selected");
			}

			// Remove any undo events.
			if (lastScoreEvent != scoreEvents.size()) {
				scoreEvents = scoreEvents.subList(0, lastScoreEvent);
			}

			// Create the event.
			ScoreButton scoreButton = (ScoreButton) view;
			ScoreEvent scoreEvent = new ScoreEvent(selectedColor,
					scoreButton.getScoreSpec());

			// Update the state.
			scoreEvents.add(scoreEvent);
			lastScoreEvent = scoreEvents.size();

			// Clear the checked button.
			colorButtons.get(selectedColor).setChecked(false);
			selectedColor = -1;

			// Update the UI.
			updateUi();
			updateScores();
		}
	};

	private int getScore(int color) {
		int score = 0;

		List<ScoreEvent> subList = scoreEvents.subList(0, lastScoreEvent);
		for (ScoreEvent scoreEvent : subList) {
			if (scoreEvent.isColor(color)) {
				score += scoreEvent.getValue();
			}
		}

		return score;
	}

	private int getScoreByType(int color, ScoreEvent.TYPE type) {
		int score = 0;

		List<ScoreEvent> subList = scoreEvents.subList(0, lastScoreEvent);
		for (ScoreEvent scoreEvent : subList) {
			if (scoreEvent.isType(type) && scoreEvent.isColor(color)) {
				score += scoreEvent.getValue();
			}
		}

		return score;
	}

	private void updateScores() {
		if (tableLayoutColors == null) {
			updateScoresSimple();
		} else {
			updateScoresTable();
		}
	}

	private void updateScoresSimple() {
		for (int color : colors) {
			Button button = (Button) findViewById(color);
			button.setText(Integer.toString(getScore(color)));
		}
	}

	private void updateScoresTable() {
		for (int i = 1; i < tableLayoutColors.getChildCount(); i++) {
			TableRow row = (TableRow) tableLayoutColors.getChildAt(i);
			int numCols = row.getChildCount();
			int col = 0;

			Button button = (Button) row.getChildAt(col++);
			int color = button.getId();
			button.setText(Integer.toString(getScore(color)));

			if (numCols > 1) {
				int score = getScoreByType(color, ScoreEvent.TYPE.TRAIN);
				TextView textView = (TextView) row.getChildAt(col++);
				textView.setText(Integer.toString(score));

				score = getScoreByType(color,
						ScoreEvent.TYPE.COMPLETED_CONTRACT)
						+ getScoreByType(color, ScoreEvent.TYPE.MISSED_CONTRACT);
				textView = (TextView) row.getChildAt(col++);
				textView.setText(Integer.toString(score));

				score = getScoreByType(color, ScoreEvent.TYPE.STATION)
						+ getScoreByType(color, ScoreEvent.TYPE.BONUS);
				textView = (TextView) row.getChildAt(col++);
				textView.setText(Integer.toString(score));
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Eula.show(this);
		final Resources res = getResources();

		//FIXME: Hack to lock smaller screens (phones) into landscape mode.
		//       This is necessary because the portrait layout does not fit
		//       on all screens.
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		if ((metrics.heightPixels < 600) || (metrics.widthPixels < 600)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

		scoreEvents = new LinkedList<ScoreEvent>();

		colorStrings = new HashMap<Integer, String>();
		for (int i = 0; i < colors.length; i++) {
			int color = colors[i];
			colorStrings.put(color, getString(colorStringIds[i]));
		}

		colorEnabled = new HashMap<Integer, Boolean>();
		readPreferences();

		// Color buttons
		colorButtons = new HashMap<Integer, ToggleButton>();
		for (int color : colors) {
			ToggleButton button = (ToggleButton) findViewById(color);
			colorButtons.put(color, button);
			button.setOnClickListener(onSelectColor);
		}

		// Buttons
		lastTurnButton = (CheckBox) findViewById(R.id.buttonLastTurnComplete);
		lastTurnButton
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				updateUi();
			}
		});

		historyView = (TextView) findViewById(R.id.history);
		scrollView = (ScrollView) findViewById(R.id.scroller);

		tableLayoutColors = (TableLayout) findViewById(R.id.tableLayoutColors);

		// Trains
		List<View> buttons = new LinkedList<View>();
		for (int i = 0; i < trainPoints.length; i++) {
			if (trainPoints[i] >= 0) {
				String text = String.format(
						res.getQuantityString(R.plurals.button_trains, i + 1),
						(i + 1), trainPoints[i]);
				Button button = new ScoreButton(this, text, new ScoreSpec(
						ScoreEvent.TYPE.TRAIN, trainPoints[i],
						Integer.toString(i + 1)));
				button.setOnClickListener(onScoreEvent);
				buttons.add(button);
			}
		}

		textViewTrains = (TextView) findViewById(R.id.textViewTrains);

		gridViewTrains = (GridView) findViewById(R.id.gridViewTrains);
		gridViewTrains.setAdapter(new ViewAdapter(this, buttons));

		// Contracts Completed
		buttons = new LinkedList<View>();
		for (int i : trainContracts) {
			String text = String.format(
					getString(R.string.button_contracts_complete), i, i);
			Button button = new ScoreButton(this, text, new ScoreSpec(
					ScoreEvent.TYPE.COMPLETED_CONTRACT, i, Integer.toString(i)));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		textViewContractsCompleted = (TextView) findViewById(R.id.textViewContractsCompleted);

		gridViewContractsCompleted = (GridView) findViewById(R.id.gridViewContractsCompleted);
		gridViewContractsCompleted.setAdapter(new ViewAdapter(this, buttons));

		// Contracts Missed
		buttons = new LinkedList<View>();
		for (int i : trainContracts) {
			String text = String.format(
					getString(R.string.button_contracts_missed), i, -i);
			Button button = new ScoreButton(this, text, new ScoreSpec(
					ScoreEvent.TYPE.MISSED_CONTRACT, -i, Integer.toString(i)));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		textViewContractsMissed = (TextView) findViewById(R.id.textViewContractsMissed);

		gridViewContractsMissed = (GridView) findViewById(R.id.gridViewContractsMissed);
		gridViewContractsMissed.setAdapter(new ViewAdapter(this, buttons));

		// Bonuses
		buttons = new LinkedList<View>();
		for (int i = 1; i <= numTrainStations; i++) {
			String text = String.format(
					res.getQuantityString(R.plurals.button_train_stations, i),
					i, trainStationValue * i);
			Button button = new ScoreButton(this, text, new ScoreSpec(
					ScoreEvent.TYPE.STATION, trainStationValue * i,
					Integer.toString(i)));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		for (int i = 0; i < bonusValue.length; i++) {
			String text = String.format(getString(bonusButtonText[i]), i,
					bonusValue[i]);
			Button button = new ScoreButton(this, text, new ScoreSpec(
					ScoreEvent.TYPE.BONUS, bonusValue[i],
					getString(bonusText[i])));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		textViewBonuses = (TextView) findViewById(R.id.textViewBonuses);

		gridViewBonuses = (GridView) findViewById(R.id.gridViewBonuses);
		gridViewBonuses.setAdapter(new ViewAdapter(this, buttons));

		// Reset the state and UI.
		newGame();

		restoreUi(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		write();
	}

	@Override
	public void onResume() {
		super.onResume();
		read();
		updateUi();
		updateScores();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem item = menu.findItem(R.id.new_game);
		item.setEnabled(newGameEnabled());

		item = menu.findItem(R.id.undo);
		item.setEnabled(undoEnabled());

		item = menu.findItem(R.id.redo);
		item.setEnabled(redoEnabled());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game:
			AlertDialog.Builder builder = new AlertDialog.Builder(
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

	private DialogInterface.OnClickListener onNewGameConfirmDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				newGame();
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + ", intent=" + intent);
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == START_ACTIVITY_PREFERENCES) {
			readPreferences();
			updateUi();
		}
	}

	private void saveUi(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}

		savedInstanceState.putInt(BUNDLE_SELECTED_COLOR, selectedColor);
		savedInstanceState.putBoolean(BUNDLE_LAST_TURN_COMPLETE,
				isLastTurnComplete());
	}

	private void restoreUi(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}

		selectedColor = savedInstanceState.getInt(BUNDLE_SELECTED_COLOR);
		lastTurnButton.setChecked(savedInstanceState
				.getBoolean(BUNDLE_LAST_TURN_COMPLETE));

		updateUi();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		saveUi(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreUi(savedInstanceState);
	}

	private void write() {
		// Log.v(TAG, "write");

		// Remove any undo events.
		if (lastScoreEvent != scoreEvents.size()) {
			scoreEvents = scoreEvents.subList(0, lastScoreEvent);
		}

		SQLiteDatabase database;

		try {
			// Log.v(TAG, "write open database");
			database = openOrCreateDatabase(DATABASE,
					SQLiteDatabase.CREATE_IF_NECESSARY, null);
			database.setLocale(Locale.getDefault());
			database.setLockingEnabled(true);
			database.setVersion(1);
			// Log.v(TAG, "write open database complete");
		} catch (SQLiteException e) {
			// Log.v(TAG, "Failed to open database.");
			e.printStackTrace();
			return;
		}

		try {
			// Log.v(TAG, "write drop events table");
			database.execSQL(DROP_TABLE_EVENTS);
		} catch (SQLiteException e) {
			// This is fine if the table does not exist.
			// Log.i(TAG, "Events table does not exist.");
		}

		try {
			// Log.v(TAG, "write create events table");
			database.execSQL(CREATE_TABLE_EVENTS);

			for (ScoreEvent scoreEvent : scoreEvents) {
				ContentValues values = new ContentValues();

				values.put(FIELD_TYPE, scoreEvent.getType().toInt());
				values.put(FIELD_VALUE, scoreEvent.getValue());
				values.put(FIELD_DESCRIPTION, scoreEvent.getParam());
				values.put(FIELD_COLOR, scoreEvent.getColor());

				// Log.v(TAG, "write event=" + values);
				database.insertOrThrow(TABLE_EVENTS, null, values);
			}
		} catch (SQLiteException e) {
			Log.e(TAG, "Failed to write events table.");
			e.printStackTrace();
		} finally {
			// Log.v(TAG, "write close database");
			database.close();
		}
	}

	private void read() {
		// Log.v(TAG, "read");

		scoreEvents.clear();
		lastScoreEvent = 0;

		SQLiteDatabase database = null;

		try {
			// Log.v(TAG, "read open database");
			database = openOrCreateDatabase(DATABASE,
					SQLiteDatabase.OPEN_READONLY, null);
			database.setLocale(Locale.getDefault());
			database.setLockingEnabled(true);
			database.setVersion(1);
			// Log.v(TAG, "read open database complete");
		} catch (SQLiteException e) {
			Log.e(TAG, "Failed to open database.");
			return;
		}

		// Read score events.
		try {
			Cursor c = database
					.query(TABLE_EVENTS, null, null, null, null, null, null);
			startManagingCursor(c);

			c.moveToFirst();
			while (!c.isAfterLast()) {
				TYPE type = TYPE.fromInt(c.getInt(1));
				int value = c.getInt(2);
				String description = c.getString(3);
				int color = c.getInt(4);

				ScoreEvent scoreEvent = new ScoreEvent(color, new ScoreSpec(
						type, value, description));
				// Log.v(TAG, "read event=" + scoreEvent);
				scoreEvents.add(scoreEvent);
				c.moveToNext();
			}

			stopManagingCursor(c);
			c.close();

			lastScoreEvent = scoreEvents.size();
		} catch (SQLiteException e) {
			// Log.d(TAG, "Failed to read events table.");
		} finally {
			// Log.v(TAG, "read close database");
			database.close();
		}
	}

	private void readPreferences() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

		for (int color : colors) {
			String colorString = colorStrings.get(color);
			boolean enabled = sp.getBoolean(colorString, false);
			Log.d(TAG, "readPreferences: color=" + colorString + ", enabled=" + enabled);
			colorEnabled.put(color, enabled);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Log.v(TAG, "onSharedPreferenceChanged: sharedPrefs=" + sharedPreferences + ", key=" + key);
	}

	private void addTestData() {
		scoreEvents.add(new ScoreEvent(R.id.buttonRed, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonRed, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 7, "4")));
		scoreEvents.add(new ScoreEvent(R.id.buttonRed, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonRed, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add(new ScoreEvent(R.id.buttonRed, new ScoreSpec(
				ScoreSpec.TYPE.COMPLETED_CONTRACT, 10, "10")));

		scoreEvents.add(new ScoreEvent(R.id.buttonGreen, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonGreen, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add(new ScoreEvent(R.id.buttonGreen, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add(new ScoreEvent(R.id.buttonGreen, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 10, "5")));
		scoreEvents.add(new ScoreEvent(R.id.buttonGreen, new ScoreSpec(
				ScoreSpec.TYPE.COMPLETED_CONTRACT, 16, "16")));

		scoreEvents.add(new ScoreEvent(R.id.buttonBlue, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlue, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 7, "4")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlue, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlue, new ScoreSpec(
				ScoreSpec.TYPE.COMPLETED_CONTRACT, 9, "9")));

		scoreEvents.add(new ScoreEvent(R.id.buttonYellow, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonYellow, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonYellow, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add(new ScoreEvent(R.id.buttonYellow, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 15, "6")));
		scoreEvents.add(new ScoreEvent(R.id.buttonYellow, new ScoreSpec(
				ScoreSpec.TYPE.COMPLETED_CONTRACT, 6, "6")));

		scoreEvents.add(new ScoreEvent(R.id.buttonBlack, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlack, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlack, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 7, "4")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlack, new ScoreSpec(
				ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add(new ScoreEvent(R.id.buttonBlack, new ScoreSpec(
				ScoreSpec.TYPE.COMPLETED_CONTRACT, 12, "12")));

		Collections.shuffle(scoreEvents);
		lastScoreEvent = scoreEvents.size();

		updateScores();
		updateUi();
	}
}
