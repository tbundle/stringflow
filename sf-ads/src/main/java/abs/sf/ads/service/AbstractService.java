package abs.sf.ads.service;

import abs.sf.ads.repository.Repository;
import abs.sf.ads.repository.Service;
import abs.sf.ads.utils.UUIDGenerator;

public abstract class AbstractService<REPOSITORY extends Repository> implements Service<REPOSITORY> {

	protected REPOSITORY repository;
	
	public AbstractService(REPOSITORY repository) {
		this.repository = repository;
	}

	public String buildJid(String id, String domain) {
		return id + "@" + domain;
	}

	public String buildGroupJid(String name, String domain) {
		return name + "-" + UUIDGenerator.secureId() + "@" + domain;
	}

}
