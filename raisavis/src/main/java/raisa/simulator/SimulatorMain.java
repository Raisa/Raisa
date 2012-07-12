package raisa.simulator;

import raisa.util.Vector2D;

public class SimulatorMain {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Rover rover =new Rover(new Vector2D(0, 0), 0, new DifferentialDrive());
		while(true) {
			float timestep = 0.01f;
			Thread.sleep((int) timestep * 1000);
			rover.tick(timestep);
		}
	}

}
