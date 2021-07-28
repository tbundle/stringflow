package abs.sf.ads.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Group implements Entity {
	public static final String JID = "jid";
	public static final String SUBJECT = "subject";
	public static final String ACCESSMODE = "accessMode";
	public static final String MEMBERS = "members";
	public static final String NICKNAME = "nickName";
	public static final String AFFILIATION = "affiliation";
	public static final String ROLE = "role";
	public static final String MEMBER_JID = "members.jid";

	@Id
	private String jid;
	private String name;
	private String subject;
	private AccessMode accessMode;
	private Set<GroupMember> members;

	public Group() {
		this.members = new HashSet<>();
	}

	public Group(String jid) {
		this(jid, null, null, null, true);
	}

	public Group(String jid, String name, String subject, AccessMode accessMode) {
		this(jid, name, subject, accessMode, true);
	}

	public Group(String jid, String name, String subject, AccessMode accessMode, boolean status) {
		this(jid, name, subject, accessMode, true, new HashSet<>());
	}

	public Group(String jid, String name, String subject, AccessMode accessMode, boolean status,
			Set<GroupMember> members) {
		this.jid = jid;
		this.name = name;
		this.subject = subject;
		this.accessMode = accessMode;
		this.members = members;
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

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public AccessMode getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}

	public Set<GroupMember> getMembers() {
		return members;
	}

	public void setMembers(Set<GroupMember> members) {
		this.members = members;
	}

	public boolean addMember(GroupMember member) {
		return this.members.add(member);
	}

	public static class GroupMember {
		private String jid;
		private String nickName;
		private Affiliation affiliation;
		private Role role;

		public GroupMember() {

		}

		public GroupMember(String jid, String nickName) {
			this(jid, nickName, null, null);
		}

		public GroupMember(String jid, String nickName, Affiliation affiliation, Role role) {
			this.jid = jid;
			this.nickName = nickName;
			this.affiliation = affiliation;
			this.role = role;
		}

		public Affiliation getAffiliation() {
			return affiliation;
		}

		public void setAffiliation(Affiliation affiliation) {
			this.affiliation = affiliation;
		}

		public Role getRole() {
			return role;
		}

		public void setRole(Role role) {
			this.role = role;
		}

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public String getJid() {
			return jid;
		}

		public void setJid(String jid) {
			this.jid = jid;
		}

	}

	public enum Affiliation {
		MEMBER("member"), ADMIN("admin"), OWNER("owner"), OUTCAST("outcast"), NONE("none");

		private String val;

		private Affiliation(String val) {
			this.val = val;
		}

		public String val() {
			return this.val;
		}

		public static Affiliation valueFrom(String val) throws IllegalArgumentException {
			for (Affiliation affliliation : values()) {
				if (affliliation.val().equalsIgnoreCase(val)) {
					return affliliation;
				}
			}
			throw new IllegalArgumentException("No Affiliation for value [" + val + "]");
		}
	}

	public enum Role {
		PARTICIPANT("participant"), MODERATOR("moderator"), VISITOR("visitor"), NONE("none");

		private String val;

		private Role(String val) {
			this.val = val;
		}

		public String val() {
			return this.val;
		}

		public static Role valueFrom(String val) throws IllegalArgumentException {
			for (Role role : values()) {
				if (role.val().equalsIgnoreCase(val)) {
					return role;
				}
			}

			throw new IllegalArgumentException("No Role for value [" + val + "]");
		}
	}

	public enum AccessMode {
		PUBLIC("public"), PRIVATE("private");

		private String val;

		private AccessMode(String val) {
			this.val = val;
		}

		public String val() {
			return this.val;
		}

		public static AccessMode valueFrom(String val) throws IllegalArgumentException {
			for (AccessMode accessMode : values()) {
				if (accessMode.val().equalsIgnoreCase(val)) {
					return accessMode;
				}
			}

			throw new IllegalArgumentException("No accessMode for value [" + val + "]");
		}

	}

}
