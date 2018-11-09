package wulcan;


public interface View2D{

	public void drawPoint(Point2D p, Color32 c);
	
	public void drawTriangle(Point2D p1, Point2D p2, Point2D p3, Color32 c);
	
	public void nextFrame();
	
	public void close();
	
}

