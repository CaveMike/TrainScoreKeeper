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

import android.content.Context;

public class ScoreSpec {
	public enum TYPE {
		INVALID(0),
		TRAIN(1),
		COMPLETED_CONTRACT(2),
		MISSED_CONTRACT(3),
		STATION(4),
		BONUS(5);

		private final int v;

		private TYPE(int v) {
			this.v = v;
		}

		public int toInt() {
			return v;
		}

		static public TYPE fromInt(int v) {
			for (TYPE e  : values()) {
				if (e.v == v) {
					return e;
				}
			}

			return INVALID;
		}
	}

	final private TYPE type;
	final private int value;
	final private String param;

	public ScoreSpec(TYPE type, int value, String param) {
		this.value = value;
		this.type = type;
		this.param = param;
	}

	public boolean isType(TYPE type) {
		return type.equals(this.type);
	}

	public TYPE getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	public String getParam() {
		return param;
	}

	public String getLongDescription(Context context) {
		String format = new String();

		switch (type) {
		case TRAIN: format = context.getString(R.string.history_train); break;
		case COMPLETED_CONTRACT: format = context.getString(R.string.history_contract_completed); break;
		case MISSED_CONTRACT: format = context.getString(R.string.history_contract_missed); break;
		case BONUS: format = context.getString(R.string.history_bonus); break;
		case STATION: format = context.getString(R.string.history_station); break;
		}

		return String.format(format, param, value);
	}

	@Override
	public String toString() {
		return "type=" + type + ", value=" + value + ", param=" + param;
	}
}