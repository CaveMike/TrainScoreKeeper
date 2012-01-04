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

public class ScoreEvent extends ScoreSpec {
	private int color;

	public ScoreEvent(int color, ScoreSpec scoreSpec) {
		super(scoreSpec.getType(), scoreSpec.getValue(), scoreSpec.getParam());
		this.color = color;
	}

	public boolean isColor(int color) {
		return color == this.color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "color=" + color + super.toString();
	}
}