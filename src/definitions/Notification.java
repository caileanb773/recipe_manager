package definitions;

import java.time.LocalDateTime;

/**
 * Author: Cailean Bernard
 * Contents: Notification definition and helper methods.
 */
public class Notification {

	private LocalDateTime timeSent;
	private StaffMember sender;
	private NotificationType notificationType;
	private Recipe recipe;
	private String optionalNotes;
	private boolean isRead;


	public Notification(LocalDateTime time, StaffMember sender, NotificationType nType,
			Recipe recipe) {
		this.timeSent = time;
		this.sender = sender;
		this.notificationType = nType;
		this.recipe = recipe;
		optionalNotes = null;
		this.isRead = false;
	}
	
	public Notification(LocalDateTime time, StaffMember sender, NotificationType nType,
			Recipe recipe, String optionalNotes) {
		this.timeSent = time;
		this.sender = sender;
		this.notificationType = nType;
		this.recipe = recipe;
		this.optionalNotes = optionalNotes;
		this.isRead = false;
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
	
	public boolean isRead() {
		return isRead;
	}
	
	public void setRead(boolean read) {
		isRead = read;
	}
	
	public String timeString() {
		
		// Format: 1999-06-25 14:30
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
