package view;

import javax.swing.ImageIcon;
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
	//private ImageIcon divider;	// separates each visual element of the notif btn 
	private final JLabel divider = new JLabel("|"); // might look okay as just "|"
	
	public NotificationButton(Notification notification) {
		new JButton();
		this.notification = notification;
		setFocusable(true);
		boolean displayWithNotes = false;
		
		// Store the values locally for now, maybe try without this if it is slow
		String notifType = notification.getNotificationType().name();
		String notifRcpName = notification.getRecipe().getTitle();
		String notifSenderName = notification.getSender().getName();
		String notifNotes = notification.getNotes();
		
		if (notifNotes != null && !notifNotes.isEmpty()) {
			displayWithNotes = true;
		}
		
		// Initialize button components with notification properties
		checkBx = new JCheckBox();
		type = new JLabel(notifType);
		rcpName = new JLabel(notifRcpName);
		sender = new JLabel(notifSenderName);
		
		if (displayWithNotes) {
			notes = new JLabel(notifNotes);			// Make sure to truncate this
		}
		
		expand = new JButton("X");
		
		// Lay out button components
		add(checkBx);
		add(divider);
		add(type);
		add(divider);
		add(rcpName);
		add(divider);
		add(sender);
		add(divider);
		
		// probably will need to add some kind of placeholder if we're not displaying notes
		if (displayWithNotes) {
			add(notes);
			add(divider);
		}
		
		add(expand);
	}
	
}
