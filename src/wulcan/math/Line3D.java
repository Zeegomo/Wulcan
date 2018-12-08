package wulcan.math;

import java.util.Iterator;

public class Line3D implements Iterable<Point3D>{
	public Point3D p1, p2;
	private Point3D tmp;
	private double xStep, yStep, zStep;
	private static double minStep = 2.0/800;
	private static double delta = minStep;
	private double steps_n;
	public Line3D(final Point3D p1, final Point3D p2) {
		this.p1 = p1;
		this.p2 = p2;
		tmp = new Point3D(p1);
		this.yStep = 2.0/800 * Math.signum(p2.y - p1.y);
		this.xStep = 2.0/800 * Math.signum(p2.x - p1.x);
		this.zStep = 2.0/800 * Math.signum(p2.z - p1.z);
		steps_n = Math.abs(p2.z - p1.z) / minStep;
		//this.yStep = (p2.y - p1.y) / 100;
		//this.xStep = (p2.x - p1.x) / 100;
	}



	public LineIterator iterator() {
		return new LineIterator();
	}

	public Point3D getPoint3DgivenY(double y){
		Point3D n = new Point3D(p1.x, y, p1.z);
		n.x = Math.abs(Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.x -p1.x)) > Math.abs(p2.x - p1.x) ? p2.x : Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.x -p1.x) + p1.x;
		n.z = Math.abs(Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.z -p1.z)) > Math.abs(p2.z - p1.z) ? p2.z : Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.z -p1.z) + p1.z;
		/* Math.abs(Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.x -p1.x)) > Math.abs(p2.x - p1.x) ? p2.x : Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.x -p1.x) + p1.x */
		return n;
	}

	public class LineIterator implements Iterator<Point3D> {

		private boolean differs(double a, double b) {
			return Math.abs(a - b) > delta;
		}

		private boolean equals(double a, double b) {
			return Math.abs(a - b) <= delta;
		}

		public boolean hasNext() {
			if(differs(p1.x, p2.x) && differs(p1.y, p2.y)) {
				if (Math.abs(tmp.x - p2.x) > Math.abs(xStep) || Math.abs(tmp.y - p2.y) > Math.abs(yStep)) {
					return true;
				}

			}else if(equals(p1.x, p2.x)){
				if (Math.abs(tmp.y - p2.y) > Math.abs(yStep)) {
					return true;
				}
			}else {
				if (Math.abs(tmp.x - p2.x) > Math.abs(xStep)) {
					return true;
				}
			}
			return false;
		}

		public Point3D next() {
			if(differs(p1.x, p2.x) && differs(p1.y, p2.y)) {
				if (Math.abs(tmp.x - p2.x) > delta || Math.abs(tmp.y - p2.y) > delta) {
					tmp.x += xStep;
					tmp.y += /*yStep*/   (p2.y - p1.y) / 100 < yStep ? (p2.y - p1.y) / 100 : yStep;
				}
			}else if(equals(p1.x, p2.x)){
				if (Math.abs(tmp.y - p2.y) > delta) {
					tmp.y += yStep;
				}
			}else {
				if (Math.abs(tmp.x - p2.x) > delta) {
					tmp.x += xStep;
					
					/*tmp.x += xStep;
					tmp.z += (p1.z - p2.z) / ((p2.x - p1.x) / xStep);
 				*/}
			}
			return tmp;
		}


	}
}
