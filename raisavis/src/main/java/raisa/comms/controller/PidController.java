package raisa.comms.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import raisa.comms.CameraResolution;
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

	private static final float HALF_PI = (float)(Math.PI / 2.0d);

	private final WorldModel world;
	private final BasicController basicController;
	private final List<Communicator> communicators = new ArrayList<Communicator>();

	private int leftSpeed;
	private int rightSpeed;

	private static final float Kp = 5.0f;
	private static final float Ki = 0.1f;
	private static final float Kd = 0.5f;

	private float prevError = 0.0f;
	private float accError = 0.0f;
	private boolean movingForward = true;
	private Vector2D currentWaypoint = null;

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
				basicController.getServos(),
				CameraResolution.NOCHANGE,
				true);
	}


	@Override
	public boolean getServos() {
		return basicController.getServos();
	}

	@Override
	public void robotStateChanged(Robot newRobot) {
		if (ControllerTypeEnum.PID_CONTROLLER != VisualizerConfig.getInstance().getControllerType() ||
				stateCounter++ % 5 != 0) {
			return;
		}
		RobotState robotState = newRobot.getMeasuredState();
		Vector2D waypointPosition = getNextWaypoint(robotState);
		if (waypointPosition == null) {
			leftSpeed = 0;
			rightSpeed = 0;
			accError = 0.0f;
			prevError = 0.0f;
			sendPackage();
			return;
		}
		if (currentWaypoint != waypointPosition) {
			currentWaypoint = waypointPosition;
			accError = 0.0f;
			prevError = 0.0f;
		}

		float error = calculateError(robotState, waypointPosition);
		if (Math.abs(error) > HALF_PI) {
			movingForward = !movingForward;
			error = calculateError(robotState, waypointPosition);
			accError = 0.0f;
			prevError = 0.0f;
		}
		float errorDot = 0.0f;
		if (prevError != 0.0f) {
			errorDot = error - prevError;
		}
		prevError = error;
		accError = accError + error;

		float control = Kp * error + Kd * errorDot + Ki * accError;
		int[] speedPowerMap = ControlMessage.getSpeedPowerMap();
		int baseSpeed = speedPowerMap[3];
		int gearChange;
		if (control > HALF_PI) {
			baseSpeed = 0;
			gearChange = speedPowerMap[1];
		} else if (control < -HALF_PI) {
			baseSpeed = 0;
			gearChange = -speedPowerMap[1];
		} else {
			gearChange = (int)((speedPowerMap[4] - speedPowerMap[3]) * control / HALF_PI);
		}
		leftSpeed = baseSpeed * (movingForward?1:-1) + gearChange;
		rightSpeed = baseSpeed * (movingForward?1:-1) - gearChange;
		sendPackage();
	}

	private float calculateError(RobotState robotState, Vector2D waypointPosition) {
		float movementConvertedHeading = robotState.getHeading();
		if (!movingForward) {
			movementConvertedHeading = (float)((movementConvertedHeading + Math.PI) % (2 * Math.PI));
		}
		float robotHeading = GeometryUtil.headingToAtan2Angle(movementConvertedHeading);
		float waypointHeading = (float)Math.atan2(
				waypointPosition.y - robotState.getPosition().y,
				waypointPosition.x - robotState.getPosition().x);
		float error = GeometryUtil.differenceBetweenAngles(robotHeading, waypointHeading);
		return error;
	}

	private Vector2D getNextWaypoint(RobotState robotState) {
		Vector2D ret = null;
		MotionPlan motionPlan = world.getMotionPlan();
		Route route = motionPlan.getSelectedRoute();
		boolean foundNextWaypoint = false;
		while (!foundNextWaypoint) {
			Waypoint nextWaypoint = route.getNextWaypoint();
			if (nextWaypoint == null) {
				break;
			}
			ret = nextWaypoint.getPosition();
			if (ret.distance(robotState.getPosition()) < 5.0f) {
				route.moveToNextWaypoint();
				nextWaypoint.setReached(true);
			} else {
				foundNextWaypoint = true;
			}
		}
		return ret;
	}

}
