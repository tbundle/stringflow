package abs.sf.ads.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import abs.sf.ads.entity.UserRosterMember;

@Repository
public class RosterRepository extends AbstractRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(RosterRepository.class);

	public int getCurrrentRosterVersion(String userJid) {
		LOGGER.debug("Getting current roster cersion for userJid {}", userJid);
		return this.database.getCurrentRosterVersion(userJid);
	}

	public boolean checkIsAlreadyRosterMember(String userJid, String contactJid) {
		LOGGER.debug("Checking contact {} is in roster of user {}", contactJid, userJid);
		return this.database.checkIsAlreadyRosterMember(userJid, contactJid);
	}


	public boolean addRosterMember(String userJid, String contactJid, String contactName, int newRosterVersion,
			int status) {
		LOGGER.debug("Add Roster member for user {} with conactJid {}, contactName {}, roster version {} and status {}",
				userJid, contactJid, contactName, newRosterVersion, status);
		return this.database.addRosterMember(userJid, contactJid, contactName, newRosterVersion, status);
	}

	public boolean updateUserRosterVersion(String userJid, int newRosterVersion) {
		LOGGER.debug("Updating user roster version for user {} with roster version {}", userJid, newRosterVersion);
		return this.database.updateUserRosterVersion(userJid, newRosterVersion);
	}

	public String getRosterContactName(String userJid, String contactJid) {
		LOGGER.debug("Get roster contactName for userJid {} and contactJid {}", userJid, contactJid);
		return this.database.getRosterContactName(userJid, contactJid);
	}

	public List<UserRosterMember> getRosterMembers(String userJid) {
		LOGGER.debug("Geting roster members for userJid {}", userJid);
		return this.database.getRosterMembers(userJid);
	}

	public boolean isSubscribedForPresence(String userJid, String subscriberJid) {
		LOGGER.debug("Checking subscriber {} is already have presence subscription with user {}", subscriberJid,
				userJid);
		return this.database.isAlreadySubscribedForPresence(userJid, subscriberJid);
	}

	public boolean addPresenceSubscriber(String userJid, String subscriberJid) {
		LOGGER.debug("Adding presense subscriber {} for user {}", subscriberJid, userJid);
		return this.database.addPresenceSubscriber(userJid, subscriberJid);
	}

	public boolean removePresenceSubscription(String userJid, String subscriberJid) {
		LOGGER.debug("Removing presense subscriber {} from user {}", subscriberJid, userJid);
		return this.database.removePresenceSubscriber(userJid, subscriberJid);
	}

}
