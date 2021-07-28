package abs.sf.ads.db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import abs.sf.ads.db.Database;
import abs.sf.ads.entity.Group.AccessMode;
import abs.sf.ads.entity.Group.GroupMember;
import abs.sf.ads.entity.User;
import abs.sf.ads.entity.User.Address;
import abs.sf.ads.entity.User.UserAvtar;
import abs.sf.ads.entity.UserRosterMember;
import abs.sf.ads.request.ChangePassword;
import abs.sf.ads.utils.StringUtils;

public class MysqlDatabase implements Database {
	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDatabase.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public MysqlDatabase() {
		// TODO Auto-generated constructor stub
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean createUser(String userJid, String password, String userName, String email, String domain) {
		LOGGER.debug("Creating new user with jid {}, name {} and domain {}", userJid, userName);
		int count = jdbcTemplate.update(MysqlQueries.SQL_CREATE_USER,
				new Object[] { userJid, userName, email, password });

		return count > 0;
	}

	@Override
	public boolean updateUserDetail(User user) {
		LOGGER.debug("Updating userDetail of user", user);
		int update = jdbcTemplate.update(MysqlQueries.SQL_UPDATE_USER,
				new Object[] { user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getNickName(),
						user.getPhone(), user.getEmail(), user.getGender(), user.getBday(),
						user.getAvtar() == null ? null : user.getAvtar().getBase64EncodedImage(),
						user.getAvtar() == null ? null : user.getAvtar().getImageType(),
						user.getAddress() == null ? null : user.getAddress().getHome(),
						user.getAddress() == null ? null : user.getAddress().getStreet(),
						user.getAddress() == null ? null : user.getAddress().getLocality(),
						user.getAddress() == null ? null : user.getAddress().getState(),
						user.getAddress() == null ? null : user.getAddress().getCity(),
						user.getAddress() == null ? null : user.getAddress().getCountry(),
						user.getAddress() == null ? null : user.getAddress().getPcode(), user.getDescription(),
						user.getJid()

				});

		return update > 0;
	}

	@Override
	public User getUserDetail(String jid) {
		LOGGER.debug("Fetching userDetail of jid {} ", jid);
		return jdbcTemplate.query(MysqlQueries.SQL_GET_USER_DETAIL, new Object[] { jid },
				new ResultSetExtractor<User>() {

					@Override
					public User extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							User u = new User();
							u.setJid(rs.getString(1));
							u.setFirstName(rs.getString(3));
							u.setMiddleName(rs.getString(4));
							u.setLastName(rs.getString(5));
							u.setNickName(rs.getString(6));
							u.setEmail(rs.getString(7));
							u.setPhone(rs.getString(8));

							u.setGender(rs.getString(9));
							u.setBday(rs.getString(10));

							String base64Image = rs.getString(11);
							String imageType = rs.getString(12);
							UserAvtar avtar = u.new UserAvtar(base64Image, imageType);

							u.setAvtar(avtar);

							Address address = u.new Address();
							address.setHome(rs.getString(13));
							address.setStreet(rs.getString(14));
							address.setLocality(rs.getString(15));
							address.setState(rs.getString(16));
							address.setCity(rs.getString(17));
							address.setCountry(rs.getString(18));
							address.setPcode(rs.getString(19));
							u.setAddress(address);

							u.setDescription(rs.getString(20));
							return u;
						}

						return null;

					}

				});

	}

	@Override
	public boolean deactivateUser(String jid) {
		LOGGER.debug("Deactivating user of jid {}", jid);

		int update = jdbcTemplate.update(MysqlQueries.SQL_DEACTIVATTE_USER, new Object[] { jid });
		return update > 0;
	}

	@Override
	public boolean activateUser(String jid) {
		LOGGER.debug("Activating user of jid {}", jid);
		int update = jdbcTemplate.update(MysqlQueries.SQL_ACTIVATE_USER, new Object[] { jid });
		return update > 0;
	}

	@Override
	public boolean deleteUser(String jid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean checkIsUserExist(String jid) {
		LOGGER.debug("Checking is user exist for jid {}", jid);
		return jdbcTemplate.query(MysqlQueries.SQL_IS_USER_EXIST, new Object[] { jid },
				new ResultSetExtractor<Boolean>() {

					@Override
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getInt(1) > 0;
						}

						return false;
					}

				});

	}

	@Override
	public boolean changePassword(ChangePassword changePassword) {
		LOGGER.debug("Changing the password of jid {}", changePassword.getJid());

		int update = jdbcTemplate.update(MysqlQueries.SQL_CHANGE_PASSWORD,
				new Object[] { changePassword.getNewPassword(), changePassword.getJid() });

		return update > 0;

	}

	@Override
	public String getOldPassword(String jid) {
		LOGGER.debug("Fetching old password for jid {}", jid);

		return jdbcTemplate.query(MysqlQueries.SQL_GET_PASSWORD, new Object[] { jid },
				new ResultSetExtractor<String>() {

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getString(1);
						}

						return null;
					}

				});

	}

	@Override
	public boolean createGroup(String groupJid, String name, String subject, AccessMode accessMode) {
		LOGGER.debug("Creating new group with groupJid {}, name {} and sbject {} and accessMode {}", groupJid, name,
				subject, accessMode);

		int count = jdbcTemplate.update(MysqlQueries.SQL_CREATE_GROUP,
				new Object[] { groupJid, name, subject, accessMode.val() });

		return count > 0;
	}

	@Override
	public boolean updateGroupSubject(String groupJid, String subject) {
		LOGGER.debug("Updating group subject of groupJid {}", groupJid);

		int update = jdbcTemplate.update(MysqlQueries.SQL_UPDATE_GROUP_SUBJECT, new Object[] { subject, groupJid });
		return update > 0;
	}

	@Override
	public boolean updateGroupAccessMode(String groupJid, AccessMode accessMode) {
		LOGGER.debug("Updating group accessMode of groupJid {}", groupJid);

		int update = jdbcTemplate.update(MysqlQueries.SQL_UPDATE_GROUP_ACCESS_MODE,
				new Object[] { accessMode.val(), groupJid });
		return update > 0;
	}

	@Override
	public boolean addGroupMember(String groupJid, @NotNull GroupMember member) {
		LOGGER.debug("Adding group member{}  in groupJid {}", member, groupJid);

		int update = jdbcTemplate.update(MysqlQueries.SQL_ADD_GROUP_MEMBER, new Object[] { groupJid, member.getJid(),
				member.getNickName(), member.getAffiliation().val(), member.getRole().val() });
		return update > 0;
	}

	@Override
	public boolean removeGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("Removing group memeber {} from groupJid {}", memberJid, groupJid);

		int delete = jdbcTemplate.update(MysqlQueries.SQL_REMOVE_GROUP_MEMBER, new Object[] { groupJid, memberJid });
		return delete > 0;

	}

	@Override
	public boolean updateGroupMemberDetail(String groupJid, GroupMember member) {
		LOGGER.debug("Updating group member {} detail in groupJid {}", member.getNickName(), member.getAffiliation(),
				member.getRole());
		int update = jdbcTemplate.update(MysqlQueries.SQL_UPDTAE_GROUP_MEMBER_DETAILS,
				new Object[] { member.getNickName(), member.getAffiliation().val(), member.getRole().val(), groupJid,
						member.getJid() });
		return update > 0;
	}

	@Override
	public boolean deleteGroup(String groupJid) {
		LOGGER.debug("Deleting group groupJid {}", groupJid);

		int update = jdbcTemplate.update(MysqlQueries.SQL_DELETE_GROUP, new Object[] { groupJid });
		return update > 0;
	}

	@Override
	public int getCurrentRosterVersion(String userJid) {
		LOGGER.debug("Fetching current roster version of userJid {}", userJid);

		return jdbcTemplate.query(MysqlQueries.SQL_GET_CURRENT_ROSTER_VERSION, new Object[] { userJid },
				new ResultSetExtractor<Integer>() {

					@Override
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getInt(1);
						}

						return null;
					}

				});

	}

	@Override
	public boolean checkIsAlreadyRosterMember(String userJid, String contactJid) {
		LOGGER.debug("Checking member {} is already in roster", userJid, contactJid);

		return jdbcTemplate.query(MysqlQueries.SQL_IS_USER_IN_ROSTER, new Object[] { userJid, contactJid },
				new ResultSetExtractor<Boolean>() {

					@Override
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getInt(1) > 0;
						}

						return false;
					}

				});
	}

	@Override
	public boolean addRosterMember(String userJid, String contactJid, String contactName, int newRosterVersion,
			int status) {
		LOGGER.debug("Inserting member {} in roster", userJid, contactJid, contactName, newRosterVersion, status);

		int update = jdbcTemplate.update(MysqlQueries.SQL_INSERTING_MEMBER_IN_ROSTER,
				new Object[] { userJid, contactJid, contactName, newRosterVersion, status });

		return update > 0;
	}

	@Override
	public boolean updateUserRosterVersion(String userJid, int newRosterVersion) {
		LOGGER.debug("Updating user{}  roster version {}", userJid, newRosterVersion);

		int update = jdbcTemplate.update(MysqlQueries.SQL_UPDATE_USER_ROSTER_VERSION,
				new Object[] { newRosterVersion, userJid });
		return update > 0;
	}

	@Override
	public String getRosterContactName(String userJid, String contactJid) {

		return jdbcTemplate.query(MysqlQueries.SQL_GET_ROSTER_CONTACT_NAME, new Object[] { userJid, contactJid },
				new ResultSetExtractor<String>() {

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getString(1);
						}

						return null;
					}

				});
	}

	@Override
	public List<UserRosterMember> getRosterMembers(String userJid) {
		LOGGER.debug("Fetching roster members userJid {}", userJid);

		return jdbcTemplate.query(MysqlQueries.SQL_GET_ROSTER_MEMBERS, new Object[] { userJid },
				new ResultSetExtractor<List<UserRosterMember>>() {

					@Override
					public List<UserRosterMember> extractData(ResultSet rs) throws SQLException, DataAccessException {
						List<UserRosterMember> members = new ArrayList<>();

						while (rs.next()) {
							UserRosterMember member = new UserRosterMember();
							member.setContactJid(rs.getString(1));
							member.setContactName(rs.getString(2));

							if (rs.getInt(3) == 1) {
								members.add(member);

							} else if (rs.getInt(3) == -1) {
								members.remove(member);

							} else if (rs.getInt(3) == 0) {
								members.remove(member);
								members.add(member);
							}
						}

						return members;
					}

				});
	}

	@Override
	public boolean addPresenceSubscriber(String userJid, String subscriberJid) {
		LOGGER.debug("Adding presence of subscriber {}", userJid, subscriberJid);

		int insert = jdbcTemplate.update(MysqlQueries.SQL_INSERTING_PRESENCE_SUBSCRIBER,
				new Object[] { userJid, subscriberJid });
		return insert > 0;
	}

	@Override
	public boolean removePresenceSubscriber(String userJid, String subscriberJid) {
		LOGGER.debug("Removing presence of subscriber {}", userJid, subscriberJid);

		int insert = jdbcTemplate.update(MysqlQueries.SQL_REMOVING_PRESENCE_SUBSCRIBER,
				new Object[] { userJid, subscriberJid });
		return insert > 0;
	}

	@Override
	public boolean isAlreadySubscribedForPresence(String userJid, String subscriberJid) {
		LOGGER.debug("Checking is already subsribed for presence userJid {} and subscriberJid {} ", userJid,
				subscriberJid);
		return jdbcTemplate.query(MysqlQueries.SQL_IS_ALREADY_SUBSCRIBED_FOR_PRESENCE,
				new Object[] { userJid, subscriberJid }, new ResultSetExtractor<Boolean>() {

					@Override
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getInt(1) > 0;
						}

						return false;
					}

				});
	}

	@Override
	public String getUserName(String userJid) {
		LOGGER.debug("Getting user name for user jid {}", userJid);

		return jdbcTemplate.query(MysqlQueries.SQL_GET_USER_NAME, new Object[] { userJid },
				new ResultSetExtractor<String>() {

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							StringBuilder sb = new StringBuilder();
							if (!StringUtils.isNullOrEmpty(rs.getString(1))) {
								sb.append(rs.getString(1));
							}

							if (!StringUtils.isNullOrEmpty(rs.getString(2))) {
								sb.append(" ").append(rs.getString(2));
							}

							if (!StringUtils.isNullOrEmpty(rs.getString(3))) {
								sb.append(" ").append(rs.getString(3));
							}

							return sb.toString();
						}

						return null;
					}

				});
	}

	@Override
	public boolean isGroupExist(String groupJid) {
		LOGGER.debug("Checking group {} exist", groupJid);

		return jdbcTemplate.query(MysqlQueries.SQL_IS_GROUP_EXIST, new Object[] { groupJid },
				new ResultSetExtractor<Boolean>() {

					@Override
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getInt(1) > 0;
						}

						return false;
					}

				});
	}

	@Override
	public boolean isGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("Checking member {} exist in groupJid", memberJid, groupJid);

		return jdbcTemplate.query(MysqlQueries.SQL_IS_GROUP_MEMBER, new Object[] { groupJid, memberJid },
				new ResultSetExtractor<Boolean>() {

					@Override
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {

							return rs.getInt(1) > 0;
						}

						return false;
					}

				});
	}

}
