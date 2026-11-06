package ca.prepledger.session;

/**
 * Sessions are generated with cryptographically secure tokens that can be used
 * to determine whether a user should be able to "skip" the login phase; if their
 * session is still valid, they can close and open the application freely and skip
 * the login phase. Sessions are invalidated on logout or for other reasons.
 * 
 * @author Cailean Bernard
 * @since v0.1.1
 */
public class UserSession {
	
	private final String token;
	private final Long userId;
	private final String email;
	
	public UserSession(String token, Long userId, String email) {
		this.token = token;
		this.userId = userId;
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public Long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}	

}
