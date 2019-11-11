package wulcan.graphics;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableMap;

public class OpenGLInputController implements InputController{

	final private long window;
	final Map< Integer, Runnable> callbacks;
	final static ImmutableMap<Key, Integer> keymap = ImmutableMap.<Key, Integer>builder()
				.put(Key.Q, GLFW.GLFW_KEY_Q)
				.put(Key.W, GLFW.GLFW_KEY_W)
				.put(Key.E, GLFW.GLFW_KEY_E)
				.put(Key.R, GLFW.GLFW_KEY_R)
				.put(Key.T, GLFW.GLFW_KEY_T)
				.put(Key.Y, GLFW.GLFW_KEY_Y)
				.put(Key.U, GLFW.GLFW_KEY_U)
				.put(Key.I, GLFW.GLFW_KEY_I)
				.put(Key.O, GLFW.GLFW_KEY_O)
				.put(Key.P, GLFW.GLFW_KEY_P)
				.put(Key.A, GLFW.GLFW_KEY_A)
				.put(Key.S, GLFW.GLFW_KEY_S)
				.put(Key.D, GLFW.GLFW_KEY_D)
				.put(Key.F, GLFW.GLFW_KEY_F)
				.put(Key.G, GLFW.GLFW_KEY_G)
				.put(Key.H, GLFW.GLFW_KEY_H)
				.put(Key.J, GLFW.GLFW_KEY_J)
				.put(Key.K, GLFW.GLFW_KEY_K)
				.put(Key.L, GLFW.GLFW_KEY_L)
				.put(Key.Z, GLFW.GLFW_KEY_Z)
				.put(Key.X, GLFW.GLFW_KEY_X)
				.put(Key.C, GLFW.GLFW_KEY_C)
				.put(Key.V, GLFW.GLFW_KEY_V)
				.put(Key.B, GLFW.GLFW_KEY_B)
				.put(Key.N, GLFW.GLFW_KEY_N)
				.put(Key.M, GLFW.GLFW_KEY_M)
				.put(Key.N1, GLFW.GLFW_KEY_1)
				.put(Key.N2, GLFW.GLFW_KEY_2)
				.put(Key.N3, GLFW.GLFW_KEY_3)
				.put(Key.N4, GLFW.GLFW_KEY_4)
				.put(Key.N5, GLFW.GLFW_KEY_5)
				.put(Key.N6, GLFW.GLFW_KEY_6)
				.put(Key.N7, GLFW.GLFW_KEY_7)
				.put(Key.N8, GLFW.GLFW_KEY_8)
				.put(Key.N9, GLFW.GLFW_KEY_9)
				.put(Key.N0, GLFW.GLFW_KEY_0)
				.put(Key.ESCAPE, GLFW.GLFW_KEY_ESCAPE)
				.put(Key.ENTER, GLFW.GLFW_KEY_SPACE)
				.put(Key.SPACE, GLFW.GLFW_KEY_ENTER)
				.put(Key.DELETE, GLFW.GLFW_KEY_DELETE)
				.build();

	public OpenGLInputController(final long window) {
		this.window = window;
		callbacks = new HashMap<>();
	}
	
	public void setCallback(final Key k, final Runnable r) {
		/*glfwSetKeyCallback(outerWindow, (window, keys, scancode, action, mods) -> {
			if ( keys == key && action == GLFW_PRESS )
				r.run(); // We will detect this in the rendering loop
		});*/
		callbacks.put(keymap.get(k), r);
	}
	
	public void poll() {
		glfwPollEvents();
		Set<Map.Entry<Integer, Runnable>> keys = callbacks.entrySet();
		for(Map.Entry<Integer, Runnable> m : keys) {
			if (GLFW.glfwGetKey(this.window, m.getKey()) == GLFW_PRESS) {
				m.getValue().run();
			}
		}
	}
	
}
