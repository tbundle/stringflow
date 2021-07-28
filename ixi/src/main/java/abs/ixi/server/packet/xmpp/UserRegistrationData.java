package abs.ixi.server.packet.xmpp;

import java.io.Serializable;

public class UserRegistrationData implements Serializable {
	private static final long serialVersionUID = 8700974202940919075L;

	private BareJID jabberId;
	private String userName;
	private String password;
	private String email;
	private boolean remove;

	public BareJID getJabberId() {
		return jabberId;
	}

	public void setJabberId(BareJID jabberId) {
		this.jabberId = jabberId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}
}
