package wulcan;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class OpenGLView implements View2D {
	
	private boolean isAvailable = false;
	private long window;
	private int height;
	private int width;

	public OpenGLView(int height, int width) {
		this.width = width;
		this.height = height;
		this.isAvailable = true;
		this.init();
	}
	
	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public boolean drawPoint(Point2D p, Color32 c) {
		if(this.isAvailable) {
			glPointSize(2);
			glBegin(GL_POINTS);
			glColor3d(c.getR(), c.getG(), c.getB());
            glVertex2d(p.getX(), p.getY());
            glEnd();
		}
		return this.isAvailable;
	}
	
	public boolean drawLine(Point2D p1, Point2D p2, Color32 c) {
		if(this.isAvailable) {
			glBegin(GL_LINES);
			glColor3d(c.getR(), c.getG(), c.getB());
            glVertex2d(p1.getX(), p1.getY());
            glVertex2d(p2.getX(), p2.getY());
            glEnd();
		}
		return this.isAvailable;
	}

	public boolean drawTriangle(Triangle2D triangle, Color32 c, boolean filled) {
		if(this.isAvailable) {
			glBegin(filled ? GL_TRIANGLES : GL_LINE_LOOP);
			glColor3d(c.getR(), c.getG(), c.getB());
			for (int i = 0; i < 3; i++)
				glVertex2d(triangle.getVertex(i).getX(), triangle.getVertex(i).getY());
			glEnd();
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
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

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
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		glfwSwapBuffers(window);
	}
	
	public void nextFrame() {
		if (!glfwWindowShouldClose(window)) {
			glfwSwapBuffers(window);
			glClear(GL_COLOR_BUFFER_BIT);
			glfwPollEvents();
			if(updateSize()) {
				glViewport(0, 0, this.width, this.height);
			}

		} else
			this.close();
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
}
