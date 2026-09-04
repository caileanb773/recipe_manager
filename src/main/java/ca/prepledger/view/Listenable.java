package view;

import java.awt.event.ActionListener;

/*
 * Author: Cailean Bernard
 * Contents: Classes that implement this interface will need to perform callbacks
 * on the controller based on internal state changes.
 */
public interface Listenable {
	
	/**
	 * Registers the Controller as a listener within the screen.
	 * Controller extends ActionListener, but they different names
	 * for the same thing here.
	 * 
	 * @param listener The instance of the Controller to register.
	 */
	public void registerController(ActionListener listener);

}
