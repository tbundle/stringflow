package abs.sf.ads.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import abs.sf.ads.request.RosterMemberRequest;
import abs.sf.ads.service.RosterService;

@RestController
@RequestMapping("/roster")
public class RosterController extends AbstractController<RosterService> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RosterController.class);

	public RosterController(RosterService service) {
		super(service);
	}

	@PutMapping("/aorm")
	public ResponseEntity<?> addOrUpdateRosterMember(@NotNull @RequestBody RosterMemberRequest rosterMemberRequest) {
		LOGGER.info("Processing add roster member request for request {}", rosterMemberRequest);
		try {

			return service.addOrUpdateRosterMember(rosterMemberRequest);

		} catch (Exception e) {
			LOGGER.error("Failed to process Add roster member request {}", rosterMemberRequest);
			throw e;
		}

	}

	@PutMapping("/arms")
	public ResponseEntity<?> addRosterMembers(@NotNull @RequestBody List<RosterMemberRequest> rosterMembers) {
		LOGGER.info("Processing add roster member request for request {}", rosterMembers);
		try {

			for (RosterMemberRequest rosterMemberRequest : rosterMembers) {
				addOrUpdateRosterMember(rosterMemberRequest);
			}

			return new ResponseEntity<>(HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error("Failed to process Add roster member request {}", rosterMembers);
			throw e;
		}

	}

	@PutMapping("/crmn")
	public ResponseEntity<?> changeRosterMemberName(@NotNull @RequestBody RosterMemberRequest rosterMemberRequest) {
		LOGGER.info("Processing changer roster member name request for request {}", rosterMemberRequest);
		try {

			return this.addOrUpdateRosterMember(rosterMemberRequest);

		} catch (Exception e) {
			LOGGER.error("Failed to process change roster member neame request {}", rosterMemberRequest);
			throw e;
		}
	}

	@PutMapping("/rrm")
	public ResponseEntity<?> removeRosterMember(@NotNull @RequestBody RosterMemberRequest rosterMemberRequest) {
		LOGGER.info("Processing remove roster member request for request {}", rosterMemberRequest);
		try {

			return service.removeRosterMember(rosterMemberRequest);

		} catch (Exception e) {
			LOGGER.error("Failed to process change roster member neame request {}", rosterMemberRequest);
			throw e;
		}

	}

	@GetMapping("/{userJid}")
	public ResponseEntity<?> getRosterMembers(@PathVariable("userJid") String userJid) {
		LOGGER.info("Processing get roster member request for user jid {}", userJid);
		try {

			return service.getRosterMembers(userJid);

		} catch (Exception e) {
			LOGGER.error("Failed to process get roster member request for userJid {}", userJid);
			throw e;
		}
	}
}
