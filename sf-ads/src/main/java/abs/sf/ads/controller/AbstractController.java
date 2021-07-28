package abs.sf.ads.controller;

import abs.sf.ads.repository.Repository;
import abs.sf.ads.repository.Service;

public class AbstractController<SERVICE extends Service<? extends Repository>> implements Controller<SERVICE> {
	protected SERVICE service;

	public AbstractController(SERVICE service) {
		this.service = service;
	}
}
