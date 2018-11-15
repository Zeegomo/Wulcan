package wulcan;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

public class InputController {
	
	final private long window;
	final Map< Integer, Runnable> callbacks;
	
	public InputController(final long window) {
		this.window = window;
		callbacks = new HashMap<>();
	}
	
	public void setCallback(final int key, Runnable r) {
		/*glfwSetKeyCallback(outerWindow, (window, keys, scancode, action, mods) -> {
			if ( keys == key && action == GLFW_PRESS )
				r.run(); // We will detect this in the rendering loop
		});*/
		callbacks.put(key, r);
	}
	
	public void poll() {
		Set<Map.Entry<Integer, Runnable>> keys = callbacks.entrySet();
		for(Map.Entry<Integer, Runnable> m : keys) {
			if (GLFW.glfwGetKey(this.window, m.getKey()) == GLFW_PRESS) {
				m.getValue().run();
			}
		}
	}
	
}
