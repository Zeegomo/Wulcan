package wulcan;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjgl.system.CallbackI.P;

import wulcan.graphics.*;

public class Test {
	static final Color32 color = new Color32(1.0, 0.0, 1.0);
	static final double fov = 3.1415/2;
	static final Point3D light = new Point3D(0,0,1);
	static final Projector projector = new Projector(fov, 1);
	static final GraphicEnviroment enviroment = new OpenGLGraphicEnviroment(projector);
	static final View2D view = enviroment.getView();
	static final InputController controller = enviroment.getController();
	static final double znear = 2;
	
	public static void main(String[] args) {
		
		Mesh monkey = new Mesh();
		try {
			monkey = Mesh.loadFromOBJ(new FileReader(new File("meshes/monkey.obj")));
		} catch (IOException e) {
			System.err.println("Error loading file!");
		}
		List<Point3D> coord = new ArrayList<>();
		coord.add(new Point3D(-6,  6, -6));
		coord.add(new Point3D(-6,  6,  6));
		coord.add(new Point3D( 6,  6,  6));
		coord.add(new Point3D( 6,  6, -6));
		coord.add(new Point3D(-6, -6, -6));
		coord.add(new Point3D(-6, -6,  6));
		coord.add(new Point3D( 6, -6,  6));
		coord.add(new Point3D( 6, -6, -6));
		System.out.println("building octree");
		Octree oc = new Octree(coord, monkey.faces);
		System.out.println("finished building octree");
		Matrix4x4 transform = Matrices.buildTranslate(monkey.getCenter().x, monkey.getCenter().y, monkey.getCenter().z)
				.mult(Matrices.buildRotate(-0.01, -0.01, -0.01))
//				.mult(Matrices.buildRotate(0, 0.01, 0))
				.mult(Matrices.buildTranslate(-monkey.getCenter().x, -monkey.getCenter().y, -monkey.getCenter().z));

		long time = System.nanoTime();
		long fps = 0;
		System.out.println("projecting");
		while(view.isAvailable()) {
			projector.setAspectRatio(view.getWidth(), view.getHeight());
			Mesh other = monkey.transform(projector.getCamera());
			other = monkey;
			//other.faces.sort((t1, t2) -> Double.compare(t2.getCenter().z, t1.getCenter().z));
			/*for (final Triangle3D face : other.faces) {
				Color32 shade = color.shade(-face.getNormal().dot(light) / light.magnitude());
				if (face.getNormal().dot(face.getCenter()) < 0) {
					view.drawTriangle(projector.project(face), shade, true);
//					drawNormal(face, 0.1);
				}
			}*/
			double t = Math.tan(fov/2)*znear;
			test(other,oc, t);

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

	private static void test(Mesh other, Octree o, double t) {
		for(double i = -1; i < 1; i += 2.0/view.getWidth()) {
			for(double j = -1; j < 1; j += 2.0/view.getHeight()) {
				Point3D screenPoint = new Point3D(i*view.getWidth()/view.getHeight()*t, j * t, znear);
				Point3D closest = null;
				Triangle3D closestFace = null;
				drawface(other, o,  i, j, screenPoint, closest, closestFace);
			}
		}
	}

	private static void drawface(Mesh other, Octree o,  double i, double j, Point3D screenPoint, Point3D closest,
			Triangle3D closestFace) {
		Point3D p = new Point3D(screenPoint);
		int a = 0;
		while(p.x <= 6 && p.x >= -6 && p.y >= -6 && p.y<= 6 && p.z >= -6 && p.z <= 6 && closest == null) {
			p = p.add(screenPoint.mult(1));
			a++;
			//System.out.println(closest);
			Optional<List<Triangle3D>> faces = o.search(p);
			//System.out.println(p.z);
			if(faces.isPresent()) {
				//System.out.println(faces.get().size());
				for (final Triangle3D face : faces.get()) {
					Optional<Point3D> intersection = intersectFaceLine(face, screenPoint, new Point3D(0, 0, 0));
					if (intersection.isPresent() && intersection.get().magnitude() >= screenPoint.magnitude()) {
						if(closest == null || closest.z > intersection.get().z) {
							closest = intersection.get();
							closestFace = face;
						}
					}
				}
			}
		}
		//System.out.println(a);
		if(closest != null) {
			Color32 shade = color.shade(-closestFace.getNormal().dot(light) / light.magnitude());
			view.drawPoint(new Point2D(i, j), shade);
		}
	}

	public static void drawNormal(final Triangle3D triangle, final double scale) {
		view.drawLine(
				projector.project(triangle.getCenter()),
				projector.project(triangle.getCenter().add(triangle.getNormal().mult(scale))),
				new Color32(0, 0, 1)
		);
	}

	static public Optional<Point3D> intersectPlaneLine(final Point3D planeNormal, final Point3D planePoint, final Point3D line, final Point3D linePoint){
		double den = line.dot(planeNormal);
		if(den != 0) {
			double d = planePoint.sub(linePoint).dot(planeNormal)/den;
			return Optional.of(line.mult(d).add(linePoint));
		}else {
			return Optional.empty();
		}
	}
	
	static public Optional<Point3D> intersectFaceLine(final Triangle3D tri, final Point3D line, final Point3D linePoint){
		Optional<Point3D> intersection = intersectPlaneLine(tri.getNormal(), tri.getVertex(0), line, linePoint);
		if(intersection.isPresent()) {
			for(int i = 0; i < 2; i++) {
				final Point3D toCheck = intersection.get().sub(tri.getVertex(i));
				final Point3D side1 = tri.getVertex(i+1).sub(tri.getVertex(i));
				final Point3D side2 = tri.getVertex((i+2)%3).sub(tri.getVertex(i));
				if(!(side1.cross(toCheck).dot(toCheck.cross(side2)) >= 0 &&
						side2.cross(toCheck).dot(toCheck.cross(side1)) >= 0)) {
					return Optional.empty();
				}
			}
			return intersection;
		}else {
			return intersection;
		}
	}
}
