package wulcan;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import wulcan.graphics.*;
import wulcan.math.Matrices;
import wulcan.math.Matrix4x4;
import wulcan.math.Point3D;
import wulcan.math.Triangle3D;

public class Test {
	static final Color32 color = new Color32(1.0, 0.0, 1.0);
	static final double fov = Math.PI/2;
	static final Point3D light = new Point3D(0,0,1);
	static final Projector projector = new Projector(fov, 1);
	static final GraphicEnviroment enviroment = new OpenGLGraphicEnviroment(projector);
	static final View2D view = enviroment.getView();
	static final InputController controller = enviroment.getController();
	// Normals of planes used to clip triangles
	static final ArrayList<Point3D> clipNormals = new ArrayList<>(Arrays.asList(
			Matrices.buildRotate(0, -fov/2, 0).mult(new Point3D(-1, 0,0)),
			Matrices.buildRotate(0,  fov/2, 0).mult(new Point3D( 1, 0,0)),
			Matrices.buildRotate( fov/2, 0, 0).mult(new Point3D( 0,-1,0)),
			Matrices.buildRotate(-fov/2, 0, 0).mult(new Point3D( 0, 1,0))
	));
	
	public static void main(String[] args) {
		// Load mesh
		Mesh monkey = new Mesh();
		try {
			monkey = Mesh.loadFromOBJ(new FileReader(new File("meshes/heart.obj")));
		} catch (Exception e) {
			System.err.println("Error loading file!");
		}

		// Build a transform matrix to rotate the mesh around its center
		Matrix4x4 transform = Matrices.buildTranslate(monkey.getCenter().x, monkey.getCenter().y, monkey.getCenter().z)
				.mult(Matrices.buildRotate(-0.01, -0.01, -0.01))
				.mult(Matrices.buildTranslate(-monkey.getCenter().x, -monkey.getCenter().y, -monkey.getCenter().z));

		long time = System.nanoTime();
		long fps = 0;

		while(view.isAvailable()) {
			projector.setAspectRatio(view.getWidth(), view.getHeight());
			final Mesh relocatedMesh = monkey.transform(projector.getCamera()); // Transform mesh accordin to camera position/rotation
			final Point3D relocatedLight = projector.getCamera().mult(light, 0); // 0 as 4th element ignores translation
			monkey.faces.sort((t1, t2) -> (int) (t2.getCenter().z / 0.01) - (int) (t1.getCenter().z / 0.01)); // Sort faces by distance to handle overlapping
			
			final List<Point3D> relocatedClipNormals = Arrays.asList(clipNormals.stream().map(n -> projector.getCamera().mult(n, 0)).toArray(Point3D[]::new));
			final Point3D relocatedClipOrigin = projector.getCamera().mult(new Point3D());
			for (final Triangle3D face : relocatedMesh.faces) {
				if (face.getNormal().dot(face.getCenter()) < 0) {
					Color32 shade = color.shade(-face.getNormal().dot(relocatedLight) / relocatedLight.magnitude());
					
					ArrayList<Triangle3D> toDraw = new ArrayList<>();
					toDraw.add(face);
					for (final Point3D planeNormal : relocatedClipNormals) {
						toDraw = clipTriangles(toDraw, planeNormal, relocatedClipOrigin);
					}
					
					for (final Triangle3D tri : toDraw) {
						view.drawTriangle(projector.project(tri), shade, false);
					}
//					view.drawTriangle(projector.project(face), shade, true);
//					drawNormal(face, 0.1);
				}
			}

			//monkey = monkey.transform(transform);

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
	
	public static Optional<Point3D> intersectPlaneLine(final Point3D planeNormal, final Point3D planePoint, final Point3D lineStart, final Point3D lineEnd) {
		final Point3D lineVec = lineEnd.sub(lineStart);
		if (planeNormal.dot(lineVec) == 0) { // Plane and line are parallel
			return Optional.empty();
		}
		
		// Multiplier for the lineVec
		final double length = planePoint.sub(lineStart).dot(planeNormal) / planeNormal.dot(lineVec);
		if (length <= 0 || length >= 1) { // Outside the vector
			return Optional.empty();
		}
		return Optional.of(lineVec.mult(length).add(lineStart));
	}
	
	public static ArrayList<Triangle3D> clipTriangles(final ArrayList<Triangle3D> tris, final Point3D planeNormal, final Point3D planePoint) {
		final ArrayList<Triangle3D> result = new ArrayList<>();
		
		for (final Triangle3D tri : tris) {
			boolean intersected = false;
			// Find the point that is alone on one side of the plane because then it's easier
			for (int i = 0; i < 3; i++) {
				final Point3D start  = tri.getVertex(i);
				final Point3D after  = tri.getVertex((i+1)%3);
				final Point3D before = tri.getVertex((i+2)%3);
				final Optional<Point3D> intersectAfter  = intersectPlaneLine(planeNormal, planePoint, start, after);
				final Optional<Point3D> intersectBefore = intersectPlaneLine(planeNormal, planePoint, start, before);
				if (intersectAfter.isPresent() && intersectBefore.isPresent()) {
					intersected = true;
					// Check if the start is visibile
					if (start.dot(planeNormal) < 0) {
						result.add(new Triangle3D(start, intersectAfter.get(), intersectBefore.get()));
					} else {
						result.add(new Triangle3D(after, before, intersectAfter.get()));
						result.add(new Triangle3D(before, intersectBefore.get(), intersectAfter.get()));
					}
					break;
				}
			}
			
			if (!intersected) { // Then the triangle is all outside or inside the plane
				if (tri.getCenter().dot(planeNormal) < 0) {
					result.add(tri);
				}
			}
		}
		
		return result;
	}
}
