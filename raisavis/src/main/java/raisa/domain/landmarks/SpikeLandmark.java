package raisa.domain.landmarks;

import raisa.util.Vector2D;

public class SpikeLandmark extends Landmark {

	private static final long serialVersionUID = 1L;

	public SpikeLandmark(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public Vector2D getPosition() {
		return new Vector2D(x, y);
	}
	
	@Override
	public void merge(Landmark landmark) {
		if (!(landmark instanceof SpikeLandmark)) {
			return;
		}
		SpikeLandmark mergedLandmark = (SpikeLandmark)landmark;
		this.x = (this.x * life + mergedLandmark.x * mergedLandmark.life) / (life + mergedLandmark.life);
		this.y = (this.y * life + mergedLandmark.y* mergedLandmark.life) / (life + mergedLandmark.life);
	}

	@Override
	public boolean isTrusted() {
		return life > 2;
	}
	
	@Override
	public float getAssociationThreshold() {
		return 30.0f;
	}
	
}
