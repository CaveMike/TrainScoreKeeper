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

public interface Rules {
	final static int[] trainPoints = { 1, 2, 4, 7, 10, 15, -1, 21, 27 };
	final static int[] trainContracts = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22 };
	final static int numTrainStations = 3;
	final static int trainStationValue = 4;
	final static int[] bonusText = { R.string.history_longest_train, R.string.history_globetrotter };
	final static int[] bonusButtonText = { R.string.button_longest_train, R.string.button_globetrotter };
	final static int[] bonusValue = { 10, 15 };
}
