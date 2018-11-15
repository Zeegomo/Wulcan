package wulcan;


/**
 * Projects a 3D entity to 2D
 */
public class Projector {
	private double FOV;
	private double aspectRatio;
	private Matrix4x4 camera = Matrices.buildIdentity();
	
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
	
	public void translateCamera(final Point3D point) {
		//Matrices.buildTranslate(point.x, point.y, point.z).print();
		camera = Matrices.buildTranslate(point.x, point.y, point.z).mult(camera);
		camera.print();
	}

	public void rotateCamera(final Point3D point) {
		//Matrices.buildTranslate(point.x, point.y, point.z).print();
		camera = Matrices.buildRotate(point.x, point.y, point.z).mult(camera);
		camera.print();
	}

	public Matrix4x4 getCamera() {
		return this.camera;
	}

	public Point2D project(final Point3D point) {
		//Point3D point = new Point3D(p);
		//point = offset.mult(point);
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
