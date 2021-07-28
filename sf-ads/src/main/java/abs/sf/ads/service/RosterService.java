package abs.sf.ads.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import abs.sf.ads.entity.UserRosterMember;
import abs.sf.ads.repository.RosterRepository;
import abs.sf.ads.request.RosterMemberRequest;
import abs.sf.ads.response.ResponseMessage;
import abs.sf.ads.utils.CollectionUtils;
import abs.sf.ads.utils.StringUtils;

@Service
public class RosterService extends AbstractService<RosterRepository> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RosterService.class);

	public RosterService(RosterRepository repository) {
		super(repository);
	}

	public ResponseEntity<?> addOrUpdateRosterMember(RosterMemberRequest rosterMemberRequest) {
		LOGGER.debug("Handaling add roster member request {}", rosterMemberRequest);
		boolean isUserSfUser = repository.checkIsUserExist(rosterMemberRequest.getUserJid());

		if (!isUserSfUser) {
			LOGGER.warn("User {} is not sf user. So can not create roster ", rosterMemberRequest.getUserJid());

			return new ResponseEntity<>(new ResponseMessage(
					"User " + rosterMemberRequest.getContactJid() + " is not sf user, So So can not create roster."),
					HttpStatus.BAD_REQUEST);
		}

		boolean isContactSfUser = repository.checkIsUserExist(rosterMemberRequest.getContactJid());

		if (!isContactSfUser) {
			LOGGER.warn("Contact {} is not sf user. So can not add it in roster of user {}",
					rosterMemberRequest.getContactJid(), rosterMemberRequest.getUserJid());

			return new ResponseEntity<>(new ResponseMessage("Contact " + rosterMemberRequest.getContactJid()
					+ " is not sf user, So can not be added in roster of user " + rosterMemberRequest.getUserJid()),
					HttpStatus.BAD_REQUEST);
		}

		int currentRosterVersion = repository.getCurrrentRosterVersion(rosterMemberRequest.getUserJid());

		boolean alreadyRosterMember = repository.checkIsAlreadyRosterMember(rosterMemberRequest.getUserJid(),
				rosterMemberRequest.getContactJid());

		int newRosterVersion = ++currentRosterVersion;

		if (alreadyRosterMember) {

			if (StringUtils.isNullOrEmpty(rosterMemberRequest.getContactName())) {
				String rosterContactName = repository.getRosterContactName(rosterMemberRequest.getUserJid(),
						rosterMemberRequest.getContactJid());

				repository.addRosterMember(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid(),
						rosterContactName, newRosterVersion, 0);

			} else {

				repository.addRosterMember(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid(),
						rosterMemberRequest.getContactName(), newRosterVersion, 0);
			}

		} else {

			if (StringUtils.isNullOrEmpty(rosterMemberRequest.getContactName())) {
				String userName = this.repository.getUserName(rosterMemberRequest.getContactJid());
				repository.addRosterMember(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid(),
						userName, newRosterVersion, 1);

			} else {
				repository.addRosterMember(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid(),
						rosterMemberRequest.getContactName(), newRosterVersion, 1);
			}

			addUserPresenceSubscription(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid());

		}

		repository.updateUserRosterVersion(rosterMemberRequest.getUserJid(), newRosterVersion);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void addUserPresenceSubscription(String userJid, String contactJid) {
		LOGGER.info("Adding presence subscription for userJid {} and contactJid {}", userJid, contactJid);

		boolean isContactSubscribedForUserPresenceAlready = repository.isSubscribedForPresence(userJid, contactJid);

		if (!isContactSubscribedForUserPresenceAlready) {
			repository.addPresenceSubscriber(userJid, contactJid);
		}

		boolean isUserSubscribedForContactPresenceAlready = repository.isSubscribedForPresence(contactJid, userJid);

		if (!isUserSubscribedForContactPresenceAlready) {
			repository.addPresenceSubscriber(contactJid, userJid);
		}

	}

	private void removeUserPresenceSubscription(String userJid, String contactJid) {
		LOGGER.info("Removing presence subscription for userJid {} and contactJid {}", userJid, contactJid);
		repository.removePresenceSubscription(userJid, contactJid);
		repository.removePresenceSubscription(contactJid, userJid);
	}

	public ResponseEntity<?> removeRosterMember(RosterMemberRequest rosterMemberRequest) {
		LOGGER.info("Handaling remove roster member request {}", rosterMemberRequest);
		boolean isUserSfUser = repository.checkIsUserExist(rosterMemberRequest.getUserJid());

		if (!isUserSfUser) {
			LOGGER.warn("User {} is not sf user. So can not create roster ", rosterMemberRequest.getUserJid());

			return new ResponseEntity<>(new ResponseMessage(
					"User " + rosterMemberRequest.getContactJid() + " is not sf user, So So can not create roster."),
					HttpStatus.BAD_REQUEST);
		}

		boolean isContactSfUser = repository.checkIsUserExist(rosterMemberRequest.getContactJid());

		if (!isContactSfUser) {
			LOGGER.warn("Contact {} is not sf user. So can not add it in roster of user {}",
					rosterMemberRequest.getContactJid(), rosterMemberRequest.getUserJid());

			return new ResponseEntity<>(new ResponseMessage("Contact " + rosterMemberRequest.getContactJid()
					+ " is not sf user, So can not be added in roster of user " + rosterMemberRequest.getUserJid()),
					HttpStatus.BAD_REQUEST);
		}

		boolean alreadyRosterMember = repository.checkIsAlreadyRosterMember(rosterMemberRequest.getUserJid(),
				rosterMemberRequest.getContactJid());

		if (alreadyRosterMember) {
			int currentRosterVersion = repository.getCurrrentRosterVersion(rosterMemberRequest.getUserJid());

			int newRosterVersion = ++currentRosterVersion;

			String rosterContactName = repository.getRosterContactName(rosterMemberRequest.getUserJid(),
					rosterMemberRequest.getContactJid());

			repository.addRosterMember(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid(),
					rosterContactName, newRosterVersion, -1);

			repository.updateUserRosterVersion(rosterMemberRequest.getUserJid(), newRosterVersion);

			removeUserPresenceSubscription(rosterMemberRequest.getUserJid(), rosterMemberRequest.getContactJid());

			return new ResponseEntity<>(HttpStatus.OK);

		} else {
			LOGGER.warn("Contact with jid {} is not in roster of user {}", rosterMemberRequest.getContactJid(),
					rosterMemberRequest.getUserJid());
			return new ResponseEntity<>(new ResponseMessage("Cotact is not in user roster"), HttpStatus.OK);
		}

	}

	public ResponseEntity<?> getRosterMembers(String userJid) {
		LOGGER.debug("Handaling get roster members request for userJid {}", userJid);
		boolean isUserSfUser = repository.checkIsUserExist(userJid);

		if (!isUserSfUser) {
			LOGGER.warn("User {} is not sf user. So can not create roster ", userJid);

			return new ResponseEntity<>(
					new ResponseMessage("User " + userJid + " is not SF user, So can not retrive roster members."),
					HttpStatus.BAD_REQUEST);
		}

		List<UserRosterMember> members = repository.getRosterMembers(userJid);

		if (CollectionUtils.isNullOrEmpty(members))
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<>(members, HttpStatus.OK);
	}

}
