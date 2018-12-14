package wulcan.graphics;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import wulcan.*;
import wulcan.math.Line2D;
import wulcan.math.Point2D;
import wulcan.math.Triangle2D;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class OpenGLView implements View2D {
	
	private boolean isAvailable = false;
	private long window;
	private int height;
	private int width;
	private InputController controller;
	private ByteBuffer byteBuffer;
	private float[][] depth;
	private double xStep;
	private double yStep;
	
	public OpenGLView(int width, int height) {
		this.width = width;
		this.height = height;
		this.isAvailable = true;
		this.init();
		this.depth = new float[width + 5][height + 5];
		this.byteBuffer = BufferUtils.createByteBuffer(3 * width * height);
		this.xStep = 2.0 / width;
		this.yStep = 2.0 / height;
	}

	public void setController(InputController controller) {
		this.controller = controller;
	}
	
	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
	
	public long getWindow() {
		return this.window;
	}

	private int getPixel(Point2D t) {
		Point2D p = new Point2D(t);
		p.x += 1;
		p.x /= 2;
		p.y += 1;
		p.y /= 2;
		if(p.x < 1.0 && p.y < 1.0 && p.x >= 0 && p.y >= 0) {
			/*if((int) ((p.y * this.height)dd * this.width  + (int) (p.x * this.width)) > 800 * 800){
				System.out.println(p.x + " " + p.y);
			}*/
			return (int) ((p.y * this.height) * this.width  + (int) (p.x * this.width));
		}
		return 0;
	}
	
	private Point2D normalizePoint(Point2D t) {
		Point2D p = new Point2D(t);
		p.x += 1;
		p.x /= 2;
		p.y += 1;
		p.y /= 2;
		return p;
	}
	
	private float getDepth(Point2D t) {
		Point2D p = normalizePoint(t);
		return depth[(int) (p.x * this.width)][(int) (p.y * this.height)];
	}
	
	private void setDepth(Point2D t, float f) {
		Point2D p = normalizePoint(t);
		depth[(int) (p.x * this.width)][(int) (p.y * this.height)] = f;
	}

	public boolean drawPoint(Point2D t, Color32 c) {
		if(this.isAvailable) {
			Point2D p = normalizePoint(t);
			
			if(p.x < 1.0 && p.y < 1.0 && p.x >= 0 && p.y >= 0) {
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3, c.getRAsByte());
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3 + 1 , c.getGAsByte());
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3 + 2 , c.getBAsByte());
			}
		}
		return this.isAvailable;
	}
	
	public boolean drawPointone(Point2D t, Color32 c) {
		if(this.isAvailable) {
			drawPoint(t, c);
			drawPoint(new Point2D(t.x + this.xStep, t.y + this.yStep), c);
			drawPoint(new Point2D(t.x + this.xStep, t.y - this.yStep), c);
			drawPoint(new Point2D(t.x - this.xStep, t.y + this.yStep), c);
			drawPoint(new Point2D(t.x - this.xStep, t.y - this.yStep), c);
		}
		return this.isAvailable;
	}
	
	private Color32 getDepthColor(double depth) {
		return new Color32(0.9, 0.9 - (Math.log(depth + 1) > 0.9 ? 0.9 : Math.log(depth + 1)), 0.5);
	}
	
	public boolean drawLine(Point2D p1, Point2D p2, Color32 c) {
		if(this.isAvailable) {
			Line2D line = new Line2D(p1, p2);
			int nstep = line.size < 1 ?  1 : (int) line.size; 
			double zStep = (p2.depth - p1.depth) / nstep;
			int a = 0;
			for(Point2D p : line) {
				//System.out.println(p.x + " " + p.y);
				if(getDepth(p) > p1.depth + zStep * a || getDepth(p) == 0) {
					//depthBuffer.put(getPixel(p), (float) p.depth); 
					setDepth(p, (float) (p1.depth + zStep * a));
					drawPoint(p, /*getColor(p1.depth + zStep * a)*/ c);
				}

				if(a > nstep)
					System.out.println("error size");
				a++;
				
			}
			
			if(getDepth(p2) > p2.depth || getDepth(p2) == 0) {
				setDepth(p2, (float) p2.depth); 
				drawPoint(p2, /*getColor(p2.depth)*/ c);
			
			}
		}
		return this.isAvailable;
	}
	
	private void triangleDrawingRoutine(Line2D l1, Line2D l2, Line2D l3, Color32 c) {
		final double step = this.yStep;
		double curr = l1.p1.y;
		double change = l1.p2.y;
		double last = l3.p2.y;
		
		Point2D prev1 = l1.p1;
		Point2D prev2 = l3.p1;
		Point2D currl1, currl3;
		
		//draw upper half of triangle
		while((curr - change) > step) {
			curr -= step;
			currl1 = l1.getPoint2DgivenY(curr);
			currl3 = l3.getPoint2DgivenY(curr);
			drawLine(currl1, currl3, c);
			
			//draw leftover pixels due to aliasing
			drawLine(new Point2D(currl1.x, prev1.y, prev1.depth), prev1, c);
			drawLine(new Point2D(currl3.x, prev2.y, prev2.depth), prev2, c);
			prev1 = currl1;
			prev2 = currl3;
		}
		
		//prev1 = triangle.getVertex(1);
		Point2D currl2;
		
		//draw lower half of triangle
		drawLine(l1.getPoint2DgivenY(change), l3.getPoint2DgivenY(change), c);
		while((curr - last) > step) {
			curr -= step;
			currl2 = l2.getPoint2DgivenY(curr);
			currl3 = l3.getPoint2DgivenY(curr);
			drawLine(currl2, currl3, c);
			
			//draw leftover pixels due to aliasing 
			drawLine(new Point2D(currl2.x, prev1.y, prev1.depth), prev1, c);
			drawLine(new Point2D(currl3.x, prev2.y, prev2.depth), prev2, c);
			
			prev1 = currl2;
			prev2 = currl3;
		}
		drawLine(l2.getPoint2DgivenY(last), l3.getPoint2DgivenY(last), c);
	}

	public boolean drawTriangle(Triangle2D triangle, Color32 c, boolean filled) {
		if(this.isAvailable) {
			//Uncomment to draw triangle vertex
			//Color32 be = new Color32(0.9, 0.9, 0.9);
			//drawPointone(triangle.getVertex(0), be);
			//drawPointone(triangle.getVertex(1), be);
			//drawPointone(triangle.getVertex(2), be);
			
			List<Integer> l = new ArrayList<>();
			l.add(0);
			l.add(1);
			l.add(2);
			l.sort((a, b) -> Double.compare(triangle.getVertex(b).y, triangle.getVertex(a).y));
			
			Line2D l1 = new Line2D(triangle.getVertex(l.get(0)), triangle.getVertex(l.get(1)));
			Line2D l2 = new Line2D(triangle.getVertex(l.get(1)), triangle.getVertex(l.get(2)));
			Line2D l3 = new Line2D(triangle.getVertex(l.get(0)), triangle.getVertex(l.get(2)));
			
			triangleDrawingRoutine(l1, l2, l3, c);
		}
		return this.isAvailable;
	}
	
	public boolean isAvailable() {
		return this.isAvailable;
	}
	
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		//require OpenGL 2.1 compatible
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

		// Create the window
		window = glfwCreateWindow(width, height, "Woolcan", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		updateSize();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(
			window,
			(vidmode.width() - this.width) / 2,
			(vidmode.height() - this.height) / 2
		);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);
		
		// Make the window visible
		glfwShowWindow(window);

		GL.createCapabilities();
		glClearColor(0.80f, 1.0f, 0.80f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		glfwSwapBuffers(window);
		
		glEnable(GL_TEXTURE_2D);
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
	}
	
	public void nextFrame(){
		
		if (!glfwWindowShouldClose(window)) {
			byteBuffer.flip();
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_BYTE, byteBuffer);
			glBegin(GL_QUADS);
			glTexCoord2d(0.0, 0.0); glVertex2f(-1.0f , -1.0f);
			glTexCoord2d(0.0, 1.0); glVertex2f(-1.0f,  1.0f);
			glTexCoord2d(1.0, 1.0); glVertex2f( 1.0f ,  1.0f);
			glTexCoord2d(1.0, 0.0); glVertex2f( 1.0f , -1.0f);
			glEnd();
			glfwSwapBuffers(window);
			glClear(GL_COLOR_BUFFER_BIT);
			this.controller.poll();
			//if(updateSize()) {
				//glViewport(0, 0, this.width, this.height);
			//}
			byteBuffer.clear();
			BufferUtils.zeroBuffer(byteBuffer);
			depth = new float[this.width + 5][this.height + 5];
		} else {
			this.close();
		}
	}

	private boolean updateSize() {
		boolean changed = false;
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			glfwGetWindowSize(window, pWidth, pHeight);
			changed = this.width != pWidth.get(0) || this.height != pHeight.get(0);
			if(changed) {
				this.width = pWidth.get(0);
				this.height = pHeight.get(0);
			}
		}
		return changed;
	}

	public void close() {
		if(this.isAvailable) {
			//Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);

			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
			this.isAvailable = false;
		}
	}

	public static enum Drawing{
		NOT_DRAWING, POINTS,  TRIANGLES, LINE_LOOP, LINES;
	}

}
