package view;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import definitions.Notification;

/*
 * Author: Cailean Bernard
 * Contents: The graphical representation of a Notification object. Contains some
 * 
 */

public class NotificationButton extends JButton {

	private Notification notification;
	private JCheckBox checkBx;
	private JLabel type;
	private JLabel rcpName;
	private JLabel sender;
	private JLabel notes;
	private JButton expand;
	
	
	public NotificationButton(Notification notification) {
		new JButton();
		this.notification = notification;
		setFocusable(true);
		
		// Store the values locally for now, maybe try without this if it is slow
		String notifType = notification.getNotificationType().name();
		String notifRcpName = notification.getRecipe().getTitle();
		String notifSenderName = notification.getSender().getName();
		String notifNotes = notification.getNotes();
		
		checkBx = new JCheckBox();
		type = new JLabel(notifType);
		rcpName = new JLabel(notifRcpName);
		sender = new JLabel(notifSenderName);
		notes = new JLabel(notifNotes);			// Make sure to truncate this
		expand = new JButton("X");
	}
	
}
