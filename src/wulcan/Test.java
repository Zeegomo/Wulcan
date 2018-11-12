package wulcan;

public class Test {
	
	public static void main(String[] args) {
		Point2D p1 = new Point2D(-1.0, 0.0);
		Point2D p2 = new Point2D(0, 0);
		Point2D p3 = new Point2D(-1, -1);
		Color32 c = new Color32(1.0, 0.0, 1.0);
		
		double fov = 3.1415/2;
		
		Point3D[] points = new Point3D[8];
		Point2D[] points_p = new Point2D[8];
		points[0] = new Point3D(1,-1,2);
		points[1] = new Point3D(1,1,2);
		points[2] = new Point3D(-1,-1,2);
		points[3] = new Point3D(-1,1,2);
		points[4] = new Point3D(1,-1,4);
		points[5] = new Point3D(1,1,4);
		points[6] = new Point3D(-1,-1,4);
		points[7] = new Point3D(-1,1,4);
		
		long time = System.nanoTime();
		long fps = 0;
		View2D view = new OpenGLView(1200, 800);
		while(view.isAvailable()) {
			for (int i = 0; i < 8; i++) {

				points_p[i] = new Point2D(points[i].x, points[i].y);
				points_p[i].x = points_p[i].x * view.getHeight() / view.getWidth();
				points_p[i].x = points_p[i].x / (Math.tan(fov/2)*points[i].z);
				points_p[i].y = points_p[i].y / (Math.tan(fov/2)*points[i].z);
				//view.drawPoint(new Point2D(points[i].x, tmp.y), c);
				points[i].x += 0.01;
				points[i].y += 0.01;
				//points[i].z += 0.01;
				//p.x += 0.01;
				//p.y += 0.01;
				//p.z += 0.01;
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
}
