package ca.prepledger.session;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Manages details about the type of session the user is using the application 
 * in as well as the connection status of the application. A user could, for 
 * example, choose to use the application in offline mode. They cannot, however, 
 * choose if the application, for any reason, cannot reach the back-end. For 
 * this reason, the two ideas are distinct.
 * 
 * @author Cailean Bernard
 * @since v0.1.3.
 */
public class ConnectionManager {
	
	private final SessionMode sessionMode;
	private final ObjectProperty<ConnectionStatus> connectionStatus =
			new SimpleObjectProperty<>(ConnectionStatus.DISCONNECTED);
	
	public ConnectionManager(SessionMode sessionMode) {
		this.sessionMode = sessionMode;
	}
	
	public ConnectionStatus getConnectionStatus() {
		return connectionStatus.get();
	}
	
	public ObjectProperty<ConnectionStatus> connectionStatusProperty(){
		return connectionStatus;
	}
	
	public SessionMode getSessionMode() {
		return sessionMode;
	}
	
	public boolean isOfflineMode() {
		return sessionMode == SessionMode.OFFLINE;
	}
}
