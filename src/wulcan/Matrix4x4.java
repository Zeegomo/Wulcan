package wulcan;

public class Matrix4x4 {
	private double[][] data;
	
	public Matrix4x4() {
		data = new double[4][4];
	}
	
	public Matrix4x4(final double[][] data) {
		this();
		for (int i = 0; i < Math.min(4, data.length); i++) {
			for (int j = 0; j < Math.min(4, data[0].length); j++) {
				this.data[i][j] = data[i][j];
			}
		}
	}

	public Matrix4x4(final Matrix4x4 copy) {
		this(copy.data);
	}
	
	public Matrix4x4 mult(final Matrix4x4 other) {
		Matrix4x4 result = new Matrix4x4();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					result.data[i][j] += this.get(i,k) * other.get(k, j);
				}
			}
		}
		
		return result;
	}

	// The 4th element of the points is always 1
	public Point3D mult(final Point3D other) {
		Point3D result = new Point3D(0,0,0);
		
		result.x = other.x * this.get(0, 0) + other.y * this.get(0, 1) + other.z * this.get(0, 2) + this.get(0, 3);
		result.y = other.x * this.get(1, 0) + other.y * this.get(1, 1) + other.z * this.get(1, 2) + this.get(1, 3);
		result.z = other.x * this.get(2, 0) + other.y * this.get(2, 1) + other.z * this.get(2, 2) + this.get(2, 3);
		
		return result;
	}
	
	public Matrix4x4 mult(final double other) {
		Matrix4x4 result = new Matrix4x4();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result.set(i, j, this.get(i, j) * other);
			}
		}
		
		return result;
	}
	
	public double get(final int row, final int col) {
		return this.data[row][col];
	}
	
	public void set(final int row, final int col, final double val) {
		this.data[row][col] = val;
	}
	
	public void print() {
		for (int i = 0; i < 4; i++) {
			System.out.print("| ");
			for (int j = 0; j < 4; j++) {
				System.out.print(this.data[i][j] + " ");
			}
			System.out.println(" |");
		}
		System.out.println();
	}
}
