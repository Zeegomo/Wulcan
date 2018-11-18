package wulcan.graphics.java2d;

import wulcan.*;

public interface View2D{
	public int getWidth();

	public int getHeight();

	public boolean drawPoint(Point2D p, Color32 c);

	public boolean drawLine(Point2D p1, Point2D p2, Color32 c);

	public boolean drawTriangle(Triangle2D triangle, Color32 c, boolean filled);
	default public boolean drawTriangle(Triangle2D triangle, Color32 c) {
		return drawTriangle(triangle, c, true);
	}

	public boolean isAvailable();

	public void nextFrame();
	
	public long getWindow();

	public void close();
	
	public void setController(InputController controller);
}

