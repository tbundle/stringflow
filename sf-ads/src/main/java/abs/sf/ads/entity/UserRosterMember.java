package abs.sf.ads.entity;

import abs.sf.ads.utils.StringUtils;

public class UserRosterMember implements Entity {
	private String contactJid;
	private String contactName;

	public UserRosterMember() {
		// TODO Auto-generated constructor stub
	}

	public UserRosterMember(String contactJid, String contactName) {
		this.contactJid = contactJid;
		this.contactName = contactName;
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
	public boolean equals(Object obj) {
		UserRosterMember userRosterMember = (UserRosterMember) obj;
		return StringUtils.safeEquals(this.contactJid, userRosterMember.getContactJid());
	}

	@Override
	public int hashCode() {
		return this.contactJid.hashCode();
	}
}
