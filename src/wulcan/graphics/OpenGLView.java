package wulcan.graphics;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import wulcan.*;
import wulcan.math.Point2D;
import wulcan.math.Triangle2D;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
	private ByteBuffer byteBuffer = BufferUtils.createByteBuffer(3 * 300 * 300);
	//private FloatBuffer doubleBuffer = byteBuffer.asFloatBuffer();
	
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

	public boolean drawPoint(Point2D p, Color32 c) {
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
			p.x += 1;
			p.x /= 2;
			p.y += 1;
			p.y /= 2;
			//System.out.println("x: " + p.x + " y: " + p.y);
			//System.out.println((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 4);
			try {
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3, c.getRAsByte());
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3 + 1 , c.getGAsByte());
			this.byteBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3 + 2 , c.getBAsByte());
			} catch (IndexOutOfBoundsException e) {
				System.out.println((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 3);
				System.out.println("x: " + p.x + "  |   p.y: " + p.y);
			}
			//this.doubleBuffer = this.doubleBuffer.put((((int) (p.y * this.height)) * this.width  + (int) (p.x * this.width)) * 4 + 3, (float) 1);
            //glEnd();
		}
		return this.isAvailable;
	}
	
	public boolean drawLine(Point2D p1, Point2D p2, Color32 c) {
		if(this.isAvailable) {
			if(drawing != Drawing.LINES) {
				glEnd();
				glBegin(GL_LINES);
				drawing = Drawing.LINES;
			}
			glColor3d(c.getR(), c.getG(), c.getB());
            glVertex2d(p1.getX(), p1.getY());
            glVertex2d(p2.getX(), p2.getY());
            //glEnd();
		}
		return this.isAvailable;
	}

	public boolean drawTriangle(Triangle2D triangle, Color32 c, boolean filled) {
		if(this.isAvailable) {
			if(!filled) {
				if(this.drawing != Drawing.NOT_DRAWING) {
					glEnd();
				}
				glBegin(GL_LINE_LOOP);
				drawing = Drawing.LINE_LOOP;
			}else {
				if(drawing != Drawing.TRIANGLES) {
					glEnd();
					glBegin(GL_TRIANGLES);
					drawing = Drawing.TRIANGLES;
				}
			}
			//glBegin(filled ? GL_TRIANGLES : GL_LINE_LOOP);
			glColor3d(c.getR(), c.getG(), c.getB());
			for (int i = 0; i < 3; i++)
				glVertex2d(triangle.getVertex(i).getX(), triangle.getVertex(i).getY());
			//glEnd();
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
			BufferUtils.zeroBuffer(byteBuffer);
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
