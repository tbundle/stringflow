package abs.sf.ads.db.mongo.entity;

import java.util.Set;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import abs.sf.ads.utils.ApplicationConstants;

@Document(collection = ApplicationConstants.MONGO_PRESENCE_SUBSCRIPTION_COLLECTION_NAME)
public class PresenseSubscriptionEntity implements MongoEntity {
	public static final String USER_JID = "userJid";
	public static final String SUBSCRIBER_JID = "subscriberJids";

	@Id
	private String userJid;
	private Set<String> subscriberJids;

	public PresenseSubscriptionEntity() {

	}

	public PresenseSubscriptionEntity(String userJid, Set<String> subscriberJid) {
		this.userJid = userJid;
		this.subscriberJids = subscriberJid;
	}

	public String getUserJid() {
		return userJid;
	}

	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}

	public Set<String> getSubscriberJids() {
		return subscriberJids;
	}

	public void setSubscriberJids(Set<String> subscriberJids) {
		this.subscriberJids = subscriberJids;
	}

}
