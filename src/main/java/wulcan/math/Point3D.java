package wulcan.math;

public class Point3D {
	public double x;
	public double y;
	public double z;
	
	public Point3D(final Point3D copy) {
		this(copy.x, copy.y, copy.z);
	}
	
	public Point3D() {
		this(0,0,0);
	}

	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double magnitude() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public Point3D mult(final double x) {
		Point3D result = new Point3D(this);
		result.x = result.x * x;
		result.y = result.y * x;
		result.z = result.z * x;
		return result;
	}

	public Point3D div(final double other) {
		Point3D result = new Point3D(this);
		result.x /= other;
		result.y /= other;
		result.z /= other;
		return result;
	}

	public Point3D sub(final Point3D other) {
		return new Point3D(this.x - other.x, this.y - other.y, this.z - other.z);
	}

	public Point3D add(final Point3D other) {
		return new Point3D(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	public Point3D normalize() {
		return this.div(this.magnitude());
	}

	public double dot(final Point3D other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	public Point3D cross(final Point3D other) {
		return new Point3D(
				(this.y * other.z - this.z * other.y),
				(this.z * other.x - this.x * other.z),
				(this.x * other.y - this.y * other.x)
		);
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
