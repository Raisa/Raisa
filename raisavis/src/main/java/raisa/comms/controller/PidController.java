package raisa.comms.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import raisa.comms.Communicator;
import raisa.comms.ControlMessage;
import raisa.config.VisualizerConfig;
import raisa.domain.WorldModel;
import raisa.domain.plan.MotionPlan;
import raisa.domain.plan.Route;
import raisa.domain.plan.Waypoint;
import raisa.domain.robot.Robot;
import raisa.domain.robot.RobotState;
import raisa.domain.robot.RobotStateListener;
import raisa.util.GeometryUtil;
import raisa.util.Vector2D;

public class PidController extends Controller implements RobotStateListener {

	private WorldModel world;
	private BasicController basicController;
	private List<Communicator> communicators = new ArrayList<Communicator>();
	
	private int leftSpeed;
	private int rightSpeed;
	
	private static final float Kp = 1.0f;
	private static final float Ki = 0.2f;
	private static final float Kd = 1.0f;
	
	private float prevError = 0.0f;
	private float accError = 0.0f;
	
	private int stateCounter = 0;
	
	public PidController(WorldModel world, BasicController basicController, Communicator ... communicators) {		
		this.world = world;
		this.basicController = basicController;
		this.communicators.addAll(Arrays.asList(communicators));
		world.addRobotStateListener(this);
	}
	
	@Override
	public boolean getLights() {
		return basicController.getLights();
	}

	@Override
	public int getLeftSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRightSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPanServoAngle() {
		return basicController.getPanServoAngle();
	}

	@Override
	public int getTiltServoAngle() {
		return basicController.getTiltServoAngle();
	}

	
	private void sendPackage() {
		for(Communicator communicator : communicators) {
			communicator.sendPackage(createPackage());
		}
	}

	private ControlMessage createPackage() {
		return new ControlMessage(
				leftSpeed, 
				rightSpeed, 
				basicController.getLights(), 
				basicController.getPanServoAngle(), 
				basicController.getTiltServoAngle(), 
				false, 
				basicController.getServos());
	}

	
	@Override
	public boolean getServos() {
		return basicController.getServos();
	}

	@Override
	public void robotStateChanged(Robot newRobot) {
		if (ControllerTypeEnum.PID_CONTROLLER != VisualizerConfig.getInstance().getControllerType()) {
			return;
		}
		if (stateCounter++ % 10 != 0) {
			return;
		}
		RobotState robotState = newRobot.getMeasuredState();
		Vector2D waypointPosition = null;

		MotionPlan motionPlan = world.getMotionPlan();
		Route route = motionPlan.getSelectedRoute();
		boolean foundNextWaypoint = false;
		while (!foundNextWaypoint) {		
			Waypoint nextWaypoint = route.getNextWaypoint();
			if (nextWaypoint == null) {
				break;
			}
			waypointPosition = nextWaypoint.getPosition();
			if (waypointPosition.distance(robotState.getPosition()) < 10.0f) {
				route.moveToNextWaypoint();
				nextWaypoint.setReached(true);
			} else {
				foundNextWaypoint = true;
			}
		}
		if (!foundNextWaypoint) {
			leftSpeed = 0;
			rightSpeed = 0;
			sendPackage();
			return;
		}
		
		float robotHeading = GeometryUtil.headingToAtan2Angle(robotState.getHeading());
		float waypointHeading = (float)Math.atan2(
				(float)(waypointPosition.y - robotState.getPosition().y),
				(float)(waypointPosition.x - robotState.getPosition().x));
		
		float error = GeometryUtil.differenceBetweenAngles(robotHeading, waypointHeading);
		float errorDot = error - prevError;
		accError = accError + error;
		
		float control = Kp * error + Kd * errorDot + Ki * accError;
		int gearChange;
		if (control > Math.PI) {
			gearChange = 3;
		} else if (control < -Math.PI) {
			gearChange = -3;
		} else {
			gearChange = (int)(3 * control / Math.PI);
		}
		
		leftSpeed = 2 + gearChange;
		rightSpeed = 2 - gearChange;		
		
		System.out.println("control: " + control +  ", left: " + leftSpeed + ", right: " + rightSpeed);
		
		sendPackage();
	}

}
