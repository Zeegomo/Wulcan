package wulcan.graphics.java2d;

public interface InputController{

	public void setCallback(final Key k, final Runnable r);
	
	public void poll();
	
	
}
