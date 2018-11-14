package wulcan;

public class Triangle3D {
	private Point3D[] vertices;
	
	public Triangle3D(final Point3D v1, final Point3D v2, final Point3D v3) {
		this.vertices = new Point3D[] {v1, v2, v3};
	}
	
	public Point3D getVertex(final int index) {
		return this.vertices[index];
	}
	
	public void setVertex(final int index, final Point3D value) {
		this.vertices[index] = value;
	}
}
