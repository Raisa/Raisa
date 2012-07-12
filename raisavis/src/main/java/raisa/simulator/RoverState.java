package raisa.simulator;

import raisa.util.Vector2D;

public interface RoverState {
	Vector2D getPosition();

	void setPosition(Vector2D position);

	/**
	 * degrees
	 */
	int getHeading();

	void setHeading(int heading);

}
