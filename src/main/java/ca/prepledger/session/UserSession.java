package ca.prepledger.session;

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
