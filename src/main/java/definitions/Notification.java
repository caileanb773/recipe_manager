package definitions;

import java.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Author: Cailean Bernard
 * Contents: Notification definition and helper methods.
 */
public class Notification {

	// Data Members
	private LocalDateTime timeSent;
	private StaffMember sender;
	private NotificationType notificationType;
	private Recipe recipe;
	private String optionalNotes;
	private boolean isActive;
	private boolean isSelected;

	// Other
	private static final Logger logger = LoggerFactory.getLogger(Notification.class);
	
	// Constants
	private static final boolean INACTIVE = false;
	private static final boolean ACTIVE = true;

	public Notification(LocalDateTime time, StaffMember sender, NotificationType nType,
			Recipe recipe) {
		this.timeSent = time;
		this.sender = sender;
		this.notificationType = nType;
		this.recipe = recipe;
		optionalNotes = null;
		isActive = ACTIVE;	
		isSelected = false;
	}
	
	public Notification(LocalDateTime time, StaffMember sender, NotificationType nType,
			Recipe recipe, String optionalNotes) {
		this.timeSent = time;
		this.sender = sender;
		this.notificationType = nType;
		this.recipe = recipe;
		this.optionalNotes = optionalNotes;
		isActive = ACTIVE;
		isSelected = false;
	}

	public StaffMember getSender() {
		return sender;
	}


	public void setSender(StaffMember sender) {
		this.sender = sender;
	}


	public NotificationType getNotificationType() {
		return notificationType;
	}


	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}


	public LocalDateTime getTimeSent() {
		return timeSent;
	}


	public void setTimeSent(LocalDateTime timeSent) {
		this.timeSent = timeSent;
	}
	
	public Recipe getRecipe() {
		return this.recipe;
	}
	
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
	
	public String getNotes() {
		return optionalNotes;
	}
	
	public void setNotes(String notes) {
		this.optionalNotes = notes;
	}
		
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Sets an active recipe as inactive. Cannot set an inactive recipe as
	 * active.
	 */
	public void setInactive() {
		if (isActive == ACTIVE) {
			isActive = INACTIVE;
		}
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean selected) {
		isSelected = selected;
	}
	
	public String timeString() {
		
		// Format: 1969-07-24 16:50
		// Military time only for now
		StringBuilder sb = new StringBuilder();
		sb.append(timeSent.getYear());
		sb.append("-");
		sb.append(timeSent.getMonthValue());
		sb.append("-");
		sb.append(timeSent.getDayOfMonth());
		sb.append(" ");
		sb.append(timeSent.getHour());
		sb.append(":");
		sb.append(timeSent.getMinute());
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(timeSent).toString();
		sb.append(" ");
		sb.append(sender.getEmail());
		sb.append(" ");
		sb.append(notificationType);
		sb.append(" ");
		sb.append(recipe.getTitle());
		
		if (optionalNotes != null) {
			sb.append(" ");
			sb.append(optionalNotes);
		}
		
		return sb.toString();
	}

}