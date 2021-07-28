package abs.sf.ads.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import abs.sf.ads.db.Database;

public abstract class AbstractRepository implements Repository {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

	@Autowired
	protected Database database;

	public boolean checkIsUserExist(String userJid) {
		LOGGER.debug("Checking user with jid {} is sf user or not", userJid);
		return this.database.checkIsUserExist(userJid);
	}

	public String getUserName(String userJid) {
		LOGGER.debug("Getting user name for userJid {}", userJid);
		return this.database.getUserName(userJid);
	}
}
