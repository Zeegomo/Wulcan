package wulcan.graphics;

import org.lwjgl.glfw.GLFW;

import wulcan.Point3D;
import wulcan.Projector;

public class OpenGLGraphicEnviroment implements GraphicEnviroment {

	private View2D view;
	private InputController controller;
	private Projector projector;
	
	public OpenGLGraphicEnviroment(Projector projector) {
		this.projector = projector;
		this.view = new OpenGLView(1200, 800);
		this.controller = new OpenGLInputController(view.getWindow());
		this.controller.setCallback(Key.ESCAPE, () -> GLFW.glfwSetWindowShouldClose(view.getWindow(), true));
		this.controller.setCallback(Key.W, () -> {
			this.projector.translateCamera(new Point3D(0,0,-0.1));
			System.out.println("w");
		});
		this.controller.setCallback(Key.S, () -> {
			this.projector.translateCamera(new Point3D(0,0,0.1));
			System.out.println("w");
		});
		this.controller.setCallback(Key.A, () -> {
			this.projector.translateCamera(new Point3D(-0.1,0,0));
			System.out.println("w");
		});
		this.controller.setCallback(Key.D, () -> {
			this.projector.translateCamera(new Point3D(0.1,0,0));
			System.out.println("w");
		});
		this.view.setController(this.controller);
	}
	
	public View2D getView() {
		return this.view;
	}


	public InputController getController() {
		return this.controller;
	}

}
