package wulcan.math;

import java.util.Iterator;

public class Line2D implements Iterable<Point2D>{
	public Point2D p1, p2;
	public Point2D tmp;
	private double xStep,yStep;
	private static double minStep = 2.0/1920;
	private static double delta = minStep;
	public double size;
	public Line2D(final Point2D p1, final Point2D p2) {
		this.p1 = p1;
		this.p2 = p2;
		tmp = new Point2D(p1);
		this.yStep = 2.0/1280 * Math.signum(p2.y - p1.y);
		this.xStep = 2.0/1920 * Math.signum(p2.x - p1.x);
		size = Math.abs(p2.x - p1.x) / minStep;
	}



	public LineIterator iterator() {
		return new LineIterator();
	}

	public Point2D getPoint2DgivenY(double y){
		return new Point2D(Math.abs(Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.x -p1.x)) > Math.abs(p2.x - p1.x) ? p2.x : Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.x -p1.x) + p1.x, y, Math.abs(Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.depth -p1.depth)) > Math.abs(p2.depth - p1.depth) ? p2.depth : Math.abs(y - p1.y)/Math.abs(p2.y - p1.y) * (p2.depth -p1.depth) + p1.depth);
	}

	public class LineIterator implements Iterator<Point2D> {

		private boolean differs(double a, double b) {
			return !equals(a, b);
		}

		private boolean equals(double a, double b) {
			return Math.abs(a - b) <= delta;
		}

		public boolean hasNext() {
			if (equals(p1.y, p2.y)){
				if (Math.abs(tmp.x - p2.x) > Math.abs(xStep)) {
					return true;
				}
			}
			return false;
		}

		public Point2D next() {
			if (equals(p1.y, p2.y)) {
				if (Math.abs(tmp.x - p2.x) > delta) {
					tmp.x += xStep;
				}
			}
			return tmp;
		}


	}
}
