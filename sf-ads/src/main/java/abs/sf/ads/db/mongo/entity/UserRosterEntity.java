package abs.sf.ads.db.mongo.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import abs.sf.ads.utils.ApplicationConstants;

@Document(collection = ApplicationConstants.MONGO_USER_ROSTER_COLLECTION_NAME)
public class UserRosterEntity implements MongoEntity {
	public static final String USER_JID = "userJid";
	public static final String CONTACT_JID = "contactJid";
	public static final String CONTACT_NAME = "contactName";
	public static final String STATUS = "status";
	public static final String VERSION = "version";
	public static final String ROSTER_MEMBERS = "rosterMembers";
	public static final String ROSTER_CONATCT_NAME = "rosterMembers.contactName";

	private String userJid;
	private List<RosterMember> rosterMembers;

	public String getUserJid() {
		return userJid;
	}

	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}

	public List<RosterMember> getRosterMembers() {
		return rosterMembers;
	}

	public void setRosterMembers(List<RosterMember> rosterMembers) {
		this.rosterMembers = rosterMembers;
	}

	public static class RosterMember {
		private String contactJid;
		private String contactName;
		private int status;
		private int version;

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

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}

	}
}
