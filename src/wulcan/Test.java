package wulcan;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Test {
	static final Color32 color = new Color32(1.0, 0.0, 1.0);
	static final double fov = 3.1415/2;
	static final View2D view = new OpenGLView(1200, 800);
	static final Point3D light = new Point3D(0,0,1);
	static final Projector projector = new Projector(fov, 1);
	
	public static void main(String[] args) {
		Matrix4x4 transform = Matrices.buildTranslate(0, 0, 5)
				.mult(Matrices.buildRotate(-0.01, -0.01, -0.01))
				.mult(Matrices.buildTranslate(0, 0, -5));
		
//		Point3D[] points = {
//				new Point3D( 1,  1, 4),
//				new Point3D( 1, -1, 4),
//				new Point3D(-1, -1, 4),
//				new Point3D(-1,  1, 4),
//
//				new Point3D( 1,  1, 6),
//				new Point3D( 1, -1, 6),
//				new Point3D(-1, -1, 6),
//				new Point3D(-1,  1, 6),
//		};

//		Mesh cube = new Mesh(Arrays.asList(new Triangle3D[]{
//			// Front
//			new Triangle3D(points[0], points[1], points[3]),
//			new Triangle3D(points[1], points[2], points[3]),
//			// Back
//			new Triangle3D(points[7], points[5], points[4]),
//			new Triangle3D(points[7], points[6], points[5]),
//			// Right
//			new Triangle3D(points[1], points[0], points[5]),
//			new Triangle3D(points[0], points[4], points[5]),
//			// Left
//			new Triangle3D(points[3], points[2], points[6]),
//			new Triangle3D(points[3], points[6], points[7]),
//			// Top
//			new Triangle3D(points[4], points[0], points[3]),
//			new Triangle3D(points[4], points[3], points[7]),
//			// Bottom
//			new Triangle3D(points[5], points[2], points[1]),
//			new Triangle3D(points[5], points[6], points[2]),
//		}));

		Mesh monkey = new Mesh();
		try {
			monkey = Mesh.loadFromOBJ(new FileReader(new File("monkey.obj")));
		} catch (IOException e) {
			System.err.println("Error loading file!");
		}

		long time = System.nanoTime();
		long fps = 0;

		while(view.isAvailable()) {
			projector.setAspectRatio(view.getWidth(), view.getHeight());
			for (int i = 0; i < monkey.faces.size(); i++) {
				Point3D center = monkey.faces.get(i).getVertex(0).add(monkey.faces.get(i).getVertex(1)).add(monkey.faces.get(i).getVertex(2)).div(3);
				Point3D normal = monkey.faces.get(i).getNormal();
				Color32 shade = color.shade(-monkey.faces.get(i).getNormal().dot(light) / light.magnitude());
				if(normal.dot(center) < 0) {
					view.drawTriangle(projector.project(monkey.faces.get(i)), shade, true);
				}

				// Transform the current triangle
				for (int j = 0; j < 3; j++) {
					monkey.faces.get(i).setVertex(j, transform.mult(monkey.faces.get(i).getVertex(j)));
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

	public void drawNormal(final Triangle3D triangle) {
		Point3D center = triangle.getVertex(0).add(triangle.getVertex(1)).add(triangle.getVertex(2)).div(3);
		view.drawLine(
				projector.project(center),
				projector.project(center.add(triangle.getNormal())),
				new Color32(0, 0, 1)
				);
	}
}
