package abs.ixi.server.muc;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import abs.ixi.server.packet.JID;
import abs.ixi.server.packet.xmpp.BareJID;

public class ChatRoom implements Serializable {
	private static final long serialVersionUID = 8242951448276397196L;

	private String name;
	private String subject;
	private BareJID roomJID;
	private AccessMode accessMode;
	private Set<ChatRoomMember> members;

	public ChatRoom(BareJID roomJID) {
		this(roomJID, null, null, null, true);
	}

	public ChatRoom(BareJID roomJID, String name, String subject, AccessMode accessMode) {
		this(roomJID, name, subject, accessMode, true);
	}

	public ChatRoom(BareJID roomJID, String name, String subject, AccessMode accessMode, boolean status) {
		this.roomJID = roomJID;
		this.name = name;
		this.subject = subject;
		this.accessMode = accessMode;
		this.members = new HashSet<>();
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

	public BareJID getRoomJID() {
		return roomJID;
	}

	public void setRoomJID(BareJID roomJID) {
		this.roomJID = roomJID;
	}

	public AccessMode getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(AccessMode accessMode) {
		this.accessMode = accessMode;
	}

	public Set<ChatRoomMember> getMembers() {
		return members;
	}

	public void setMembers(Set<ChatRoomMember> members) {
		this.members = members;
	}

	public boolean addMember(ChatRoomMember member) {
		return this.members.add(member);
	}

	public boolean delMember(BareJID userJID) {
		ChatRoomMember member = getMember(userJID);
		return delMember(member);
	}

	public boolean delMember(ChatRoomMember member) {
		return this.members.remove(member);
	}

	public ChatRoomMember getMember(BareJID userJID) {
		for (ChatRoomMember member : members) {
			if (member.getUserJID().equals(userJID)) {
				return member;
			}
		}

		return null;
	}

	public boolean isRoomMember(BareJID userJID) {
		return getMember(userJID) != null;
	}

	public ChatRoomMember getMemberByNickName(String nickName) {
		for (ChatRoomMember member : members) {
			if (member.getNickName().equals(nickName)) {
				return member;
			}
		}
		return null;
	}

	public boolean doesNickNameConflict(ChatRoomMember member) {
		boolean result = false;
		if (getMemberByNickName(member.getNickName()) != null) {
			result = true;
		}
		return result;
	}

	public boolean isRoomOwner(BareJID bareJID) {
		ChatRoomMember member = getMember(bareJID);

		return member != null && member.getAffiliation() == Affiliation.OWNER;

	}

	public boolean isRoomAdmin(BareJID bareJID) {
		ChatRoomMember member = getMember(bareJID);

		return member != null && member.getAffiliation() == Affiliation.ADMIN;
	}

	public class ChatRoomMember implements Serializable {
		private static final long serialVersionUID = -670197123230086582L;

		private String nickName;
		private BareJID userJID;
		private Affiliation affiliation;
		private Role role;

		public ChatRoomMember(JID userJID, String nickName) {
			this(userJID, nickName, null, null);
		}

		public ChatRoomMember(JID userJID, String nickName, Affiliation affiliation, Role role) {
			this.userJID = userJID.getBareJID();
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

		public boolean equals(ChatRoomMember member) {
			if (roomJID.equals(member.getRoomJID()) && this.userJID.equals(member.getUserJID())) {
				return true;
			}

			return false;
		}

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public BareJID getRoomJID() {
			return roomJID;
		}

		public BareJID getUserJID() {
			return userJID;
		}

		public void setUserJID(BareJID userJID) {
			this.userJID = userJID;
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
			for (AccessMode role : values()) {
				if (role.val().equalsIgnoreCase(val)) {
					return role;
				}
			}

			throw new IllegalArgumentException("No mode for value [" + val + "]");
		}

	}

}
