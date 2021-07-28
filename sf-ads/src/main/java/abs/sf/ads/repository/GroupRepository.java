package abs.sf.ads.repository;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import abs.sf.ads.entity.Group.AccessMode;
import abs.sf.ads.entity.Group.GroupMember;

@Repository
public class GroupRepository extends AbstractRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupRepository.class);

	public boolean createGroup(String groupJid, String name, String subject, AccessMode accessMode) {
		LOGGER.debug("Creating group with groupJid {} and name {} and subject {} and accessMode {}", groupJid, name,
				subject);
		return this.database.createGroup(groupJid, name, subject, accessMode);
	}

	public boolean updateGroupSubject(String groupJid, String subject) {
		LOGGER.debug("Handling updateGroupSubject request for groupJid {}", groupJid);
		return this.database.updateGroupSubject(groupJid, subject);
	}

	public boolean updateGroupAccessMode(String groupJid, AccessMode accessMode) {
		LOGGER.debug("Handling updateGroupAccessMode request for groupJid {}", groupJid);
		return this.database.updateGroupAccessMode(groupJid, accessMode);
	}

	public boolean addGroupMember(String groupJid, @NotNull GroupMember member) {
		LOGGER.debug("Handling addGroupMember request for groupJid {}", groupJid);
		return this.database.addGroupMember(groupJid, member);
	}

	public boolean removeGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("Handling removeGroupMember request for groupJid {} and memberJid {]", memberJid);
		return this.database.removeGroupMember(groupJid, memberJid);
	}

	public boolean updateGroupMemberDetail(String groupJid, GroupMember member) {
		LOGGER.debug("Handling updateGroupMemberDetail request for groupJid and memberJid {}", member.getJid());
		return this.database.updateGroupMemberDetail(groupJid, member);
	}

	public boolean deleteGroup(String groupJid) {
		LOGGER.debug("Handling deleteGroup request for groupId {}", groupJid);
		return this.database.deleteGroup(groupJid);
	}

	public boolean isGroupExist(String groupJid) {
		LOGGER.debug("Handling isGoupExist request for groupId {}", groupJid);
		return this.database.isGroupExist(groupJid);
	}

	public boolean isGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("Handling isGroupMember request for groupId {} and jid", groupJid, memberJid);
		return this.database.isGroupMember(groupJid, memberJid);
	}
}
