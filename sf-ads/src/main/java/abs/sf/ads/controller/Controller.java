package abs.sf.ads.controller;

import abs.sf.ads.repository.Repository;
import abs.sf.ads.repository.Service;

public interface Controller<SERVICE extends Service<? extends Repository>> {

}
