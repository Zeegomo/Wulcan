package wulcan;


public interface View2D{
	public int getWidth();

	public int getHeight();

	public boolean drawPoint(Point2D p, Color32 c);

	public boolean drawLine(Point2D p1, Point2D p2, Color32 c);

	public boolean drawTriangle(Point2D p1, Point2D p2, Point2D p3, Color32 c);

	public boolean isAvailable();

	public void nextFrame();

	public void close();
	
}

