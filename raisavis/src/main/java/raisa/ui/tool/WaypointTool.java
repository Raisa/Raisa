package raisa.ui.tool;

import java.awt.event.MouseEvent;

import raisa.domain.WorldModel;
import raisa.domain.plan.MotionPlan;
import raisa.domain.plan.Route;
import raisa.domain.plan.Waypoint;
import raisa.ui.VisualizerFrame;
import raisa.util.Vector2D;

public class WaypointTool extends BasicTool {

	private WorldModel world;
	 
	public WaypointTool(VisualizerFrame frame, WorldModel world) {
		super(frame);
		this.world = world;
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent, Vector2D mouse) {
		MotionPlan motionPlan = world.getMotionPlan();
		Route route = motionPlan.getSelectedRoute();
		Vector2D worldPosition = getVisualizerFrame().toWorld(mouse);
		route.addWaypoint(new Waypoint(worldPosition));
	}

}
