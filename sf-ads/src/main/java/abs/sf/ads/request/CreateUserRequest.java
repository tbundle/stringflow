package abs.sf.ads.request;

import javax.validation.constraints.NotNull;

public class CreateUserRequest {
	@NotNull
	private String userId;
	@NotNull
	private String userName;
	@NotNull
	private String email;
	@NotNull
	private String domain;
	@NotNull
	private String password;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "CreateUserRequest [userId=" + userId + ", userName=" + userName + ", email=" + email + ", domain="
				+ domain + ", password=" + password + "]";
	}

}
