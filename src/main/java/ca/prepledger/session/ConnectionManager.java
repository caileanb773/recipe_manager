package ca.prepledger.session;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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
