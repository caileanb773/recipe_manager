package view;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import definitions.Constants;
import definitions.Notification;

/*
 * Author: Cailean Bernard
 * Contents: The graphical representation of a Notification object. Contains some
 * 
 */

public class NotificationPanel extends JPanel {

	private Notification notification;
	private JCheckBox checkBx;
	private JLabel timeStamp;
	private JLabel type;
	private JLabel rcpName;
	private JLabel sender;
	private JLabel notes;
	private JButton expand;
	//private ImageIcon divider;	// separates each visual element of the notif btn 
	
	public NotificationPanel(Notification notification) {
		new JPanel(new FlowLayout());
		Border emptyBorder = BorderFactory.createEmptyBorder(2,2,0,2);
		setBorder(BorderFactory.createCompoundBorder(emptyBorder,
				Constants.softRaisedBorder));
		this.notification = notification;
		boolean displayWithNotes = false;
		
		// Store the values locally for now, maybe try without this if it is slow
		String time = notification.timeString();
		String notifType = notification.getNotificationType().name();
		String notifRcpName = notification.getRecipe().getTitle();
		String notifSenderName = notification.getSender().getName();
		String notifNotes = notification.getNotes();
		
		if (notifNotes != null && !notifNotes.isEmpty()) {
			displayWithNotes = true;
		}
		
		// Initialize button components with notification properties
		checkBx = new JCheckBox();
		timeStamp = new JLabel(time);
		type = new JLabel(notifType);
		rcpName = new JLabel(notifRcpName);
		sender = new JLabel(notifSenderName);
		
		if (displayWithNotes) {
			notes = new JLabel(notifNotes);			// Make sure to truncate this
		}
		
		expand = new JButton("X");
		
		// Lay out button components
		add(checkBx);
		add(timeStamp);
		add(new JLabel("|"));
		add(type);
		add(new JLabel("|"));
		add(rcpName);
		add(new JLabel("|"));
		add(sender);
		add(new JLabel("|"));
		
		// probably will need to add some kind of placeholder if we're not displaying notes
		if (displayWithNotes) {
			add(notes);
			add(new JLabel("|"));
		}
		
		add(expand);
	}
	
	public Notification getNotification() {
		return this.notification;
	}
	
	public void setSelected(boolean isSelected) {
		this.checkBx.setSelected(isSelected);
	}
	
}
