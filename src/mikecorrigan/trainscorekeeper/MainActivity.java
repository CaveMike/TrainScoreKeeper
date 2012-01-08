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
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements Rules {
	final private String TAG = this.getClass().getSimpleName();

	// State
	private List<ScoreEvent> scoreEvents;
	private int lastScoreEvent;

	final static int[] colors = { R.id.buttonRed, R.id.buttonGreen, R.id.buttonBlue, R.id.buttonYellow, R.id.buttonBlack };
	final static int[] colorStringIds = { R.string.red, R.string.green, R.string.blue, R.string.yellow, R.string.black };
	private Map<Integer, String> colorStrings;

	// UI
	private TableLayout tableLayoutColors;

	private LinearLayout linearLayoutTrains;
	private LinearLayout linearLayoutContractsCompleted;
	private LinearLayout linearLayoutBonuses;
	private LinearLayout linearLayoutContractsMissed;

	private GridView gridViewTrains;
	private GridView gridViewContractsCompleted;
	private GridView gridViewBonuses;
	private GridView gridViewContractsMissed;

	private int selectedColor;
	private Map<Integer, ToggleButton> colorButtons;
	private CheckBox lastTurnButton;
	private TextView historyView;

	// Database
	final private String DATABASE = "database.db";
	final private String TABLE = "events";
	final private String FIELD_TYPE = "type";
	final private String FIELD_VALUE = "value";
	final private String FIELD_DESCRIPTION = "description";
	final private String FIELD_COLOR = "color";
	final private String CREATE_TABLE = "CREATE TABLE " + TABLE + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_TYPE + " INTEGER, " + FIELD_VALUE + " INTEGER, " + FIELD_DESCRIPTION + " TEXT, " + FIELD_COLOR + " INTEGER);";
	final private String DROP_TABLE = "DROP TABLE " + TABLE;

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

	private void help() {
		About.show(this);
	}

	private void updateUi() {
		final int visibleA = isLastTurnComplete() ? View.GONE : View.VISIBLE;
		final int visibleB = isLastTurnComplete() ? View.VISIBLE : View.GONE;
		final int visibleC = selectedColor == -1 ? View.VISIBLE : View.INVISIBLE;

		final boolean enabledA = selectedColor != -1;

		for (int color : colors) {
			if (color != selectedColor) {
				colorButtons.get(color).setVisibility(visibleC);
			}
			colorButtons.get(color).setText(Integer.toString(getScore(color)));
		}

		linearLayoutTrains.setVisibility(visibleA);
		enableViewRecursive(linearLayoutTrains, enabledA);
		enableGridView(gridViewTrains, enabledA);

		linearLayoutContractsCompleted.setVisibility(visibleA);
		enableViewRecursive(linearLayoutContractsCompleted, enabledA);
		enableGridView(gridViewContractsCompleted, enabledA);

		linearLayoutBonuses.setVisibility(visibleB);
		enableViewRecursive(linearLayoutBonuses, enabledA);
		enableGridView(gridViewBonuses, enabledA);

		linearLayoutContractsMissed.setVisibility(visibleB);
		enableViewRecursive(linearLayoutContractsMissed, enabledA);
		enableGridView(gridViewContractsMissed, enabledA);

		updateHistoryView();
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
			logEntry = String.format(getString(R.string.history_entry), colorStrings.get(scoreEvent.getColor()), scoreEvent.getLongDescription(getApplicationContext()), score );
			historyView.append(logEntry);
		}
	}

	private void enableViewRecursive(ViewGroup view, boolean enabled) {
		for (int i = 0; i < view.getChildCount(); i++) {
			View child = view.getChildAt(i);
			child.setEnabled(enabled);

			if (child instanceof ViewGroup) {
				enableViewRecursive((ViewGroup)child, enabled);
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
			ScoreEvent scoreEvent = new ScoreEvent(selectedColor, scoreButton.getScoreSpec());

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
		for (int i = 1; i < tableLayoutColors.getChildCount(); i++) {
			TableRow row = (TableRow) tableLayoutColors.getChildAt(i);
			int col = 0;

			Button button = (Button) row.getChildAt(col++);
			int color = button.getId();

			int score = getScoreByType(color, ScoreEvent.TYPE.TRAIN);
			TextView textView = (TextView) row.getChildAt(col++);
			textView.setText(Integer.toString(score));

			score = getScoreByType(color, ScoreEvent.TYPE.COMPLETED_CONTRACT) + getScoreByType(color, ScoreEvent.TYPE.MISSED_CONTRACT);
			textView = (TextView) row.getChildAt(col++);
			textView.setText(Integer.toString(score));

			score = getScoreByType(color, ScoreEvent.TYPE.STATION) + getScoreByType(color, ScoreEvent.TYPE.BONUS);
			textView = (TextView) row.getChildAt(col++);
			textView.setText(Integer.toString(score));

			score = getScore(color);
			button.setText(Integer.toString(score));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//Eula.show(this);
		final Resources res = getResources();

		scoreEvents = new LinkedList<ScoreEvent>();

		colorStrings = new HashMap<Integer, String>();
		for (int i = 0; i < colors.length; i++) {
			int color = colors[i];
			colorStrings.put(color, getString(colorStringIds[i]));
		}

		// Color buttons
		colorButtons = new HashMap<Integer, ToggleButton>();
		for (int color : colors) {
			ToggleButton button = (ToggleButton) findViewById(color);
			colorButtons.put(color, button);
			button.setOnClickListener(onSelectColor);
		}

		// Buttons
		lastTurnButton = (CheckBox) findViewById(R.id.buttonLastTurnComplete);
		lastTurnButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateUi();
			}
		});

		historyView = (TextView) findViewById(R.id.history);

		tableLayoutColors = (TableLayout) findViewById(R.id.tableLayoutColors);

		// Trains
		List<View> buttons = new LinkedList<View>();
		for (int i = 0; i < trainPoints.length; i++) {
			if (trainPoints[i] >= 0) {
				String text = String.format(res.getQuantityString(R.plurals.button_trains, i+1), (i+1), trainPoints[i]);
				Button button = new ScoreButton(this, text, new ScoreSpec(ScoreEvent.TYPE.TRAIN, trainPoints[i],
						Integer.toString(i+1)));
				button.setOnClickListener(onScoreEvent);
				buttons.add(button);
			}
		}

		linearLayoutTrains = (LinearLayout) findViewById(R.id.linearLayoutTrains);

		gridViewTrains = (GridView) findViewById(R.id.gridViewTrains);
		gridViewTrains.setAdapter(new ViewAdapter(this, buttons));

		// Contracts Completed
		buttons = new LinkedList<View>();
		for (int i : trainContracts) {
			String text = String.format(getString(R.string.button_contracts_complete), i, i);
			Button button = new ScoreButton(this, text, new ScoreSpec(ScoreEvent.TYPE.COMPLETED_CONTRACT, i, Integer.toString(i)));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		linearLayoutContractsCompleted = (LinearLayout) findViewById(R.id.linearLayoutContractsCompleted);

		gridViewContractsCompleted = (GridView) findViewById(R.id.gridViewContractsCompleted);
		gridViewContractsCompleted.setAdapter(new ViewAdapter(this, buttons));

		// Contracts Missed
		buttons = new LinkedList<View>();
		for (int i : trainContracts) {
			String text = String.format(getString(R.string.button_contracts_missed), i, -i);
			Button button = new ScoreButton(this, text, new ScoreSpec(ScoreEvent.TYPE.MISSED_CONTRACT, -i, Integer.toString(i)));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		linearLayoutContractsMissed = (LinearLayout) findViewById(R.id.linearLayoutContractsMissed);

		gridViewContractsMissed = (GridView) findViewById(R.id.gridViewContractsMissed);
		gridViewContractsMissed.setAdapter(new ViewAdapter(this, buttons));

		// Bonuses
		buttons = new LinkedList<View>();
		for (int i = 1; i <= numTrainStations; i++) {
			String text = String.format(res.getQuantityString(R.plurals.button_train_stations, i), i, trainStationValue * i);
			Button button = new ScoreButton(this, text, new ScoreSpec(ScoreEvent.TYPE.STATION, trainStationValue * i,
					Integer.toString(i)));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		for (int i = 0; i < bonusValue.length; i++) {
			String text = String.format(getString(bonusButtonText[i]), i, bonusValue[i]);
			Button button = new ScoreButton(this, text, new ScoreSpec(ScoreEvent.TYPE.BONUS, bonusValue[i],
					getString(bonusText[i])));
			button.setOnClickListener(onScoreEvent);
			buttons.add(button);
		}

		linearLayoutBonuses = (LinearLayout) findViewById(R.id.linearLayoutBonuses);

		gridViewBonuses = (GridView) findViewById(R.id.gridViewBonuses);
		gridViewBonuses.setAdapter(new ViewAdapter(this, buttons));

		// Reset the state and UI.
		newGame();

		restoreUi(savedInstanceState);
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
			.setNegativeButton(R.string.no, onNewGameConfirmDialog).show();
			return true;
		case R.id.undo:
			undo();
			return true;
		case R.id.redo:
			redo();
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

	private void saveUi(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}

		savedInstanceState.putInt(BUNDLE_SELECTED_COLOR, selectedColor);
		savedInstanceState.putBoolean(BUNDLE_LAST_TURN_COMPLETE, isLastTurnComplete());
	}

	private void restoreUi(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}

		selectedColor = savedInstanceState.getInt(BUNDLE_SELECTED_COLOR);
		lastTurnButton.setChecked(savedInstanceState.getBoolean(BUNDLE_LAST_TURN_COMPLETE));

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
		//Log.v(TAG, "write");

		// Remove any undo events.
		if (lastScoreEvent != scoreEvents.size()) {
			scoreEvents = scoreEvents.subList(0, lastScoreEvent);
		}

		SQLiteDatabase database;

		try {
			//Log.v(TAG, "write open");
			database = openOrCreateDatabase(DATABASE, SQLiteDatabase.CREATE_IF_NECESSARY, null);
			database.setLocale(Locale.getDefault());
			database.setLockingEnabled(true);
			database.setVersion(1);
			//Log.v(TAG, "write complete");
		} catch (SQLiteException e) {
			//Log.v(TAG, "Failed to open saved settings.");
			e.printStackTrace();
			return;
		}

		try {
			//Log.v(TAG, "write drop table");
			database.execSQL(DROP_TABLE);
		} catch (SQLiteException e) {
			// This is fine if the table does not exist.
			//Log.i(TAG, "Saved game does not exist.");
		}

		try {
			//Log.v(TAG, "write create table");
			database.execSQL(CREATE_TABLE);

			for (ScoreEvent scoreEvent : scoreEvents) {
				ContentValues values = new ContentValues();

				values.put(FIELD_TYPE, scoreEvent.getType().toInt());
				values.put(FIELD_VALUE, scoreEvent.getValue());
				values.put(FIELD_DESCRIPTION, scoreEvent.getParam());
				values.put(FIELD_COLOR, scoreEvent.getColor());

				//Log.v(TAG, "write event=" + values);
				database.insertOrThrow(TABLE, null, values);
			}
		} catch (SQLiteException e) {
			Log.e(TAG, "Failed to write saved game.");
			e.printStackTrace();
		} finally {
			//Log.v(TAG, "write close");
			database.close();
		}
	}

	private void read() {
		//Log.v(TAG, "read");

		scoreEvents.clear();
		lastScoreEvent = 0;

		SQLiteDatabase database = null;

		try {
			//Log.v(TAG, "read open");
			database = openOrCreateDatabase(DATABASE, SQLiteDatabase.OPEN_READONLY, null);
			database.setLocale(Locale.getDefault());
			database.setLockingEnabled(true);
			database.setVersion(1);
			//Log.v(TAG, "read open complete");
		} catch (SQLiteException e) {
			Log.e(TAG, "Failed to open saved settings.");
			return;
		}

		try {
			Cursor c = database.query(TABLE, null, null, null, null, null, null);
			startManagingCursor(c);

			c.moveToFirst();
			while (!c.isAfterLast()) {
				TYPE type = TYPE.fromInt(c.getInt(1));
				int value = c.getInt(2);
				String description = c.getString(3);
				int color = c.getInt(4);

				ScoreEvent scoreEvent = new ScoreEvent(color, new ScoreSpec(type, value, description));
				//Log.v(TAG, "read event=" + scoreEvent);
				scoreEvents.add(scoreEvent);
				c.moveToNext();
			}

			stopManagingCursor(c);

			lastScoreEvent = scoreEvents.size();
		} catch (SQLiteException e) {
			Log.e(TAG, "Failed to read saved game.");
			e.printStackTrace();
		} finally {
			//Log.v(TAG, "read close");
			database.close();
		}
	}

	private void addTestData()
	{
		scoreEvents.add( new ScoreEvent(R.id.buttonRed, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonRed, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 7, "4")));
		scoreEvents.add( new ScoreEvent(R.id.buttonRed, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonRed, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add( new ScoreEvent(R.id.buttonRed, new ScoreSpec(ScoreSpec.TYPE.COMPLETED_CONTRACT, 10, "10")));

		scoreEvents.add( new ScoreEvent(R.id.buttonGreen, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonGreen, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add( new ScoreEvent(R.id.buttonGreen, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add( new ScoreEvent(R.id.buttonGreen, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 10, "5")));
		scoreEvents.add( new ScoreEvent(R.id.buttonGreen, new ScoreSpec(ScoreSpec.TYPE.COMPLETED_CONTRACT, 16, "16")));

		scoreEvents.add( new ScoreEvent(R.id.buttonBlue, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlue, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 7, "4")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlue, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlue, new ScoreSpec(ScoreSpec.TYPE.COMPLETED_CONTRACT, 9, "9")));

		scoreEvents.add( new ScoreEvent(R.id.buttonYellow, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonYellow, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonYellow, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add( new ScoreEvent(R.id.buttonYellow, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 15, "6")));
		scoreEvents.add( new ScoreEvent(R.id.buttonYellow, new ScoreSpec(ScoreSpec.TYPE.COMPLETED_CONTRACT, 6, "6")));

		scoreEvents.add( new ScoreEvent(R.id.buttonBlack, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 2, "2")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlack, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlack, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 7, "4")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlack, new ScoreSpec(ScoreSpec.TYPE.TRAIN, 4, "3")));
		scoreEvents.add( new ScoreEvent(R.id.buttonBlack, new ScoreSpec(ScoreSpec.TYPE.COMPLETED_CONTRACT, 12, "12")));

		Collections.shuffle(scoreEvents);
		lastScoreEvent = scoreEvents.size();

		updateScores();
		updateUi();
	}
}
