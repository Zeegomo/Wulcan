package wulcan.graphics;

import org.lwjgl.glfw.GLFW;

public class OpenGLGraphicEnviroment implements GraphicEnviroment {

	private View2D view;
	private InputController controller;
	
	public OpenGLGraphicEnviroment() {
		this.view = new OpenGLView(1200, 800);
		this.controller = new OpenGLInputController(view.getWindow());
		this.controller.setCallback(Key.ESCAPE, () -> GLFW.glfwSetWindowShouldClose(view.getWindow(), true));
		this.view.setController(this.controller);
	}
	
	public View2D getView() {
		return this.view;
	}


	public InputController getController() {
		return this.controller;
	}

}
