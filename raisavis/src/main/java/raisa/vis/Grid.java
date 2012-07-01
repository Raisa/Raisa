package raisa.vis;

import static java.awt.geom.Point2D.Float;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Grid {
	private static class Tile {
		public static final int size = 32;
		int tileX;
		int tileY;
		Float topLeft;
		float[] cells = new float[size * size];
		BufferedImage buffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		public Tile(int tileX, int tileY) {
			this.tileX = tileX;
			this.tileY = tileY;
			topLeft = new Float(tileX * size, tileY * size);
		}

		public void addSpot(Spot spot) {
			int x = (int)Math.floor(spot.x) - tileX * size;
			int y = (int)Math.floor(spot.y) - tileY * size;
			int p = y * size + x;
			float intensity = 0.5f + 0.5f * cells[p];
			cells[p] = intensity;
			float i = Math.min(1.0f, Math.max(0.0f, 0.5f - 0.5f * intensity));
			int c = (int)(255 * 256 * 256 * 256 + (i * 256 * 256 * 256) + (i * 256 * 256) + i * 256);
			buffer.setRGB(x, y, c);
		}

		public void draw(Graphics2D g2, VisualizerPanel panel) {
			Float tl = panel.toScreen(topLeft);
			int s = (int)panel.toScreen(size);
			//g2.setColor(Color.lightGray);
			//g2.drawRect((int)tl.x, (int)tl.y, s, s);
			g2.drawImage(buffer, (int)tl.x, (int)tl.y, s, s, null);
		}
	}
	
	private Map<String, Tile> tiles = new HashMap<String, Tile>();

	public void addSpot(Spot spot) {
		int tileX = tile(spot.x);
		int tileY = tile(spot.y);
		String tileId = tileId(tileX, tileY);
		Tile tile = tiles.get(tileId);
		if (tile == null) {
			tile = new Tile(tileX, tileY);
			tiles.put(tileId, tile);
		}
		tile.addSpot(spot);
	}

	private String tileId(int tileX, int tileY) {
		return tileX + " " + tileY;
	}

	private int tile(float f) {
		return (int)Math.floor(f / Tile.size);
	}

	public void draw(Graphics2D g2, Rectangle2D.Float rectangle, VisualizerPanel panel) {
		float left = rectangle.x;
		float right = left + rectangle.width;
		int leftTile = tile(left);
		int rightTile = tile(right);
		float top = rectangle.y;
		float bottom = top + rectangle.height;
		int topTile = tile(top);
		int bottomTile = tile(bottom);

		for (int ty = topTile; ty < bottomTile; ++ty) {
			for (int tx = leftTile; tx < rightTile; ++tx) {
				Tile tile = tiles.get(tileId(tx, ty));
				if (tile != null) {
					tile.draw(g2, panel);
				}
			}
		}
	}
}