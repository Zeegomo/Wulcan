package wulcan;


public class Test {

	static Matrix4x4 rotation = new Matrix4x4(new double[][]{
			{0.9999	,0.0100495	,-0.0099495, 0},
			{-0.0099495	,0.9999	,0.0100495, 0},
			{0.0100495	,-0.0099495	,0.9999, 0},
			{0, 0, 0, 1}
			});
	
	static Matrix4x4 translation = new Matrix4x4(new double[][] {
		{1, 0, 0, 0},
		{0, 1, 0, 0},
		{0, 0, 1, -5},
		{0, 0, 0, 1}
	});

	public static void main(String[] args) {
		final Color32 color = new Color32(1.0, 0.0, 1.0);
		final double fov = 3.1415/2;
		
		Matrix4x4 transform = new Matrix4x4(translation);
		transform = rotation.mult(transform);
		translation.set(2, 3, 5);
		transform = translation.mult(transform);
		translation.set(2, 3, -5);
		
		Point3D[] points = {
				new Point3D( 1,  1, 4),
				new Point3D( 1, -1, 4),
				new Point3D(-1, -1, 4),
				new Point3D(-1,  1, 4),
				
				new Point3D( 1,  1, 6),
				new Point3D( 1, -1, 6),
				new Point3D(-1, -1, 6),
				new Point3D(-1,  1, 6),
		};
		
		Triangle3D[] mesh = {
			// Front
			new Triangle3D(points[0], points[1], points[3]),
			new Triangle3D(points[1], points[2], points[3]),
			// Back
			new Triangle3D(points[7], points[5], points[4]),
			new Triangle3D(points[7], points[6], points[5]),
			// Right
			new Triangle3D(points[1], points[0], points[5]),
			new Triangle3D(points[0], points[4], points[5]),
			// Left
			new Triangle3D(points[3], points[2], points[6]),
			new Triangle3D(points[3], points[6], points[7]),
			// Top
			new Triangle3D(points[4], points[0], points[3]),
			new Triangle3D(points[4], points[3], points[7]),
			// Bottom
			new Triangle3D(points[5], points[2], points[1]),
			new Triangle3D(points[5], points[6], points[2]),
		};

		long time = System.nanoTime();
		long fps = 0;
		View2D view = new OpenGLView(1200, 800);
		Projector projector = new Projector(fov, 1);

		while(view.isAvailable()) {
			projector.setAspectRatio(view.getWidth(), view.getHeight());
			for (int i = 0; i < mesh.length; i++) {
				view.drawTriangle(projector.project(mesh[i]), color, false);
				
				for (int j = 0; j < 3; j++) {
					mesh[i].setVertex(j, transform.mult(mesh[i].getVertex(j)));
				}
			}

			if (System.nanoTime() - time > 1000000000) {
				time = System.nanoTime();
				System.out.println("fps: "+ fps);
				fps = 0;
			}
			fps++;
			view.nextFrame();
		}
		System.out.println("finished");
	}

	static void rotate(Point3D p) {
		Point3D rotated = rotation.mult(p);
		p.x = rotated.x;
		p.y = rotated.y;
		p.z = rotated.z;
	}
}
