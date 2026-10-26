package ca.prepledger.session;

public interface SessionHandler {
	
	void onLoginSuccess(UserSession session);
	
	void onLogout();

}
