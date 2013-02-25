package raisa.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raisa.config.InputOutputTargetEnum;
import raisa.config.LocalizationModeEnum;
import raisa.config.VisualizerConfig;
import raisa.config.VisualizerConfigListener;
import raisa.domain.Grid;
import raisa.domain.WorldModel;
import raisa.domain.landmarks.Landmark;
import raisa.domain.landmarks.LineLandmark;
import raisa.domain.landmarks.SpikeLandmark;
import raisa.domain.landmarks.RansacExtractor;
import raisa.domain.particlefilter.Particle;
import raisa.domain.robot.Robot;
import raisa.domain.robot.RobotState;
import raisa.domain.samples.Sample;
import raisa.domain.samples.SampleListener;
import raisa.simulator.RobotSimulator;
import raisa.util.CollectionUtil;
import raisa.util.GeometryUtil;
import raisa.util.GraphicsUtil;
import raisa.util.Segment2D;
import raisa.util.Vector2D;

public class VisualizerPanel extends JPanel implements SampleListener, VisualizerConfigListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(VisualizerPanel.class);
	private Color measurementColor = new Color(0.4f, 0.4f, 0.4f);
	private Color particleColor = new Color(0.3f, 0.3f, 0.3f);
	private Color mapMarkerColor = new Color(0.8f, 0.2f, 0.2f);
	private Color trailMarkerColor = new Color(0.5f, 0.5f, 0.9f);
	private Vector2D camera = new Vector2D();
	private Vector2D mouse = new Vector2D();
	private Vector2D mouseDownPosition = new Vector2D();
	private Vector2D mouseDragStart = new Vector2D();
	private boolean mouseDragging = false;
	private float scale = 1.0f;
	private List<Sample> latestIR = new ArrayList<Sample>();
	private List<Sample> latestSR = new ArrayList<Sample>();
	private Stroke dashed;
	private Stroke arrow;
	private VisualizerFrame visualizerFrame;
	private WorldModel worldModel;
	private RobotSimulator robotSimulator;
	private BufferedImage currentImage;
	private PopupMenu popupMenu = new PopupMenu();
	
	public void reset() {
		worldModel.reset();
		camera = new Vector2D();
		mouse = new Vector2D();
		mouseDownPosition = new Vector2D();
		mouseDragStart = new Vector2D();
		scale = 1.0f;
		latestIR = new ArrayList<Sample>();
		latestSR = new ArrayList<Sample>();
	}

	@Override
	public void visualizerConfigChanged(VisualizerConfig config) {
		repaint();
	}
	
	public VisualizerPanel(VisualizerFrame frame, WorldModel worldModel, RobotSimulator robotSimulator) {
		this.visualizerFrame = frame;
		this.worldModel = worldModel;
		this.robotSimulator = robotSimulator;
		worldModel.addSampleListener(this);
		setBackground(Color.gray);
		setFocusable(true);
		addHierarchyBoundsListener(new PanelSizeHandler());
		addMouseMotionListener(new MouseMotionHandler());
		addMouseListener(new MouseHandler());
		addMouseWheelListener(new MouseWheelHandler());
		reset();
		dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 15.0f }, 0.0f);
		arrow = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
	}

	@Override
	public void sampleAdded(Sample sample) {
		RobotState latestState = worldModel.getLatestState().getEstimatedState();
		if (sample.isInfrared1MeasurementValid()) {
			Vector2D spotPosition = GeometryUtil.calculatePosition(latestState.getPosition(), latestState.getHeading() + sample.getInfrared1Angle(), sample.getInfrared1Distance());
			worldModel.setGridPosition(spotPosition, true);
		}
		if (sample.isInfrared2MeasurementValid()) {
			Vector2D spotPosition = GeometryUtil.calculatePosition(latestState.getPosition(), latestState.getHeading() + sample.getInfrared2Angle(), sample.getInfrared2Distance());
			worldModel.setGridPosition(spotPosition, true);
		}		
		if (sample.isInfrared1MeasurementValid() || sample.isInfrared2MeasurementValid()) {
			latestIR.add(sample);
			latestIR = CollectionUtil.takeLast(latestIR, 10);
		}
		if (sample.isUltrasound1MeasurementValid() || sample.isUltrasound2MeasurementValid()) {
			//grid.addSpot(sample.getSrSpot());
			latestSR.add(sample);
			latestSR = CollectionUtil.takeLast(latestSR, 10);
		}
		if (sample.getImage() != null) {
			currentImage = sample.getImage();
		}
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		clearScreen(g);
		Set<MapAreaElementEnum> mapAreaElements = VisualizerConfig.getInstance().getDisplayedMapAreaElements();
		if(mapAreaElements.contains(MapAreaElementEnum.MAP)) {
			drawGrid(g2);
		}
		if (VisualizerConfig.getInstance().getLocalizationMode() == LocalizationModeEnum.PARTICLE_FILTER
				&& mapAreaElements.contains(MapAreaElementEnum.PARTICLES)) {
			drawParticles(g2);
		}
		if(mapAreaElements.contains(MapAreaElementEnum.ROBOT_TRAIL)) {
			drawRobotTrail(g2, worldModel.getStates());
		}
		drawOriginArrows(g2);
		if(VisualizerConfig.getInstance().getInputOutputTarget().equals(InputOutputTargetEnum.REALTIME_SIMULATOR)) {
			drawRobotSimulator(g2);
		}
		if(mapAreaElements.contains(MapAreaElementEnum.ROBOT)) {
			drawRobot(g2);
			drawRobotDirectionArrow(g2);
		}
		if(mapAreaElements.contains(MapAreaElementEnum.ULTRASONIC_SCANNER)) {
			drawUltrasoundResults(g);
		}
		if(mapAreaElements.contains(MapAreaElementEnum.INFRARED_SCANNER)) {
			drawIrResults(g2);
		}
		Vector2D worldMouse = toWorld(mouse);
		if (mouseDragging) {
			g.setColor(measurementColor);
			Vector2D worldMouseDown = toWorld(mouseDownPosition);
			drawMeasurementLine(g2, worldMouseDown, worldMouse);
			drawAngle(g2, worldMouseDown, worldMouse);
		}
		drawCoordinates(g2, worldMouse);
		if (mapAreaElements.contains(MapAreaElementEnum.LANDMARKS)) {
			drawLandmarks(g2);
		}
		drawCurrentImage(g2);
	}

	private void drawLandmarks(Graphics2D g2) {
		List<Landmark> landmarks = worldModel.getLandmarks();
		g2.setStroke(new BasicStroke(3.0f));
		for (Vector2D v : RansacExtractor.allPoints) {
			g2.setColor(Color.pink);
			Vector2D s = toScreen(v);
			g2.drawRect((int)s.x, (int)s.y, 1, 1);
		}
		for (Landmark landmark : landmarks) {
			if (landmark instanceof LineLandmark && landmark.isTrusted()) {
				Color c = Color.magenta;
				for (int i=0; i<landmark.getLife(); i++) {
					c = c.brighter();
				}
				g2.setColor(c);
				g2.setStroke(new BasicStroke(3.0f));
				Segment2D segment = ((LineLandmark)landmark).getSegment();
				Vector2D startPoint = toScreen(segment.x1, segment.y1);
				Vector2D endPoint = toScreen(segment.x2, segment.y2);
				g2.drawLine((int)startPoint.x, (int)startPoint.y, (int)endPoint.x, (int)endPoint.y);
				
				g2.setStroke(new BasicStroke(8.0f));
				Vector2D s = toScreen(landmark.getPosition());
				g2.drawRect((int)s.x, (int)s.y, 1, 1);
				
				if (landmark.getDetectedLandmark()!=null) {
					g2.setColor(Color.blue);
					s = toScreen(landmark.getDetectedLandmark().getPosition());
					g2.drawRect((int)s.x, (int)s.y, 1, 1);
				}
				if (landmark.getAdjustedPosition()!=null) {
					g2.setColor(Color.yellow);
					s = toScreen(landmark.getAdjustedPosition());
					g2.drawRect((int)s.x, (int)s.y, 1, 1);
				}
			} 
		}
		for (Landmark landmark : landmarks) {
			if (landmark instanceof SpikeLandmark && landmark.isTrusted()) {
				Color c = Color.green;
				for (int i=0; i<landmark.getLife(); i++) {
					c = c.brighter();
				}
				g2.setColor(c);
				g2.setStroke(new BasicStroke(7.0f));
				Vector2D s = toScreen(landmark);
				g2.drawRect((int)s.x, (int)s.y, 1, 1);
			}
		}		
	}
	
	private void drawCurrentImage(Graphics2D g2) {
		if (currentImage != null) {
			g2.drawImage(currentImage, 0, 0, null);
		}
	}
	
	private void drawOriginArrows(Graphics2D g2) {
		Vector2D origin = toScreen(0, 0);
		g2.setColor(mapMarkerColor);
		int length = 30;
		g2.drawLine((int)origin.x, (int)origin.y, (int)origin.x, (int)origin.y + length);
		g2.drawLine((int)origin.x-5, (int)origin.y + length - 10, (int)origin.x, (int)origin.y + length);
		g2.drawLine((int)origin.x+5, (int)origin.y + length - 10, (int)origin.x, (int)origin.y + length);
		
		g2.drawLine((int)origin.x, (int)origin.y, (int)origin.x + length, (int)origin.y);
		g2.drawLine((int)origin.x + length - 10, (int)origin.y-5, (int)origin.x + length, (int)origin.y);
		g2.drawLine((int)origin.x + length - 10, (int)origin.y+5, (int)origin.x + length, (int)origin.y);
	}

	private void drawCoordinates(Graphics2D g2, Vector2D position) {
		Vector2D p1 = toScreen(position);
		String coordinateString = String.format("(%3.1f, %3.1f)", position.x, position.y);
		g2.drawString(coordinateString, (int) (p1.x - 10.0f), (int) (p1.y + 40.0f));
	}

	private void drawAngle(Graphics2D g2, Vector2D from, Vector2D to) {
		Vector2D p1 = toScreen(from);
		Vector2D p2 = toScreen(to);
		float angle = (float)Math.atan2(to.y - from.y, to.x - from.x);
		double angleInDegrees = ((angle / Math.PI) * 180.0f + 90);
		if (angleInDegrees < 0) {
			angleInDegrees += 360;
		}
		String angleString = String.format("%3.1f °", angleInDegrees);
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		float l = (float)Math.sqrt(dx * dx + dy * dy);
		g2.drawLine((int)p1.x, (int)(p1.y), (int)p1.x, (int)(p1.y - Math.max(30.0f, l)));
		g2.drawString(angleString, (int) (p1.x - 15.0f), (int) (p1.y + 20.0f));
		float start = 90;
		g2.drawArc((int)(p1.x - 0.3f * l), (int)(p1.y - 0.3f * l), (int)(0.6f * l), (int)(0.6f * l), (int)start, (int)(start - angleInDegrees - 90));
		
	}

	private void drawParticles(Graphics2D g2) {
		for (Particle particle : visualizerFrame.getParticleFilter().getParticles()) {
			if(particle.getAge() < VisualizerConfig.getInstance().getDisplayMinAgeForParticles()) {
				continue;
			}
			int value = 50 + Math.min(120, particle.getAge() / 3);
			Color color = new Color(value, value, value);
			g2.setColor(color);
			drawParticle(g2, particle);
		}
	}

	private void drawParticle(Graphics2D g2, Particle particle) {
		RobotState robot = particle.getLastState();
		if (robot != null) {
			Vector2D to = GeometryUtil.calculatePosition(robot.getPosition(), robot.getHeading(), toWorld(10.0f));
			drawPoint(g2, robot.getPosition());
			drawLine(g2, robot.getPosition(), to);
		}
	}

	private void drawRobotTrail(Graphics2D g2, List<Robot> states) {
		RobotState lastState = null;
		float distanceSoFar = 0.0f;
		float lastDistanceString = -100.0f;
		boolean drawDistanceString = false;
		float distanceMarkerSize = 4.0f;
		for (Robot state : states) {
			RobotState estimatedState = state.getEstimatedState();
			g2.setColor(Color.gray);
			if (lastState != null) {
				drawLine(g2, lastState.getPosition(), estimatedState.getPosition());
				distanceSoFar += lastState.getPosition().distance(estimatedState.getPosition());
			}
			if (distanceSoFar - lastDistanceString >= 100.0f) {
				drawDistanceString = true;
			}
			if (drawDistanceString) {
				String distanceString = String.format("%3.1f m", distanceSoFar / 100);
				Vector2D screenPosition = toScreen(estimatedState.getPosition());
				g2.fillRect((int)(screenPosition.x - 0.5f * distanceMarkerSize), (int)(screenPosition.y - 0.5f * distanceMarkerSize), (int)distanceMarkerSize, (int)distanceMarkerSize);
				g2.setColor(trailMarkerColor);
				g2.drawString(distanceString, (int)screenPosition.x, (int)screenPosition.y);
				drawDistanceString = false;
				lastDistanceString = distanceSoFar;
			}
			lastState = estimatedState;
		}
	}

	private void drawLine(Graphics2D g2, Vector2D from, Vector2D to) {
		Vector2D screenFrom = toScreen(from);
		Vector2D screenTo = toScreen(to);
		g2.drawLine((int)screenFrom.x, (int)screenFrom.y, (int)screenTo.x, (int)screenTo.y);
	}

	private void clearScreen(Graphics g) {
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		g.clearRect(0, 0, screenWidth, screenHeight);
	}

	private void drawGrid(Graphics2D g2) {
		float size = Grid.GRID_SIZE * Grid.CELL_SIZE;
		Vector2D screen = toScreen(new Vector2D(- size * 0.5f, - size * 0.5f));
		int screenSize = (int)toScreen(size);
		g2.drawImage(worldModel.getUserImage(), (int)screen.x, (int)screen.y, screenSize, screenSize, null);
		g2.drawImage(worldModel.getBlockedImage(), (int)screen.x, (int)screen.y, screenSize, screenSize, null);
	}
	
	private void drawIrResults(Graphics2D g2) {
		if (!latestIR.isEmpty()) {
			List<Sample> irs = new ArrayList<Sample>(latestIR);
			Collections.reverse(irs);
			float ir = 1.0f;
			RobotState robot = worldModel.getLatestState().getEstimatedState();
			for (Sample sample : irs) {
				drawIrMeasurement(g2, ir, sample.isInfrared1MeasurementValid(), sample.getInfrared1Angle(), sample.getInfrared1Distance(), robot);
				drawIrMeasurement(g2, ir, sample.isInfrared2MeasurementValid(), sample.getInfrared2Angle(), sample.getInfrared2Distance(), robot);
				ir *= 0.8f;
			}
		}
	}

	private void drawIrMeasurement(Graphics2D g2, float ir, boolean irMeasurementValid, float irAngle, float irDistance,
			RobotState robot) {
		if (irMeasurementValid) {
			g2.setColor(GraphicsUtil.makeTransparentColor(measurementColor, ir));
			Vector2D spot = GeometryUtil.calculatePosition(robot.getPosition(), robot.getHeading() + irAngle, irDistance);
			if (ir >= 1.0f) {
				drawMeasurementLine(g2, robot.getPosition(), spot);
			} else {
				drawMeasurementLine(g2, robot.getPosition(), spot, false);
			}
			drawPoint(g2, spot);
		} else {
			g2.setColor(new Color(1.0f, 0.0f, 0.0f, ir));
			Stroke stroke = g2.getStroke();
			g2.setStroke(dashed);
			float angle = robot.getHeading() + irAngle - (float) Math.PI * 0.5f;
			float dx = (float) Math.cos(angle) * 250.0f;
			float dy = (float) Math.sin(angle) * 250.0f;
			Vector2D position = robot.getPosition();
			Vector2D away = new Vector2D(position.x + dx, position.y + dy);
			drawMeasurementLine(g2, position, away, false);
			g2.setStroke(stroke);
		}
	}
	
	private void drawUltrasoundResults(Graphics g) {
		RobotState robot = worldModel.getLatestState().getEstimatedState();
		if (!latestSR.isEmpty()) {
			List<Sample> srs = new ArrayList<Sample>(latestSR);
			Collections.reverse(srs);
			float sr = 1.0f;
			Vector2D position = robot.getPosition();
			for (Sample sample : srs) {
				drawUltrasoundMeasurement(g, robot, sr, position, sample.getUltrasound1Angle(), sample.getUltrasound1Distance());
				drawUltrasoundMeasurement(g, robot, sr, position, sample.getUltrasound2Angle(), sample.getUltrasound2Distance());
				sr *= 0.8f;
			}
		}
	}

	private void drawUltrasoundMeasurement(Graphics g, RobotState robot,
			float sr, Vector2D position, float ultrasoundAngle, float ultrasoundDistance) {
		float sonarWidth = 42.0f;
		Vector2D spot = GeometryUtil.calculatePosition(position, robot.getHeading() + ultrasoundAngle, ultrasoundDistance);
		g.setColor(new Color(0.0f, 0.6f, 0.6f, sr));
		if (sr >= 1.0f) {
			drawMeasurementLine(g, position, spot);
			// g.setColor(new Color(0.0f, 0.6f, 0.6f, 0.05f));
			drawSector(g, position, spot, sonarWidth);
		} else {
			// g.setColor(new Color(0.0f, 0.6f, 0.6f, 0.05f));
			drawSector(g, position, spot, sonarWidth);
		}
		g.setColor(new Color(0.0f, 0.6f, 0.6f, sr));
		drawPoint(g, spot);
	}

	private void drawRobotSimulator(Graphics2D g2) {
		float baseWidth = toScreen(20f);
		float baseHeigth = toScreen(30f);
		double x1 = -baseWidth * 0.5f;
		double x2 = baseWidth * 0.5f;
		double y1 = baseHeigth * 0.5;
		double y2 = -baseHeigth * 0.5;
		Path2D.Float p = new Path2D.Float();
		p.moveTo(x1, y1);
		p.lineTo(x2, y1);
		p.lineTo(0, y2);
		p.lineTo(x1, y1);
		p.closePath();
		Vector2D robotScreen = toScreen(robotSimulator.getPosition());
		p.transform(AffineTransform.getRotateInstance(Math.toRadians(-robotSimulator.getHeading())));
		p.transform(AffineTransform.getTranslateInstance(robotScreen.x, robotScreen.y));
		g2.setColor(new Color(0.3f, 0.3f, 0.8f));
		g2.fill(p);
		g2.setColor(new Color(0.7f, 0.7f, 0.9f));
		g2.draw(p);
	}
	
	private void drawRobot(Graphics2D g2) {
		RobotState robot = worldModel.getLatestState().getEstimatedState();	
		
		g2.setColor(Color.gray);
		Vector2D robotScreen = toScreen(robot.getPosition());
		float widthScreen = toScreen(11.0f);
		float heightScreen = toScreen(20.0f);
		float turretScreen = toScreen(5.4f);
		Path2D.Float p = new Path2D.Float();
		double x1 = -widthScreen * 0.5f;
		double x2 = +widthScreen * 0.5f;
		double y1 = -(heightScreen - turretScreen);
		double y2 = +turretScreen;
		p.moveTo(x1, y1);
		p.lineTo(x2, y1);
		p.lineTo(x2, y2);
		p.lineTo(x1, y2);
		p.closePath();
		p.transform(AffineTransform.getRotateInstance(robot.getHeading()));
		p.transform(AffineTransform.getTranslateInstance(robotScreen.x, robotScreen.y));
		g2.fill(p);

		Vector2D wheelLeftScreen = toScreen(robot.getPositionLeftTrack());
		Path2D.Float wheelLeft = new Path2D.Float();
		wheelLeft.moveTo(-2f, +turretScreen) ;
		wheelLeft.lineTo(-2f, -(heightScreen - turretScreen));
		wheelLeft.lineTo(2f, -(heightScreen - turretScreen));
		wheelLeft.lineTo(2f, +turretScreen);
		wheelLeft.closePath();
		wheelLeft.transform(AffineTransform.getRotateInstance(robot.getHeading()));
		wheelLeft.transform(AffineTransform.getTranslateInstance(wheelLeftScreen.x, wheelLeftScreen.y));
		g2.setColor(Color.orange);
		g2.fill(wheelLeft);			

		Vector2D wheelRightScreen = toScreen(robot.getPositionRightTrack());
		Path2D.Float wheelRight = new Path2D.Float();
		wheelRight.moveTo(-2f, +turretScreen) ;
		wheelRight.lineTo(-2f, -(heightScreen - turretScreen));
		wheelRight.lineTo(2f, -(heightScreen - turretScreen));
		wheelRight.lineTo(2f, +turretScreen);
		wheelRight.closePath();
		wheelRight.transform(AffineTransform.getRotateInstance(robot.getHeading()));
		wheelRight.transform(AffineTransform.getTranslateInstance(wheelRightScreen.x, wheelRightScreen.y));
		g2.setColor(Color.RED);
		g2.fill(wheelRight);			
	}

	private void drawRobotDirectionArrow(Graphics2D g2) {
		RobotState robot = worldModel.getLatestState().getEstimatedState();
		g2.setColor(Color.black);
		Vector2D robotScreen = toScreen(robot.getPosition());
		Path2D.Float p = new Path2D.Float();
		p.moveTo(0, 0);
		p.lineTo(0, toScreen(-30.0f));
		p.lineTo(-toScreen(4.0f), -toScreen(25.0f));
		p.moveTo(0, toScreen(-30.0f));
		p.lineTo(+toScreen(4.0f), -toScreen(25.0f));
		p.transform(AffineTransform.getRotateInstance(robot.getHeading()));
		p.transform(AffineTransform.getTranslateInstance(robotScreen.x, robotScreen.y));
		Stroke old = g2.getStroke();
		g2.setStroke(arrow);
		g2.draw(p);
		g2.setStroke(old);
	}

	private void drawSector(Graphics g, Vector2D from, Vector2D to, float sector) {
		Vector2D p1 = toScreen(from);
		Vector2D p2 = toScreen(to);
		g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		float dx = (p2.x - p1.x);
		float dy = (p2.y - p1.y);
		float l = (float) Math.sqrt(dx * dx + dy * dy);
		float a = (float) (Math.atan2(-dy, dx) / Math.PI * 180.0) - sector * 0.5f;
		g.drawArc((int) (p1.x - l), (int) (p1.y - l), (int) (2.0f * l), (int) (2.0f * l), (int) a, (int) sector);
	}

	private void drawPoint(Graphics g, Vector2D point) {
		float w = 5.0f;
		float h = 5.0f;
		Vector2D p = toScreen(point);
		g.fillRect((int) (p.x - 0.5f * w), (int) (p.y - 0.5f * h), (int) w, (int) h);
	}

	private void drawMeasurementLine(Graphics g, Vector2D from, Vector2D to) {
		drawMeasurementLine(g, from, to, true);
	}

	private void drawMeasurementLine(Graphics g, Vector2D from, Vector2D to, boolean drawDistanceString) {
		Vector2D p1 = toScreen(from);
		Vector2D p2 = toScreen(to);
		g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		if (drawDistanceString) {
			float dx = (from.x - to.x);
			float dy = (from.y - to.y);
			float l = (float) Math.sqrt(dx * dx + dy * dy);
			String distanceString = String.format("%3.1f cm", l);
			g.drawString(distanceString, (int) (p2.x + 10), (int) p2.y);
		}
	}

	public float toWorld(float screenDistance) {
		return screenDistance / scale;
	}
	
	public Vector2D toWorld(Vector2D screen) {
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		return new Vector2D(camera.x + (screen.x - screenWidth * 0.5f) / scale, camera.y
				+ (screen.y - screenHeight * 0.5f) / scale);
	}


	public float toScreen(float size) {
		return size * scale;
	}

	public Vector2D toScreen(Vector2D v) {
		return toScreen(v.x, v.y);
	}
	
	public Vector2D toScreen(float x, float y) {
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		float x1 = (x - camera.x) * scale + 0.5f * screenWidth;
		float y1 = (y - camera.y) * scale + 0.5f * screenHeight;
		Vector2D f = new Vector2D(x1, y1);
		return f;
	}
	
	private final class PanelSizeHandler implements HierarchyBoundsListener {
		@Override
		public void ancestorResized(HierarchyEvent arg0) {
			resetMouseLocationLine();
			repaint();
		}

		private void resetMouseLocationLine() {
			mouse.x = camera.x;
			mouse.y = camera.y;
		}

		@Override
		public void ancestorMoved(HierarchyEvent arg0) {
		}
	}

	private final class MouseMotionHandler extends MouseAdapter {
		@Override
		public void mouseMoved(MouseEvent mouseEvent) {
			mouse.x = mouseEvent.getX();
			mouse.y = mouseEvent.getY();
			visualizerFrame.getCurrentTool().mouseMoved(mouseEvent, mouse);
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent mouseEvent) {
			mouse.x = mouseEvent.getX();
			mouse.y = mouseEvent.getY();
			visualizerFrame.getCurrentTool().mouseDragged(mouseEvent, mouseDragStart, mouse);
			mouseDragStart.x = mouse.x;
			mouseDragStart.y = mouse.y;
			repaint();
		}
	}

	private final class MouseWheelHandler implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			if (event.isShiftDown() || event.isMetaDown() || event.isControlDown() || event.isAltGraphDown() || event.isPopupTrigger()) {
				camera.x += event.getWheelRotation() * 10.0f / scale;				
				VisualizerPanel.this.repaint();
			} else {
				camera.y += event.getWheelRotation() * 10.0f / scale;
				VisualizerPanel.this.repaint();
			}
		}
	}
	
	private final class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent mouseEvent) {
			mouseDragStart.x = mouseEvent.getX();
			mouseDragStart.y = mouseEvent.getY();			
			mouseDownPosition.x = mouseEvent.getX();
			mouseDownPosition.y = mouseEvent.getY();
			mouseDragging = true;
			visualizerFrame.getCurrentTool().mousePressed(mouseEvent, mouseDragStart);
			if (mouseEvent.isPopupTrigger()) {
				popupMenu.doPopup(mouseEvent);
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouseEvent) {
			mouse.x = mouseEvent.getX();
			mouse.y = mouseEvent.getY();
			mouseDragging = false;
			visualizerFrame.getCurrentTool().mouseReleased(mouseEvent, mouseDownPosition, mouse);
			if (mouseEvent.isPopupTrigger()) {
				popupMenu.doPopup(mouseEvent);
			}
		}
	}
	
	private final class PopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem placeRobot = new JMenuItem("Place robot here");
		JMenuItem placeSimulator = new JMenuItem("Place simulator here");
		
		public PopupMenu() {
			placeRobot.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					RobotState s = new RobotState(toWorld(new Vector2D(mouse.x, mouse.y)), 0);
					worldModel.addState(new Robot(s, s));	
					redraw();
				}
			});
			placeSimulator.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					visualizerFrame.getRobotSimulator().setPosition(toWorld(new Vector2D(mouse.x, mouse.y)));
					redraw();
				}
			});
		}
		public void doPopup(MouseEvent mouseEvent) {
			this.removeAll();
			VisualizerConfig config = VisualizerConfig.getInstance();
			if (config.getInputOutputTarget().equals(InputOutputTargetEnum.REALTIME_SIMULATOR)) {
				add(placeSimulator);
			}
			add(placeRobot);
			this.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
		}
	}

	public void redraw() {
		repaint();
	}
	
	public void zoomIn() {
		scale *= 1.25f;
		repaint();
	}

	public void zoomOut() {
		scale *= 0.8f;
		repaint();
	}
	
	public void clear() {
		worldModel.clearSamples();
		repaint();
	}

	public void removeOldSamples() {
		worldModel.removeOldSamples(1000);
		repaint();
	}

	public float getScale() {
		return scale;
	}

	public void panCameraBy(float dx, float dy) {
		camera.x += dx;
		camera.y += dy;
	}
	
}
