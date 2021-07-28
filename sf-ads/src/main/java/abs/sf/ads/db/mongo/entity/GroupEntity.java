package abs.sf.ads.db.mongo.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import abs.sf.ads.entity.Group;
import abs.sf.ads.utils.ApplicationConstants;

@Document(collection = ApplicationConstants.MONGO_GROUP_COLLECTION_NAME)
public class GroupEntity extends Group implements MongoEntity {
	public static final String STATUS = "status";

	private int status;
	private long createTime;
	private long lastModifiedTime;

	public GroupEntity() {
		// TODO Auto-generated constructor stub
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
