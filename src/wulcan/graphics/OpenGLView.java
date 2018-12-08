package wulcan.graphics;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import wulcan.*;
import wulcan.math.Line2D;
import wulcan.math.Line3D;
import wulcan.math.Line3D.LineIterator;
import wulcan.math.Point2D;
import wulcan.math.Triangle2D;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
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
	private Drawing drawing = Drawing.NOT_DRAWING;
	private ByteBuffer byteBuffer = BufferUtils.createByteBuffer(3 * 800 * 800);
	private FloatBuffer depthBuffer = BufferUtils.createFloatBuffer(802 * 802);
	private float[][] depth = new float[801][801];
	
	public OpenGLView(int height, int width) {
		this.width = width;
		this.height = height;
		this.isAvailable = true;
		this.init();
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
	
	private float getDepth(Point2D t) {
		Point2D p = new Point2D(t);
		p.x += 1;
		p.x /= 2;
		p.y += 1;
		p.y /= 2;
		return depth[(int) (p.x * this.width)][(int) (p.y * this.height)];
	}
	
	private void setDepth(Point2D t, float f) {
		Point2D p = new Point2D(t);
		p.x += 1;
		p.x /= 2;
		p.y += 1;
		p.y /= 2;
		depth[(int) (p.x * this.width)][(int) (p.y * this.height)] = f;
	}

	public boolean drawPoint(Point2D t, Color32 c) {
		if(this.isAvailable) {
			//glPointSize(2);
			//glBegin(GL_POINTS);
			/*if(drawing != Drawing.POINTS) {
				glEnd();
				glPointSize(2);
				glBegin(GL_POINTS);
				drawing = Drawing.POINTS;
			}
			glColor3d(c.getR(), c.getG(), c.getB());
            glVertex2d(p.getX(), p.getY());*/
			Point2D p = new Point2D(t);
			p.x += 1;
			p.x /= 2;
			p.y += 1;
			p.y /= 2;
			//System.out.println("x: " + p.x + " y: " + p.y);
			//System.out.println((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3);
			if(p.x < 1.0 && p.y < 1.0 && p.x >= 0 && p.y >= 0) {
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3, c.getRAsByte());
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3 + 1 , c.getGAsByte());
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3 + 2 , c.getBAsByte());
			}

			//this.doubleBuffer = this.doubleBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 4 + 3, (float) 1);
            //glEnd();
		}
		return this.isAvailable;
	}
	
	public boolean drawPointone(Point2D t, Color32 c) {
		if(this.isAvailable) {
			drawPoint(t, c);
			drawPoint(new Point2D(t.x + 0.0025, t.y + 0.0025), c);
			drawPoint(new Point2D(t.x + 0.0025, t.y - 0.0025), c);
			drawPoint(new Point2D(t.x - 0.0025, t.y + 0.0025), c);
			drawPoint(new Point2D(t.x - 0.0025, t.y - 0.0025), c);
		}
		return this.isAvailable;
	}
	
	private Color32 getColor(double depth) {
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

	public boolean drawTriangle(Triangle2D triangle, Color32 c, boolean filled) {
		if(this.isAvailable) {
			
			Color32 be = new Color32(0.9, 0.9, 0.9);
			//drawPointone(triangle.getVertex(0), be);
			//drawPointone(triangle.getVertex(1), be);
			//drawPointone(triangle.getVertex(2), be);
			List<Integer> l = new ArrayList<>();
			l.add(0);
			l.add(1);
			l.add(2);
			l.sort((a, b) -> Double.compare(triangle.getVertex(b).y, triangle.getVertex(a).y));

			//drawLine(triangle.getVertex(0), triangle.getVertex(1), be);
			//drawLine(triangle.getVertex(1), triangle.getVertex(2), be);
			//drawLine(triangle.getVertex(0), triangle.getVertex(2), be);
			
			double curr = triangle.getVertex(l.get(0)).y;
			double change = triangle.getVertex(l.get(1)).y;
			double last = triangle.getVertex(l.get(2)).y;
			Line2D l1 = new Line2D(triangle.getVertex(l.get(0)), triangle.getVertex(l.get(1)));
			Line2D l2 = new Line2D(triangle.getVertex(l.get(1)), triangle.getVertex(l.get(2)));
			Line2D l3 = new Line2D(triangle.getVertex(l.get(0)), triangle.getVertex(l.get(2)));
			while((curr - change) > 2.0/800) {
				drawLine(l1.getPoint2DgivenY(curr), l3.getPoint2DgivenY(curr), c);
				curr -= 2.0/800;
			}
			drawLine(l1.getPoint2DgivenY(change), l3.getPoint2DgivenY(change), c);
			while((curr - last) > 2.0/800) {
				drawLine(l2.getPoint2DgivenY(curr), l3.getPoint2DgivenY(curr), c);
				curr -= 2.0/800;
			}
			drawLine(l2.getPoint2DgivenY(last), l3.getPoint2DgivenY(last), c);
			
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
		window = glfwCreateWindow(height, width, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		/*glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});*/

		updateSize();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(
			window,
			(vidmode.width() - this.width) / 2,
			(vidmode.height() - this.height) / 2
		);

		/*// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically*/

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
		//if(this.drawing != Drawing.NOT_DRAWING) {glEnd();this.drawing = Drawing.NOT_DRAWING;}
		
		/*for(int i = 0; i < 300; i++) {
			for(int j = 0; j < 300; j++) {
				byteBuffer = byteBuffer.put((i*300 +j)*3, (byte) 100);
				byteBuffer = byteBuffer.put((i*300 +j)*3 + 1, (byte) 100);
				byteBuffer = byteBuffer.put((i*300 +j)*3 + 2, (byte) 0);
			}
		}*/
		
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
			depthBuffer.clear();
			BufferUtils.zeroBuffer(byteBuffer);
			BufferUtils.zeroBuffer(depthBuffer);
			depth = new float[801][801];
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
