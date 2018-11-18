package wulcan.graphics.java2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import wulcan.Color32;
import wulcan.Point2D;
import wulcan.Triangle2D;

public class Java2DView implements View2D {
	
	private boolean isAvailable = false;
	private int width;
	private int height;
	//private InputController controller;
	private final JFrame frame;
	private final JPanel currentGraphicPanel;
//	private BufferedImage img2;
	private BufferedImage img;
	private Graphics2D g2d;
	
	public Java2DView (int height, int width) {
		frame = new JFrame("Wulcan: the 3D engine by that Gay of Zeeg");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.width = width;
		this.height = height;
		isAvailable = true;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();

		currentGraphicPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
	            // Drawing the image.
	            int w = img.getWidth();
	            int h = img.getHeight();
	            g2.drawImage(img, 0, 0, w, h, null);
	            // At the end, we dispose the
	            // Graphics copy we've created
	            g2.dispose();
			}
		};
		frame.setContentPane(currentGraphicPanel);
		frame.setSize(width, height);
		frame.setVisible(true);
		
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public boolean drawPoint(Point2D p, Color32 c) {
		return this.drawLine(p, p, c);
	}

	@Override
	public boolean drawLine(Point2D p1, Point2D p2, Color32 c) {
		if(this.isAvailable()) {
			Color color = new Color((int)c.getR(), (int)c.getG(), (int)c.getB());
			g2d.setColor(color);
			g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
			return true;
		}
		return false;
	}

	@Override
	public boolean drawTriangle(Triangle2D triangle, Color32 c, boolean filled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public void nextFrame() {
//		img = img2;
//		img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		g2d = img2.createGraphics();
		currentGraphicPanel.repaint();
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		frame.setVisible(true);
	}

	@Override
	public long getWindow() {
		return -1;
	}

	@Override
	public void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

	}

	@Override
	public void setController(InputController controller) {
		//this.controller = controller;
	}

}
