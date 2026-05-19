package definitions;

/*
 * Author: Cailean Bernard
 * Contents: A user is represented by at least an email, as well as other optional
 * descriptive information (DoB, location, name)
 */

public class StaffMember {

	private int id;
	private String email;
	private String name;

	public StaffMember(int id, String email, String name) {
		if (id < 0 || email == null || name == null || email.isEmpty() || name.isEmpty()) {
			return;
		}

		this.id = id;
		this.email = email;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append(" ");
		sb.append(email);
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		return sb.toString();
	}

}
