package raisa.simulator;

import raisa.util.Vector2D;

public interface SimulatorState {
	Vector2D getPosition();

	void setPosition(Vector2D position);

	/**
	 * degrees
	 */
	float getHeading();

	void setHeading(float heading);

}
