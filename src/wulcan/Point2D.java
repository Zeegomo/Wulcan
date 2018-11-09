package wulcan;

public class Point2D {
	private double x;
	private double y;
	
	
	public Point2D(final double x, final double y) {
		this.x = x;
		this.y = y;
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
