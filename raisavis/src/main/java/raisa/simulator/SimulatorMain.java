package raisa.simulator;

import raisa.util.Vector2D;

public class SimulatorMain {

	public static void main(String[] args) throws InterruptedException {
		DifferentialDrive drive = new DifferentialDrive(0.20f, 1);
		Rover rover = new Rover(new Vector2D(0, 0), 0, drive);
		drive.setLeftWheelSpeed(0.5f);
		drive.setRightWheelSpeed(0.2f);
		while(true) {
			float timestep = 0.01f;
			Thread.sleep((int) timestep * 1000);
			rover.tick(timestep);
		}
	}

}
