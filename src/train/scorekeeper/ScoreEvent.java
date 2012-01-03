package train.scorekeeper;


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