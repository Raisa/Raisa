package raisa.simulator;

import raisa.domain.Robot;
import raisa.domain.WorldModel;
import raisa.util.Vector2D;

public class SimulatorMain {

	public static void main(String[] args) throws InterruptedException {
		DifferentialDrive drive = new DifferentialDrive(Robot.ROBOT_WIDTH, Robot.WHEEL_DIAMETER);
		
		WorldModel worldModel = new WorldModel();
		worldModel.loadMap("data/sightseeing1.png");
		RobotSimulator rover = new RobotSimulator(new Vector2D(0, 0), 0, drive, worldModel);
		drive.setLeftWheelSpeed(0.5f);
		drive.setRightWheelSpeed(0.2f);
		while(true) {
			float timestep = 0.01f;
			Thread.sleep((int) timestep * 1000);
			rover.tick(timestep);
		}
	}

}
