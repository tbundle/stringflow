package abs.sf.ads.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import abs.sf.ads.entity.User;
import abs.sf.ads.repository.UserRepository;
import abs.sf.ads.request.ChangePassword;
import abs.sf.ads.request.CreateUserRequest;
import abs.sf.ads.response.ResponseMessage;
import abs.sf.ads.utils.StringUtils;

@Service
public class UserService extends AbstractService<UserRepository> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	private static final String USER_CREATE_FAILURE_MESSAGE = "Server couldn't create new user";
	private static final String USER_UPDATE_FAILURE_MESSAGE = "Server couldn't update user detaill";
	private static final String USER_DETAIL_NOT_FOUND_MESSAGE = "User detail not found";
	private static final String USER_DEACTIVATE_SUCCESS_MESSAGE = "User deactivated successfully";
	private static final String USER_DEACTIVATE_FAILURE_MESSAGE = "Server couldn't deactivate user";
	private static final String USER_ACTIVATE_SUCCESS_MESSAGE = "User activated successfully";
	private static final String USER_ACTIVATE_FAILURE_MESSAGE = "Server couldn't activate user";
	private static final String USER_DELETE_SUCCESS_MESSAGE = "User deleted successfully";
	private static final String USER_DELETE_FAILURE_MESSAGE = "Server couldn't delete user";
	private static final String USER_ALREADY_EXIST_MESSAGE = "User with given userId already exists, Give another userId";
	private static final String CHANGED_PASSWORD_SUCCESS_MESSAGE = "Password changed successfully";
	private static final String CHANGED_PASSWORD_FAILURE_MESSAGE = "Server couldn't update password";
	private static final String PASSWORD_DOES_NOT_MATCH = "Your old password is doesn't match with previous password";

	public UserService(UserRepository repository) {
		super(repository);

	}

	public ResponseEntity<?> createUser(CreateUserRequest userRequest) {
		LOGGER.debug("Handaling create user request");

		String userJid = this.buildJid(userRequest.getUserId(), userRequest.getDomain());

		if (this.repository.checkIsUserExist(userJid)) {
			LOGGER.error("Failed to create new user beacuce user with jid {} already exists", userJid);
			return new ResponseEntity<>(new ResponseMessage(USER_ALREADY_EXIST_MESSAGE), HttpStatus.CONFLICT);
		}

		boolean created = this.repository.createUser(userJid, userRequest.getPassword(), userRequest.getUserName(),
				userRequest.getEmail(), userRequest.getDomain());

		if (created) {
			LOGGER.info("New User is created succesfully with jid {}, name {} and domain {}", userJid,
					userRequest.getUserName(), userRequest.getUserName());
			User user = new User(userJid, userRequest.getUserName(), userRequest.getEmail());

			return new ResponseEntity<>(user, HttpStatus.CREATED);

		} else {
			LOGGER.error("Failed to create new user with jid {}, name {} and domain {}", userJid,
					userRequest.getUserName(), userRequest.getUserName());
			return new ResponseEntity<>(new ResponseMessage(USER_CREATE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> updateUserDetail(User user) {
		LOGGER.debug("Handaling update user detail  request for jid {}", user.getJid());
		boolean updated = this.repository.updateUserDetail(user);

		if (updated) {
			LOGGER.info("User details updated successfully for jid {}", user.getJid());
			return new ResponseEntity<>(this.repository.getUserDetail(user.getJid()), HttpStatus.OK);

		} else {
			LOGGER.error("Failed to update user detail for jid {}", user.getJid());
			return new ResponseEntity<>(new ResponseMessage(USER_UPDATE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getUserDetail(String jid) {
		LOGGER.debug("Handaling get user detail  request for jid {}", jid);
		User user = this.repository.getUserDetail(jid);

		if (user == null) {
			LOGGER.info("No User details found for jid {}", jid);
			return new ResponseEntity<>(new ResponseMessage(USER_DETAIL_NOT_FOUND_MESSAGE), HttpStatus.NOT_FOUND);

		} else {

			return new ResponseEntity<>(user, HttpStatus.OK);
		}

	}

	public ResponseEntity<?> deactivateUser(String jid) {
		LOGGER.debug("Handaling deactivate user request for jid {}", jid);
		boolean deactivated = this.repository.deactivateUser(jid);

		if (deactivated) {
			return new ResponseEntity<>(new ResponseMessage(USER_DEACTIVATE_SUCCESS_MESSAGE), HttpStatus.OK);

		} else {
			LOGGER.error("Failed to deactivate user with jid {}", jid);
			return new ResponseEntity<>(new ResponseMessage(USER_DEACTIVATE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> activateUser(String jid) {
		LOGGER.debug("Handaling activate user request for jid {}", jid);
		boolean activated = this.repository.activateUser(jid);

		if (activated) {
			return new ResponseEntity<>(new ResponseMessage(USER_ACTIVATE_SUCCESS_MESSAGE), HttpStatus.OK);

		} else {
			LOGGER.error("Failed to activate user with jid {}", jid);
			return new ResponseEntity<>(new ResponseMessage(USER_ACTIVATE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> deleteUser(String jid) {
		LOGGER.debug("Handaling delete user request for jid {}", jid);
		boolean deleted = this.repository.deleteUser(jid);

		if (deleted) {
			return new ResponseEntity<>(new ResponseMessage(USER_DELETE_SUCCESS_MESSAGE), HttpStatus.OK);

		} else {
			LOGGER.error("Failed to delete user with jid {}", jid);
			return new ResponseEntity<>(new ResponseMessage(USER_DELETE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> changePassword(ChangePassword changePassword) {
		LOGGER.debug("Handling changePassword request for jid {}", changePassword.getJid());

		String oldPassword = repository.getOldPassword(changePassword.getJid());
		if (StringUtils.safeEquals(oldPassword, changePassword.getOldPassword())) {

			boolean changed = this.repository.changePassowrd(changePassword);

			if (changed) {

				return new ResponseEntity<>(new ResponseMessage(CHANGED_PASSWORD_SUCCESS_MESSAGE), HttpStatus.OK);

			} else {

				LOGGER.error("Failed to change the password for  jid {}", changePassword.getJid());
				return new ResponseEntity<>(new ResponseMessage(CHANGED_PASSWORD_FAILURE_MESSAGE),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOGGER.error("Old Password doesn't match with previous password", changePassword.getOldPassword());
			return new ResponseEntity<>(new ResponseMessage(PASSWORD_DOES_NOT_MATCH), HttpStatus.FORBIDDEN);
		}

	}

}
