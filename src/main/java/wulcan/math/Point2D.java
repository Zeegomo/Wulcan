package wulcan.math;

public class Point2D {
	public double x;
	public double y;
	public double depth;
	
	
	public Point2D(final double x, final double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point2D(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.depth = z;
	}
	
	public Point2D(Point2D t) {
		this(t.x, t.y, t.depth);
	}

	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void add(double x) {
		this.x += x;
		this.y += x;
	}
}
