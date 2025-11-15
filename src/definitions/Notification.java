package definitions;

import java.time.LocalDateTime;

public class Notification {

	private LocalDateTime timeSent;
	private StaffMember sender;
	private NotificationType notificationType;
	private Recipe recipe;
	private String optionalNotes;


	public Notification(LocalDateTime time, StaffMember sender, NotificationType nType,
			Recipe recipe) {
		this.timeSent = time;
		this.sender = sender;
		this.notificationType = nType;
		this.recipe = recipe;
		optionalNotes = null;
	}
	
	public Notification(LocalDateTime time, StaffMember sender, NotificationType nType,
			Recipe recipe, String optionalNotes) {
		this.timeSent = time;
		this.sender = sender;
		this.notificationType = nType;
		this.recipe = recipe;
		this.optionalNotes = optionalNotes;
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
