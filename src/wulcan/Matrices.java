package wulcan;

public class Matrices {
	static public Matrix4x4 buildTranslate(final double x, final double y, final double z) {
		return new Matrix4x4(new double[][] {
			{1, 0, 0, x},
			{0, 1, 0, y},
			{0, 0, 1, z},
			{0, 0, 0, 1}
		});
	}
	
	static public Matrix4x4 buildRotate(final double x, final double y, final double z) {
		final Matrix4x4 rotX = new Matrix4x4(new double[][] {
			{1,            0,            0, 0},
			{0, Math.cos(x), -Math.sin(x), 0},
			{0, Math.sin(x),  Math.cos(x), 0},
			{0,            0,            0, 1}
		});
		final Matrix4x4 rotY = new Matrix4x4(new double[][] {
			{ Math.cos(y), 0, Math.sin(y), 0},
			{           0, 1,           0, 0},
			{-Math.sin(y), 0, Math.cos(y), 0},
			{           0, 0,           0, 1}
		});
		final Matrix4x4 rotZ = new Matrix4x4(new double[][] {
			{Math.cos(z), -Math.sin(z), 0, 0},
			{Math.sin(z),  Math.cos(z), 0, 0},
			{          0,            0, 1, 0},
			{          0,            0, 0, 1}
		});
		
		return rotX.mult(rotY).mult(rotZ);
	}
}
