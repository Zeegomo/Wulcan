package wulcan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wulcan.graphics.GraphicEnviroment;
import wulcan.math.Point3D;

public class Scene {
	private List<Mesh> meshes;
	private Point3D light;
	
	public Projector projector; //TODO
	public GraphicEnviroment ge; //TODO
	
	public Scene(final Collection<Mesh> meshes) {
		this(meshes, new Point3D());
	}
	
	public Scene(final Point3D light) {
		this(new ArrayList<Mesh>(), light);
	}
	
	public Scene(final Collection<Mesh> meshes, final Point3D light) {
		this.meshes = new ArrayList<>(meshes);
		this.light = new Point3D(light);
	}
	
	
}
