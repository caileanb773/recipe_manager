package model;

/**
 * Author: Cailean Bernard
 * Contents: NotificationListener alerts subscribers of its events when the state
 * of the notification service has changed (a new notification has arrived, or
 * a notification has been marked as read, etc.)
 */
public interface NotificationListener {
	
	/**
	 * This is called when a notification is not just "received" or removed, but
	 * when several are "seen" at the same time: i.e., at start up when there may
	 * be several unread notifications.
	 */
	void notificationsChanged();
	
	/**
	 * Called when just one notification is added and the application is running.
	 */
	void notificationAdded();
	
	/**
	 * Called when a notification is deleted.
	 */
	void notificationRemoved();
	
	/**
	 * Called when a notification is marked as read.
	 */
	void notificationMarkedAsRead();
	
	/**
	 * Called when a notification is marked as unread.
	 */
	void notificationMarkedAsUnread();

}
