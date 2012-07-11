package raisa.domain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import raisa.util.CollectionUtil;
import raisa.util.Vector2D;

public class Grid {
	public static final int CELL_SIZE = 8;
	public static final int GRID_SIZE = 400;
	public static final int MAX_UNDO_LEVELS = 10;
	private static final Color transparentColor = new Color(1.0f, 0.0f, 1.0f, 0.0f);
	private static final Color blockedColor = new Color(0.5f, 0.6f, 0.7f, 1.0f);
	private static final Color userBlockedColor = Color.black;
	private static final Color clearColor = Color.white;

	private BufferedImage blockedImage = new BufferedImage(GRID_SIZE, GRID_SIZE, BufferedImage.TYPE_INT_ARGB);
	private List<BufferedImage> userUndoLevels = new ArrayList<BufferedImage>();
	private int userUndoLevel = 0;

	public Grid() {
		BufferedImage userImage = new BufferedImage(GRID_SIZE, GRID_SIZE, BufferedImage.TYPE_INT_ARGB);
		userUndoLevels.add(userImage);
		resetImage(blockedImage);
		resetImage(getUserImage());
	}

	private void resetImage(BufferedImage image) {
		for (int y = 0; y < GRID_SIZE; ++y) {
			for (int x = 0; x < GRID_SIZE; ++x) {
				image.setRGB(x, y, transparentColor.getRGB());
			}
		}
	}

	public void setGridPosition(Vector2D position, boolean isBlocked) {
		int x = Math.round(position.x / CELL_SIZE) + GRID_SIZE / 2;
		int y = Math.round(position.y / CELL_SIZE) + GRID_SIZE / 2;
		int rgb1 = (isBlocked ? blockedColor : clearColor).getRGB();
		this.blockedImage.setRGB(x, y, rgb1);
	}

	public void setUserPosition(Vector2D position, boolean isBlocked) {
		int x = Math.round(position.x / CELL_SIZE) + GRID_SIZE / 2;
		int y = Math.round(position.y / CELL_SIZE) + GRID_SIZE / 2;
		int rgb1 = (isBlocked ? userBlockedColor : clearColor).getRGB();
		getLatestUserImage().setRGB(x, y, rgb1);
	}

	private BufferedImage getLatestUserImage() {
		return this.userUndoLevels.get(userUndoLevels.size()-1);
	}

	public BufferedImage getBlockedImage() {
		return blockedImage;
	}

	public BufferedImage getUserImage() {
		return userUndoLevels.get(userUndoLevel);
	}

	public void pushUserUndoLevel() {
		if (isUserEditRedoable()) {
			userUndoLevels = userUndoLevels.subList(0, userUndoLevel + 1);
		}
		BufferedImage copy = new BufferedImage(getLatestUserImage().getWidth(), getLatestUserImage().getHeight(), getLatestUserImage().getType());
		copy.setData(getLatestUserImage().getData());
		userUndoLevels.add(copy);
		userUndoLevels = CollectionUtil.takeLast(userUndoLevels, MAX_UNDO_LEVELS + 1);
		userUndoLevel = userUndoLevels.size() - 1;
	}

	public void redoUserUndoLevel() {
		if (isUserEditRedoable()) {
			++userUndoLevel;
		}
	}

	public void popUserUndoLevel() {
		if (isUserEditUndoable()) {
			--userUndoLevel;
		}
	}
	
	public boolean isUserEditUndoable() {
		return userUndoLevel > 0;
	}
	
	public boolean isUserEditRedoable() {
		return userUndoLevel < userUndoLevels.size() - 1;		
	}

	public int getUserUndoLevels() {
		return userUndoLevel;
	}

	public int getUserRedoLevels() {
		return userUndoLevels.size() - userUndoLevel - 1;
	}

	public void setUserImage(BufferedImage mapImage) {
		getUserImage().setData(mapImage.getData());
	}

	public void resetUserImage() {
		resetImage(getUserImage());
	}

	public float traceRay(Vector2D from, float angle) {
		angle = angle - (float)Math.PI * 0.5f;
		float x = from.x / CELL_SIZE;
		float y = from.y / CELL_SIZE;
		float dx = (float)Math.cos(angle);
		float dy = (float)Math.sin(angle);
		float maxDistanceInGrid = GRID_SIZE;
		BufferedImage userImage = getUserImage();
		int clearRgb = clearColor.getRGB();
		int transparentRgb = transparentColor.getRGB();
		
		for (float currentDistance = 0.0f; currentDistance < maxDistanceInGrid; currentDistance += 1.0f) {
			x += dx;
			y += dy;
			
			if (x >= 0 && x < userImage.getWidth() - 1 && y >= 0 && y < userImage.getHeight() - 1) {
				int rgb = userImage.getRGB((int)x, (int)y);
				if (rgb != transparentRgb && rgb != clearRgb) {
					return currentDistance * CELL_SIZE;
				}				
			} else {
				return maxDistanceInGrid;
			}
		}
		
		return maxDistanceInGrid * CELL_SIZE;
	}

	public float getWidth() {
		return GRID_SIZE * CELL_SIZE;
	}
	
	public float getHeight() {
		return GRID_SIZE * CELL_SIZE;
	}
}