package model;

import java.awt.event.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Listenable;

/*
 * Author: Cailean Bernard
 * Contents: Manages read notifications vs. unread notifications and handles when
 * incoming notifications.
 */
public class NotificationService implements Listenable {
	
	private ActionListener listener;
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	@Override
	public void registerController(ActionListener listener) {
		listener = listener;
	}
	
}
