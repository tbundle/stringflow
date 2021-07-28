package abs.sf.ads.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import abs.sf.ads.entity.User;
import abs.sf.ads.request.ChangePassword;
import abs.sf.ads.request.CreateUserRequest;
import abs.sf.ads.response.ResponseMessage;
import abs.sf.ads.service.UserService;
import abs.sf.ads.utils.StringUtils;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<UserService> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	public UserController(UserService service) {
		super(service);
	}

	@PostMapping
	public ResponseEntity<?> createUser(@Valid @NotNull @RequestBody CreateUserRequest userRequest) throws Exception {
		LOGGER.info("Processing create user request {}", userRequest.toString());
		try {

			return service.createUser(userRequest);

		} catch (Exception e) {
			LOGGER.error("Failed to process create user request ", e);
			throw e;
		}

	}

	@PutMapping
	public ResponseEntity<?> updateUserDetail(@Valid @NotNull @RequestBody User user) throws Exception {
		try {

			if (StringUtils.isNullOrEmpty(user.getJid())) {
				LOGGER.warn("User jid is not given in update user detail request");

				return new ResponseEntity<>(new ResponseMessage("User jid not given"), HttpStatus.BAD_REQUEST);
			}

			LOGGER.info("Processing create user request ");
			return service.updateUserDetail(user);

		} catch (Exception e) {
			LOGGER.error("Failed to process update user detail request ", e);
			throw e;
		}
	}

	@GetMapping("/{jid}")
	public ResponseEntity<?> getUserDetail(@NotNull @PathVariable("jid") String jid) throws Exception {
		try {

			LOGGER.info("Processing get user detail request for jid {}", jid);
			return service.getUserDetail(jid);

		} catch (Exception e) {
			LOGGER.error("Failed to process get user detail request ", e);
			throw e;
		}
	}

	@PutMapping("/dau/{jid}")
	public ResponseEntity<?> deactivateUser(@NotNull @PathVariable("jid") String jid) throws Exception {
		try {

			LOGGER.info("Processing deactivate user request jid {}", jid);
			return service.deactivateUser(jid);

		} catch (Exception e) {
			LOGGER.error("Failed to process deactivate user request ", e);
			throw e;
		}
	}

	@PutMapping("/au/{jid}")
	public ResponseEntity<?> activateUser(@NotNull @PathVariable("jid") String jid) throws Exception {
		try {

			LOGGER.info("Processing activate user request jid {}", jid);
			return service.activateUser(jid);

		} catch (Exception e) {
			LOGGER.error("Failed to process activate user request ", e);
			throw e;
		}
	}

	@DeleteMapping("/{jid}")
	@Deprecated
	public ResponseEntity<?> deleteUser(@NotNull @PathVariable("jid") String jid) throws Exception {
		try {

			LOGGER.info("Processing activate user request jid {}", jid);
			return service.deleteUser(jid);

		} catch (Exception e) {
			LOGGER.error("Failed to process delete user request ", e);
			throw e;
		}
	}

	@PostMapping("/cp")
	public ResponseEntity<?> changePassword(@NotNull @RequestBody ChangePassword changePassword) throws Exception {
		try {

			if (StringUtils.isNullOrEmpty(changePassword.getJid())) {
				LOGGER.warn("User jid is not given in changePassword request");

				return new ResponseEntity<>(new ResponseMessage("User jid not given"), HttpStatus.BAD_REQUEST);
			}

			LOGGER.info("Processing changePassword request ");
			return service.changePassword(changePassword);

		} catch (Exception e) {
			LOGGER.error("Failed to process changePassword request ", e);
			throw e;
		}
	}

}
