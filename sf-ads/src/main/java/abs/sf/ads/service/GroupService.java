package abs.sf.ads.service;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import abs.sf.ads.entity.Group;
import abs.sf.ads.entity.Group.AccessMode;
import abs.sf.ads.entity.Group.Affiliation;
import abs.sf.ads.entity.Group.GroupMember;
import abs.sf.ads.entity.Group.Role;
import abs.sf.ads.repository.GroupRepository;
import abs.sf.ads.request.CreateGroupRequest;
import abs.sf.ads.response.ResponseMessage;
import abs.sf.ads.utils.StringUtils;

@Service
public class GroupService extends AbstractService<GroupRepository> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

	private static final String GROUP_CREATE_FAILURE_MESSAGE = "Server couldn't create new group";
	private static final String GROUP_NAME_NULL_MESSAGE = "Group name can't be Empty";
	private static final String UPDATE_GROUP_SUBJECT_SUCCESSFULLY = "Group subject updated successfully";
	private static final String UPDATE_GROUP_SUBJECT_FAILURE_MESSAGE = "Server couldn't update group subject";
	private static final String UPDATE_GROUP_ACCESSMODE_SUCCESSFULLY = "Group accessMode updated successfully";
	private static final String UPDATE_GROUP_ACCESSMODE_FAILURE_MESSAGE = "Server couldn't update group accessMode";
	private static final String ADDED_GROUP_MEMBER_SUCCESSFULLY = "Group member added successfully";
	private static final String ADDED_GROUP_MEMBER_FAILURE_MESSAGE = "Server couldn't add member in group";
	private static final String REMOVED_GROUP_MEMBER_SUCCESSFULLY = "Group member removed successfully";
	private static final String REMOVED_GROUP_MEMBER_FAILURE_MESSAGE = "Server couldn't remove group";
	private static final String UPDATED_GROUP_MEMBER_DETAIL_SUCCESSFULLY = "Group member detail updated successfully";
	private static final String UPDATE_GROUP_MEMBER_DETAIL_FAILURE_MESSAGE = "Server couldn't update group member detail";
	private static final String DELETED_GROUP_SUCCESSFULLY = "Group deleted successfully";
	private static final String DELTED_GROUP_FAILURE_MESSAGE = "Server couldn't delete group";
	private static final String GROUP_CREATOR_DOES_NOT_EXIST = "Group creator doesn't exist in system";
	private static final String GROUP_MEMBER_IS_NOT_SF_USER = "Group member jid doen't exist in Stringflow users";
	private static final String GROUP_DOES_NOT_EXIT = "Group with given jid doesn't exist";
	private static final String MEMBER_IS_NOT_IN_GROUP = "Member with given jid does not exist in group";
	private static final String ALREADY_IN_GROUP_AS_MEMBER = "Already in group as a member";

	public GroupService(GroupRepository repository) {
		super(repository);
	}

	public ResponseEntity<?> createGroup(CreateGroupRequest createGroupRequest) {
		LOGGER.debug("Handling createGroup Request");

		if (!this.repository.checkIsUserExist(createGroupRequest.getOwnerJid())) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_CREATOR_DOES_NOT_EXIST), HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isNullOrEmpty(createGroupRequest.getName())) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_NAME_NULL_MESSAGE), HttpStatus.BAD_REQUEST);
		}

		String groupJid = this.buildGroupJid(createGroupRequest.getName(), createGroupRequest.getDomain());

		String subject = createGroupRequest.getSubject() == null ? createGroupRequest.getName()
				: createGroupRequest.getSubject();
		AccessMode accessmode = createGroupRequest.getAccessMode() == null ? AccessMode.PUBLIC
				: createGroupRequest.getAccessMode();

		boolean created = repository.createGroup(groupJid, createGroupRequest.getName(), subject, accessmode);

		if (created) {
			String ownerNickName = createGroupRequest.getOwnerNickName() == null
					? this.repository.getUserName(createGroupRequest.getOwnerJid())
					: createGroupRequest.getOwnerNickName();

			GroupMember member = new GroupMember(createGroupRequest.getOwnerJid(), ownerNickName);
			member.setAffiliation(Affiliation.OWNER);
			member.setRole(Role.MODERATOR);

			repository.addGroupMember(groupJid, member);

			Group group = new Group(groupJid, createGroupRequest.getName(), createGroupRequest.getSubject(),
					createGroupRequest.getAccessMode());

			group.addMember(member);
			return new ResponseEntity<>(group, HttpStatus.CREATED);

		} else {
			LOGGER.error("Failed to create new group with jid {}, name {} and and subject {} and domain {}", groupJid,
					createGroupRequest.getName(), createGroupRequest.getSubject(), createGroupRequest.getDomain());
			return new ResponseEntity<>(new ResponseMessage(GROUP_CREATE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	public ResponseEntity<?> updateGroupSubject(String groupJid, String subject) {
		LOGGER.debug("Handling updateGroupSubject request for groupId {} and subject {}", groupJid, subject);
		boolean updated = repository.updateGroupSubject(groupJid, subject);

		if (updated) {

			LOGGER.info("group subject updated successfully for groupId {}", groupJid);
			return new ResponseEntity<>(new ResponseMessage(UPDATE_GROUP_SUBJECT_SUCCESSFULLY), HttpStatus.OK);

		} else {

			return new ResponseEntity<>(new ResponseMessage(UPDATE_GROUP_SUBJECT_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> updateGroupAccessMode(String groupJid, AccessMode accessMode) {
		LOGGER.debug("Handling updateGroupAccessMode request for groupId {} and accessMode {}", groupJid, accessMode);
		boolean updated = repository.updateGroupAccessMode(groupJid, accessMode);

		if (updated) {

			LOGGER.info("group accessMode updated successfully for groupId {}", groupJid);
			return new ResponseEntity<>(new ResponseMessage(UPDATE_GROUP_ACCESSMODE_SUCCESSFULLY), HttpStatus.OK);

		} else {

			return new ResponseEntity<>(new ResponseMessage(UPDATE_GROUP_ACCESSMODE_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> addGroupMember(String groupJid, GroupMember member) {
		LOGGER.debug("Handling addGroupMember request of groupJid {} and memberJid {}", groupJid, member.getJid());

		if (!this.repository.checkIsUserExist(member.getJid())) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_MEMBER_IS_NOT_SF_USER), HttpStatus.BAD_REQUEST);
		}

		if (!repository.isGroupExist(groupJid)) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_DOES_NOT_EXIT), HttpStatus.BAD_REQUEST);
		}

		if (repository.isGroupMember(groupJid, member.getJid())) {
			LOGGER.info("member with jid {} already in groupJid {}", groupJid);
			return new ResponseEntity<>(new ResponseMessage(ADDED_GROUP_MEMBER_SUCCESSFULLY), HttpStatus.OK);
		}

		if (StringUtils.isNullOrEmpty(member.getNickName())) {
			member.setNickName(repository.getUserName(member.getJid()));
		}

		if (repository.isGroupMember(groupJid, member.getJid())) {
			LOGGER.info("Member {} is already in  group {}. So trying to update it", member.getJid(), groupJid);
			return new ResponseEntity<>(new ResponseMessage(ALREADY_IN_GROUP_AS_MEMBER), HttpStatus.OK);

		} else {
			if (member.getRole() == null) {
				member.setRole(Role.PARTICIPANT);
			}

			if (member.getAffiliation() == null) {
				member.setAffiliation(Affiliation.MEMBER);
			}

			boolean added = repository.addGroupMember(groupJid, member);

			if (added) {

				LOGGER.info("Group member added  successfully for groupJid {}", groupJid);
				return new ResponseEntity<>(new ResponseMessage(ADDED_GROUP_MEMBER_SUCCESSFULLY), HttpStatus.OK);

			} else {

				return new ResponseEntity<>(new ResponseMessage(ADDED_GROUP_MEMBER_FAILURE_MESSAGE),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> removeGroupMember(String groupJid, String memberJid) {
		LOGGER.debug("Handling removeGroupMember request of groupId {} and memberJid {}", groupJid, memberJid);

		if (!this.repository.checkIsUserExist(memberJid)) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_MEMBER_IS_NOT_SF_USER), HttpStatus.BAD_REQUEST);
		}

		if (!repository.isGroupExist(groupJid)) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_DOES_NOT_EXIT), HttpStatus.BAD_REQUEST);
		}

		boolean removed = repository.removeGroupMember(groupJid, memberJid);

		if (removed) {

			LOGGER.info("Group member removed  successfully for groupId {}", groupJid);
			return new ResponseEntity<>(new ResponseMessage(REMOVED_GROUP_MEMBER_SUCCESSFULLY), HttpStatus.OK);

		} else {

			return new ResponseEntity<>(new ResponseMessage(REMOVED_GROUP_MEMBER_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> updateGroupMemberDetail(String groupJid, @NotNull GroupMember member) {
		LOGGER.debug("Handling updateGroupMemberDetail request of groupId {} and memberJid {}", groupJid,
				member.getJid());

		if (!repository.isGroupExist(groupJid)) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_DOES_NOT_EXIT), HttpStatus.BAD_REQUEST);
		}

		if (!repository.isGroupMember(groupJid, member.getJid())) {
			return new ResponseEntity<>(new ResponseMessage(MEMBER_IS_NOT_IN_GROUP), HttpStatus.BAD_REQUEST);
		}

		if (updateGroupMember(groupJid, member)) {

			LOGGER.info("Group member detail updated  successfully for groupId {}", groupJid);
			return new ResponseEntity<>(new ResponseMessage(UPDATED_GROUP_MEMBER_DETAIL_SUCCESSFULLY), HttpStatus.OK);

		} else {

			return new ResponseEntity<>(new ResponseMessage(UPDATE_GROUP_MEMBER_DETAIL_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean updateGroupMember(String groupJid, GroupMember member) {
		if (member.getRole() == null) {
			member.setRole(Role.PARTICIPANT);
		}

		if (member.getAffiliation() == null) {
			member.setAffiliation(Affiliation.MEMBER);
		}

		return repository.updateGroupMemberDetail(groupJid, member);

	}

	public ResponseEntity<?> deleteGroup(String groupJid) {
		LOGGER.debug("Handling deleteGroup request of groupId {}", groupJid);
		boolean deleted = repository.deleteGroup(groupJid);

		if (!repository.isGroupExist(groupJid)) {
			return new ResponseEntity<>(new ResponseMessage(GROUP_DOES_NOT_EXIT), HttpStatus.BAD_REQUEST);
		}

		if (deleted) {

			LOGGER.info("Group deleted successfully for groupId {}", groupJid);
			return new ResponseEntity<>(new ResponseMessage(DELETED_GROUP_SUCCESSFULLY), HttpStatus.OK);

		} else {

			return new ResponseEntity<>(new ResponseMessage(DELTED_GROUP_FAILURE_MESSAGE),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
