package raisa.domain;

import java.awt.Color;
import java.awt.image.BufferedImage;

import raisa.util.Vector2D;

public class Grid {
	public static final int CELL_SIZE = 16;
	public static final int GRID_SIZE = 100;
	private BufferedImage blockedImage = new BufferedImage(GRID_SIZE, GRID_SIZE, BufferedImage.TYPE_INT_ARGB);
	private static final Color transparentColor = new Color(1.0f, 0.0f, 1.0f, 0.0f);
	private static final Color blockedColor = Color.black;
	private static final Color clearColor = Color.white;
	
	public Grid() {
		for (int y = 0; y < GRID_SIZE; ++y) {
			for (int x = 0; x < GRID_SIZE; ++x) {
				blockedImage.setRGB(x, y, transparentColor.getRGB());
			}			
		}
	}
	
	public void setPosition(Vector2D position, boolean isBlocked) {
		int x = Math.round(position.x / CELL_SIZE) + GRID_SIZE / 2;
		int y = Math.round(position.y / CELL_SIZE) + GRID_SIZE / 2;
		int rgb1 = (isBlocked ? blockedColor : clearColor).getRGB();
		this.blockedImage.setRGB(x, y, rgb1);
	}
	
	public BufferedImage getBlockedImage() {
		return blockedImage;
	}
}