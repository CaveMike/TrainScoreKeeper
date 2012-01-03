package train.scorekeeper;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

public class ScoreButton extends Button {
	private final ScoreSpec scoreSpec;

	public ScoreButton(Context context, String text, ScoreSpec scoreSpec) {
		super(context);
		this.scoreSpec = scoreSpec;

		setText(text);

		if (scoreSpec.getValue() < 0) {
			setTextColor(Color.RED);
		}
	}

	public ScoreSpec getScoreSpec() {
		return scoreSpec;
	}
}