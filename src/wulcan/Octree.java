package wulcan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Octree {
	private static int LIMIT = 50;
	private static int MAX_DEPTH = 1000;
	private List<Triangle3D> faces;
	private Node root;
	
	
	public Octree(List<Point3D> coord, List<Triangle3D> faces) {
		this.faces = faces;
		this.root = new Node(coord, 0);
		this.root.addFaces(faces);
		if(this.root.facesReference.isPresent() && this.root.facesReference.get().size() > LIMIT) {
			this.root.split();
		}
	}
	
	/*public Point3D getStep() {
		return 
	}*/
	
	public Optional<List<Triangle3D>> search(Point3D p){
		return this.root.search(p);
	}

	public class Node {
		private Optional<List<Node>> children = Optional.empty();
		private final List<Point3D> coord;
		private Optional<List<Triangle3D>> facesReference;
		private int depth;
		
		private Node(final List<Point3D> coord, int depth) {
			this.coord = coord;
			this.depth = depth;
		}
		
		private void addFaces(List<Triangle3D> faces) {
			this.facesReference = Optional.of(faces.stream().filter(f -> containsTriangle(f)).collect(Collectors.toList()));
		}
 		
		private void split() {
			children = Optional.of(new ArrayList<>());
			children.get().add(null);
			children.get().add(null);
			children.get().add(null);
			children.get().add(null);
			children.get().add(null);
			children.get().add(null);
			children.get().add(null);
			children.get().add(null);
			List<List<Point3D>> coords = getCoord();
			for(int i = 0; i < children.get().size(); i++) {
				children.get().set(i,new Node(coords.get(i), this.depth+1));
				children.get().get(i).addFaces(facesReference.get());
				if(children.get().get(i).facesReference.isPresent() && children.get().get(i).facesReference.get().size() > LIMIT &&  this.depth < MAX_DEPTH) {
					children.get().get(i).split();
				}
			}
			
		}
		
		private List<List<Point3D>> getCoord() {
			List<List<Point3D>> newCoord = new ArrayList<>();
			Point3D stepx = this.coord.get(1).sub(this.coord.get(0)).div(2);
			Point3D stepy = this.coord.get(3).sub(this.coord.get(0)).div(2);
			Point3D stepz = this.coord.get(4).sub(this.coord.get(0)).div(2);
			newCoord.add(buildCubeFromPoint(this.coord.get(0), stepx, stepy, stepz));
			for(int i = 1; i < 4; i++) {
				newCoord.add(buildCubeFromPoint(newCoord.get(0).get(i), stepx, stepy, stepz));
			}
			newCoord.add(buildCubeFromPoint(newCoord.get(0).get(4), stepx, stepy, stepz));
			for(int i = 1; i < 4; i++) {
				newCoord.add(buildCubeFromPoint(newCoord.get(4).get(i), stepx, stepy, stepz));
			}
			return newCoord;
		}
		
		private List<Point3D> buildCubeFromPoint(final Point3D start, final Point3D stepx, final Point3D stepy, final Point3D stepz) {
			List<Point3D> newCoord = new ArrayList<>(8);
			newCoord.add( start);
			newCoord.add(start.add(stepx));
			newCoord.add(start.add(stepy).add(stepx));
			newCoord.add(start.add(stepy));
			newCoord.add(newCoord.get(0).add(stepz));
			newCoord.add(newCoord.get(1).add(stepz));
			newCoord.add(newCoord.get(2).add(stepz));
			newCoord.add(newCoord.get(3).add(stepz));
			return newCoord;
		}
		
		private boolean containsTriangle(Triangle3D i) {
			//Triangle3D tri = faces.get(t);
			if(contains(i.getVertex(0)) || contains(i.getVertex(1)) || contains(i.getVertex(2))) {
				return true;
			}else {
				return false;
			}
		}
		
		private boolean contains(Point3D tri) {
			//Triangle3D tri = faces.get(t);
			if(tri.x >= this.coord.get(0).x &&tri.x <= this.coord.get(6).x 
					&& tri.y >= this.coord.get(6).y && tri.y <= this.coord.get(0).y
					&& tri.z >= this.coord.get(0).z && tri.y <= this.coord.get(6).z
					)
			return true;
			else
				return false;
		}
		
		private Optional<List<Triangle3D>> search(Point3D p){
			if(!children.isPresent()) {
				return this.facesReference;
			} else {
				for(Node c : children.get()) {
					if (c.facesReference.isPresent() && c.facesReference.get().size() > 0 && c.contains(p)) {
						return c.search(p);
					}
				}
				return Optional.empty();
			}
		}
		/*
		private Point3D locate(Point3D p){
			if(!children.isPresent()) {
				return this.coord.get(1).sub(this.coord.get(0)).div(2);
			} else {
				for(Node c : children.get()) {
					if (c.contains(p)) {
						return c.search(p);
					}
				}
				return Optional.empty();
			}
		}*/
		
	}
}


