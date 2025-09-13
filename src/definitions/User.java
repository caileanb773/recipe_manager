package definitions;

/*
 * Author: Cailean Bernard
 * Contents: A user is represented by at least an email, as well as other optional
 * descriptive information (DoB, location, name)
 */

public class User {
	
	private String email;
	private String password; // this is temporary
	
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	// XXX delete this
	public String getPassword() {
		return password;
	}

}
