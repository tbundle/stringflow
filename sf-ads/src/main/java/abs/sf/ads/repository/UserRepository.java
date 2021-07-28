package abs.sf.ads.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import abs.sf.ads.entity.User;
import abs.sf.ads.request.ChangePassword;

@Repository
public class UserRepository extends AbstractRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);

	public boolean createUser(String userJid, String password, String userName, String email, String domain) {
		LOGGER.debug("Creating user with userJid {} userName {} domain {}", userJid, userName, domain);
		return this.database.createUser(userJid, password, userName, email, domain);
	}

	public boolean updateUserDetail(User user) {
		LOGGER.debug("Updating user detail for jid {}", user.getJid());
		return this.database.updateUserDetail(user);
	}

	public User getUserDetail(String jid) {
		LOGGER.debug("Fetching user detail for jid {}", jid);
		return this.database.getUserDetail(jid);
	}

	public boolean deactivateUser(String jid) {
		LOGGER.debug("Deactivating user {}", jid);
		return this.database.deactivateUser(jid);
	}

	public boolean activateUser(String jid) {
		LOGGER.debug("Activating user {}", jid);
		return this.database.activateUser(jid);
	}

	public boolean deleteUser(String jid) {
		LOGGER.debug("Deleting user {}", jid);
		return this.database.deleteUser(jid);
	}

	public boolean changePassowrd(ChangePassword changePassword) {
		LOGGER.debug("changing the password for jid {]", changePassword.getJid());
		return this.database.changePassword(changePassword);
	}

	public String getOldPassword(String jid) {

		return this.database.getOldPassword(jid);
	}

}
