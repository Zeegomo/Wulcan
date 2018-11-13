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

	public static void main(String[] args) throws InterruptedException {
		Point2D p1 = new Point2D(-1.0, 0.0);
		Point2D p2 = new Point2D(0, 0);
		Point2D p3 = new Point2D(-1, -1);
		Color32 c = new Color32(1.0, 0.0, 1.0);

		double fov = 3.1415/2;

		Point3D[] points = new Point3D[8];
		Point2D[] points_p = new Point2D[8];
		points[0] = new Point3D(1,-1,4);
		points[1] = new Point3D(1,1,4);
		points[2] = new Point3D(-1,-1,4);
		points[3] = new Point3D(-1,1,4);
		points[4] = new Point3D(1,-1,6);
		points[5] = new Point3D(1,1,6);
		points[6] = new Point3D(-1,-1,6);
		points[7] = new Point3D(-1,1,6);

		long time = System.nanoTime();
		long fps = 0;
		View2D view = new OpenGLView(1200, 800);

		while(view.isAvailable()) {
			for (int i = 0; i < 8; i++) {
				Matrix4x4 transform = new Matrix4x4(translation);
				transform = rotation.mult(transform);
				translation.set(2, 3, 5);
				transform = translation.mult(transform);
				translation.set(2, 3, -5);
				points[i] = transform.mult(points[i]);

				points_p[i] = new Point2D(points[i].x, points[i].y);
				points_p[i].x = points_p[i].x * view.getHeight() / view.getWidth();
				points_p[i].x = points_p[i].x / (Math.tan(fov/2)*(points[i].z));
				points_p[i].y = points_p[i].y / (Math.tan(fov/2)*(points[i].z));
			}
			view.drawLine(points_p[0], points_p[1], c);
			view.drawLine(points_p[0], points_p[2], c);
			view.drawLine(points_p[2], points_p[3], c);
			view.drawLine(points_p[1], points_p[3], c);
			view.drawLine(points_p[4], points_p[5], c);
			view.drawLine(points_p[4], points_p[6], c);
			view.drawLine(points_p[6], points_p[7], c);
			view.drawLine(points_p[5], points_p[7], c);
			view.drawLine(points_p[0], points_p[4], c);
			view.drawLine(points_p[1], points_p[5], c);
			view.drawLine(points_p[6], points_p[2], c);
			view.drawLine(points_p[3], points_p[7], c);

//			Thread.sleep(1000);
			
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
