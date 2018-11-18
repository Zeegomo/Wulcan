package wulcan.graphics.java2d;

import java.awt.Color;

import wulcan.Color32;
import wulcan.Point2D;

public class Test {

	public static void main(String[] args) {
		View2D sborn = new Java2DView(400,400);
		Point2D p1 = new Point2D(1, 1);
		Point2D p2 = new Point2D(100, 100);
		Point2D p3 = new Point2D(100, 1);
		Point2D p4 = new Point2D(1, 100);
		Color32 c = new Color32(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue());
		sborn.drawLine(p1, p2, c);
		sborn.nextFrame();
		sborn.drawLine(p3, p4, c);
		sborn.nextFrame();
		//g2d.close();

	}

}
