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
		Mesh monkey = new Mesh();
		try {
			monkey = Mesh.loadFromOBJ(new FileReader(new File("monkey.obj")));
		} catch (IOException e) {
			System.err.println("Error loading file!");
		}

		Matrix4x4 transform = Matrices.buildTranslate(monkey.getCenter().x, monkey.getCenter().y, monkey.getCenter().z)
				.mult(Matrices.buildRotate(-0.01, -0.01, -0.01))
//				.mult(Matrices.buildRotate(0, 0.01, 0))
				.mult(Matrices.buildTranslate(-monkey.getCenter().x, -monkey.getCenter().y, -monkey.getCenter().z));

		long time = System.nanoTime();
		long fps = 0;

		while(view.isAvailable()) {
			projector.setAspectRatio(view.getWidth(), view.getHeight());
			monkey.faces.sort((t1, t2) -> (int) (t2.getCenter().z / 0.01) - (int) (t1.getCenter().z / 0.01));
			for (final Triangle3D face : monkey.faces) {
				Color32 shade = color.shade(-face.getNormal().dot(light) / light.magnitude());
				if (face.getNormal().dot(face.getCenter()) < 0) {
					view.drawTriangle(projector.project(face), shade, true);
//					drawNormal(face, 0.1);
				}
			}

			monkey = monkey.transform(transform);

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

	public static void drawNormal(final Triangle3D triangle, final double scale) {
		view.drawLine(
				projector.project(triangle.getCenter()),
				projector.project(triangle.getCenter().add(triangle.getNormal().mult(scale))),
				new Color32(0, 0, 1)
		);
	}
}
