package abs.sf.ads.db;

import java.util.List;

import javax.validation.constraints.NotNull;

import abs.sf.ads.entity.Group.AccessMode;
import abs.sf.ads.entity.Group.GroupMember;
import abs.sf.ads.entity.User;
import abs.sf.ads.entity.UserRosterMember;
import abs.sf.ads.request.ChangePassword;

public interface Database {

	boolean createUser(String userJid, String password, String userName, String email, String domain);

	boolean updateUserDetail(User user);

	User getUserDetail(String jid);

	boolean deactivateUser(String jid);

	boolean activateUser(String jid);

	boolean deleteUser(String jid);

	boolean checkIsUserExist(String jid);

	boolean changePassword(ChangePassword changePassword);

	String getOldPassword(String jid);

	boolean createGroup(String groupId, String name, String subject, AccessMode accessMode);

	boolean updateGroupSubject(String groupId, String subject);

	boolean updateGroupAccessMode(String groupId, AccessMode accessMode);

	boolean addGroupMember(String groupId, @NotNull GroupMember member);

	boolean removeGroupMember(String groupId, String memberJid);

	boolean updateGroupMemberDetail(String groupId, GroupMember member);

	boolean deleteGroup(String groupId);

	int getCurrentRosterVersion(String userJid);

	boolean checkIsAlreadyRosterMember(String userJid, String contactJid);

	boolean addRosterMember(String userJid, String contactJid, String contactName, int newRosterVersion, int status);

	boolean updateUserRosterVersion(String userJid, int newRosterVersion);

	String getRosterContactName(String userJid, String contactJid);

	List<UserRosterMember> getRosterMembers(String userJid);

	boolean addPresenceSubscriber(String userJid, String subscriberJid);

	boolean removePresenceSubscriber(String userJid, String subscriberJid);

	boolean isAlreadySubscribedForPresence(String userJid, String subscriberJid);

	String getUserName(String userJid);

	boolean isGroupExist(String groupJid);

	boolean isGroupMember(String groupJid, String memberJid);

}
