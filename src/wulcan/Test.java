package wulcan; 

public class Test {
	
	public static void main(String[] args) {
		Point2D p1 = new Point2D(-1.0, 0.0);
		Point2D p2 = new Point2D(0, 0);
		Point2D p3 = new Point2D(-1, -1);
		Color32 c = new Color32(1.0, 0.0, 1.0);
		long start = System.nanoTime();
		View2D view = new OpenGLView(600, 300);
		while(view.drawTriangle(p1, p2, p3, c)) {
			//view.drawTriangle(p1, p2, p3, c);
			p1.add(0.01);
			p2.add(0.01);
			p3.add(0.01);
			view.nextFrame();
		}
		view.close();
		System.out.println("finished");
	}
}
