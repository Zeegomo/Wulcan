package wulcan; 

public class Test {
	
	public static void main(String[] args) {
		Point2D p1 = new Point2D(-1.0, 0.0);
		Point2D p2 = new Point2D(0, 0);
		Point2D p3 = new Point2D(-1, -1);
		Color32 c = new Color32(1.0, 0.0, 1.0);
		
		double fov = 3.1415/2;
		
		Point3D[] points = new Point3D[8];
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
		View2D view = new OpenGLView(600, 300);
		while(view.isAvailable()) {
			for (Point3D p : points) {
				if (System.nanoTime() - time > 1000000000) {
					time = System.nanoTime();
					System.out.println("fps: "+ fps);
					fps = 0;
				}
				Point3D tmp = new Point3D(p.x, p.y, p.z);
				tmp.x = tmp.x * 300 / 600;
				tmp.x = tmp.x / (Math.tan(fov/2)*tmp.z);
				tmp.y = tmp.y / (Math.tan(fov/2)*tmp.z);
				view.drawPoint(new Point2D(tmp.x, tmp.y), c);
				fps++;
				p.x += 0.01;
				p.y += 0.01;
				p.z += 0.01;
			}
			view.nextFrame();
		}
		System.out.println("finished");
	}
}
