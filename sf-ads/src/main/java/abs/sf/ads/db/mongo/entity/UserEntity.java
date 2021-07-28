package abs.sf.ads.db.mongo.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import abs.sf.ads.entity.User;
import abs.sf.ads.utils.ApplicationConstants;

@Document(collection = ApplicationConstants.MONGO_USER_COLLECTION_NAME)
public class UserEntity extends User implements MongoEntity {
	public static final String STATUS = "status";
	public static final String PASSWORD = "password";
	public static final String ROSTER_VERSION_COUNT = "rosterVersionCount";

	private String password;
	private int rosterVersionCount;
	private int status;
	private long createTime;
	private long lastModifiedTime;

	public UserEntity() {

	}

	public UserEntity(String jid, String password, String firstName, String email) {
		super(jid, firstName, email);
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRosterVersionCount() {
		return rosterVersionCount;
	}

	public void setRosterVersionCount(int rosterVersionCount) {
		this.rosterVersionCount = rosterVersionCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

}
