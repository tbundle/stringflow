package abs.sf.ads.db.mongo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;

import abs.sf.ads.db.Database;
import abs.sf.ads.db.mongo.entity.GroupEntity;
import abs.sf.ads.db.mongo.entity.PresenseSubscriptionEntity;
import abs.sf.ads.db.mongo.entity.UserEntity;
import abs.sf.ads.db.mongo.entity.UserRosterEntity;
import abs.sf.ads.db.mongo.entity.UserRosterEntity.RosterMember;
import abs.sf.ads.entity.Group.AccessMode;
import abs.sf.ads.entity.Group.GroupMember;
import abs.sf.ads.entity.User;
import abs.sf.ads.entity.UserRosterMember;
import abs.sf.ads.request.ChangePassword;
import abs.sf.ads.utils.CollectionUtils;
import abs.sf.ads.utils.StringUtils;

public class MongoDb implements Database {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDb.class);

	private static final String DOT = ".";
	private static final String DOT_DOLLAR_DOT = ".$.";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public boolean createUser(String userJid, String password, String userName, String email, String domain) {
		LOGGER.debug("Creating new user with jid {}, name {} and domain {}", userJid, userName, domain);
		UserEntity entity = new UserEntity(userJid, password, userName, email);
		entity.setStatus(1);
		entity.setCreateTime(System.currentTimeMillis());
		entity.setLastModifiedTime(System.currentTimeMillis());

		return mongoTemplate.insert(entity) != null;
	}

	@Override
	public boolean updateUserDetail(User user) {
		LOGGER.debug("Updating user detail of user {}", user);
		Criteria jidCriteria = Criteria.where(UserEntity.JID).is(user.getJid());
		Query q = new Query(jidCriteria);

		Update u = new Update();

		if (user.getFirstName() != null) {
			u.set(UserEntity.FIRST_NAME, user.getFirstName());
		}

		if (user.getMiddleName() != null) {
			u.set(UserEntity.MIDDLE_NAME, user.getMiddleName());
		}

		if (user.getLastName() != null) {
			u.set(UserEntity.LAST_NAME, user.getLastName());
		}

		if (user.getNickName() != null) {
			u.set(UserEntity.NICK_NAME, user.getNickName());
		}

		if (user.getPhone() != null) {
			u.set(UserEntity.PHONE, user.getPhone());
		}

		if (user.getEmail() != null) {
			u.set(UserEntity.EMAIL, user.getEmail());
		}

		if (user.getGender() != null) {
			u.set(UserEntity.GENDER, user.getGender());
		}

		if (user.getBday() != null) {
			u.set(UserEntity.BDAY, user.getBday());
		}
		if (user.getAvtar() != null) {
			u.set(UserEntity.AVTAR, user.getAvtar());
		}

		if (user.getAddress() != null) {
			u.set(UserEntity.ADDRESS, user.getAddress());
		}

		if (user.getDescription() != null) {
			u.set(UserEntity.DESCRIPTION, user.getDescription());
		}

		UpdateResult result = mongoTemplate.updateFirst(q, u, UserEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public User getUserDetail(String jid) {
		LOGGER.debug("Fetching userDetail of jid", jid);
		Criteria jidCriteria = Criteria.where(UserEntity.JID).is(jid);

		Query q = new Query(jidCriteria);

		List<UserEntity> user = mongoTemplate.find(q, UserEntity.class);
		return user.get(0);
	}

	@Override
	public boolean deactivateUser(String jid) {
		LOGGER.debug("Deactivating user of jid {}", jid);
		Criteria jidCriteria = Criteria.where(UserEntity.JID).is(jid);

		Query q = new Query(jidCriteria);

		Update u = new Update();
		u.set(UserEntity.STATUS, 0);

		UpdateResult result = mongoTemplate.updateFirst(q, u, UserEntity.class);

		return result.getModifiedCount() > 0;

	}

	@Override
	public boolean activateUser(String jid) {
		LOGGER.debug("Activating user of jid {}", jid);
		Criteria jidCriteria = Criteria.where(UserEntity.JID).is(jid);

		Query q = new Query(jidCriteria);

		Update u = new Update();
		u.set(UserEntity.STATUS, 1);

		UpdateResult result = mongoTemplate.updateFirst(q, u, UserEntity.class);

		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean deleteUser(String jid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkIsUserExist(String jid) {
		Criteria jidCriteria = Criteria.where(UserEntity.JID).is(jid);

		Query q = new Query(jidCriteria);

		return mongoTemplate.exists(q, UserEntity.class);

	}

	@Override
	public boolean changePassword(ChangePassword changePassword) {
		Criteria jidCriteria = Criteria.where(UserEntity.JID).is(changePassword.getJid());
		Criteria passwordCriteria = Criteria.where(UserEntity.PASSWORD).is(changePassword.getOldPassword());

		Query q = new Query(new Criteria().andOperator(jidCriteria, passwordCriteria));

		Update u = new Update();
		u.set(UserEntity.PASSWORD, changePassword.getNewPassword());

		UpdateResult result = mongoTemplate.updateFirst(q, u, UserEntity.class);
		return result.getModifiedCount() > 0;
	}

	public String getOldPassword(String jid) {
		UserEntity entity = mongoTemplate.findById(jid, UserEntity.class);
		return entity.getPassword();
	}

	@Override
	public boolean createGroup(String groupJid, String name, String subject, AccessMode accessMode) {
		LOGGER.debug("Creating new  group with groupId {} and name {} and subject {} and accessMode {}", groupJid, name,
				subject, accessMode);

		GroupEntity entity = new GroupEntity();
		entity.setJid(groupJid);
		entity.setName(name);
		entity.setSubject(subject);
		entity.setAccessMode(accessMode);
		entity.setStatus(1);
		entity.setCreateTime(System.currentTimeMillis());
		entity.setLastModifiedTime(System.currentTimeMillis());

		return mongoTemplate.insert(entity) != null;
	}

	@Override
	public boolean updateGroupSubject(String groupJid, String subject) {
		LOGGER.debug("Updating group subject for groupJid {}", groupJid);
		Criteria groupIdCriteria = Criteria.where(GroupEntity.JID).is(groupJid);

		Query q = new Query(groupIdCriteria);
		Update u = new Update();
		u.set(GroupEntity.SUBJECT, subject);

		UpdateResult result = mongoTemplate.updateFirst(q, u, GroupEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean updateGroupAccessMode(String groupJid, AccessMode accessMode) {
		LOGGER.debug("Updating group accessMode for groupJid {}", groupJid);
		Criteria groupIdCriteria = Criteria.where(GroupEntity.JID).is(groupJid);

		Query q = new Query(groupIdCriteria);
		Update u = new Update();
		u.set(GroupEntity.ACCESSMODE, accessMode);

		UpdateResult result = mongoTemplate.updateFirst(q, u, GroupEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean addGroupMember(String groupJid, GroupMember member) {
		LOGGER.debug("Adding member in group for groupJid {}", groupJid);
		Criteria groupIdCriteria = Criteria.where(GroupEntity.JID).is(groupJid);

		Query q = new Query(groupIdCriteria);
		Update u = new Update();
		u.addToSet(GroupEntity.MEMBERS, member);

		UpdateResult result = mongoTemplate.updateFirst(q, u, GroupEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean removeGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("removing member {} from groupJid {}", groupJid);
		Criteria groupIdCriteria = Criteria.where(GroupEntity.JID).is(groupJid);
		Query q = new Query(groupIdCriteria);

		Update u = new Update();
		u.pull(GroupEntity.MEMBERS, new BasicDBObject(GroupEntity.JID, memberJid));

		UpdateResult result = mongoTemplate.updateFirst(q, u, GroupEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean updateGroupMemberDetail(String groupJid, GroupMember member) {
		LOGGER.debug("Updating group member detail for groupJid {}", groupJid);

		Criteria groupIdCriteria = Criteria.where(GroupEntity.JID).is(groupJid);
		Criteria memberJidCriteria = Criteria.where(GroupEntity.MEMBER_JID).is(member.getJid());
		Query q = new Query(new Criteria().andOperator(groupIdCriteria, memberJidCriteria));

		Update u = new Update();

		if (member.getNickName() != null) {
			u.set(GroupEntity.MEMBERS + DOT_DOLLAR_DOT + GroupEntity.NICKNAME, member.getNickName());
		}

		if (member.getAffiliation() != null) {
			u.set(GroupEntity.MEMBERS + DOT_DOLLAR_DOT + GroupEntity.AFFILIATION, member.getAffiliation());
		}

		if (member.getRole() != null) {
			u.set(GroupEntity.MEMBERS + DOT_DOLLAR_DOT + GroupEntity.ROLE, member.getRole());

		}

		UpdateResult result = mongoTemplate.updateFirst(q, u, GroupEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean deleteGroup(String groupJid) {
		LOGGER.debug("Deleting group of groupJid {}", groupJid);

		Criteria groupIdCriteria = Criteria.where(GroupEntity.JID).is(groupJid);

		Query q = new Query(groupIdCriteria);

		Update u = new Update();
		u.set(GroupEntity.STATUS, 0);

		UpdateResult result = mongoTemplate.updateFirst(q, u, GroupEntity.class);

		return result.getModifiedCount() > 0;
	}

	@Override
	public int getCurrentRosterVersion(String userJid) {
		LOGGER.debug("Fetching current roster version of userJid {}", userJid);

		UserEntity roster = mongoTemplate.findById(userJid, UserEntity.class);
		return roster.getRosterVersionCount();
	}

	@Override
	public boolean checkIsAlreadyRosterMember(String userJid, String contactJid) {
		LOGGER.debug("Checking user is already in Roster", userJid, contactJid);

		Criteria userJidCriteria = Criteria.where(UserRosterEntity.USER_JID).is(userJid);
		Criteria contactJidCriteria = Criteria
				.where(UserRosterEntity.ROSTER_MEMBERS + DOT + UserRosterEntity.CONTACT_JID).is(contactJid);

		Query q = new Query(new Criteria().andOperator(userJidCriteria, contactJidCriteria));

		return mongoTemplate.exists(q, UserRosterEntity.class);
	}

	@Override
	public boolean addRosterMember(String userJid, String contactJid, String contactName, int newRosterVersion,
			int status) {
		LOGGER.debug("Adding roster member {}", userJid);
		Criteria userJidCriteria = Criteria.where(UserRosterEntity.USER_JID).is(userJid);

		Query q = new Query(userJidCriteria);

		RosterMember member = new RosterMember();
		member.setContactJid(contactJid);
		member.setContactName(contactName);
		member.setVersion(newRosterVersion);
		member.setStatus(status);

		Update u = new Update();
		u.addToSet(UserRosterEntity.ROSTER_MEMBERS, member);

		UpdateResult result = mongoTemplate.upsert(q, u, UserRosterEntity.class);

		return result.getModifiedCount() > 0;

	}

	@Override
	public boolean updateUserRosterVersion(String userJid, int newRosterVersion) {
		LOGGER.debug("Updating roster version of userJid {}", userJid);

		Criteria userJidCriteria = Criteria.where(UserEntity.JID).is(userJid);

		Query q = new Query(userJidCriteria);

		Update u = new Update();
		u.set(UserEntity.ROSTER_VERSION_COUNT, newRosterVersion);

		UpdateResult result = mongoTemplate.updateFirst(q, u, UserEntity.class);
		return result.getModifiedCount() > 0;
	}

	@Override
	public String getRosterContactName(String userJid, String contactJid) {
		LOGGER.debug("Fetching roster contact name of userJid {} and contactJid {}", userJid, contactJid);

		Criteria userJidCriteria = Criteria.where(UserRosterEntity.USER_JID).is(userJid);
		Query q = new Query(userJidCriteria);

		q.fields().elemMatch(UserRosterEntity.ROSTER_MEMBERS,
				Criteria.where(UserRosterEntity.CONTACT_JID).is(contactJid));

		List<UserRosterEntity> rosters = mongoTemplate.find(q, UserRosterEntity.class);

		if (CollectionUtils.isNullOrEmpty(rosters)) {
			return null;
		}

		UserRosterEntity roster = rosters.get(0);

		if (CollectionUtils.isNullOrEmpty(roster.getRosterMembers())) {
			return null;
		}

		return roster.getRosterMembers().get(0).getContactName();
	}

	@Override
	public List<UserRosterMember> getRosterMembers(String userJid) {
		LOGGER.debug("Fetching roster member of userJid {} ", userJid);

		Criteria userJidCriteria = Criteria.where(UserRosterEntity.USER_JID).is(userJid);

		Query q = new Query(userJidCriteria);

		List<UserRosterEntity> rosters = mongoTemplate.find(q, UserRosterEntity.class);

		if (CollectionUtils.isNullOrEmpty(rosters)) {
			return null;
		}

		UserRosterEntity roster = rosters.get(0);

		if (CollectionUtils.isNullOrEmpty(roster.getRosterMembers())) {
			return null;
		}

		List<UserRosterMember> members = new ArrayList<>();

		for (RosterMember rosterMember : roster.getRosterMembers()) {
			UserRosterMember member = new UserRosterMember(rosterMember.getContactJid(), rosterMember.getContactName());

			if (rosterMember.getStatus() == 1) {
				members.add(member);

			} else if (rosterMember.getStatus() == -1) {
				members.remove(member);

			} else if (rosterMember.getStatus() == 0) {
				members.remove(member);
				members.add(member);
			}
		}

		return members;
	}

	@Override
	public boolean addPresenceSubscriber(String userJid, String subscriberJid) {
		LOGGER.debug("Inserting Presence subscriber for userJid{}", userJid);

		Criteria userJidCriteria = Criteria.where(PresenseSubscriptionEntity.USER_JID).is(userJid);

		Query q = new Query(userJidCriteria);

		Update u = new Update();
		u.addToSet(PresenseSubscriptionEntity.SUBSCRIBER_JID, subscriberJid);

		UpdateResult result = mongoTemplate.upsert(q, u, PresenseSubscriptionEntity.class);

		return result.getModifiedCount() > 0;
	}

	@Override
	public boolean removePresenceSubscriber(String userJid, String subscriberJid) {
		LOGGER.debug("Removing Presence subscriber for userJid{}", userJid);

		Criteria userJidCriteria = Criteria.where(PresenseSubscriptionEntity.USER_JID).is(userJid);

		Query q = new Query(userJidCriteria);

		Update u = new Update();
		u.pull(PresenseSubscriptionEntity.SUBSCRIBER_JID, subscriberJid);

		UpdateResult result = mongoTemplate.upsert(q, u, PresenseSubscriptionEntity.class);

		return result.getModifiedCount() > 0;

	}

	@Override
	public boolean isAlreadySubscribedForPresence(String userJid, String subscriberJid) {
		LOGGER.debug("Checking subscriber presence for userJid {}", userJid);
		Criteria userJidCriteria = Criteria.where(PresenseSubscriptionEntity.USER_JID).is(userJid);
		Criteria subscriberJidCriteria = Criteria.where(PresenseSubscriptionEntity.SUBSCRIBER_JID).is(subscriberJid);

		Query q = new Query(new Criteria().andOperator(userJidCriteria, subscriberJidCriteria));

		return mongoTemplate.exists(q, UserEntity.class);
	}

	@Override
	public String getUserName(String userJid) {
		LOGGER.debug("Fetching user name of userJid {}", userJid);
		Criteria userJidCriteria = Criteria.where(UserEntity.JID).is(userJid);

		Query q = new Query(userJidCriteria);

		List<UserEntity> entity = mongoTemplate.find(q, UserEntity.class);

		if (CollectionUtils.isNullOrEmpty(entity)) {
			return null;
		}

		User user = entity.get(0);
		StringBuilder builder = new StringBuilder();

		if (!StringUtils.isNullOrEmpty(user.getFirstName())) {
			builder.append(user.getFirstName());
		}
		if (!StringUtils.isNullOrEmpty(user.getLastName())) {
			builder.append("").append(user.getLastName());

		}
		if (!StringUtils.isNullOrEmpty(user.getMiddleName())) {
			builder.append("").append(user.getMiddleName());

		}

		return builder.toString();

	}

	@Override
	public boolean isGroupExist(String groupJid) {
		LOGGER.debug("Checking group {} exist", groupJid);

		Criteria groupJidCriteria = Criteria.where(GroupEntity.JID).is(groupJid);

		Query q = new Query(groupJidCriteria);

		return mongoTemplate.exists(q, GroupEntity.class);
	}

	@Override
	public boolean isGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("Checking member {} exist in groupJid", memberJid, groupJid);

		Criteria groupJidCriteria = Criteria.where(GroupEntity.JID).is(groupJid);
		Criteria memberJidCriteria = Criteria.where(GroupEntity.MEMBER_JID).is(memberJid);

		Query q = new Query(new Criteria().andOperator(groupJidCriteria, memberJidCriteria));

		return mongoTemplate.exists(q, GroupEntity.class);
	}

}
