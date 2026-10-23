package ca.prepledger.app;

public interface SessionHandler {
	
	void onLoginSuccess(UserSession session);
	
	void onLogout();

}
