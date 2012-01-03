package train.scorekeeper;

public interface Rules {
	final static int[] trainPoints = { 1, 2, 4, 7, 10, 15, -1, 21 };
	final static int[] trainContracts = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, /*14, 15,*/ 16, 17, /*18, 19,*/ 20, 21, 22 };
	final static int numTrainStations = 3;
	final static int trainStationValue = 4;
	final static int[] bonusText = { R.string.history_longest_train };
	final static int[] bonusButtonText = { R.string.button_longest_train };
	final static int[] bonusValue = { 10 };
}
