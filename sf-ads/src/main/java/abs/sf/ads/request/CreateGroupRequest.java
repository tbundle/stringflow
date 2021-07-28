package abs.sf.ads.request;

import javax.validation.constraints.NotNull;

import abs.sf.ads.entity.Group.AccessMode;

public class CreateGroupRequest {
	@NotNull
	private String name;
	private String subject;
	private AccessMode accessMode;
	@NotNull
	private String ownerJid;
	private String ownerNickName;
	@NotNull
	private String domain;

	public CreateGroupRequest() {

	}

	public CreateGroupRequest(String name, String subject, AccessMode accessMode, String ownerJid, String ownerNickName,
			String domain) {
		this.name = name;
		this.subject = subject;
		this.accessMode = accessMode;
		this.ownerJid = ownerJid;
		this.ownerNickName = ownerNickName;
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public AccessMode getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}

	public String getOwnerJid() {
		return ownerJid;
	}

	public void setOwnerJid(String ownerJid) {
		this.ownerJid = ownerJid;
	}

	public String getOwnerNickName() {
		return ownerNickName;
	}

	public void setOwnerNickName(String ownerNickName) {
		this.ownerNickName = ownerNickName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
