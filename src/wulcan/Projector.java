package wulcan;


/**
 * Projects a 3D entity to 2D
 */
public class Projector {
	private double FOV;
	private double aspectRatio;
	
	public Projector(final double FOV, final double aspectRatio) {
		this.FOV = FOV;
		this.aspectRatio = aspectRatio;
	}
	
	public void setAspectRatio(final double width, final double height) {
		this.aspectRatio = height / width;
	}
	
	public double getAspectRatio() {
		return this.aspectRatio;
	}
	
	public void setFOV(final double value) {
		this.FOV = value;
	}
	
	public double getFOV() {
		return this.FOV;
	}
	
	public Point2D project(final Point3D point) {
		Point2D projected = new Point2D(point.x, point.y);
		projected.x = point.x * this.aspectRatio;
		projected.x = projected.x / (Math.tan(this.FOV / 2) * (point.z));
		projected.y = point.y / (Math.tan(this.FOV / 2) * (point.z));
		return projected;
	}
	
	public Triangle2D project(final Triangle3D triangle) {
		return new Triangle2D(
				this.project(triangle.getVertex(0)),
				this.project(triangle.getVertex(1)),
				this.project(triangle.getVertex(2))
		);
	}
}
