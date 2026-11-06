package ca.prepledger.session;

/**
 * Interface for handling session data; when the user successfully logs in, a new
 * session is generated. When they log out, the session is invalidated.
 * 
 * @author Cailean Bernard
 * @since v0.1.1
 */
public interface SessionHandler {
	
	void onLoginSuccess(UserSession session);
	
	void onLogout();

}
