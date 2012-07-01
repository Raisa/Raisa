package raisa.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * See http://arduino.cc/playground/Interfacing/Java for RXTX library setup
 */
public class Visualizer extends JPanel {
	private static final long serialVersionUID = 1L;
	private float rx;
	private float ry;
	private float cx;
	private float cy;
	private float mx;
	private float my;
	private float mdx;
	private float mdy;
	float scale = 2.0f;
	public List<Spot> spots;

	public Visualizer() {
		setBackground(Color.gray);
		setFocusable(true);
		addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override
			public void ancestorResized(HierarchyEvent arg0) {
				// cx = getBounds().width * 0.5f;
				// cy = getBounds().height * 0.5f;
				resetMouseLocationLine();
				repaint();
			}

			private void resetMouseLocationLine() {
				mx = cx;
				my = cy;
			}

			@Override
			public void ancestorMoved(HierarchyEvent arg0) {
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent mouseEvent) {
				mx = mouseEvent.getX();
				my = mouseEvent.getY();
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent mouseEvent) {
				my = mouseEvent.getY();
				mx = mouseEvent.getX();
				cx += (mdx - mx) / scale;
				cy += (mdy - my) / scale;
				mdx = mx;
				mdy = my;
				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				mdx = mouseEvent.getX();
				mdy = mouseEvent.getY();
			}			
		});
		addKeyListener(new KeyAdapter() {
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
		});
	}

	public void update(List<Spot> arg) {
		this.spots = arg;
		repaint();
	}

	public void paintComponent(Graphics g) {
		int screenWidth = getBounds().width;
		int screenHeight = getBounds().height;
		g.clearRect(0, 0, screenWidth, screenHeight);
		g.setColor(Color.black);
		for (Spot spot : spots) {
			float w = 3.0f;
			float h = 3.0f;
			float x = 0.5f * screenWidth + scale * (spot.x - cx) - 0.5f * w;
			float y = 0.5f * screenHeight + scale * (spot.y - cy) - 0.5f * h;
			g.fillRect((int) x, (int) y, (int) w, (int) h);
		}

		g.setColor(Color.blue);
		float x = (rx - cx) * scale + 0.5f * screenWidth;
		float y = (ry - cy) * scale + 0.5f * screenHeight;
		g.drawLine((int) x, (int) y, (int) mx, (int) my);
		float dx = (x - mx) / scale;
		float dy = (y - my) / scale;
		float l = (float) Math.sqrt(dx * dx + dy * dy);
		String distanceString = String.format("%3.1f cm", l);
		g.drawString(distanceString, (int) (mx + 10), (int) my);

		float sx = spots.get(spots.size() - 1).x;
		float sy = spots.get(spots.size() - 1).y;

		g.setColor(Color.red);
		x = (rx - cx) * scale + 0.5f * screenWidth;
		y = (ry - cy) * scale + 0.5f * screenHeight;
		float lx = (sx - cx) * scale + 0.5f * screenWidth;
		float ly = (sy - cy) * scale + 0.5f * screenHeight;
		g.drawLine((int) x, (int) y, (int) lx, (int) ly);
		dx = (x - lx) / scale;
		dy = (y - ly) / scale;
		l = (float) Math.sqrt(dx * dx + dy * dy);
		distanceString = String.format("%3.1f cm", l);
		g.drawString(distanceString, (int) (lx + 10), (int) ly);
	}

	private static List<Sample> getExampleSamples() {
		ExampleWorld1 world = new ExampleWorld1();
		float x = 0.0f;
		float y = 0.0f;
		List<String> sampleStrings = new ArrayList<String>();
		for (int i = 0; i < 100; ++i) {
			sampleStrings.add(world.sample(x, y, i / 50.0f * (float) Math.PI));
		}
		List<Sample> samples = new ArrayList<Sample>();
		for (String str : sampleStrings) {
			samples.add(new Sample(x, y, str));
		}
		return samples;
	}

	private static List<Sample> getFileSamples(String filename)
			throws Exception {
		BufferedReader fr = new BufferedReader(new FileReader(filename));
		List<Sample> samples = new ArrayList<Sample>();
		String line = fr.readLine();
		while (line != null) {
			System.out.println(line);
			if (!line.matches("J\\d+,\\d+")) {
				System.out.println("Invalid sample!");
			} else {
				samples.add(new Sample(0, 0, line));
			}
			line = fr.readLine();
		}
		return samples;
	}

	public static void main(String[] args) throws Exception {
		Visualizer visualizer = new Visualizer();
		List<Spot> spots = new ArrayList<Spot>();
		if (args.length == 0) {
			List<Sample> samples = getExampleSamples();
			spots = new ArrayList<Spot>();
			for (Sample sample : samples) {
				spots.add(sample.getSpot());
			}

		} else {
			String inputMode = args[0];
			if ("serial".equals(inputMode)) {
				SerialWorld serialWorld = new SerialWorld(visualizer);
				serialWorld.initialize();
			} else if ("file".equals(inputMode)) {
				if (args.length != 2) {
					System.out.println("Missing filename");
				}
				String filename = args[1];
				List<Sample> samples = getFileSamples(filename);
				spots = new ArrayList<Spot>();
				for (Sample sample : samples) {
					spots.add(sample.getSpot());
				}
			}
		}

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(600, 400);
		visualizer.spots = spots;
		frame.add(visualizer);
		frame.setVisible(true);
		visualizer.requestFocusInWindow();
	}
}
