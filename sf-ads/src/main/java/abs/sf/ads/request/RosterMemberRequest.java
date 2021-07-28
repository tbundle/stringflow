package abs.sf.ads.request;

import javax.validation.constraints.NotNull;

public class RosterMemberRequest {
	@NotNull
	private String userJid;
	@NotNull
	private String contactJid;

	private String contactName;

	public RosterMemberRequest() {
		// TODO Auto-generated constructor stub
	}

	public String getUserJid() {
		return userJid;
	}

	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}

	public String getContactJid() {
		return contactJid;
	}

	public void setContactJid(String contactJid) {
		this.contactJid = contactJid;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	@Override
	public String toString() {
		return "RosterMemberRequest[userJid : " + this.userJid + ", contactJid : " + this.contactJid
				+ ", ContactName : " + this.contactName + "]";
	}

}
