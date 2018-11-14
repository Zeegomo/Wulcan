package wulcan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mesh {
	public List<Triangle3D> faces = new ArrayList<>();
	
	public Mesh() {}
	
	public Mesh(final Collection<Triangle3D> faces) {
		this.faces.addAll(faces);
	}
	
	public Mesh transform(final Matrix4x4 trans) {
		Mesh result = new Mesh();

		for (final Triangle3D face : this.faces) {
			result.faces.add(new Triangle3D(trans.mult(face.getVertex(0)), trans.mult(face.getVertex(1)),
					trans.mult(face.getVertex(2))));
		}

		return result;
	}

	public static Mesh loadFromOBJ(final Reader input) throws IOException {
		Mesh result = new Mesh();
		
		List<Point3D> vertices = new ArrayList<>();
		
		final BufferedReader reader = new BufferedReader(input);
		String line;
		while ((line = reader.readLine()) != null) {
			String parts[] = line.split(" ");
			if (parts.length > 0) {
				if (parts[0].equals("v")) {
					vertices.add(new Point3D(
							Double.parseDouble(parts[1].split("/")[0]),
							Double.parseDouble(parts[2].split("/")[0]),
							Double.parseDouble(parts[3].split("/")[0])
					));
				} else if (parts[0].equals("f")) {
					result.faces.add(new Triangle3D(
							vertices.get(Integer.parseInt(parts[1]) - 1),
							vertices.get(Integer.parseInt(parts[2]) - 1),
							vertices.get(Integer.parseInt(parts[3]) - 1)
					));
				}
			}
		}
		
		return result;
	}
}
