package wulcan.graphics;

public interface InputController{

	public void setCallback(final int key, final Runnable r);
	
	public void poll();
	
	
}
