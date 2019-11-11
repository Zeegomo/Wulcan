package wulcan.math;

public class Triangle2D {
	private Point2D[] vertices;
	
	public Triangle2D(final Point2D v1, final Point2D v2, final Point2D v3) {
		this.vertices = new Point2D[] {v1, v2, v3};
	}
	
	public Point2D getVertex(final int index) {
		return vertices[index];
	}
}
