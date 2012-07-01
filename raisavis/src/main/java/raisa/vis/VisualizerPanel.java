package raisa.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class VisualizerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Float robot = new Float();
	private Float camera = new Float();
	private Float mouse = new Float();
	private Float mouseDragStart = new Float();
	private float scale = 1.0f;
	private List<Spot> spots;
	private Grid grid = new Grid();

	public VisualizerPanel() {
		setBackground(Color.gray);
		setFocusable(true);
		addHierarchyBoundsListener(new PanelSizeHandler());
		addMouseMotionListener(new MouseMotionHandler());
		addMouseListener(new MouseHandler());
		addKeyListener(new KeyboardHandler());
	}

	public void update(List<Spot> spots) {
		for (Spot spot : spots) {
			grid.addSpot(spot);
		}
		synchronized (spots) {			
			if (spots.size() > 100) {
				this.spots = spots.subList(0, 100);
			} else {
				this.spots = spots;				
			}
		}
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		g.clearRect(0, 0, screenWidth, screenHeight);

		Float tl = new Float(camera.x - screenWidth * 0.5f / scale, camera.y
				- screenHeight * 0.5f / scale);
		grid.draw(g2, new Rectangle2D.Float(tl.x * scale, tl.y * scale,
				screenWidth * scale, screenHeight * scale), this);

		g.setColor(Color.black);
		for (Spot spot : spots) {
			drawPoint(g, spot);
		}

		g.setColor(Color.blue);
		drawMeasurementLine(g, robot, toWorld(mouse));

		synchronized (spots) {
			if (!spots.isEmpty()) {
				Spot latestSpot = spots.get(spots.size() - 1);
				g.setColor(Color.red);
				drawMeasurementLine(g, robot, latestSpot);
			}
		}
	}

	private void drawPoint(Graphics g, Float point) {
		float w = 3.0f;
		float h = 3.0f;
		Float p = toScreen(point);
		g.fillRect((int) (p.x - 0.5f * w), (int) (p.y - 0.5f * h), (int) w,
				(int) h);
	}

	private void drawMeasurementLine(Graphics g, Float from, Float to) {
		Float p1 = toScreen(from);
		Float p2 = toScreen(to);
		g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
		float dx = (from.x - to.x);
		float dy = (from.y - to.y);
		float l = (float) Math.sqrt(dx * dx + dy * dy);
		String distanceString = String.format("%3.1f cm", l);
		g.drawString(distanceString, (int) (p2.x + 10), (int) p2.y);
	}

	public Float toWorld(Float screen) {
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		return new Float(camera.x + (screen.x - screenWidth * 0.5f) / scale,
				camera.y + (screen.y - screenHeight * 0.5f) / scale);
	}

	public float toScreen(float size) {
		return size * scale;
	}
	
	public Float toScreen(Float from) {
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		float x1 = (from.x - camera.x) * scale + 0.5f * screenWidth;
		float y1 = (from.y - camera.y) * scale + 0.5f * screenHeight;
		Float f = new Float(x1, y1);
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
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent mouseEvent) {
			mouse.x = mouseEvent.getX();
			mouse.y = mouseEvent.getY();
			camera.x += (mouseDragStart.x - mouse.x) / scale;
			camera.y += (mouseDragStart.y - mouse.y) / scale;
			mouseDragStart.x = mouse.x;
			mouseDragStart.y = mouse.y;
			repaint();
		}
	}

	private final class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent mouseEvent) {
			mouseDragStart.x = mouseEvent.getX();
			mouseDragStart.y = mouseEvent.getY();
		}
	}

	private final class KeyboardHandler extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent arg0) {
			int keyCode = arg0.getKeyCode();
			if (keyCode == KeyEvent.VK_C) {
				System.out.println("Cleared");
				spots.clear();
				repaint();
			} else if (keyCode == KeyEvent.VK_H) {
				System.out.println("Removed old points");
				int fromIndex = Math.max(0, spots.size() - 1000);
				int toIndex = spots.size();
				List<Spot> newSpots = new ArrayList<Spot>();
				newSpots.addAll(spots.subList(fromIndex, toIndex));
				spots = newSpots;
				repaint();
			} else if (keyCode == KeyEvent.VK_PLUS) {
				scale *= 1.25f;
				repaint();
			} else if (keyCode == KeyEvent.VK_MINUS) {
				scale *= 0.8f;
				repaint();
			}
		}
	}
}
